package org.apache.ecs.html;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlTest {

    @Test
    public void testHtml() {
        assertEquals("<html><body><div>&</div></body></html>",
                new Html().addElement(new Body().addElement(new Div("&"))).toString("UTF-8"));
    }
}
