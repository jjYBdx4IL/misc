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
package some.app;

import com.github.jjYBdx4IL.utils.cfg.AbstractConfig;

//CHECKSTYLE:OFF
import java.util.Locale;

/**
 * This config class needs to reside in a different (arbitrary) package hierarchy in order to
 * verify that the xstream deserialization security mechanism works in the way we have set it up. 
 *
 * @author Github jjYBdx4IL Projects
 */
public class ExampleConfig extends AbstractConfig {

    public String configOption1 = DEFAULT_STRING_VALUE;
    public String configOption2 = DEFAULT_STRING_VALUE;

    public ExampleConfig(String appName) {
        super(appName);
    }

    @Override
    public void postprocess() {
        if (configOption2 != null) {
            configOption2 = configOption2.toUpperCase(Locale.ROOT);
        }
    }

}
