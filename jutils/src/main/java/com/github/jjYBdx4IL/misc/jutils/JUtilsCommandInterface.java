/*
 * #%L
 * Java Command Line Utilities
 * %%
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 * %%
 * #L%
 */
package com.github.jjYBdx4IL.misc.jutils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
public interface JUtilsCommandInterface {

    public static final String OPTNAME_DEVTESTS = "devtests";

    int run(CommandLine line) throws Exception;

    Options getCommandLineOptions();
}
