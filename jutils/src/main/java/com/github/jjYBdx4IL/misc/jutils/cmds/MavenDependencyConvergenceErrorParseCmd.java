/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.misc.jutils.cmds;

import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation;
import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandInterface;
import com.github.jjYBdx4IL.parser.maven.enforcer.DependencyConvergenceReportParser;
import com.github.jjYBdx4IL.parser.maven.enforcer.MavenDependency;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
//@formatter:off
@JUtilsCommandAnnotation(
        name = "mep",
        help = "produce pom.xml dependency management fragment from enforcer plugin convergence error output stored in clipboard",
        usage = "",
        minArgs = 0,
        maxArgs = 0
)
//@formatter:on
public class MavenDependencyConvergenceErrorParseCmd implements JUtilsCommandInterface {

    @Override
    public int run(CommandLine line, String[] args) throws UnsupportedFlavorException, IOException, XMLStreamException {
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        String dependencyConvergenceErrorOutput = (String) clpbrd.getData(DataFlavor.stringFlavor);

        Collection<MavenDependency> deps = DependencyConvergenceReportParser.parse(dependencyConvergenceErrorOutput);
        Collection<MavenDependency> newest = DependencyConvergenceReportParser.selectNewestOnly(deps);
        String xmlFragment = DependencyConvergenceReportParser.toMavenPomXmlFragment(newest);
        System.out.println(xmlFragment);

        StringSelection stringSelection = new StringSelection(xmlFragment);
        clpbrd.setContents(stringSelection, null);

        return 0;
    }

    @Override
    public Options getCommandLineOptions() {
        Options options = new Options();
        return options;
    }

}
