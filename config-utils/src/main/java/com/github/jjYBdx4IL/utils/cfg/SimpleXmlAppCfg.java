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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class SimpleXmlAppCfg {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleXmlAppCfg.class);
    
    @SuppressWarnings("unused")
    private static String appId;
    private static Properties props;

    public static synchronized void loadConfiguration(String appId) throws IOException {
        if (props != null) {
            return;
        }

        String appCfgPath = getConfigFileName(appId);
        if (!new File(appCfgPath).exists()) {
            LOG.info("app configuration file not found at: " + appCfgPath);
            SimpleXmlAppCfg.props = new Properties();
            SimpleXmlAppCfg.appId = appId;
            return;
        }
        LOG.info("reading app configuration file at: " + appCfgPath);

        Properties props2 = new Properties();
        try (FileInputStream fis = new FileInputStream(appCfgPath)) {
            props2.loadFromXML(fis);
        } catch (IOException ex) {
            throw ex;
        }
        SimpleXmlAppCfg.props = props2;
        SimpleXmlAppCfg.appId = appId;
    }

    public static Properties getConfiguration() {
        if (props == null) {
            throw new IllegalStateException("app configuration not loaded");
        }
        return props;
    }

    public static String getConfigFileName(String appId) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("user.home"));
        sb.append(File.separator);
        sb.append(".config");
        sb.append(File.separator);
        sb.append(appId);
        sb.append(File.separator);
        sb.append("properties.xml");
        return sb.toString();
    }
}
