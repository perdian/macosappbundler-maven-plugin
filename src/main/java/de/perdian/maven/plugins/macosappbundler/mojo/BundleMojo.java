package de.perdian.maven.plugins.macosappbundler.mojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import de.perdian.maven.plugins.macosappbundler.mojo.model.DmgConfiguration;
import de.perdian.maven.plugins.macosappbundler.mojo.model.PlistConfiguration;

/**
 * Create all artifacts to publish a Java application as macOS application bundle.
 */

@Mojo(name = "bundle", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PACKAGE)
public class BundleMojo extends AbstractMojo {

    @Component
    private MavenProject project = null;

    @Parameter(required = true)
    private PlistConfiguration plist = null;

    @Parameter
    private DmgConfiguration dmg = new DmgConfiguration();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (StringUtils.isEmpty(this.plist.JVMMainClassName) && StringUtils.isEmpty(this.plist.JVMMainModuleName)) {
            throw new MojoExecutionException("Neither 'JVMMainClassName' nor 'JVMMainModuleName' have been defined!");
        } else if (StringUtils.isNotEmpty(this.plist.JVMMainClassName) && StringUtils.isNotEmpty(this.plist.JVMMainModuleName)) {
            throw new MojoExecutionException("Both 'JVMMainClassName' and 'JVMMainModuleName' have been defined! Make sure to define only one to signalize whether to use a classic classpath application or a moduel application.");
        } else {

            this.plist.CFBundleDisplayName = StringUtils.defaultIfEmpty(this.plist.CFBundleDisplayName, this.project.getName());
            this.plist.CFBundleName = StringUtils.defaultIfEmpty(this.plist.CFBundleName, this.project.getName());
            this.plist.CFBundleIdentifier = StringUtils.defaultIfEmpty(this.plist.CFBundleIdentifier, this.project.getGroupId() + "." + this.project.getArtifactId());
            this.plist.CFBundleShortVersionString = StringUtils.defaultIfEmpty(this.plist.CFBundleShortVersionString, this.project.getVersion());
            this.plist.CFBundleExecutable = StringUtils.defaultIfEmpty(this.plist.CFBundleExecutable, "JavaLauncher");

            File targetDirectory = new File(this.project.getBuild().getDirectory());
            File bundleDirectory = new File(targetDirectory, "bundle");
            File appDirectory = new File(bundleDirectory, this.project.getBuild().getFinalName() + ".app");
            this.getLog().info("Creating app directory at: " + appDirectory.getAbsolutePath());
            appDirectory.mkdirs();

            this.getLog().info("Copy application dependencies");
            this.copyApplicationDependencies(new File(appDirectory, "Contents/Java"));

            this.getLog().info("Copy native executable");
            this.copyNativeExecutable(new File(appDirectory, "Contents/MacOS"));

            this.getLog().info("Generating Info.plist");
            this.generatePlist(new File(appDirectory, "Contents/"));

            if (this.dmg.generate) {
                this.getLog().info("Generating DMG archive");
                this.generateDmgArchive(bundleDirectory);
            }

        }
    }

    private void copyApplicationDependencies(File appJavaDirectory) throws MojoExecutionException, MojoFailureException {
        this.getLog().debug("Copy application dependencies to: " + appJavaDirectory.getAbsolutePath());
        try {
            if (StringUtils.isNotEmpty(this.plist.JVMMainClassName)) {
                this.copyClasspathApplicationDependencies(new File(appJavaDirectory, "classpath"));
            } else if (StringUtils.isNotEmpty(this.plist.JVMMainModuleName)) {
                this.copyModuleApplicationDependencies(new File(appJavaDirectory, "modules"));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot copy dependencies", e);
        }
    }

    private void copyClasspathApplicationDependencies(File classpathDirectory) throws IOException {
        ArtifactRepositoryLayout repositoryLayout = new DefaultRepositoryLayout();
        this.copyClasspathApplicationDependencyArtifact(this.project.getArtifact(), classpathDirectory, repositoryLayout);
        for (Artifact artifact : this.project.getArtifacts()) {
            this.copyClasspathApplicationDependencyArtifact(artifact, classpathDirectory, repositoryLayout);
        }
    }

    private void copyClasspathApplicationDependencyArtifact(Artifact artifact, File targetDirectory, ArtifactRepositoryLayout repositoryLayout) throws IOException {
        File targetFile = new File(targetDirectory, repositoryLayout.pathOf(artifact));
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        FileUtils.copyFile(artifact.getFile(), targetFile);
    }

    private void copyModuleApplicationDependencies(File modulesDirectory) throws IOException {
        this.copyModuleApplicationDependencyArtifact(this.project.getArtifact(), modulesDirectory);
        for (Artifact artifact : this.project.getArtifacts()) {
            this.copyModuleApplicationDependencyArtifact(artifact, modulesDirectory);
        }
    }

    private void copyModuleApplicationDependencyArtifact(Artifact artifact, File modulesDirectory) throws IOException {
        StringBuilder targetFileName = new StringBuilder();
        targetFileName.append(artifact.getArtifactId());
        targetFileName.append("-").append(artifact.getVersion());
        targetFileName.append(".").append(FilenameUtils.getExtension(artifact.getFile().getName()));
        File targetFile = new File(modulesDirectory, targetFileName.toString());
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        FileUtils.copyFile(artifact.getFile(), targetFile);
    }

    private void copyNativeExecutable(File targetDirectory) throws MojoExecutionException, MojoFailureException {
        try {
            URL nativeExecutableSource = this.getClass().getClassLoader().getResource("JavaLauncher");
            if (nativeExecutableSource == null) {
                throw new MojoExecutionException("No native executable packaged in plugin");
            } else {
                String targetFileName = StringUtils.defaultIfEmpty(this.plist.CFBundleExecutable, "JavaLauncher");
                File targetFile = new File(targetDirectory, targetFileName);
                try (InputStream nativeExecutableStream = nativeExecutableSource.openStream()) {
                    FileUtils.copyToFile(nativeExecutableStream, targetFile);
                }
                targetFile.setExecutable(true);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot copy native executable", e);
        }
    }

    private void generatePlist(File contentsDirectory) throws MojoExecutionException, MojoFailureException {
        Map<String, String> additionalProperties = new LinkedHashMap<>();
        String iconFileName = this.copyIcon(contentsDirectory);
        if (StringUtils.isNotEmpty(iconFileName)) {
            additionalProperties.put("CFBundleIconFile", iconFileName);
        }
        try {
            FileUtils.write(new File(contentsDirectory, "Info.plist"), this.plist.toXmlString(additionalProperties), "UTF-8");
        } catch (Exception e) {
            throw new MojoExecutionException("Cannot generate Info.plist file", e);
        }
    }

    private String copyIcon(File contentsDirectory) throws MojoExecutionException, MojoFailureException {
        String iconFileValue = this.plist.CFBundleIconFile;
        if (StringUtils.isNotEmpty(iconFileValue)) {
            File iconFile = new File(this.project.getBasedir(), iconFileValue);
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

    private void generateDmgArchive(File bundleDirectory) {
        throw new UnsupportedOperationException("DMG archive generation not supported yet!");
    }

}
