package org.jboss.pressgang.ccms.utils.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
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
                "\t</para>\n" +
                "\t<para>\n" +
                "\t\t<!-- Inject: 3737 -->\n"+
                "\t</para>\n" +
                "</section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When
        List<StringToNodeCollection> nodes = XMLUtilities.getTranslatableStringsV2 (doc, false);

        // Then
        assertThat(nodes.size(), is(3));
    }

    @Test
    public void shouldMaintainWhitespaceWhenConvertingNode() throws SAXException {
        // Given some XML that is converted into a dom.
        String xml = "<section><para> A universal installer<filename> .jar</filename> file is provided for installing JBoss&nbsp;Developer&nbsp;Studio and" +
                " it is available from the Customer Portal. <phrase condition=\"install\">A second version of the universal installer for" +
                " installing both JBoss&nbsp;Developer&nbsp;Studio and JBoss EAP is also available from the Customer Portal. </phrase>The" +
                " procedure below guides you though obtaining the universal installer<phrase condition=\"install\">s</phrase> and the " +
                "installation process. \n</para><para>Inline injection <!-- Inject: 8670 --></para><!-- Normal Comment " +
                "-->\n\n<programlisting><![CDATA[\nSome test CDATA content with an entity &amp;]]></programlisting></section>\n";
        Document doc = XMLUtilities.convertStringToDocument(xml);

        final List<String> inline = Arrays.asList("code","prompt","command","firstterm","ulink","guilabel","filename","replaceable","parameter","literal","classname","sgmltag","guibutton","guimenuitem","guimenu","menuchoice","citetitle","systemitem","application","acronym","keycap","emphasis","package","quote","trademark","abbrev","phrase","anchor","citation","glossterm","link","xref","markup","tag","keycode","keycombo","accel","guisubmenu","keysym","shortcut","mousebutton","constant","errorcode","errorname","errortype","function","msgtext","property","returnvalue","symbol","token","varname","database","email","hardware","option","optional","type","methodname","interfacename","uri","productname","productversion","revnumber","date",
                "computeroutput","firstname","surname","exceptionname","guiicon","property");
        final List<String> verbatim = Arrays.asList("screen","programlisting","literallayout","synopsis","address");
        final List<String> verbatimInline = Arrays.asList("title", "term");

        // When converting the node to a string
        final String convertedXml = XMLUtilities.convertNodeToString(doc, verbatim, inline, verbatimInline, true);

        // Then
        final String expectedXml = "<section>\n" +
                "\t<para>\n" +
                "\t\tA universal installer<filename> .jar</filename> file is provided for installing JBoss&nbsp;Developer&nbsp;Studio and" +
                " it is available from the Customer Portal. <phrase condition=\"install\">A second version of the universal installer for" +
                " installing both JBoss&nbsp;Developer&nbsp;Studio and JBoss EAP is also available from the Customer Portal. </phrase>The" +
                " procedure below guides you though obtaining the universal installer<phrase condition=\"install\">s</phrase> and the " +
                "installation process.\n" +
                "\t</para>\n" +
                "\t<para>\n" +
                "\t\tInline injection <!-- Inject: 8670 -->\n" +
                "\t</para>\n" +
                "\t<!-- Normal Comment -->\n" +
                "\t<programlisting><![CDATA[\n" +
                "Some test CDATA content with an entity &amp;]]></programlisting>\n" +
                "</section>";
        assertEquals(expectedXml, convertedXml);
    }

    @Test
    public void shouldMaintainWhitespaceWhenConvertingNode2() throws SAXException {
        // Given some XML that is converted into a dom.
        String xml = "<section>\n" +
                "\t<title>Configuring Fence Devices</title>\n"+
                "\t<para>\n"+
                "\t\tChoose <guimenu>File</guimenu> =&gt;\n" +
                "\t\t<guimenuitem>Save</guimenuitem> to save the changes to the cluster configuration.\n" +
                "\t</para>\n" +
                "</section>";
        Document doc = XMLUtilities.convertStringToDocument(xml);

        final List<String> inline = Arrays.asList("code","prompt","command","firstterm","ulink","guilabel","filename","replaceable","parameter","literal","classname","sgmltag","guibutton","guimenuitem","guimenu","menuchoice","citetitle","systemitem","application","acronym","keycap","emphasis","package","quote","trademark","abbrev","phrase","anchor","citation","glossterm","link","xref","markup","tag","keycode","keycombo","accel","guisubmenu","keysym","shortcut","mousebutton","constant","errorcode","errorname","errortype","function","msgtext","property","returnvalue","symbol","token","varname","database","email","hardware","option","optional","type","methodname","interfacename","uri","productname","productversion","revnumber","date",
                "computeroutput","firstname","surname","exceptionname","guiicon","property");
        final List<String> verbatim = Arrays.asList("screen","programlisting","literallayout","synopsis","address");
        final List<String> verbatimInline = Arrays.asList("title", "term");

        // When converting the node to a string
        final String convertedXml = XMLUtilities.convertNodeToString(doc, verbatim, inline, verbatimInline, true);

        // Then
        final String expectedXml = "<section>\n" +
            "\t<title>Configuring Fence Devices</title>\n"+
            "\t<para>\n"+
            "\t\tChoose <guimenu>File</guimenu> =&gt; <guimenuitem>Save</guimenuitem> to save the changes to the cluster configuration.\n" +
            "\t</para>\n" +
            "</section>";
        assertEquals(expectedXml, convertedXml);
    }
}