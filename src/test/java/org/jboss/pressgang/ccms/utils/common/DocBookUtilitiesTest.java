package org.jboss.pressgang.ccms.utils.common;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.pressgang.ccms.utils.structures.StringToNodeCollection;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DocBookUtilitiesTest {

    @Test
    public void shouldEscapeTitleString() {
        // Given a string testing replacing < with &lt;
        final String test = "<title>Product A > Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string testing replacing > with &gt;
        final String test2 = "<title>Product A < Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string testing replacing & with &amp;
        final String test3 = "<title>Product A & Product B<phrase condition=\"beta\">-Beta</phrase></title>";
        // and a string to test entities aren't altered
        final String test4 = "<title>Product A &amp; Product B<phrase condition=\"beta\">-Beta</phrase></title>";

        // When escaping the title string
        final String output = DocBookUtilities.escapeForXML(test);
        final String output2 = DocBookUtilities.escapeForXML(test2);
        final String output3 = DocBookUtilities.escapeForXML(test3);
        final String output4 = DocBookUtilities.escapeForXML(test4);

        // Then make sure the relevant characters have been replaced
        assertEquals(output, "<title>Product A &gt; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output2, "<title>Product A &lt; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output3, "<title>Product A &amp; Product B<phrase condition=\"beta\">-Beta</phrase></title>");
        assertEquals(output4, test4);
    }

    @Test
    public void shouldAddDocBook5Namespace() {
        // Given a basic xml document
        final String xml = "<section>\n<title>Test</title>\n<para>Some section content</para>\n</section>";

        // When
        final String fixedXML = DocBookUtilities.addDocBook50Namespace(xml, "section");

        // Then
        assert fixedXML.contains("xmlns=\"http://docbook.org/ns/docbook\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"5.0\"");
    }

    @Test
    public void shouldFindTranslatableElements() throws SAXException {
        // Given
        String xml = "<section>\n" +
                "\t<title>TLS/SSL Certification</title>\n" +
                "\t<para>\n" +
                "\t\tThe API requires Hypertext Transfer Protocol Secure (HTTPS) \n" +
                "\t\t<footnote>\n" +
                "\t\t\t<para>\n" +
                "\t\t\t\tHTTPS is described in <ulink url=\"http://tools.ietf.org/html/rfc2818\">RFC 2818 HTTP Over TLS</ulink>.\n" +
                "\t\t\t</para>\n" +
                "\t\t</footnote> for secure transport-level encryption of requests. This involves a process of attaining a certificate " +
                "from your Red Hat Enterprise Virtualization Manager server and importing it into your client's certificate store.\n" +
                "\t</para>\n" +
                "\t<para>\n" +
                "\t\t<!-- Inject: 3737 -->\n"+
                "\t</para>\n" +
                "</section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When
        List<StringToNodeCollection> nodes = DocBookUtilities.getTranslatableStringsV2 (doc, false);

        // Then
        assertThat(nodes.size(), is(3));
    }
}
