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
package com.github.jjYBdx4IL.utils.cfg;

//CHECKSTYLE:OFF
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author jjYBdx4IL
 */
public abstract class AbstractConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfig.class);
    protected static final String DEFAULT_STRING_VALUE = "replace or delete me";
    public static final Pattern APP_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.]+$");
    @XStreamOmitField
    private final File cfgDir;
    @XStreamOmitField
    private final boolean manualEditMode;

    /**
     * 
     *
     * @param appName identifies your application and implicitly defines the location of your config directory
     * on disk. Allowed characters: A-Z, a-z, 0-9
     * @param manualEditMode write default config to disk and throw an exception telling the user to edit the config
     * if no config is found when trying to read it. In this operational mode the postprocess method should also check
     * whether the user has actually edited the config file and bail if not.
     */
    public AbstractConfig(String appName, boolean manualEditMode) {
    	this.manualEditMode = manualEditMode;
        if (appName == null || !APP_NAME_PATTERN.matcher(appName).find()) {
            throw new IllegalArgumentException("appName param is null or invalid");
        }
        cfgDir = new File(System.getProperty("user.home"), ".config" + File.separatorChar + appName);
    }
    
    public AbstractConfig(Class<?> klazz, boolean manualEditMode) {
        this(klazz.getCanonicalName(), manualEditMode);
    }
    
    public AbstractConfig(String appName) {
    	this(appName, false);
    }

    public AbstractConfig(Class<?> klazz) {
    	this(klazz.getCanonicalName(), false);
    }

    public static String formatXml(String xml) {

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

    protected void postprocess() {
    }

    protected void readDidntFindConfigFile() {
    	if (manualEditMode) {
			try {
				write();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			File file = getConfigFile();
			throw new RuntimeException("writing new config file, please update " + file.getAbsolutePath());
    	}
    }
    
    public File getConfigFile() {
        String filename = getClass().getSimpleName().toLowerCase(Locale.ROOT);
        filename = filename.replaceFirst("config$", "");
        filename += ".xml";

        return new File(cfgDir, filename);
    }
    
    public boolean read() throws FileNotFoundException, IOException {
        File configFile = getConfigFile();
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[] {
            AbstractConfig.this.getClass().getPackage().getName() + ".**"
        });
        xstream.autodetectAnnotations(true);

        if (configFile.exists()) {
            xstream.fromXML(configFile, this);
            postprocess();
            return true;
        } else {
        	readDidntFindConfigFile();
        }

        return false;
    }

    public void write() throws FileNotFoundException, IOException {
        File configFile = getConfigFile();
        configFile.getParentFile().mkdirs();
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.allowTypesByWildcard(new String[] {
            AbstractConfig.this.getClass().getPackage().getName() + ".**"
        });
        xstream.autodetectAnnotations(true);
        String xml = xstream.toXML(this);
        try (OutputStream os = new FileOutputStream(configFile)) {
            IOUtils.write(formatXml(xml), os, "UTF-8");
        }
    }


}
