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
package com.github.jjYBdx4IL.desktop.clipboardmgr.plugin;

import com.github.jjYBdx4IL.desktop.clipboardmgr.ClipBoardPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transforms maven dependency information dumped by the maven enforcer plugin into
 * maven dependency xml text suitable for use in pom.xml files.
 */
public class MavenDependencyParserPlugin implements ClipBoardPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(MavenDependencyParserPlugin.class);
    
    public static final Pattern PAT = Pattern.compile("^((?=[a-z]\\S*:)[^:]+):((?=[a-z]\\S*:)[^:]+):((?=\\S*$)[^:]+)$",
        Pattern.CASE_INSENSITIVE);
    
    @Override
    public String onNewText(String newTextContent) {
        Matcher matcher = PAT.matcher(newTextContent);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String groupId = matcher.group(1);
            String artifactId = matcher.group(2);
            String version = matcher.group(3);
            sb.append("<dependency>\n");
            sb.append("\t<groupId>" + groupId + "</groupId>\n");
            sb.append("\t<artifactId>" + artifactId + "</artifactId>\n");
            sb.append("\t<version>" + version + "</version>\n");
            sb.append("</dependency>\n");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(" -> " + sb.toString());
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

}
