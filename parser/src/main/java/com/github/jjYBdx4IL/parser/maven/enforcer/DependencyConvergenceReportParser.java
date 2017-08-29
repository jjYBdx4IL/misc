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
package com.github.jjYBdx4IL.parser.maven.enforcer;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DependencyConvergenceReportParser {

    private static final Pattern dependencyPattern = Pattern.compile("^\\s*\\+-([^:]+):([^:]+):([^:]+)$",
        Pattern.CASE_INSENSITIVE);

    /**
     * The parse method.
     * 
     * @param input
     *            the error output produced by the maven enforcer:enforce goal
     *            when it finds dependency convergence errors
     * @return a list of maven dependencies that are causing the errors. This
     *         list contains all the leaves displayed in the error report. That
     *         means it also contains multiple versions of the same artifact.
     */
    public static Collection<MavenDependency> parse(String input) {
        // make sure the last line does not match:
        String[] lines = (input + "\nPAD").split("\r?\n");

        Set<MavenDependency> deps = new HashSet<>();
        MavenDependency lastMatchedDependency = null;
        for (String line : lines) {
            Matcher matcher = dependencyPattern.matcher(line);
            if (matcher.matches()) {
                lastMatchedDependency = new MavenDependency(matcher.group(1), matcher.group(2), matcher.group(3));
            } else if (lastMatchedDependency != null) {
                deps.add(lastMatchedDependency);
                lastMatchedDependency = null;
            }
        }

        return deps;
    }

    /**
     * Reduce the given set to a collection containg only the newest version of
     * each artifact.
     * 
     * @param deps
     *            a maven dependency set
     * @return the potentially reduced collection
     */
    public static Collection<MavenDependency> selectNewestOnly(Collection<MavenDependency> deps) {
        // remember highest version for earch artifact:
        Map<String, MavenDependency> versionMap = new HashMap<>();

        for (MavenDependency dep : deps) {
            String key = dep.getGroupId() + ":" + dep.getArtifactId();
            MavenDependency highestVersionDep = versionMap.get(key);
            if (highestVersionDep == null
                || VersionComparator.largerThan(dep.getVersion(), highestVersionDep.getVersion())) {
                versionMap.put(key, dep);
            }
        }

        return versionMap.values();
    }

    /**
     * Create Maven pom.xml fragment from the given Maven dependency collection.
     * 
     * @param deps
     *            the list of maven dependencies from which to construct the xml
     *            fragment
     * @return the xml fragment
     * @throws XMLStreamException
     *             on error
     * @throws UnsupportedEncodingException
     *             on error
     */
    public static String toMavenPomXmlFragment(Collection<MavenDependency> deps)
        throws XMLStreamException, UnsupportedEncodingException {
        
        List<MavenDependency> list = new ArrayList<>(deps);
        Collections.sort(list);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xml = outputFactory.createXMLStreamWriter(baos, "UTF-8");

        for (MavenDependency dep : list) {
            xml.writeStartElement("dependency");
            xml.writeCharacters(System.lineSeparator() + "    ");
            xml.writeStartElement("groupId");
            xml.writeCharacters(dep.getGroupId());
            xml.writeEndElement();
            xml.writeCharacters(System.lineSeparator() + "    ");
            xml.writeStartElement("artifactId");
            xml.writeCharacters(dep.getArtifactId());
            xml.writeEndElement();
            xml.writeCharacters(System.lineSeparator() + "    ");
            xml.writeStartElement("version");
            xml.writeCharacters(dep.getVersion());
            xml.writeEndElement();
            xml.writeCharacters(System.lineSeparator());
            xml.writeEndElement();
            xml.writeCharacters(System.lineSeparator());
        }

        xml.flush();
        
        return baos.toString("UTF-8");
    }
}
