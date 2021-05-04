package org.apache.ecs.html;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlTest {

    @Test
    public void testHtml() {
        assertEquals("<html>\n" +
            "    <body>\n" +
            "        <div>\n" +
            "            &#38;\n" +
            "        </div>\n" +
            "    </body>\n" +
            "</html>",
            new Html().addElement(new Body().addElement(new Div("&"))).toString("UTF-8").replace("\r", "").replace("\t",
                "    "));
    }
}
