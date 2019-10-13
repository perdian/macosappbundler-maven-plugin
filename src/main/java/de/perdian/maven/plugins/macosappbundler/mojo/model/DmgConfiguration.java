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

    @Parameter
    public boolean useGenIsoImage = false;

    @Parameter
    public boolean autoFallback = false;

}
