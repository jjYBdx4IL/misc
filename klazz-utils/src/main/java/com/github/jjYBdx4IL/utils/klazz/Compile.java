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
package com.github.jjYBdx4IL.utils.klazz;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public abstract class Compile {

    private static final Logger log = LoggerFactory.getLogger(Compile.class);
    private static final Pattern typeNamePattern = Pattern.compile("\\b(interface|class|enum)\\s+(\\S+)\\b");
    private File classOutputDir = null;
    private File sourceOutputDir = null;
    private final String EOL = "\n";
    private String compilerOutput = null;
    private String jvmOutput = null;
    private final String compiler = "javac";
    private final String jvm = "java";
    private final List<File> javaFilesToCompile = new ArrayList<>();
    private final List<File> lastJavaFilesToCompile = new ArrayList<>();

    protected void writeClass(String classDef, String content) {
        writeClass(null, classDef, content);
    }

    protected void writeClass(String packageName, String typeDef, String content) {
        writeClass(packageName, null, typeDef, content);
    }

    /**
     * 
     * @param packageName
     *            the package name
     * @param imports
     *            the imports
     * @param typeDef
     *            the type definition
     * @param content
     *            the content
     */
    protected void writeClass(String packageName, String[] imports, String typeDef, String content) {
        try {
            Matcher m = typeNamePattern.matcher(typeDef);
            m.find();
            String shortClassName = m.group(2);
            String className = ((packageName != null && !packageName.isEmpty()) ? packageName + '.' : "")
                + shortClassName;
            File javaFileToCompile = new File(className.replace(".", File.separator) + ".java");
            File classFile = new File(sourceOutputDir, javaFileToCompile.getPath());
            StringBuilder sb = new StringBuilder();
            if (packageName != null && !packageName.isEmpty()) {
                sb.append("package ").append(packageName).append(";").append(EOL);
            }
            if (imports != null) {
                for (String imp : imports) {
                    sb.append("import ").append(imp).append(";").append(EOL);
                }
            }
            sb.append(typeDef).append(" {").append(EOL);
            sb.append(content).append(EOL);
            sb.append("}").append(EOL);
            File parentDir = classFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (OutputStream os = new FileOutputStream(classFile)) {
                IOUtils.write(sb.toString(), os);
            }
            if (log.isDebugEnabled()) {
                log.debug("source code for " + javaFileToCompile.getPath() + " :");
                log.debug(sb.toString());
            }
            javaFilesToCompile.add(javaFileToCompile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void assertRun(String mainClass) {
        try {
            List<String> args = new ArrayList<>();
            args.add(jvm);
            args.add("-cp");
            args.add(".:" + System.getProperty("java.class.path"));
            args.add(mainClass);
            log.debug("command: " + StringUtils.join(args.toArray(new String[] {}), " "));
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            pb.directory(getClassOutputDir());
            Process p = pb.start();
            jvmOutput = IOUtils.toString(p.getInputStream());
            int exitCode = p.waitFor();
            log.debug(jvm + " exit code: " + exitCode);
            log.debug(jvm + " console output: " + jvmOutput);
            if (exitCode != 0) {
                throw new RuntimeException("bad exit code: " + jvmOutput);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    protected void assertCompile() {
        int compilerExitCode = compile(true);
        if (compilerExitCode != 0) {
            throw new RuntimeException("bad compiler exit code " + compilerExitCode + ": " + compilerOutput);
        }
    }

    protected void assertNotCompile() {
        int compilerExitCode = compile(true);
        if (compilerExitCode == 0) {
            throw new RuntimeException("bad compiler exit code " + compilerExitCode + ": " + compilerOutput);
        }
    }

    protected void compile() {
        compile(false);
    }

    protected int compile(boolean ignoreCompilationError) {
        try {
            List<String> args = new ArrayList<>();
            args.add(compiler);
            args.add("-cp");
            args.add("." + File.pathSeparator + System.getProperty("java.class.path"));
            args.add("-d");
            args.add(classOutputDir.getCanonicalPath());
            if (javaFilesToCompile.isEmpty()) {
                log.debug("no new sources written, recompiling the last ones");
                javaFilesToCompile.addAll(lastJavaFilesToCompile);
            }
            for (File f : javaFilesToCompile) {
                args.add(f.getPath());
            }
            log.debug("command: " + StringUtils.join(args.toArray(new String[] {}), " "));
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true);
            pb.directory(sourceOutputDir);
            Process p = pb.start();
            compilerOutput = IOUtils.toString(p.getInputStream());
            int exitCode = p.waitFor();
            lastJavaFilesToCompile.clear();
            lastJavaFilesToCompile.addAll(javaFilesToCompile);
            javaFilesToCompile.clear();
            if (ignoreCompilationError || exitCode == 0) {
                log.debug(compiler + " exit code: " + exitCode);
                log.debug(compiler + " console output: " + compilerOutput);
            } else {
                log.error(compiler + " execution failed, command was: " + Arrays.toString(args.toArray()));
                log.error(compiler + " exit code: " + exitCode);
                log.error(compiler + " console output: " + compilerOutput);
                throw new IllegalStateException(compiler + " returned with exit code " + exitCode);
            }
            return exitCode;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    /**
     * @return the classOutputDir
     */
    public File getClassOutputDir() {
        return classOutputDir;
    }

    /**
     * @param classOutputDir
     *            the classOutputDir to set
     */
    public void setClassOutputDir(File classOutputDir) {
        this.classOutputDir = classOutputDir;
        this.sourceOutputDir = new File(classOutputDir, "src.tmp");
    }

    /**
     * @return the compilerOutput
     */
    public String getCompilerOutput() {
        return compilerOutput;
    }
}
