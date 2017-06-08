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
public @interface JUtilsCommandAnnotation {

    String name();
    String help();
    String usage();
    /**
     * minimum number of unnamed arguments
     */
    int minArgs();
    /**
     * maximum number of unnamed arguments
     */
    int maxArgs();
}
