package de.perdian.maven.plugins.macosappbundler.mojo.impl.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.mappers.MapperException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

public class IO {

    public static void copyFileSets(File baseDirectory, Collection<FileSet> fileSets) throws IOException, MapperException {
        FileSetManager fileSetManager = new FileSetManager();
        for (FileSet fileSet : fileSets) {

            File fileSetDirectory = new File(fileSet.getDirectory());
            Map<String, String> mappedFiles = fileSetManager.mapIncludedFiles(fileSet);
            if (!fileSetDirectory.isAbsolute()) {
                fileSetDirectory = new File(baseDirectory, fileSet.getDirectory());
            }

            File outputDirectory = IO.resolveTargetDirectory(baseDirectory, fileSet);
            for (Map.Entry<String, String> mappedFile : mappedFiles.entrySet()){
                File sourceFile = new File(fileSetDirectory, mappedFile.getKey());
                File targetFile = new File(outputDirectory, mappedFile.getKey());
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }

        }
    }

    private static File resolveTargetDirectory(File baseDirectory, FileSet fileSet) {
        if (StringUtils.isNotEmpty(fileSet.getOutputDirectory())) {
            File targetDirectory = new File(fileSet.getOutputDirectory());
            if (targetDirectory.isAbsolute()) {
                return targetDirectory;
            } else {
                return new File(baseDirectory, fileSet.getOutputDirectory());
            }
        } else {
            return baseDirectory;
        }
    }

}
