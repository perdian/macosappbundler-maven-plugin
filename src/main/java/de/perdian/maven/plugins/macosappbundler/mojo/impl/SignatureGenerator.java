/*
 * macOS app bundler Maven plugin
 * Copyright 2019 Christian Seifert
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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.utils.cli.Commandline;

import de.perdian.maven.plugins.macosappbundler.mojo.model.CodesignConfiguration;

public class SignatureGenerator {

    private CodesignConfiguration configuration = null;
    private Log log = null;

    public SignatureGenerator(CodesignConfiguration configuration, Log log) {
        this.setConfiguration(configuration);
        this.setLog(log);
    }

    public void sign(File appDirectory) throws MojoExecutionException {

        this.getLog().info("Signing application '" + appDirectory.getName() + "' using identity: '" + this.getConfiguration().identity + "'");

        String preserveMetadataValue = StringUtils.join(this.getConfiguration().preserveMetadata, ",");

        Commandline codesignCommandLine = new Commandline();
        codesignCommandLine.setExecutable("codesign");
        codesignCommandLine.createArg().setValue("--force");
        codesignCommandLine.createArg().setValue("--timestamp");
        codesignCommandLine.createArg().setValue("--sign");
        codesignCommandLine.createArg().setValue(this.getConfiguration().identity);
        if (StringUtils.isNotEmpty(preserveMetadataValue)) {
            codesignCommandLine.createArg().setValue("--preserve-metadata=" + preserveMetadataValue);
        }
        codesignCommandLine.createArg().setFile(appDirectory);

        try {
            Process codesignProcess = codesignCommandLine.execute();
            int codesignReturnValue = codesignProcess.waitFor();
            if (codesignReturnValue != 0) {
                throw new Exception("Command 'codesign' exited with status " + codesignReturnValue);
            }
        } catch (Exception e) {
            this.getLog().error("Cannot sign app", e);
            throw new MojoExecutionException("Cannot sign app", e);
        }

    }

    private CodesignConfiguration getConfiguration() {
        return this.configuration;
    }
    private void setConfiguration(CodesignConfiguration configuration) {
        this.configuration = configuration;
    }

    private Log getLog() {
        return this.log;
    }
    private void setLog(Log log) {
        this.log = log;
    }

}
