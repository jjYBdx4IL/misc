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

import com.github.jjYBdx4IL.utils.ci.jenkins.DependencyGraph;

import java.net.URL;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
@JUtilsCommandAnnotation(
        name = "jra",
        help = "jenkins, rebuild all jobs",
        usage = "",
        minArgs = 0,
        maxArgs = 0
)
public class JenkinsRebuildAllJobs implements JUtilsCommandInterface {

    private static final Logger log = LoggerFactory.getLogger(JenkinsRebuildAllJobs.class);
    private static final String OPTNAME_URL = "u";
    private static final String OPTNAME_PREPEND = "p";
    private static final String OPTNAME_DRYRUN = "d";
    private String[] optPrepend = null;

    @Override
    public int run(CommandLine line) throws Exception {
        if (!line.hasOption(OPTNAME_URL)) {
            throw new ParseException("need URL");
        }
        final String jenkinsUrl = line.getOptionValue(OPTNAME_URL);
        optPrepend = line.getOptionValues(OPTNAME_PREPEND);

        DependencyGraph dg = new DependencyGraph(new URL(jenkinsUrl));
        List<String> queuedJobs = dg.queue(optPrepend, null, line.hasOption(OPTNAME_DRYRUN));
        System.err.println(String.format("%d jobs queued:", queuedJobs.size()));
        for (String s : queuedJobs) {
            System.out.println(s);
        }

        return 0;
    }

    @Override
    public Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(OPTNAME_URL, "url", true, "jenkins url, will append " + DependencyGraph.URL_SUFFIX);
        options.addOption(OPTNAME_PREPEND, "prepend", true, "prepend these jobs to the build queue");
        options.addOption(OPTNAME_DRYRUN, "dry-run", false, "don't submit jobs, just log what would have been done");
        return options;
    }

}
