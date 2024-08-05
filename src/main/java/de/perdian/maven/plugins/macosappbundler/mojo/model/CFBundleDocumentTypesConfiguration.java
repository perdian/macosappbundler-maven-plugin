package de.perdian.maven.plugins.macosappbundler.mojo.model;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CFBundleDocumentTypesConfiguration {

    @Parameter
    public List<String> CFBundleTypeExtensions = new ArrayList<>();

    @Parameter
    public String CFBundleTypeName = null;

    @Parameter
    public List<String> CFBundleTypeOSTypes = new ArrayList<>();

    @Parameter
    public String CFBundleTypeRole = null;

}
