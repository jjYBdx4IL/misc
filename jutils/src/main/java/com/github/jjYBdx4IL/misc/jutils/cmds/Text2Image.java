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
import com.github.jjYBdx4IL.utils.gfx.Text2ImageAppMain;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
@JUtilsCommandAnnotation(
        name = "2img",
        help = "convert text/html to an image",
        usage = "-html -i <input-file> -o out.png",
        minArgs = 0,
        maxArgs = 0
)
public class Text2Image implements JUtilsCommandInterface {

    @Override
    public int run(CommandLine line, String[] args) {
        Text2ImageAppMain.main(args);
        return 0;
    }

    @Override
    public Options getCommandLineOptions() {
        return Text2ImageAppMain.getOptions();
    }

}
