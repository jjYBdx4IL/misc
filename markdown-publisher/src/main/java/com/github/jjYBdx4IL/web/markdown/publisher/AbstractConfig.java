/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.web.markdown.publisher;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * Config.
 * 
 * @author jjYBdx4IL
 */
abstract class AbstractConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfig.class);
    public static final String DEFAULT_STRING_VALUE = "replace or delete me";
    public static final File CFG_DIR = new File(System.getProperty("user.home"),
        ".config" + File.separatorChar + "MarkDownPublisher");

    void postprocess() {
    }

    private File getConfigFile() {
        String filename = getClass().getSimpleName().toLowerCase(Locale.ROOT);
        filename = filename.replaceFirst("config$", "");
        filename += ".xml";

        return new File(CFG_DIR, filename);
    }

    boolean read() throws FileNotFoundException, IOException {
        File configFile = getConfigFile();
        XStream xstream = getXStream();

        if (configFile.exists()) {
            xstream.fromXML(configFile, this);
            postprocess();
            return true;
        }

        return false;
    }

    void write() throws FileNotFoundException, IOException {
        File configFile = getConfigFile();
        configFile.getParentFile().mkdirs();
        XStream xstream = getXStream();
        String xml = xstream.toXML(this);
        try (OutputStream os = new FileOutputStream(configFile)) {
            IOUtils.write(formatXml(xml), os, "UTF-8");
        }
    }

    static String formatXml(String xml) {

        try {
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());

            serializer.transform(xmlSource, res);

            return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());

        } catch (IllegalArgumentException | TransformerException e) {
            LOG.error("", e);
            return xml;
        }
    }

    static XStream getXStream() {
        XStream xstream = new XStream(new StaxDriver());
        xstream.autodetectAnnotations(true);
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[] {
            AbstractConfig.class.getPackage().getName() + ".**"
        });
        return xstream;
    }

}
