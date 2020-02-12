/*
 * macOS app bundler Maven plugin
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
package de.perdian.maven.plugins.macosappbundler.mojo;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.DefaultArtifactHandlerStub;

import java.io.File;

public class TestArtifactStub extends ArtifactStub {

    public TestArtifactStub(Model model) {
        setArtifactId(model.getArtifactId());
        setGroupId(model.getGroupId());
        setVersion(model.getVersion());
    }

    @Override
    public String getBaseVersion() {
        return this.getVersion();
    }

    @Override
    public ArtifactHandler getArtifactHandler() {
        return new DefaultArtifactHandlerStub("jar");
    }

    @Override
    public File getFile() {
        ArtifactRepositoryLayout repositoryLayout = new DefaultRepositoryLayout();
        return new File("./target", repositoryLayout.pathOf(this));
    }
}
