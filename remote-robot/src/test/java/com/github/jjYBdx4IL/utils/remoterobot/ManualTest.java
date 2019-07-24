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

import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Maven;
import com.github.jjYBdx4IL.utils.env.Surefire;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;

public class ManualTest {

    public static final File TEMP_DIR = Maven.getTempTestDir(ManualTest.class);

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
    public void testGetScreenshot() throws IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        try (RobotClient client = RobotClient.connect(InetAddress.getLoopbackAddress(), RobotServer.DEFAULT_PORT)) {
            BufferedImage screenshot = client.createScreenCapture(null);
            ImageIO.write(screenshot, "png", new File(TEMP_DIR, "screenshot.png"));
        }
    }

    @Test
    public void memLeakTest() throws IOException {
        assumeTrue(Surefire.isSingleTestExecution());

        try (RobotClient client = RobotClient.connect(InetAddress.getLoopbackAddress(), RobotServer.DEFAULT_PORT)) {
            for (;;) {
                client.createScreenCapture(null);
            }
        }
    }
    
    @Test
    public void clickTest() throws IOException, InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        Thread.sleep(3000);
        
        try (RobotClient client = RobotClient.connect(InetAddress.getLoopbackAddress(), RobotServer.DEFAULT_PORT)) {
            client.mousePress(MouseButton.LEFT);
            client.mouseRelease(MouseButton.LEFT);
        }
    }
    
    @Test
    public void mouseMoveTest() throws IOException, InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        Thread.sleep(3000);
        
        try (RobotClient client = RobotClient.connect(InetAddress.getLoopbackAddress(), RobotServer.DEFAULT_PORT)) {
            client.setAutoDelayMs(1000L);
            client.mouseMove(50, 50);
            client.mouseMove(new Point(100, 100));
        }
    }
}
