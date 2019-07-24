/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.env.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;

public class RobotServerTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(RobotServerTest.class);

    RobotServer server = null;
    Thread serverThread = null;

    @Before
    public void before() throws InterruptedException {
        server = new RobotServer(0);
        server.start();
        server.waitStarted();
    }

    @After
    public void after() throws Exception {
        server.shutdownWait();
    }

    @Test
    public void test() throws InterruptedException, IOException, ClassNotFoundException {
        assumeFalse(GraphicsEnvironment.isHeadless());

        try (RobotClient client = RobotClient.connect(InetAddress.getLoopbackAddress(), server.getPort())) {
            Color pixel = client.getPixelColor(0, 0);
            assertNotNull(pixel);

            BufferedImage screenshot = client.createScreenCapture(null);
            ImageIO.write(screenshot, "png", new File(TEMP_DIR, "screenshot.png"));
        }
    }

}
