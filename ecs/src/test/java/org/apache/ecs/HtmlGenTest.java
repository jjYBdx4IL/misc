package org.apache.ecs;

import static org.junit.Assert.assertEquals;

import org.apache.ecs.html.Body;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.H3;
import org.apache.ecs.html.Head;
import org.apache.ecs.html.Html;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Title;
import org.apache.ecs.xml.XML;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlGenTest {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlGenTest.class);

    @BeforeClass
    public static void beforeClass() {
        LOG.info(ECSDefaults.debugString());
    }

    @Test
    public void testHtmlGeneration() {
        Option optionElement = new Option();
        optionElement.setTagText("bar");
        optionElement.setValue("foo");
        optionElement.setSelected(false);

        assertEquals("<option value='foo'>bar</option>", optionElement.toString());

        Document doc = new Document();
        // doc.setCodeset();
        doc.appendBody(optionElement);
        // doc.appendBody(new P().addElement("&"));

        String result = doc.toString();
        LOG.debug(result);
        assertEquals("<html>\n" +
            "    <head>\n" +
            "        <title></title>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <option value='foo'>bar</option>\n" +
            "    </body>\n" +
            "</html>",
            result.replace("\r", "").replace("\t", "    "));
    }

    @Test
    public void testHtmlGeneration2() {
        Html html = new Html()
            .addElement(new Head()
                .addElement(new Title("Demo")))
            .addElement(new Body()
                .addElement(new H1("Demo Header"))
                .addElement(new H3("Sub Header:"))
                .addElement(new Font().setSize("+1")
                    .setColor(HtmlColor.WHITE)
                    .setFace("Times")
                    .addElement("The big dog & the little cat chased each other.")));
        assertEquals("<html>\n" +
            "    <head>\n" +
            "        <title>\n" +
            "            Demo\n" +
            "        </title>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <h1>\n" +
            "            Demo Header\n" +
            "        </h1>\n" +
            "        <h3>\n" +
            "            Sub Header:\n" +
            "        </h3>\n" +
            "        <font color='#FFFFFF' face='Times' size='+1'>\n" +
            "            The big dog &#38; the little cat chased each other.\n" +
            "        </font>\n" +
            "    </body>\n" +
            "</html>",
            html.toString().replace("\r", "").replace("\t", "    "));
    }

    @Test
    public void testHtmlGeneration3() {
        Document doc = (Document) new Document()
            .appendTitle("Demo")
            .appendBody(new H1("Demo Header"))
            .appendBody(new H3("Sub Header:"))
            .appendBody(new Font().setSize("+1")
                .setColor(HtmlColor.WHITE)
                .setFace("Times")
                .setTagText("The big dog & the little cat chased each other."));
        assertEquals("<html>\n" +
            "    <head>\n" +
            "        <title>\n" +
            "            Demo\n" +
            "        </title>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <h1>\n" +
            "            Demo Header\n" +
            "        </h1>\n" +
            "        <h3>\n" +
            "            Sub Header:\n" +
            "        </h3>\n" +
            "        <font color='#FFFFFF' face='Times' size='+1'>The big dog &#38; the little cat chased each other.</font>\n"
            +
            "    </body>\n" +
            "</html>",
            doc.toString().replace("\r", "").replace("\t", "    "));
    }

    @Test
    public void testCustomElement() {
        Document doc = new Document();
        doc.appendBody(new XML("customElement"));

        String result = doc.toString();
        LOG.debug(result);
        assertEquals("<html>\n" + 
            "    <head>\n" + 
            "        <title></title>\n" + 
            "    </head>\n" + 
            "    <body>\n" + 
            "        <customElement></customElement>\n" + 
            "    </body>\n" + 
            "</html>",
            result.replace("\r", "").replace("\t", "    "));
    }

}
