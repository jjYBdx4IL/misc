package org.apache.ecs;

import static org.junit.Assert.assertEquals;

import org.apache.ecs.html.Option;
import org.apache.ecs.html.P;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlGenTest {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlGenTest.class);
    
    @Test
    public void testHtmlGeneration() {
        Option optionElement = new Option();
        optionElement.setTagText("bar");
        optionElement.setValue("foo");
        optionElement.setSelected(false);

        assertEquals("<option value='foo'>bar</option>", optionElement.toString());

        Document doc = new Document();
        //doc.setCodeset();
        doc.appendBody(optionElement);
        //doc.appendBody(new P().addElement("&"));

        String result = doc.toString(); 
        LOG.debug(result);
        assertEquals("<html><head><title></title></head><body><option value='foo'>bar</option></body></html>",
                result);
    }
}
