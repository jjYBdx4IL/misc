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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

//CHECKSTYLE:OFF
public class DependencyConvergenceReportParserTest {

    @Test
    public void testParse() throws IOException {
        String dependencyConvergenceErrorOutput = IOUtils
            .toString(getClass().getResourceAsStream("DependencyConvergenceReport.txt"), "UTF-8");

        Collection<MavenDependency> deps = DependencyConvergenceReportParser.parse(dependencyConvergenceErrorOutput);

        assertEquals(4, deps.size());

        //@formatter:off
	    //		+-com.github.jjYBdx4IL.evaluation:java-evaluation:1.0-SNAPSHOT
        //		  +-com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.8.8
        //		and
        //		+-com.github.jjYBdx4IL.evaluation:java-evaluation:1.0-SNAPSHOT
        //		  +-org.glassfish.jersey.media:jersey-media-json-jackson:2.25.1
        //		    +-com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.8.4
        //		      +-com.fasterxml.jackson.module:jackson-module-jaxb-annotations:2.8.4
        //@formatter:on

        assertTrue(deps
            .contains(new MavenDependency("com.fasterxml.jackson.module", "jackson-module-jaxb-annotations", "2.8.4")));
        assertTrue(deps
            .contains(new MavenDependency("com.fasterxml.jackson.module", "jackson-module-jaxb-annotations", "2.8.8")));
        assertFalse(
            deps.contains(new MavenDependency("org.glassfish.jersey.media", "jersey-media-json-jackson", "2.25.1")));
    }

    @Test
    public void testSelectNewestOnly() throws IOException {
        String dependencyConvergenceErrorOutput = IOUtils
            .toString(getClass().getResourceAsStream("DependencyConvergenceReport.txt"), "UTF-8");

        Collection<MavenDependency> deps = DependencyConvergenceReportParser.parse(dependencyConvergenceErrorOutput);
        Collection<MavenDependency> newest = DependencyConvergenceReportParser.selectNewestOnly(deps);

        assertEquals(2, newest.size());

        assertTrue(deps
            .contains(new MavenDependency("com.fasterxml.jackson.module", "jackson-module-jaxb-annotations", "2.8.8")));
        assertTrue(deps
            .contains(new MavenDependency("com.fasterxml.jackson.core", "jackson-databind", "2.8.8")));
    }

    @Test
    public void testToMavenPomXmlFragment() throws Exception {
        String dependencyConvergenceErrorOutput = IOUtils
            .toString(getClass().getResourceAsStream("DependencyConvergenceReport.txt"), "UTF-8");

        Collection<MavenDependency> deps = DependencyConvergenceReportParser.parse(dependencyConvergenceErrorOutput);
        Collection<MavenDependency> newest = DependencyConvergenceReportParser.selectNewestOnly(deps);
        String xmlFragment = DependencyConvergenceReportParser.toMavenPomXmlFragment(newest);

        assertEquals("" +
            "<dependency>\n" +
            "    <groupId>com.fasterxml.jackson.core</groupId>\n" +
            "    <artifactId>jackson-databind</artifactId>\n" +
            "    <version>2.8.8</version>\n" +
            "</dependency>\n" +
            "<dependency>\n" +
            "    <groupId>com.fasterxml.jackson.module</groupId>\n" +
            "    <artifactId>jackson-module-jaxb-annotations</artifactId>\n" +
            "    <version>2.8.8</version>\n" +
            "</dependency>\n", xmlFragment);
    }
}
