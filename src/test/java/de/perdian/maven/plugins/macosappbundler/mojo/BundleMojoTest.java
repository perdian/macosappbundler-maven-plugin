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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class BundleMojoTest extends AbstractMojoTestCase {

    @Test
    public void testBundleMojoGoal() throws Exception {

        File pom = getTestFile( "src/test/resources/basic-test-plugin-config.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        TestProjectStub project = TestProjectStub.fromFile(pom);
        createFile(project.getArtifact().getFile(), 10);

        BundleMojo bundleMojo = (BundleMojo)lookupMojo("bundle", pom);
        bundleMojo.setProject(project);

        assertNotNull( bundleMojo );
        bundleMojo.execute();
    }

    protected void createFile(File file, int sizeInKilobytes) throws IOException {
        if(!Files.exists(Paths.get(file.getAbsolutePath()))) {
            Files.createDirectories(Paths.get(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator))));
            FileOutputStream fos = new FileOutputStream(file);
            Random r = new Random(System.currentTimeMillis());
            byte[] randomBytes = new byte[1024*sizeInKilobytes];
            r.nextBytes(randomBytes);
            fos.write(randomBytes);
            fos.close();
        }
    }

}
