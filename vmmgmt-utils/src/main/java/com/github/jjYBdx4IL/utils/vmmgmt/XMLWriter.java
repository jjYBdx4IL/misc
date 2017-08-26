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
public class XMLWriter {

    private final static Charset charset = Charset.forName("UTF-8");

    public static String createDomainCreateXML(String xmlTplResName, Map<String, Object> tplData)
            throws IOException, TemplateException {
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDefaultEncoding(charset.name());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        String tplCode = IOUtils.toString(XMLWriter.class.getResourceAsStream(xmlTplResName), charset);
        Template tpl = new Template(xmlTplResName, tplCode, cfg);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (Writer out = new OutputStreamWriter(os)) {
                tpl.process(tplData, out);
            }
            return os.toString(charset.name());
        }
    }

}
