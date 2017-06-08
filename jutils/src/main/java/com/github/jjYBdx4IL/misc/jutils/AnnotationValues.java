/*
 * #%L
 * Java Command Line Utilities
 * %%
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 * %%
 * #L%
 */
package com.github.jjYBdx4IL.misc.jutils;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
public class AnnotationValues {

    private final String name;
    private final String help;
    private final String usage;
    private final int minArgs;
    private final int maxArgs;

    public AnnotationValues(String name, String help, String usage, int minArgs, int maxArgs) {
        this.name = name;
        this.help = help;
        this.usage = usage;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the help
     */
    public String getHelp() {
        return help;
    }

    /**
     * @return the minArgs
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * @return the maxArgs
     */
    public int getMaxArgs() {
        return maxArgs;
    }

    /**
     * @return the usage
     */
    public String getUsage() {
        return usage;
    }

}
