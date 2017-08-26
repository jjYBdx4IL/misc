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
package com.github.jjYBdx4IL.utils.vmmgmt;

//CHECKSTYLE:OFF
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 *
 * @author jjYBdx4IL
 */
public class KickStart {

    private final Map<String, Object> tplData = new HashMap<>();
    private final OS os;
    private final Charset charset = Charset.forName("UTF-8");

    public KickStart(OS os) {
        if (os == null) {
            throw new IllegalArgumentException();
        }
        this.os = os;
        // set defaults
        tplData.put("initialUser", "ubuntu");
        tplData.put("initialUserPwd", "password");
    }

    public void setInitialUser(String value) {
        tplData.put("initialUser", value);
    }

    public void setInitialUserPassword(String value) {
        tplData.put("initialUserPwd", value);
    }

    public byte[] createFileContent() throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding(charset.name());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        String tplCode = IOUtils.toString(getClass().getResourceAsStream(os.getKickstartTplName()), charset);
        Template tpl = new Template(os.name(), tplCode, cfg);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (Writer out = new OutputStreamWriter(baos)) {
                tpl.process(tplData, out);
            }
            return baos.toByteArray();
        }
    }

}
