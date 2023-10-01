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
package de.perdian.maven.plugins.macosappbundler.mojo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class PlistConfigurationTest {

    public static String EXPECTED_PLIST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
            "<plist version=\"1.0\">\n" +
            "    <dict>\n" +
            "        <key>CFBundleDisplayName</key>\n" +
            "        <string>Test bundle display name</string>\n" +
            "        <key>CFBundleExecutable</key>\n" +
            "        <string>JavaLauncher</string>\n" +
            "        <key>CFBundleIdentifier</key>\n" +
            "        <string>com.test.bundle</string>\n" +
            "        <key>CFBundleName</key>\n" +
            "        <string>Test bundle name</string>\n" +
            "        <key>CFBundleShortVersionString</key>\n" +
            "        <string>1.0.0</string>\n" +
            "        <key>CFBundleDevelopmentRegion</key>\n" +
            "        <string>English</string>\n" +
            "        <key>CFBundleURLTypes</key>\n" +
            "        <array>\n" +
            "            <dict>\n" +
            "                <key>CFBundleURLSchemes</key>\n" +
            "                <array>\n" +
            "                    <string>xxx</string>\n" +
            "                    <string>yyy</string>\n" +
            "                    <string>zzz</string>\n" +
            "                </array>\n" +
            "            </dict>\n" +
            "        </array>\n" +
            "        <key>JVMMainClassName</key>\n" +
            "        <string>com.test.bundle.main</string>\n" +
            "        <key>JVMMainModuleName</key>\n" +
            "        <string>com.test.bundle.module.main</string>\n" +
            "        <key>NSHighResolutionCapable</key>\n" +
            "        <true/>\n" +
            "        <key>NSSupportsAutomaticGraphicsSwitching</key>\n" +
            "        <true/>\n" +
            "        <key>CFBundleIconFile</key>\n" +
            "        <string>test.icns</string>\n" +
            "    </dict>\n" +
            "</plist>\n";

    @Test
    void simplePlistTest() throws Exception {
        PlistConfiguration plist = generatePlist();

        Map<String, String> additionalProperties = new LinkedHashMap<>();
        additionalProperties.put("CFBundleIconFile", "test.icns");
        String plistXml = plist.toXmlString(additionalProperties);

        assertNotNull(plistXml);
        assertEquals(EXPECTED_PLIST_XML, plistXml);
    }

    static PlistConfiguration generatePlist() throws Exception {
        PlistConfiguration plist = new PlistConfiguration();
        plist.CFBundleDisplayName = StringUtils.defaultIfEmpty(plist.CFBundleDisplayName, "Test bundle display name");
        plist.CFBundleName = StringUtils.defaultIfEmpty(plist.CFBundleName, "Test bundle name");
        plist.CFBundleIdentifier = StringUtils.defaultIfEmpty(plist.CFBundleIdentifier, "com.test.bundle");
        plist.CFBundleShortVersionString = StringUtils.defaultIfEmpty(plist.CFBundleShortVersionString, "1.0.0");
        plist.CFBundleExecutable = StringUtils.defaultIfEmpty(plist.CFBundleExecutable, "JavaLauncher");
        plist.CFBundleDevelopmentRegion = StringUtils.defaultIfEmpty(plist.CFBundleDevelopmentRegion, "English");
        plist.JVMMainClassName = StringUtils.defaultIfEmpty(plist.JVMMainClassName, "com.test.bundle.main");
        plist.JVMMainModuleName = StringUtils.defaultIfEmpty(plist.JVMMainModuleName, "com.test.bundle.module.main");
        plist.CFBundleURLTypes = Arrays.asList("xxx", "yyy", "zzz");
        return plist;
    }
}
