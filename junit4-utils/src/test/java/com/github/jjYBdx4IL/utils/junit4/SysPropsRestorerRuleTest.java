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

import java.util.Random;

import com.github.jjYBdx4IL.utils.junit4.SysPropsRestorerRule;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 *
 * @author jjYBdx4IL
 */
public class SysPropsRestorerRuleTest {


    @ClassRule
    public static TestRule test = new SysPropsRestorerRule(3);

    @Test
    public void test1() {
        Random r = new Random();
        Object[] keys = (Object[]) System.getProperties().keySet().toArray();
        System.setProperty((String) keys[0], Float.toString(r.nextFloat()));
        System.getProperties().remove(keys[1]);
        System.setProperty(Float.toString(r.nextFloat()), Float.toString(r.nextFloat()));
    }

    @Test
    public void test2() {
    }

}
