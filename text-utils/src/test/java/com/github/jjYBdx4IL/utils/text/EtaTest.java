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
package com.github.jjYBdx4IL.utils.text;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtaTest {

    private static final Logger LOG = LoggerFactory.getLogger(EtaTest.class);
    
    @Test
    public void test() throws InterruptedException {
        Eta<Integer> eta = new Eta<>(0, 10, 300);
        LOG.info(eta.toString(0));
        for(int i=0; i<9; i++) {
            Thread.sleep(200l);
            String etaStr = eta.toStringPeriodical(i+1);
            if (etaStr != null) {
                LOG.info(etaStr);
            }
        }
    }
}
