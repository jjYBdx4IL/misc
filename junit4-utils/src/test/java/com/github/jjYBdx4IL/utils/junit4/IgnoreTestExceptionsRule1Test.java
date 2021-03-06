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
package com.github.jjYBdx4IL.utils.junit4;

import java.net.SocketTimeoutException;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class IgnoreTestExceptionsRule1Test {

    @Rule
    public final IgnoreTestExceptionsRule ignoreSocketTimeoutException;
    
    public IgnoreTestExceptionsRule1Test() {
        ignoreSocketTimeoutException = new IgnoreTestExceptionsRule();
        ignoreSocketTimeoutException.addException(SocketTimeoutException.class);
    }

    @Test
    public void test1() {
        assertTrue(true);
    }

}
