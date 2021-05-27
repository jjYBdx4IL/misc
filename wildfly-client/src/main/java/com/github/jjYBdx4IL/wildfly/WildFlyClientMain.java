/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.wildfly;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.File;

public class WildFlyClientMain {

    protected static final CommandLineParser parser = new DefaultParser();

    private final File basedir;
    private final WildFlyClient client;

    private WildFlyClientMain() throws Exception {
        String bd = System.getProperty("basedir", null);
        checkNotNull(bd);
        basedir = new File(bd);
        checkArgument(basedir.exists() && basedir.isDirectory());
        client = new WildFlyClient("admin", "admin", 9991);
    }

    protected void run(CommandLine cmd) throws Exception {
        for (String dsName : cmd.getOptionValues("h2")) {
            StringBuilder sb = new StringBuilder();
            sb.append("/subsystem=datasources/data-source=");
            sb.append(dsName);
            sb.append(":add");
            sb.append("(jndi-name=java:jboss/datasources/");
            sb.append(dsName);
            sb.append(",driver-name=h2,connection-url=\"jdbc:h2:");
            sb.append(basedir.toURI().toASCIIString());
            sb.append("/db;TRACE_LEVEL_SYSTEM_OUT=0\",user-name=sa,validate-on-match=true,");
            sb.append("background-validation=false,driver-class=org.h2.Driver)");
            client.exec(sb.toString());
        }
    }

    /**
     * The main entry point.
     * 
     * <p>Used with exec-maven-plugin to configure WildFly test databases.
     * Sadly, there seems to be no other way to specify an absolute path.
     */
    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.addOption(null, "h2", true, "add h2 datasource if not exists (file loc: $basedir/db.mv.db)");
            CommandLine cmd = parser.parse(options, args);
            new WildFlyClientMain().run(cmd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
