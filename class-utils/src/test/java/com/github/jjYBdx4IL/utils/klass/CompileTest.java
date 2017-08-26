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
package com.github.jjYBdx4IL.utils.klass;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jjYBdx4IL
 */
public class CompileTest extends Compile {
	
    private final static File tempDir = Maven.getTempTestDir(CompileTest.class);

    @Before
    public void before() throws IOException {
    	// run tests on Linux only
    	Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
    	FileUtils.cleanDirectory(tempDir);
        setClassOutputDir(tempDir);
    }

    @Test
    public void test1() {
        writeClass("pkg", "public class A", "public static int main(String[] args) {}");
        assertNotCompile();
        writeClass("pkg", "public class A", "public static void main(String[] args) {}");
        assertCompile();
        assertRun("pkg.A");
    }

}
