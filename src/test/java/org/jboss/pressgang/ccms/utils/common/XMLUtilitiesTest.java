package org.jboss.pressgang.ccms.utils.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.pressgang.ccms.utils.structures.StringToNodeCollection;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLUtilitiesTest {

    @Test
    public void shouldFindXMLPreamble() {
        // Given an XML String with preamble
        String xml = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<section>\n</section>\n";
        // and another string with preamble and a doctype
        String xmlWithDoctype = "\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE section SYSTEM \"docbook" +
                ".dtd\">\n<section>\n</section>\n";
        // and another string with no preamble and a program listing that also contains a preamble example
        String xmlNoPreambleWithExample = "<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>]]>\n</programlisting></section>\n";

        // When finding the xml preamble
        String xmlPreamble = XMLUtilities.findPreamble(xml);
        String xmlWithDoctypePreamble = XMLUtilities.findPreamble(xmlWithDoctype);
        String xmlNoPreambleWithExamplePreamble = XMLUtilities.findPreamble(xmlNoPreambleWithExample);

        // Then check that the preamble was found
        assertThat(xmlPreamble, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(xmlWithDoctypePreamble, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertNull(xmlNoPreambleWithExamplePreamble);
    }

    @Test
    public void shouldFindXMLEncoding() {
        // Given an XML String with preamble
        String xml = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<section>\n</section>\n";
        // and another string with preamble and a program listing that also contains a preamble example
        String xmlWithExample = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE section SYSTEM \"docbook" +
                ".dtd\">\n<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>]]>\n</programlisting></section>\n";
        // and another string with no preamble and a program listing that also contains a preamble example
        String xmlNoPreambleWithExample = "<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>]]>\n</programlisting></section>\n";

        // When finding the xml preamble
        String xmlEncoding = XMLUtilities.findEncoding(xml);
        String xmlWithExampleEncoding = XMLUtilities.findEncoding(xmlWithExample);
        String xmlNoPreambleWithExampleEncoding = XMLUtilities.findEncoding(xmlNoPreambleWithExample);

        // Then check that the encoding was found
        assertThat(xmlEncoding, is("UTF-8"));
        assertThat(xmlWithExampleEncoding, is("UTF-8"));
        assertNull(xmlNoPreambleWithExampleEncoding);
    }

    @Test
    public void shouldFindXMLDoctype() {
        // Given an XML String with preamble
        String xml = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<section>\n</section>\n";
        // and another string with preamble and a doctype
        String xmlWithDoctype = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE section SYSTEM \"docbook" +
                ".dtd\">\n<section>\n</section>\n";
        // and another string with preamble and a public doctype
        String xmlWithPublicDoctype = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n  <!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook " +
                "XML V4.5//EN\" \"docbook.dtd\">\n<section>\n</section>\n";
        // and another string with preamble and a program listing that also contains a preamble example
        String xmlWithExample = " <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook " +
                "XML V4.5//EN\" \"docbook.dtd\">\n<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>\n<!DOCTYPE section SYSTEM \"docbook.dtd\" >]]>\n</programlisting></section>\n";

        // When finding the xml preamble
        String xmlDoctype = XMLUtilities.findDocumentType(xml);
        String xmlWithDoctypeDoctype= XMLUtilities.findDocumentType(xmlWithDoctype);
        String xmlWithPublicDoctypeDoctype= XMLUtilities.findDocumentType(xmlWithPublicDoctype);
        String xmlWithExampleDoctype= XMLUtilities.findDocumentType(xmlWithExample);

        // Then check that the preamble was found as expected
        assertNull(xmlDoctype);
        assertThat(xmlWithDoctypeDoctype, is("<!DOCTYPE section SYSTEM \"docbook.dtd\">"));
        assertThat(xmlWithPublicDoctypeDoctype, is("<!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"docbook.dtd\">"));
        assertThat(xmlWithExampleDoctype, is("<!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"docbook.dtd\">"));
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
                "\t</para>\n</section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When
        List<StringToNodeCollection> nodes = XMLUtilities.getTranslatableStringsV2 (doc, false);

        // Then
        assertThat(nodes.size(), is(2));
    }
}
