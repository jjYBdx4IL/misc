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
package com.github.jjYBdx4IL.jmon;

import com.github.jjYBdx4IL.jmon.dto.ServiceStateXfer;
import com.github.jjYBdx4IL.utils.io.IoUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreInSpool implements IExecModule {

    public static final Pattern PAT = Pattern.compile("^(\\d+):([a-zA-Z0-9_-]+):(.*)$");
    
    @Override
    public void exec() {
        String s = Config.cmd.getOptionValue(Config.OPT_ACTION_STORE_IN_SPOOL);
        Matcher m = PAT.matcher(s);
        if (!m.find()) {
            throw new RuntimeException("invalid status message format: " + s);
        }
        ServiceStateXfer ssx = new ServiceStateXfer();
        ssx.state = Integer.parseInt(m.group(1));
        ssx.service = m.group(2);
        ssx.msg = m.group(3);
        String json = Config.gson.toJson(ssx);
        try {
            IoUtils.safeWriteTo(Config.spoolDir.resolve(ssx.service), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        // not used
    }
}
