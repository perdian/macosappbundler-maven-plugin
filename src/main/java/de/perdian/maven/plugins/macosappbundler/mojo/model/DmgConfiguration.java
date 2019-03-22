package de.perdian.maven.plugins.macosappbundler.mojo.model;

import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;

public class DmgConfiguration {

    @Parameter
    public boolean generate = false;

    @Parameter
    public List<FileSet> additionalResources = null;

    @Parameter
    public boolean createApplicationsSymlink = true;

}
