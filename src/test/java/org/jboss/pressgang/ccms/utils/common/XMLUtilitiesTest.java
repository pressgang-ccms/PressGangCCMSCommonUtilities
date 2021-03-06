/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.utils.common;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

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

        // When finding the xml preamble
        String xmlPreamble = XMLUtilities.findPreamble(xml);
        String xmlWithDoctypePreamble = XMLUtilities.findPreamble(xmlWithDoctype);

        // Then check that the preamble was found
        assertThat(xmlPreamble, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(xmlWithDoctypePreamble, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    @Test
    public void shouldNotFindXMLPreamble() {
        // Given an XML String with preamble used as an example
        String xml = "<section>\n<programlisting>\n<![CDATA[\n<?xml version=\"1.0\" encoding=\"UTF-8\" " +
                "standalone=\"yes\"?>\n]]></programlisting></section>\n";
        // and another string with no preamble and a program listing that also contains a preamble example
        String xml2 = "<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>]]>\n</programlisting></section>\n";

        // When finding the xml preamble
        String xmlPreamble = XMLUtilities.findPreamble(xml);
        String xmlPreamble2 = XMLUtilities.findPreamble(xml);

        // Then check that no preamble was found
        assertNull(xmlPreamble);
        assertNull(xmlPreamble2);
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
    public void shouldEscapeReservedXMLCharacters() throws SAXException {
        // Given a string with reserved xml characters via character references
        final String xml = "<section><title></title><para blah=\"&#34;&#38;&#60;&#62;\">This is an " +
                "&#38;&#60;&#62;&amp;test</para></section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When converting a node to a string
        final String output = XMLUtilities.convertNodeToString(doc, true);

        // Then the output should have the reserved characters escaped as entities
        assertThat(output, is("<section><title /><para blah=\"&quot;&amp;&lt;&gt;\">This is an &amp;&lt;&gt;&amp;test</para></section>"));
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
    public void shouldNotFindXMLDoctype() {
        // Given an XML String with doctype in the example
        String xmlWithExample = "<section>\n<programlisting>\n<![CDATA[<?xml version=\"1.0\" " +
                "encoding=\"UTF-8\"?>\n<!DOCTYPE section SYSTEM \"docbook.dtd\" >]]>\n</programlisting></section>\n";

        // When finding the xml preamble
        String xmlDoctype = XMLUtilities.findDocumentType(xmlWithExample);


        // Then check that no doctype was found
        assertNull(xmlDoctype);
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

    @Test
    public void shouldMaintainProcessingInstructionWhenConvertingNode() throws SAXException {
        // Given some XML that is converted into a dom.
        String xml = "<simplesect xmlns=\"http://docbook.org/ns/docbook\">\n" +
                "    <title>Creating a base profile</title>\n" +
                "    <para>To create a base profile:</para>\n" +
                "    <procedure>\n" +
                "      <step>\n" +
                "        <para>Optionally create a new profile version using the \n" +
                "          <command>fabric:version-create</command> command.</para>\n" +
                "        <para>This will create a new copy of the existing profiles.</para>\n" +
                "      </step>\n" +
                "      <step>\n" +
                "        <para>Import the new XML template into the registry using the \n" +
                "          <command>fabric:import</command> command as shown in \n" +
                "          <xref linkend=\"FMQAdminConfigFabricCreateProfile\"/>.</para>\n" +
                "        <example xml:id=\"FMQAdminConfigFabricImportXML\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" pgwide=\"1\">\n" +
                "          <?dbfo pgwide=\"1\"?>\n" +
                "          <title>Importing an XML Configuration Template</title>\n" +
                "          <screen><prompt>JBossA-MQ:karaf@root&gt;</prompt> <userinput>fabric:import -t " +
                "/fabric/configs/versions/<replaceable>version</replaceable>/profiles/mq-base/<replaceable>xmlTemplate</replaceable> " +
                "<replaceable>xmlTemplatePath</replaceable></userinput></screen>\n" +
                "        </example>\n" +
                "      </step>\n" +
                "      <step>\n" +
                "        <para>Create a new configuration profile instance to hold the new XML template using the \n" +
                "          <command>fabric:mq-create</command> command as shown in \n" +
                "          <xref linkend=\"FMQAdminConfigFabricCreateProfile\"/>.</para>\n" +
                "        <example xml:id=\"FMQAdminConfigFabricCreateProfile\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" " +
                "pgwide=\"1\">\n" +
                "          <?dbfo pgwide=\"1\"?>\n" +
                "          <title>Creating a Profile Using an XML Configuration Template</title>\n" +
                "          <screen><prompt>JBossAMQ:karaf@root&gt;</prompt> <userinput>fabric:mq-create --config " +
                "<replaceable>xmlTemplate</replaceable> <replaceable>profileName</replaceable></userinput></screen>\n" +
                "        </example>\n" +
                "        <para>This will create a new profile that is based on the default broker profile but uses \n" +
                "          the imported XML template.</para>\n" +
                "      </step>\n" +
                "    </procedure>\n" +
                "  </simplesect>";
        Document doc = XMLUtilities.convertStringToDocument(xml);

        final List<String> inline = Arrays.asList("code","prompt","command","firstterm","ulink","guilabel","filename","replaceable","parameter","literal","classname","sgmltag","guibutton","guimenuitem","guimenu","menuchoice","citetitle","systemitem","application","acronym","keycap","emphasis","package","quote","trademark","abbrev","phrase","anchor","citation","glossterm","link","xref","markup","tag","keycode","keycombo","accel","guisubmenu","keysym","shortcut","mousebutton","constant","errorcode","errorname","errortype","function","msgtext","property","returnvalue","symbol","token","varname","database","email","hardware","option","optional","type","methodname","interfacename","uri","productname","productversion","revnumber","date",
                "computeroutput","firstname","surname","exceptionname","guiicon","property");
        final List<String> verbatim = Arrays.asList("screen","programlisting","literallayout","synopsis","address");
        final List<String> verbatimInline = Arrays.asList("title", "term");

        // When converting the node to a string
        final String convertedXml = XMLUtilities.convertNodeToString(doc, verbatim, inline, verbatimInline, true);

        // Then
        assertThat(convertedXml, containsString("<?dbfo pgwide=\"1\"?>"));
    }
}