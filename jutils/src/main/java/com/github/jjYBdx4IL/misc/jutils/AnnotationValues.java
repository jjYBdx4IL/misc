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

/**
 * Command config values container.
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
//CHECKSTYLE:OFF
public class AnnotationValues {

    private final String name;
    private final String help;
    private final String usage;
    private final int minArgs;
    private final int maxArgs;

    /**
     * 
     * 
     * @param name the name
     * @param help the help
     * @param usage the usage
     * @param minArgs min (unparsed/positional) args
     * @param maxArgs max (unparsed/positional) args
     */
    public AnnotationValues(String name, String help, String usage, int minArgs, int maxArgs) {
        this.name = name;
        this.help = help;
        this.usage = usage;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    /**
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return the help
     */
    public String getHelp() {
        return help;
    }

    /**
     * 
     * @return the minArgs
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * 
     * @return the maxArgs
     */
    public int getMaxArgs() {
        return maxArgs;
    }

    /**
     * 
     * @return the usage
     */
    public String getUsage() {
        return usage;
    }

}
