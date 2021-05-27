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
package com.github.jjYBdx4IL.utils.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.crypto.AEADBadTagException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class CryptUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(CryptUtilsTest.class);

    @Test
    public void testPositive() throws Exception {

        CryptUtils cu = new CryptUtils();
        
        long encStartTime = System.currentTimeMillis();

        String result1a = cu.encrypt("msg", "pwd");
        String result1b = cu.encrypt("msg", "pwd");
        String result2 = cu.encrypt("msg", "pwd2");

        long encEndTime = System.currentTimeMillis();

        LOG.info(result1a);
        LOG.info(result1b);
        LOG.info(result2);
        
        assertNotEquals(result1a, result1b);
        assertNotEquals(result1a, result2);
        
        long decStartTime = System.currentTimeMillis();

        assertEquals("msg", cu.decrypt(result1a, "pwd"));
        assertEquals("msg", cu.decrypt(result1b, "pwd"));
        assertEquals("msg", cu.decrypt(result2, "pwd2"));

        long decEndTime = System.currentTimeMillis();

        LOG.info(String.format("encryption speed: %.2f msgs/second, decryption speed: %.2f msgs/second",
                3000.0 / (encEndTime - encStartTime), 3000.0 / (decEndTime - decStartTime)));
    }
    
    @Test(expected = AEADBadTagException.class)
    public void testWrongPwd() throws Exception {

        CryptUtils cu = new CryptUtils();
        String result1a = cu.encrypt("msg", "pwd");
        cu.decrypt(result1a, "wrongpwd");
    }
    
    @Test(expected = AEADBadTagException.class)
    public void testWrongParams() throws Exception {

        CryptUtils cu = new CryptUtils();
        String result1a = cu.encrypt("msg", "pwd");
        cu.setGcmTagLength(12);
        cu.decrypt(result1a, "pwd");
    }
    
    @Test(expected = AEADBadTagException.class)
    public void testWrongParams2() throws Exception {

        CryptUtils cu = new CryptUtils();
        String result1a = cu.encrypt("msg", "pwd");
        cu.setKeySize(192);
        cu.decrypt(result1a, "pwd");
    }
}
