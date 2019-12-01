/*
 * macOS app bundler Maven plugin
 * Copyright 2019 Christian Robert
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.apache.maven.shared.utils.cli.Commandline;

import de.perdian.maven.plugins.macosappbundler.mojo.model.DmgConfiguration;

public class DmgGenerator {

    private DmgConfiguration dmgConfiguration = null;
    private String volumeName = null;
    private Log log = null;

    public DmgGenerator(DmgConfiguration dmgConfiguration, String volumeName, Log log) {
        this.setDmgConfiguration(dmgConfiguration);
        this.setVolumeName(volumeName);
        this.setLog(log);
    }

    public void generateDmg(MavenProject project, File appDirectory, File bundleDirectory, File dmgFile) throws MojoExecutionException {

        try {
            File bundleAppDirectory = new File(bundleDirectory, appDirectory.getName());
            if (!bundleAppDirectory.exists()) {
                this.getLog().debug("Creating bundle app directory at: " + bundleAppDirectory.getAbsolutePath());
                bundleAppDirectory.mkdirs();
            }
            Iterator<Path> files = Files.walk(appDirectory.toPath()).iterator();
            while (files.hasNext()) {
                Path sourcePathAbsolute = files.next();
                Path targetPathRelative = appDirectory.toPath().relativize(sourcePathAbsolute);
                Path targetPathAbsolute = bundleAppDirectory.toPath().resolve(targetPathRelative);
                Files.copy(sourcePathAbsolute, targetPathAbsolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (IOException e) {
            this.getLog().error("Cannot copy app directory", e);
            throw new MojoExecutionException("Cannot copy app directory", e);
        }

        if (this.getDmgConfiguration().additionalResources != null && !this.getDmgConfiguration().additionalResources.isEmpty()) {
            this.getLog().info("Copy additional resources");
            this.copyAdditionalDmgResources(project, this.getDmgConfiguration().additionalResources, bundleDirectory);
        }
        if (this.getDmgConfiguration().createApplicationsSymlink) {
            this.getLog().info("Create Applications symlink");
            try {
                Files.createSymbolicLink(new File(bundleDirectory, "Applications").toPath(), Paths.get("/Applications"));
            } catch (IOException e) {
                throw new MojoExecutionException("Cannot create link to Applications folder at: " + new File(bundleDirectory, "Applications").getAbsolutePath(), e);
            }
        }
        this.getLog().info("Generating DMG archive");
        this.generateDmgArchive(bundleDirectory, dmgFile);

    }

    private void generateDmgArchive(File bundleDirectory, File dmgFile) throws MojoExecutionException {
        try {
            if (this.getDmgConfiguration().useGenIsoImage) {
                this.generateDmgArchiveGenIsoImage(bundleDirectory, dmgFile, false);
            } else {
                this.generateDmgArchiveHdiUtil(bundleDirectory, dmgFile, false);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Cannot generate DMG archive at: " + dmgFile.getAbsolutePath(), e);
        }
    }

    private void generateDmgArchiveGenIsoImage(File bundleDirectory, File dmgFile, boolean fallback) throws Exception {
        Commandline dmgCommandLine = new Commandline();
        dmgCommandLine.setExecutable("genisoimage");
        dmgCommandLine.createArg().setValue("-D");
        dmgCommandLine.createArg().setValue("-V");
        dmgCommandLine.createArg().setValue(this.getVolumeName());
        dmgCommandLine.createArg().setValue("-no-pad");
        dmgCommandLine.createArg().setValue("-r");
        dmgCommandLine.createArg().setValue("-apple");
        dmgCommandLine.createArg().setValue("-quiet");
        dmgCommandLine.createArg().setValue("-o");
        dmgCommandLine.createArg().setValue(dmgFile.getAbsolutePath());
        dmgCommandLine.createArg().setValue(bundleDirectory.getAbsolutePath());
        int returnValue = dmgCommandLine.execute().waitFor();
        if (returnValue != 0) {
            if (this.getDmgConfiguration().autoFallback && !fallback) {
                generateDmgArchiveHdiUtil(bundleDirectory, dmgFile, true);
            } else {
                throw new Exception("Command 'genisoimage' exited with status " + returnValue);
            }
        }
    }

    private void generateDmgArchiveHdiUtil(File bundleDirectory, File dmgFile, boolean fallback) throws Exception {
        Commandline dmgCommandLine = new Commandline();
        dmgCommandLine.setExecutable("hdiutil");
        dmgCommandLine.createArg().setValue("create");
        dmgCommandLine.createArg().setValue("-srcfolder");
        dmgCommandLine.createArg().setValue(bundleDirectory.getAbsolutePath());
        dmgCommandLine.createArg().setValue(dmgFile.getAbsolutePath());
        dmgCommandLine.createArg().setValue("-volname");
        dmgCommandLine.createArg().setValue(this.getVolumeName());
        int returnValue = dmgCommandLine.execute().waitFor();
        if (returnValue != 0) {
            if (this.getDmgConfiguration().autoFallback && !fallback) {
                generateDmgArchiveGenIsoImage(bundleDirectory, dmgFile, true);
            } else {
                throw new Exception("Command 'hdiutil' exited with status " + returnValue);
            }
        }
    }

    private void copyAdditionalDmgResources(MavenProject project, List<FileSet> additionalResources, File bundleDirectory) throws MojoExecutionException {
        try {
            FileSetManager fileSetManager = new FileSetManager();
            for (FileSet fileSet : additionalResources) {
                File fileSetDirectory = new File(fileSet.getDirectory());
                Map<String, String> mappedFiles = fileSetManager.mapIncludedFiles(fileSet);
                if (!fileSetDirectory.isAbsolute()) {
                    fileSetDirectory = new File(project.getBasedir(), fileSet.getDirectory());
                }
                for (Map.Entry<String, String> mappedFile : mappedFiles.entrySet()) {
                    File sourceFile = new File(fileSetDirectory, mappedFile.getKey());
                    File targetFile = new File(bundleDirectory, mappedFile.getKey());
                    FileUtils.copyFile(sourceFile, targetFile);
                }
            }
        } catch (Exception e) {
            this.getLog().error("Cannot copy additional resources", e);
            throw new MojoExecutionException("Cannot copy additional resources", e);
        }
    }

    private DmgConfiguration getDmgConfiguration() {
        return this.dmgConfiguration;
    }
    private void setDmgConfiguration(DmgConfiguration dmgConfiguration) {
        this.dmgConfiguration = dmgConfiguration;
    }

    private String getVolumeName() {
        return this.volumeName;
    }
    private void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    private Log getLog() {
        return this.log;
    }
    private void setLog(Log log) {
        this.log = log;
    }

}
