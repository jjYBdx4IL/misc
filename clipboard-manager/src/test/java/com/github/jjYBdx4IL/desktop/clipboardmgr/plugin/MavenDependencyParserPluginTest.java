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
package com.github.jjYBdx4IL.desktop.clipboardmgr.plugin;

import static com.github.jjYBdx4IL.desktop.clipboardmgr.plugin.MavenDependencyParserPlugin.PAT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class MavenDependencyParserPluginTest {
    
    @Test
    public void testMavenDependencyPattern() {
        assertTrue(PAT.matcher("a:b:1").find());
        assertTrue(PAT.matcher("a.b.c:b-a:jar").find());
        assertFalse(PAT.matcher("a:1b:1").find());
        assertFalse(PAT.matcher("a:\nb:1").find());
        assertFalse(PAT.matcher("a:h b:1").find());
        assertFalse(PAT.matcher("a:hb:1 2").find());
        assertTrue(PAT.matcher("org.eclipse.jetty.websocket:websocket-server:jar").find());
    }
    
}
