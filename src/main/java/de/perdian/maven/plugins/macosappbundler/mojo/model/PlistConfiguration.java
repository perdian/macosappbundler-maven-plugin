/*
 * macOS app bundler Maven plugin
 * Copyright 2019 Christian Seifert
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

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PlistConfiguration {

    @Parameter
    public String CFBundleIconFile;

    @Parameter
    public String CFBundleIdentifier = null;

    @Parameter
    public String CFBundleDisplayName = null;

    @Parameter
    public String CFBundleName = null;

    @Parameter
    public String CFBundleShortVersionString = null;

    @Parameter
    public String CFBundleExecutable = null;

    @Parameter
    public List<String> CFBundleURLTypes = null;

    @Parameter
    public String CFBundleDevelopmentRegion = null;

    @Parameter
    public String CFBundlePackageType = null;

    @Parameter
    public String JVMVersion = null;

    @Parameter
    public String JVMMainClassName = null;

    @Parameter
    public String JVMMainModuleName = null;

    @Parameter
    public List<String> JVMOptions = null;

    @Parameter
    public List<String> JVMArguments = null;

    @Parameter
    public String JVMRuntimePath = null;

    @Parameter
    public String JVMLogLevel = null;

    @Parameter
    public Boolean NSHighResolutionCapable = Boolean.TRUE;

    @Parameter
    public Boolean LSUIElement = null;

    @Parameter
    public Boolean NSSupportsAutomaticGraphicsSwitching = Boolean.TRUE;
    
    @Parameter
    public String NSMicrophoneUsageDescription = null;
    
    @Parameter
    public String NSCameraUsageDescription = null;

  public String toXmlString(Map<String, String> additionalValues) throws Exception {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter writer = new StringWriter();
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n");
        transformer.transform(new DOMSource(this.toXmlDocument(additionalValues)), new StreamResult(writer));
        return writer.toString();

    }

    private Document toXmlDocument(Map<String, String> additionalValues) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        document.appendChild(document.getImplementation().createDocumentType("plist", "-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd"));
        Element dictElement = document.createElement("dict");
        this.appendKeyWithString(dictElement, document, "CFBundleDisplayName", this.CFBundleDisplayName);
        this.appendKeyWithString(dictElement, document, "CFBundleExecutable", this.CFBundleExecutable);
        this.appendKeyWithString(dictElement, document, "CFBundleIdentifier", this.CFBundleIdentifier);
        this.appendKeyWithString(dictElement, document, "CFBundleName", this.CFBundleName);
        this.appendKeyWithString(dictElement, document, "CFBundleShortVersionString", this.CFBundleShortVersionString);
        this.appendKeyWithString(dictElement, document, "CFBundleDevelopmentRegion", this.CFBundleDevelopmentRegion);
        this.appendKeyWithString(dictElement, document, "CFBundlePackageType", this.CFBundlePackageType);
        this.appendCFBundleURLTypes(dictElement, document, this.CFBundleURLTypes);
        this.appendKeyWithArrayOfStrings(dictElement, document, "JVMArguments", this.JVMArguments);
        this.appendKeyWithString(dictElement, document, "JVMMainClassName", this.JVMMainClassName);
        this.appendKeyWithString(dictElement, document, "JVMMainModuleName", this.JVMMainModuleName);
        this.appendKeyWithArrayOfStrings(dictElement, document, "JVMOptions", this.JVMOptions);
        this.appendKeyWithString(dictElement, document, "JVMRuntimePath", this.JVMRuntimePath);
        this.appendKeyWithString(dictElement, document, "JVMVersion", this.JVMVersion);
        this.appendKeyWithString(dictElement, document, "JVMLogLevel", this.JVMLogLevel);
        this.appendKeyWithBoolean(dictElement, document, "NSHighResolutionCapable", this.NSHighResolutionCapable);
        this.appendKeyWithBoolean(dictElement, document, "LSUIElement", this.LSUIElement);
        this.appendKeyWithBoolean(dictElement, document, "NSSupportsAutomaticGraphicsSwitching", this.NSSupportsAutomaticGraphicsSwitching);
        this.appendKeyWithString(dictElement, document, "NSMicrophoneUsageDescription", this.NSMicrophoneUsageDescription);
        this.appendKeyWithString(dictElement, document, "NSCameraUsageDescription", this.NSCameraUsageDescription);
        for (Map.Entry<String, String> additionalValue : additionalValues.entrySet()) {
            this.appendKeyWithString(dictElement, document, additionalValue.getKey(), additionalValue.getValue());
        }
        Element plistElement = document.createElement("plist");
        plistElement.setAttribute("version", "1.0");
        plistElement.appendChild(dictElement);
        document.appendChild(plistElement);
        return document;
    }

    private void appendKeyWithString(Element dictElement, Document document, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            Element keyElement = document.createElement("key");
            keyElement.setTextContent(key);
            dictElement.appendChild(keyElement);
            Element stringElement = document.createElement("string");
            stringElement.setTextContent(value);
            dictElement.appendChild(stringElement);
        }
    }

    private void appendKeyWithArrayOfStrings(Element dictElement, Document document, String key, List<String> value) {
        if (value != null && !value.isEmpty()) {
            Element keyElement = document.createElement("key");
            keyElement.setTextContent(key);
            dictElement.appendChild(keyElement);
            Element arrayElement = document.createElement("array");
            for (String valueItem : value) {
                if (!StringUtils.isEmpty(valueItem)) {
                    Element stringElement = document.createElement("string");
                    stringElement.setTextContent(valueItem);
                    arrayElement.appendChild(stringElement);
                }
            }
            dictElement.appendChild(arrayElement);
        }
    }

    private void appendKeyWithBoolean(Element dictElement, Document document, String key, Boolean value) {
        if (value != null) {
            Element keyElement = document.createElement("key");
            keyElement.setTextContent(key);
            dictElement.appendChild(keyElement);
            dictElement.appendChild(document.createElement(value.toString()));
        }
    }

    private void appendCFBundleURLTypes(Element dictElement, Document document, List<String> value) {
        if (value != null && !value.isEmpty()) {
            Element keyElement = document.createElement("key");
            keyElement.setTextContent("CFBundleURLTypes");
            dictElement.appendChild(keyElement);
            Element arrayElement = document.createElement("array");
            Element arrayDictElement = document.createElement("dict");
            this.appendKeyWithArrayOfStrings(arrayDictElement, document, "CFBundleURLSchemes", value);
            arrayElement.appendChild(arrayDictElement);
            dictElement.appendChild(arrayElement);
        }
    }

}
