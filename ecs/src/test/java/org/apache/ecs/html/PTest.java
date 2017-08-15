package org.apache.ecs.html;

import static org.junit.Assert.*;

import org.junit.Test;

public class PTest {

    @Test
    public void testP() {
        assertEquals("<p align='left'>&", new P("&", "left").toString("UTF-8"));
    }
}
