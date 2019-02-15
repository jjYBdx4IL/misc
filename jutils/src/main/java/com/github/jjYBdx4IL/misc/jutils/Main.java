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
package com.github.jjYBdx4IL.misc.jutils;

import com.github.jjYBdx4IL.utils.cli.ExtendedGnuParser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    public static final String PROGNAME = "jutils";
    public static final String OPTNAME_HELP = "h";

    public static void main(String[] args) {
        try {
            List<String> _args = new ArrayList<>();
            _args.addAll(Arrays.asList(args));
            int exitCode = new Main().run(_args);
            System.exit(exitCode);
        } catch (Exception ex) {
            LOG.error("", ex);
            System.exit(3);
        }
    }

    public int run(List<String> args) throws Exception {
        JUtilsCommandInterface jCmd = null;
        String selectedCmdName = extractFirstNonDashedArg(args);

        Map<String, JUtilsCommandInterface> cmds = GeneratedCommandCollector.getAllCommandInstances();
        Map<String, AnnotationValues> cmdAnnoValues = GeneratedCommandCollector.getAllCommandAnnotationValues();

        CommandLineParser parser = new ExtendedGnuParser(true);
        Options options = new Options();
        options.addOption(OPTNAME_HELP, "help", false, "show help");
        CommandLine line = parser.parse(options, args.toArray(new String[args.size()]), false);

        // help display logic:
        // 1.) exit after showing help
        // 2.) show complete help if no valid command could be detected
        // 3.) show specific help if we have a valid command and help option has
        // been used
        // 4.) show specific help if we have a valid command and command line
        // parsing for that command failed
        AnnotationValues selectedCmdAnnoValues = null;

        if (selectedCmdName != null) {
            if (!cmds.containsKey(selectedCmdName)) {
                LOG.error("unknown command " + selectedCmdName);
                selectedCmdName = null;
            } else {
                jCmd = cmds.get(selectedCmdName);
                selectedCmdAnnoValues = cmdAnnoValues.get(selectedCmdName);
            }
        }

        boolean cmdOptionParseFailed = false;

        if (jCmd != null) {
            Options cmdOptions = jCmd.getCommandLineOptions();
            if (cmdOptions != null) {
                for (Object opt : cmdOptions.getOptions().toArray()) {
                    if (!(opt instanceof Option)) {
                        throw new RuntimeException();
                    }
                    Option _opt = (Option) opt;
                    if (OPTNAME_HELP.equals(_opt.getArgName())) {
                        throw new RuntimeException();
                    }
                    options.addOption(_opt);
                }
            }
            line = parser.parse(options, args.toArray(new String[args.size()]), false);

            int numUnnamedArgs = line.getArgs().length;
            if (numUnnamedArgs < selectedCmdAnnoValues.getMinArgs()
                || numUnnamedArgs > selectedCmdAnnoValues.getMaxArgs()) {
                LOG.error("invalid number of arguments");
                cmdOptionParseFailed = true;
            }
        }

        if (line.hasOption(OPTNAME_HELP) || jCmd == null || cmdOptionParseFailed) {

            PrintWriter pw = new PrintWriter(System.err);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(pw, 78, PROGNAME, null, options, 2, 4, null, false);

            List<String> cmdNames = new ArrayList<>(cmds.keySet());
            Collections.sort(cmdNames);

            for (String cmdName : cmdNames) {
                if (selectedCmdName != null && !selectedCmdName.equals(cmdName)) {
                    continue;
                }
                AnnotationValues annoValues = cmdAnnoValues.get(cmdName);
                JUtilsCommandInterface _jCmd = cmds.get(cmdName);
                Options jCmdOptions = _jCmd.getCommandLineOptions();
                if (jCmdOptions == null) {
                    jCmdOptions = new Options();
                }
                String jCmdUsage = annoValues.getUsage();
                String cmdLineSyntax = String.format("%s %s -- %s", PROGNAME, cmdName, annoValues.getHelp());
                if (!jCmdUsage.isEmpty()) {
                    cmdLineSyntax = String.format("%s %s %s -- %s", PROGNAME, cmdName, jCmdUsage, annoValues.getHelp());
                }
                formatter.printHelp(pw, 78, cmdLineSyntax, null, jCmdOptions, 2, 4, null, false);
            }
            pw.flush();
            return 1;
        }

        return jCmd.run(line, args.toArray(new String[args.size()]));
    }

    private String extractFirstNonDashedArg(List<String> args) {
        for (String s : args) {
            if (!s.startsWith("-")) {
                args.remove(s);
                return s;
            }
        }
        return null;
    }

}
