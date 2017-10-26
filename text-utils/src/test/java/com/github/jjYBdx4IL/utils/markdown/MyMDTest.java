package com.github.jjYBdx4IL.utils.markdown;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MyMDTest {

    @Test
    public void testEmpty() {
        assertEquals("", MyMD.html(""));
    }

    @Test
    public void testParagraph() {
        assertEquals("<p srcLines=\"0-0\">one</p>", MyMD.html("one"));
    }

    @Test
    public void testParagraphEscape() {
        assertEquals("<p srcLines=\"0-0\">one&lt;</p>", MyMD.html("one<"));
    }

    @Test
    public void testList() {
        assertEquals("<ul srcLines=\"0-0\"><li>one&lt;</li></ul>", MyMD.html("* one<"));
        assertEquals("<ul srcLines=\"0-1\"><li>one</li><li>two</li></ul>", MyMD.html("* one\n* two"));
    }

    @Test
    public void testHeadlines() {
        assertEquals("<h1 srcLines=\"0-0\">one</h1>", MyMD.html("# one"));
        assertEquals("<h2 srcLines=\"0-0\">one</h2>", MyMD.html("## one"));
        assertEquals("<h3 srcLines=\"0-0\">one</h3>", MyMD.html("### one"));
        assertEquals("<h4 srcLines=\"0-0\">one</h4>", MyMD.html("#### one"));
        assertEquals("<h5 srcLines=\"0-0\">one</h5>", MyMD.html("##### one"));
        assertEquals("<h6 srcLines=\"0-0\">one</h6>", MyMD.html("###### one"));
    }

    @Test
    public void testHeadlinesEscape() {
        assertEquals("<h1 srcLines=\"0-0\">one&lt;</h1>", MyMD.html("# one<"));
        assertEquals("<h2 srcLines=\"0-0\">one&lt;</h2>", MyMD.html("## one<"));
        assertEquals("<h3 srcLines=\"0-0\">one&lt;</h3>", MyMD.html("### one<"));
        assertEquals("<h4 srcLines=\"0-0\">one&lt;</h4>", MyMD.html("#### one<"));
        assertEquals("<h5 srcLines=\"0-0\">one&lt;</h5>", MyMD.html("##### one<"));
        assertEquals("<h6 srcLines=\"0-0\">one&lt;</h6>", MyMD.html("###### one<"));
    }

    @Test
    public void testFormatting() {
        assertEquals("<p srcLines=\"0-0\"><i>one</i> <b>two</b> <b><i>three</i></b> <u>four</u> <i>five</i></p>",
            MyMD.html("*one* **two** ***three*** _four_ *five*"));
        assertEquals("<p srcLines=\"0-0\"><code>one</code></p>", MyMD.html("`one`"));
    }

    @Test
    public void testFormattingEscape() {
        assertEquals("<p srcLines=\"0-0\"><i>one&lt;</i> <b>two&lt;</b> <b><i>three&lt;</i></b> <u>four&lt;</u></p>",
            MyMD.html("*one<* **two<** ***three<*** _four<_"));
        assertEquals("<p srcLines=\"0-0\"><code>one&lt;</code></p>", MyMD.html("`one<`"));
    }

    @Test
    public void testBlockquote() {
        assertEquals("<blockquote srcLines=\"0-0\">one</blockquote>", MyMD.html("> one"));
    }

    @Test
    public void testBlockquoteEscape() {
        assertEquals("<blockquote srcLines=\"0-0\">one&lt;</blockquote>", MyMD.html("> one<"));
    }

    @Test
    public void testIndentedPre() {
        assertEquals("<pre srcLines=\"0-0\">one</pre>", MyMD.html("    one"));
    }

    @Test
    public void testIndentedPreEscape() {
        assertEquals("<pre srcLines=\"0-0\">one&lt;</pre>", MyMD.html("    one<"));
    }

    @Test
    public void testPre() {
        assertEquals("<pre srcLines=\"0-2\">one</pre>", MyMD.html("```\none\n```"));
    }

    @Test
    public void testPreEscape() {
        assertEquals("<pre srcLines=\"0-2\">one&lt;</pre>", MyMD.html("```\none<\n```"));
    }

    @Test
    public void testLink() {
        assertEquals("<p srcLines=\"0-0\"><a href=\"two\">one</a></p>", MyMD.html("[one](two)"));
    }

    @Test
    public void testLinkEscape() {
        assertEquals("<p srcLines=\"0-0\"><a href=\"two\">one&lt;</a></p>", MyMD.html("[one<](two)"));
    }

    @Test
    public void testFullExample() {
        String input = "# title\n"
            + "The *first* **paragraph of** this ***simple te***xt.\n"
            + "And the `next one`, and some par`ti`al code plus some [link](http://host).\n"
            + "> The *first* **blockquote of** this ***simple te***xt.\n"
            + "> And the `next one`, and some par`ti`al code plus some [link](http://host).\n"
            + "    and now some <pre-formatted\n"
            + "     text.\n"
            + "```\n"
            + "some other preformatted text.<\n"
            + "  second line\n"
            + "```";
        String output = "<h1 srcLines=\"0-0\">title</h1>"
            + "<p srcLines=\"1-1\">The <i>first</i> <b>paragraph of</b> this <b><i>simple te</i></b>xt.</p>"
            + "<p srcLines=\"2-2\">And the <code>next one</code>, and some par<code>ti</code>al code plus"
            + " some <a href=\"http://host\">link</a>.</p>"
            + "<blockquote srcLines=\"3-3\">The <i>first</i> <b>blockquote of</b> this <b><i>simple te</i></b>xt.</blockquote>"
            + "<blockquote srcLines=\"4-4\">And the <code>next one</code>, and some par<code>ti</code>al code plus"
            + " some <a href=\"http://host\">link</a>.</blockquote>"
            + "<pre srcLines=\"5-6\">and now some &lt;pre-formatted\n text.</pre>"
            + "<pre srcLines=\"7-10\">some other preformatted text.&lt;\n  second line</pre>";
        assertEquals(output, MyMD.html(input));
    }
}
