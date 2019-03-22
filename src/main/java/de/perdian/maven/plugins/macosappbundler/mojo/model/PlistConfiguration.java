/*
 * The MIT License
 *
 * Copyright (c) 2019 Christian Robert
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
        this.appendKeyWithArrayOfStrings(dictElement, document, "JVMArguments", this.JVMArguments);
        this.appendKeyWithString(dictElement, document, "JVMMainClassName", this.JVMMainClassName);
        this.appendKeyWithString(dictElement, document, "JVMMainModuleName", this.JVMMainModuleName);
        this.appendKeyWithArrayOfStrings(dictElement, document, "JVMOptions", this.JVMOptions);
        this.appendKeyWithString(dictElement, document, "JVMRuntimePath", this.JVMRuntimePath);
        this.appendKeyWithString(dictElement, document, "JVMVersion", this.JVMVersion);
        this.appendKeyWithString(dictElement, document, "JVMLogLevel", this.JVMLogLevel);
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

}
