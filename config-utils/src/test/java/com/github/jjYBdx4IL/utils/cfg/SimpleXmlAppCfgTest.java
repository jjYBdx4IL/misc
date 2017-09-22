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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 *
 */
public class SimpleXmlAppCfgTest {

    @Test
    public void testGetConfigFileName() {
        System.out.println(SimpleXmlAppCfg.getConfigFileName("appname"));
        String fileSeparator = File.separator;
        if (fileSeparator.equals("\\")) {
        	fileSeparator = "\\\\";
        }
        assertTrue(nativeMatch(
        		".+/.config/appname/properties.xml$".replace("/", fileSeparator),
        		SimpleXmlAppCfg.getConfigFileName("appname")
        		));
    }

    @Test
    public void testLoadConfiguration() {
        try {
            SimpleXmlAppCfg.loadConfiguration("not-existing-app-id-8943785634");
            assertTrue(SimpleXmlAppCfg.getConfiguration().isEmpty());
        } catch (IOException ex) {
            fail();
        }
    }

    @Ignore
    public boolean nativeMatch(String regex, String test) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(test);
        return m.find();
    }
}
