/*
 * macOS app bundler Maven plugin
 * Copyright 2019 Christian Seifert
 * Copyright 2020 Håvard Bakke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.maven.plugins.macosappbundler.mojo.impl;

import de.perdian.maven.plugins.macosappbundler.mojo.impl.support.IO;
import de.perdian.maven.plugins.macosappbundler.mojo.model.AppConfiguration;
import de.perdian.maven.plugins.macosappbundler.mojo.model.NativeBinaryType;
import de.perdian.maven.plugins.macosappbundler.mojo.model.PlistConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppGenerator {

    private PlistConfiguration plistConfiguration = null;
    private NativeBinaryType nativeBinaryType = NativeBinaryType.UNIVERSAL;
    private boolean includeJdk = false;
    private String jdkLocation = null;
    private Log log = null;
    private AppConfiguration appConfiguration = null;

    public AppGenerator(PlistConfiguration plistConfiguration, AppConfiguration appConfiguration, Log log) {
        this.setPlistConfiguration(plistConfiguration);
        this.setAppConfiguration(appConfiguration);
        this.setLog(log);
    }

    private AppConfiguration getAppConfiguration() {
        return this.appConfiguration;
    }
    private void setAppConfiguration(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    public void generateApp(MavenProject project, File appDirectory) throws MojoExecutionException {
        this.copyApplicationClasses(project, new File(appDirectory, "Contents/Java"));
        this.copyJdk(new File(appDirectory, "Contents/Java/runtime"));
        this.copyNativeExecutable(new File(appDirectory, "Contents/MacOS"));
        this.generatePlist(project, new File(appDirectory, "Contents/"));

        if (this.getAppConfiguration().additionalResources != null && !this.getAppConfiguration().additionalResources.isEmpty()) {
            this.getLog().info("Copy additional app resources");
            this.copyAdditionalAppResources(project, this.getAppConfiguration().additionalResources, appDirectory);
        }
    }

    private void copyAdditionalAppResources(MavenProject project, List<FileSet> additionalResources, File appDirectory) throws MojoExecutionException {
        try {
            IO.copyFileSets(appDirectory, additionalResources);
        } catch (Exception e) {
            this.getLog().error("Cannot copy additional app resources", e);
            throw new MojoExecutionException("Cannot copy additional app resources", e);
        }
    }

    private void copyApplicationClasses(MavenProject project, File appJavaDirectory) throws MojoExecutionException {
        this.getLog().info("Copy application classes to: " + appJavaDirectory.getAbsolutePath());
        try {
            if (StringUtils.isNotEmpty(this.getPlistConfiguration().JVMMainClassName)) {
                this.copyClasspathApplicationClasses(project, new File(appJavaDirectory, "classpath"));
            } else if (StringUtils.isNotEmpty(this.getPlistConfiguration().JVMMainModuleName)) {
                this.copyModulesApplicationClasses(project, new File(appJavaDirectory, "modules"));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot copy dependencies", e);
        }
    }

    private void copyClasspathApplicationClasses(MavenProject project, File classpathDirectory) throws IOException {
        ArtifactRepositoryLayout repositoryLayout = new DefaultRepositoryLayout();
        Artifact primaryArtifact = this.resolvePrimaryArtifact(project);
        this.copyClasspathApplicationDependencyArtifact(primaryArtifact, classpathDirectory, repositoryLayout);
        if (this.getAppConfiguration().isIncludeDependencies()) {
            for (Artifact artifact : project.getArtifacts()) {
                this.copyClasspathApplicationDependencyArtifact(artifact, classpathDirectory, repositoryLayout);
            }
        } else {
            this.getLog().debug("Inclusion of dependencies has been disbaled");
        }
    }

    private Artifact resolvePrimaryArtifact(MavenProject project) {
        String classifier = this.getAppConfiguration().getPrimaryArtifactClassifier();
    	if (StringUtils.isNotEmpty(classifier)) {
            for (Artifact attachedArtifact : project.getAttachedArtifacts()) {
                if (classifier.equalsIgnoreCase(attachedArtifact.getClassifier())) {
                    return attachedArtifact;
                }
            }
        }
        return project.getArtifact();
    }

    private void copyClasspathApplicationDependencyArtifact(Artifact artifact, File targetDirectory, ArtifactRepositoryLayout repositoryLayout) throws IOException {
        File targetFile = new File(targetDirectory, repositoryLayout.pathOf(artifact));
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        FileUtils.copyFile(artifact.getFile(), targetFile);
    }

    private void copyModulesApplicationClasses(MavenProject project, File modulesDirectory) throws IOException {
        Artifact primaryArtifact = this.resolvePrimaryArtifact(project);
    	this.copyModulesApplicationClassesArtifact(primaryArtifact, modulesDirectory);
        if (this.getAppConfiguration().isIncludeDependencies()) {
            for (Artifact artifact : project.getArtifacts()) {
                this.copyModulesApplicationClassesArtifact(artifact, modulesDirectory);
            }
        } else {
            this.getLog().debug("Inclusion of dependencies has been disbaled");
        }
    }

    private void copyModulesApplicationClassesArtifact(Artifact artifact, File modulesDirectory) throws IOException {
        StringBuilder targetFileName = new StringBuilder();
        targetFileName.append(artifact.getArtifactId());
        targetFileName.append("-").append(artifact.getVersion());
        String classifier = artifact.getClassifier();
        if (classifier != null && !classifier.isEmpty()) targetFileName.append("-").append(classifier);
        targetFileName.append(".").append(FilenameUtils.getExtension(artifact.getFile().getName()));
        File targetFile = new File(modulesDirectory, targetFileName.toString());
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        FileUtils.copyFile(artifact.getFile(), targetFile);
    }

    private void copyNativeExecutable(File targetDirectory) throws MojoExecutionException {
        try {
            URL nativeBinarySource = this.resolveNativeExecutable();
            String targetFileName = StringUtils.defaultIfEmpty(this.getPlistConfiguration().CFBundleExecutable, "JavaLauncher");
            File targetFile = new File(targetDirectory, targetFileName);
            this.getLog().info("Copy native executable for binary type " + this.getNativeBinaryType() + " to: " + targetFile.getAbsolutePath());
            try (InputStream nativeExecutableStream = nativeBinarySource.openStream()) {
                FileUtils.copyInputStreamToFile(nativeExecutableStream, targetFile);
            }
            targetFile.setExecutable(true, false);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot copy native executable", e);
        }
    }

    private URL resolveNativeExecutable() throws MojoExecutionException {
        URL nativeBinarySource = this.getClass().getClassLoader().getResource(this.getNativeBinaryType().getFilename());
        if (nativeBinarySource == null) {
            throw new MojoExecutionException("No native executable packaged in plugin for native binary type " + this.getNativeBinaryType().name() + " found at location: " + this.getNativeBinaryType().getFilename());
        } else {
            return nativeBinarySource;
        }
    }

    private void generatePlist(MavenProject project, File contentsDirectory) throws MojoExecutionException {
        Map<String, String> additionalProperties = new LinkedHashMap<>();
        String iconFileName = this.copyIcon(project, contentsDirectory);
        if (StringUtils.isNotEmpty(iconFileName)) {
            additionalProperties.put("CFBundleIconFile", iconFileName);
        }
        try {
            File plistFile = new File(contentsDirectory, "Info.plist");
            this.getLog().info("Generating Info.plist");
            FileUtils.write(plistFile, this.getPlistConfiguration().toXmlString(additionalProperties), "UTF-8");
        } catch (Exception e) {
            throw new MojoExecutionException("Cannot generate Info.plist file", e);
        }
    }

    private String copyIcon(MavenProject project, File contentsDirectory) throws MojoExecutionException {
        String iconFileValue = this.getPlistConfiguration().CFBundleIconFile;
        if (StringUtils.isNotEmpty(iconFileValue)) {
            File iconFile = new File(project.getBasedir(), iconFileValue);
            if (!iconFile.exists()) {
                throw new MojoExecutionException("Cannot find declared icon file " + iconFile.getName() + " at: " + iconFile.getAbsolutePath());
            } else {
                File resourcesDirectory = new File(contentsDirectory, "Resources");
                File targetFile = new File(resourcesDirectory, iconFile.getName());
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                try {
                    FileUtils.copyFile(iconFile, targetFile);
                    return targetFile.getName();
                } catch (IOException e) {
                    throw new MojoExecutionException("Cannot copy icon file to: " + targetFile.getAbsolutePath(), e);
                }
            }
        } else {
            return null;
        }
    }

    private void copyJdk(File targetDirectory) throws MojoExecutionException {
        if (this.isIncludeJdk()) {
            if (StringUtils.isEmpty(this.getJdkLocation())) {
                this.getLog().info("Copy JDK from system default directory at: " + System.getProperty("java.home"));
                this.copyJdkFromDirectory(targetDirectory, new File(System.getProperty("java.home")));
            } else {
                File jdkDirectory = new File(this.getJdkLocation());
                if (!jdkDirectory.exists()) {
                    throw new MojoExecutionException("Specified JDK directory doesn't exist at: " + jdkDirectory.getAbsolutePath());
                } else {

                    // The JDK is located below the "Contents/Home" directory, so in case the location
                    // is the parent directory (e.g. /Library/Java/JavaVirtualMachines//Library/Java/JavaVirtualMachines/adoptopenjdk-14.jdk
                    // we can defer the actual JDK directory if a "Contents/Home" subdirectory is existing
                    File contentsHomeDirectory = new File(jdkDirectory, "Contents/Home/");
                    if (contentsHomeDirectory.exists() && contentsHomeDirectory.isDirectory()) {
                        jdkDirectory = contentsHomeDirectory;
                    }

                    this.getLog().info("Copy JDK from explicit directory at: " + jdkDirectory.getAbsolutePath());
                    this.copyJdkFromDirectory(targetDirectory, jdkDirectory);

                }
            }
        }
    }

    private void copyJdkFromDirectory(File targetDirectory, File sourceDirectory) throws MojoExecutionException {
        Path sourceDirectoryPath = sourceDirectory.toPath();
        Path targetDirectoryPath = targetDirectory.toPath();
        try {
            Files.walk(sourceDirectoryPath).forEach(sourcePath -> {
                try {
                    Path sourcePathRelative = sourceDirectoryPath.relativize(sourcePath);
                    Path targetPath = targetDirectoryPath.resolve(sourcePathRelative);
                    if (Files.isDirectory(sourcePath)) {
                        if (!Files.exists(targetPath)) {
                            Files.createDirectory(targetPath);
                        }
                    } else {
                        if (!Files.exists(targetPath.getParent())) {
                            Files.createDirectories(targetPath.getParent());
                        }
                        this.getLog().debug("Copying JDK file from '" + sourcePath + "' to '" + targetPath + "'");
                        Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Cannot copy JDK directory '" + sourceDirectory + "' to '" + targetDirectory + "'", e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Cannot copy JDK directory '" + sourceDirectory + "' to '" + targetDirectory + "'", e);
        }
    }

    private PlistConfiguration getPlistConfiguration() {
        return this.plistConfiguration;
    }
    private void setPlistConfiguration(PlistConfiguration plistConfiguration) {
        this.plistConfiguration = plistConfiguration;
    }

    public boolean isIncludeJdk() {
        return this.includeJdk;
    }
    public void setIncludeJdk(boolean includeJdk) {
        this.includeJdk = includeJdk;
    }

    public NativeBinaryType getNativeBinaryType() {
        return this.nativeBinaryType;
    }
    public void setNativeBinaryType(NativeBinaryType nativeBinaryType) {
        this.nativeBinaryType = nativeBinaryType;
    }

    public String getJdkLocation() {
        return this.jdkLocation;
    }
    public void setJdkLocation(String jdkLocation) {
        this.jdkLocation = jdkLocation;
    }

    private Log getLog() {
        return this.log;
    }
    private void setLog(Log log) {
        this.log = log;
    }

}
