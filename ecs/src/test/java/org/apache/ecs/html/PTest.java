package org.apache.ecs.html;

import static org.junit.Assert.*;

import org.junit.Test;

public class PTest {

    @Test
    public void testP() {
        assertEquals("<p align='left'>\n" + 
            "    &#38;", new P("&", "left").toString("UTF-8").replace("\r", "").replace("\t", "    "));
    }
}
