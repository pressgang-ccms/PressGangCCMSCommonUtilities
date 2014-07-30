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

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.utils.structures.DocBookVersion;
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
                "\t\t<!-- Inject: 3737 -->\n" +
                "\t</para>\n" +
                "</section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When
        List<StringToNodeCollection> nodes = DocBookUtilities.getTranslatableStringsV3(doc, false);

        // Then
        assertThat(nodes.size(), is(3));
    }

    @Test
    public void shouldFindTranslatableElementsWhenCommentsUsed() throws SAXException {
        // Given
        String xml = "<section>\n" +
                "\t<para>\n" +
                "\t\tOpen the <filename>deploy.cab</filename> \n" +
                "\t\t<!--filename>WindowsXP-KB838080-SP2-DeployTools-ENU.cab</filename--> file and add its contents to " +
                "<filename>c:\\sysprep</filename>.\n" +
                "\t</para>\n" +
                "</section>";
        final Document doc = XMLUtilities.convertStringToDocument(xml);

        // When
        List<StringToNodeCollection> nodes = DocBookUtilities.getTranslatableStringsV3(doc, false);

        // Then
        assertThat(nodes.size(), is(1));
        assertThat(nodes.get(0).getTranslationString(),
                is("Open the <filename>deploy.cab</filename> <!--filename>WindowsXP-KB838080-SP2-DeployTools-ENU.cab</filename--> file " +
                        "and add its contents to <filename>c:\\sysprep</filename>."));
    }

    public static Document getXMLEntityTestDoc() throws SAXException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<section>\n");
        stringBuilder.append("	<title>A Title</title>\n");
        stringBuilder.append("	<para>\n");
        stringBuilder.append("		This is a standard docbook entity: &nbsp;\n");
        stringBuilder.append("	</para>\n");
        stringBuilder.append("	<para>\n");
        stringBuilder.append("		This is a custom entity: &PRODUCT;\n");
        stringBuilder.append("	</para>\n");
        stringBuilder.append("</section>\n");

        return XMLUtilities.convertStringToDocument(stringBuilder.toString(), true);
    }

    /**
     * Confirm that custom and default docbook entities are found by allEntitiesAccountedFor
     *
     * @throws SAXException
     */
    @Test
    public void testAllStringEntitiesAccountedFor() throws SAXException {
        assertTrue(
                DocBookUtilities.allEntitiesAccountedFor(getXMLEntityTestDoc(), DocBookVersion.DOCBOOK_45, "<!ENTITY PRODUCT \"A Test\">"));
    }

    /**
     * Confirm that custom and default docbook entities are found by allEntitiesAccountedFor
     *
     * @throws SAXException
     */
    @Test
    public void testAllListEntitiesAccountedFor() throws SAXException {
        assertTrue(DocBookUtilities.allEntitiesAccountedFor(getXMLEntityTestDoc(), DocBookVersion.DOCBOOK_45, new ArrayList<String>() {{
            add("PRODUCT");
        }}));
    }

    /**
     * Confirm that missing custom entities are found by allEntitiesAccountedFor
     *
     * @throws SAXException
     */
    @Test
    public void testMissingCustomEntitiesFound() throws SAXException {
        assertFalse(DocBookUtilities.allEntitiesAccountedFor(getXMLEntityTestDoc(), DocBookVersion.DOCBOOK_45, ""));
    }

    /**
     * Confirm that missing custom entities are found by allEntitiesAccountedFor
     *
     * @throws SAXException
     */
    @Test
    public void testMissingCustomEntitiesFound2() throws SAXException {
        assertFalse(DocBookUtilities.allEntitiesAccountedFor(getXMLEntityTestDoc(), DocBookVersion.DOCBOOK_45, (String) null));
    }

    /**
     * Confirm that missing default entities are found by allEntitiesAccountedFor
     *
     * @throws SAXException
     */
    @Test
    public void testMissingDefaultEntitiesFound() throws SAXException {
        assertFalse(DocBookUtilities.allEntitiesAccountedFor(getXMLEntityTestDoc(), null, "<!ENTITY PRODUCT \"A Test\">"));
    }
}
