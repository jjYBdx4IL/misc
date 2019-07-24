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
package net.sf.launch4j;

import java.io.File;
import java.io.IOException;

import net.sf.launch4j.config.ConfigPersister;
import net.sf.launch4j.config.ConfigPersisterException;
import net.sf.launch4j.formimpl.MainFrame;

public class MainRelocated {

    public static void main(String[] args) throws IOException, ConfigPersisterException, BuilderException {
        File workdir = new File(args[1]);

        for (File bin : new File(workdir, "bin").listFiles()) {
            bin.setExecutable(true);
        }

        //Properties props = Util.getProperties();

        if (args.length == 0) {
            ConfigPersister.getInstance().createBlank();
            MainFrame.createInstance();
            return;
        }
        
        ConfigPersister.getInstance().load(new File(args[0]));
        Builder b = new Builder(Log.getConsoleLog(), workdir);
        b.build();
    }

}
