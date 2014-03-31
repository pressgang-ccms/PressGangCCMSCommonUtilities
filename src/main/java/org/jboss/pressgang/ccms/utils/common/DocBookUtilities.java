package org.jboss.pressgang.ccms.utils.common;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.structures.DocBookVersion;
import org.jboss.pressgang.ccms.utils.structures.Pair;
import org.jboss.pressgang.ccms.utils.structures.StringToNodeCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A collection of static variables and functions that can be used when working
 * with DocBook
 */
public class DocBookUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(DocBookUtilities.class);

    // See http://stackoverflow.com/a/4307261/1330640
    private static String UNICODE_WORD = "\\pL\\pM\\p{Nd}\\p{Nl}\\p{Pc}[\\p{InEnclosedAlphanumerics}&&\\p{So}]";
    private static String UNICODE_TITLE_START_CHAR = "\\pL\\p{Nd}\\p{Nl}";

    /**
     * The name of the section tag
     */
    public static final String TOPIC_ROOT_NODE_NAME = "section";
    /**
     * The name of the id attribute
     */
    public static final String TOPIC_ROOT_ID_ATTRIBUTE = "id";
    /**
     * The name of the title tag
     */
    public static final String TOPIC_ROOT_TITLE_NODE_NAME = "title";
    /**
     * The name of the sectioninfo tag
     */
    public static final String TOPIC_ROOT_SECTIONINFO_NODE_NAME = "sectioninfo";

    /**
     * The Docbook elements that contain translatable text
     */
    public static final ArrayList<String> TRANSLATABLE_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"ackno", "bridgehead", "caption", "conftitle", "contrib", "entry", "firstname", "glossterm", "indexterm",
                    "jobtitle", "keyword", "label", "lastname", "lineannotation", "lotentry", "member", "orgdiv", "orgname", "othername",
                    "para", "phrase", "productname", "refclass", "refdescriptor", "refentrytitle", "refmiscinfo", "refname",
                    "refpurpose", "releaseinfo", "revremark", "screeninfo", "secondaryie", "seealsoie", "seeie", "seg", "segtitle",
                    "simpara", "subtitle", "surname", "term", "termdef", "tertiaryie", "title", "titleabbrev", "screen",
                    "programlisting", "literallayout"});
    /**
     * The Docbook elements that contain translatable text, and need to be kept inline
     */
    public static final ArrayList<String> INLINE_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"footnote", "citerefentry", "indexterm", "productname", "phrase"});
    /**
     * The Docbook elements that should not have their text reformatted
     */
    public static final ArrayList<String> VERBATIM_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"screen", "programlisting", "literallayout"});
    /**
     * The Docbook elements that should be translated only if their parent is not listed in TRANSLATABLE_ELEMENTS
     */
    public static final ArrayList<String> TRANSLATABLE_IF_STANDALONE_ELEMENTS = CollectionUtilities.toArrayList(
            new String[]{"indexterm", "productname", "phrase"});

    /**
     * Finds the first title element in a DocBook XML file.
     *
     * @param xml The docbook xml file to find the title from.
     * @return The first title found in the xml.
     */
    public static String findTitle(final String xml) {
        // Convert the string to a document to make it easier to get the proper title
        Document doc = null;
        try {
            doc = XMLUtilities.convertStringToDocument(xml);
        } catch (Exception ex) {
            LOG.debug("Unable to convert String to a DOM Document", ex);
        }

        return findTitle(doc);
    }

    /**
     * Finds the first title element in a DocBook XML file.
     *
     * @param doc The docbook xml transformed into a DOM Document to find the title from.
     * @return The first title found in the xml.
     */
    public static String findTitle(final Document doc) {
        if (doc == null) return null;

        // loop through the child nodes until the title element is found
        final NodeList childNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            // check if the node is the title and if its parent is the document root element
            if (node.getNodeName().equals(TOPIC_ROOT_TITLE_NODE_NAME) && node.getParentNode().equals(doc.getDocumentElement())) {
                return XMLUtilities.convertNodeToString(node, false);
            }
        }

        return null;
    }

    /**
     * Escapes a title so that it is alphanumeric or has a fullstop, underscore or hyphen only.
     * It also removes anything from the front of the title that isn't alphanumeric.
     *
     * @param title The title to be escaped
     * @return The escaped title string.
     */
    public static String escapeTitle(final String title) {
        final String escapedTitle = title.replaceAll("^[^" + UNICODE_TITLE_START_CHAR +"]*", "").replaceAll("[^" + UNICODE_WORD + ". -]", "");
        if (isNullOrEmpty(escapedTitle)) {
            return "";
        } else {
            // Remove whitespace
            return escapedTitle.replaceAll("\\s+", "_").replaceAll("(^_+)|(_+$)", "").replaceAll("__", "_");
        }
    }

    public static void setSectionTitle(final DocBookVersion docBookVersion, final String titleValue, final Document doc) {
        assert doc != null : "The doc parameter can not be null";
        final Element docElement = doc.getDocumentElement();

        // Check to make sure the document is a section. If it isn't than just return
        if (docElement == null || !docElement.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_NODE_NAME)) return;

        // Attempt to parse the title as XML. If this fails then just set the title as plain text.
        final Element newTitle = doc.createElement(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        try {
            final Document tempDoc = XMLUtilities.convertStringToDocument("<title>" + escapeForXML(titleValue) + "</title>");
            final Node titleEle = doc.importNode(tempDoc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                newTitle.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            newTitle.appendChild(doc.createTextNode(titleValue));
        }

        final NodeList titleNodes = docElement.getElementsByTagName(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        // see if we have a title node whose parent is the section
        if (titleNodes.getLength() != 0 && titleNodes.item(0).getParentNode().equals(docElement)) {
            final Node title = titleNodes.item(0);
            title.getParentNode().replaceChild(newTitle, title);
        } else {
            // Find the first node that isn't text or a comment
            Node firstNode = docElement.getFirstChild();
            while (firstNode != null && firstNode.getNodeType() != Node.ELEMENT_NODE) {
                firstNode = firstNode.getNextSibling();
            }

            // DocBook 5.0+ changed where the info node needs to be. In 5.0+ it is after the title, whereas 4.5 has to be before the title.
            if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                if (firstNode != null) {
                    docElement.insertBefore(newTitle, firstNode);
                } else {
                    docElement.appendChild(newTitle);
                }
            } else {
                // Set the section title based on if the first node is a "sectioninfo" node.
                if (firstNode != null && firstNode.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_SECTIONINFO_NODE_NAME)) {
                    final Node nextNode = firstNode.getNextSibling();
                    if (nextNode != null) {
                        docElement.insertBefore(newTitle, nextNode);
                    } else {
                        docElement.appendChild(newTitle);
                    }
                } else if (firstNode != null) {
                    docElement.insertBefore(newTitle, firstNode);
                } else {
                    docElement.appendChild(newTitle);
                }
            }
        }
    }

    public static void setRootElementTitle(final String titleValue, final Document doc) {
        assert doc != null : "The doc parameter can not be null";

        final Element newTitle = doc.createElement(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
        
        /* 
         * Attempt to parse the title as XML. If this fails
         * then just set the title as plain text.
         */
        try {
            final Document tempDoc = XMLUtilities.convertStringToDocument("<title>" + escapeForXML(titleValue) + "</title>");
            final Node titleEle = doc.importNode(tempDoc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                newTitle.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            newTitle.appendChild(doc.createTextNode(titleValue));
        }

        final Element docElement = doc.getDocumentElement();
        if (docElement != null) {
            final NodeList titleNodes = docElement.getElementsByTagName(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME);
            // See if we have a title node whose parent is the section
            if (titleNodes.getLength() != 0 && titleNodes.item(0).getParentNode().equals(docElement)) {
                final Node title = titleNodes.item(0);
                title.getParentNode().replaceChild(newTitle, title);
            } else {
                docElement.appendChild(newTitle);
            }
        }
    }

    /**
     * Escapes a String so that it can be used in a Docbook Element, ensuring that any entities or elements are maintained.
     *
     * @param content The string to be escaped.
     * @return The escaped string that can be used in XML.
     */
    public static String escapeForXML(final String content) {
        if (content == null) return "";

        /*
         * Note: The following characters should be escaped: & < > " '
         *
         * However, all but ampersand pose issues when other elements are included in the title.
         *
         * eg <title>Product A > Product B<phrase condition="beta">-Beta</phrase></title>
         *
         * should become
         *
         * <title>Product A &gt; Product B<phrase condition="beta">-Beta</phrase></title>
         */

        String fixedContent = content.replaceAll("&(?!\\S+?;)", "&amp;");

        // Loop over and find all the XML Elements as they should remain untouched.
        final LinkedList<String> elements = new LinkedList<String>();
        if (fixedContent.indexOf('<') != -1) {
            int index = -1;
            while ((index = fixedContent.indexOf('<', index + 1)) != -1) {
                int endIndex = fixedContent.indexOf('>', index);
                int nextIndex = fixedContent.indexOf('<', index + 1);

                /*
                  * If the next opening tag is less than the next ending tag, than the current opening tag isn't a match for the next
                  * ending tag, so continue to the next one
                  */
                if (endIndex == -1 || (nextIndex != -1 && nextIndex < endIndex)) {
                    continue;
                } else if (index + 1 == endIndex) {
                    // This is a <> sequence, so it should be ignored as well.
                    continue;
                } else {
                    elements.add(fixedContent.substring(index, endIndex + 1));
                }

            }
        }

        // Find all the elements and replace them with a marker
        String escapedTitle = fixedContent;
        for (int count = 0; count < elements.size(); count++) {
            escapedTitle = escapedTitle.replace(elements.get(count), "###" + count + "###");
        }

        // Perform the replacements on what's left
        escapedTitle = escapedTitle.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");

        // Replace the markers
        for (int count = 0; count < elements.size(); count++) {
            escapedTitle = escapedTitle.replace("###" + count + "###", elements.get(count));
        }

        return escapedTitle;
    }

    public static void setInfo(final DocBookVersion docBookVersion, final Element info, final Element parentNode) {
        assert parentNode != null : "The parentNode parameter can not be null";
        assert info != null : "The info parameter can not be null";

        if (parentNode != null) {
            final NodeList infoNodes = parentNode.getElementsByTagName(info.getNodeName());
            // See if we have an info node whose parent is the section
            if (infoNodes.getLength() != 0 && infoNodes.item(0).getParentNode().equals(parentNode)) {
                final Node sectionInfoNode = infoNodes.item(0);
                sectionInfoNode.getParentNode().replaceChild(info, sectionInfoNode);
            } else {
                // Find the first node that isn't text or a comment
                Node firstNode = parentNode.getFirstChild();
                while (firstNode != null && firstNode.getNodeType() != Node.ELEMENT_NODE) {
                    firstNode = firstNode.getNextSibling();
                }

                // DocBook 5.0+ changed where the info node needs to be. In 5.0+ it is after the title, whereas 4.5 has to be before the title.
                if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                    // Set the section title based on if the first node is a info node.
                    if (firstNode != null && firstNode.getNodeName().equals(DocBookUtilities.TOPIC_ROOT_TITLE_NODE_NAME)) {
                        final Node nextNode = firstNode.getNextSibling();
                        if (nextNode != null) {
                            parentNode.insertBefore(info, nextNode);
                        } else {
                            parentNode.appendChild(info);
                        }
                    } else if (firstNode != null) {
                        parentNode.insertBefore(info, firstNode);
                    } else {
                        parentNode.appendChild(info);
                    }
                } else {
                    if (firstNode != null) {
                        parentNode.insertBefore(info, firstNode);
                    } else {
                        parentNode.appendChild(info);
                    }
                }
            }
        }
    }

    public static String buildChapter(final String contents, final String title) {
        return buildChapter(contents, title, null);
    }

    public static String buildChapter(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<chapter" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</chapter>";
    }

    public static String buildAppendix(final String contents, final String title) {
        return buildAppendix(contents, title, null);
    }

    public static String buildAppendix(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<appendix" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</appendix>";
    }

    public static String buildCleanSection(final String contents, final String title) {
        return buildCleanSection(contents, title, null);
    }

    public static String buildCleanSection(final String contents, final String title, final String id) {
        final String titleContents = title == null || title.length() == 0 ? "" : title;
        final String chapterContents = contents == null || contents.length() == 0 ? "" : contents;
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        return "<section" + idAttribute + "><title>" + titleContents + "</title>" + chapterContents + "</section>";
    }

    public static String addDocBook45Doctype(final String xml) {
        return addDocBook45Doctype(xml, null, "chapter");
    }

    public static String addDocBook45Doctype(final String xml, final String entityFileName, final String rootElementName) {
        return XMLUtilities.addPublicDoctype(xml, "-//OASIS//DTD DocBook XML V4.5//EN",
                "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd", entityFileName, rootElementName);
    }

    public static String addDocBook50Doctype(final String xml) {
        return addDocBook50Doctype(xml, null, "chapter");
    }

    public static String addDocBook50Doctype(final String xml, final String entityFileName, final String rootElementName) {
        return XMLUtilities.addPublicDoctype(xml, "-//OASIS//DTD DocBook XML V5.0//EN",
                "http://www.oasis-open.org/docbook/xml/5.0/docbookx.dtd", entityFileName, rootElementName);
    }

    public static String addDocBook50Namespace(final String xml) {
        // Find the root element name
        final String rootEleName = XMLUtilities.findRootElementName(xml);
        return addDocBook50Namespace(xml, rootEleName);
    }

    public static String addDocBook50Namespace(final String xml, final String rootElementName) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final Pattern pattern = Pattern.compile("(?<ELEMENT><" + rootElementName + ".*?)>");
        final Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            final String element = matcher.group("ELEMENT");
            // Remove any current namespace declaration
            String fixedElement = element.replaceFirst(" xmlns\\s*=\\s*('|\").*?('|\")", "");
            // Remove any current version declaration
            fixedElement = fixedElement.replaceFirst(" version\\s*=\\s*('|\").*?('|\")", "");
            // Remove any current xlink namespace declaration
            fixedElement = fixedElement.replaceFirst(" xmlns:xlink\\s*=\\s*('|\").*?('|\")", "");
            return xml.replaceFirst(element, fixedElement + " xmlns=\"http://docbook.org/ns/docbook\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                    " version=\"5.0\"");
        } else {
            return xml;
        }
    }

    /**
     * Some docbook elements need to be wrapped up so they can be properly transformed by the docbook XSL.
     * @param xmlDoc
     */
    public static void wrapUpDocbookElementsForRendering(final Document xmlDoc) {
        /*
            Some topics need to be wrapped up to be rendered properly
         */
        final String documentElementNodeName = xmlDoc.getDocumentElement().getNodeName();
        if (documentElementNodeName.equals("authorgroup") || documentElementNodeName.equals("legalnotice")) {
            final Element currentChild = xmlDoc.createElement(documentElementNodeName);

            xmlDoc.renameNode(xmlDoc.getDocumentElement(), null, "book");
            final Element bookInfo = xmlDoc.createElement("bookinfo");
            xmlDoc.getDocumentElement().appendChild(bookInfo);
            bookInfo.appendChild(currentChild);

            final NodeList existingChildren = xmlDoc.getDocumentElement().getChildNodes();
            for (int childIndex = 0; childIndex < existingChildren.getLength(); ++childIndex) {
                final Node child = existingChildren.item(childIndex);
                if (child != bookInfo) {
                    currentChild.appendChild(child);
                }
            }
        }
    }

    public static void addNamespaceToDocElement(final Integer format, final Document doc) {
        if (format == null) throw new IllegalArgumentException("format cannot be null");
        if (format == CommonConstants.DOCBOOK_50) {
            addDocBook50NamespaceToDocElement(doc);
        }
    }

    public static void addDocBook50NamespaceToDocElement(final Document doc) {
        doc.getDocumentElement().setAttribute("xmlns", "http://docbook.org/ns/docbook");
        doc.getDocumentElement().setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        doc.getDocumentElement().setAttribute("version", "5.0");
    }

    public static String buildDocbookDoctype(final Integer format, final String rootElementName, final String entities) {
        if (format == null) throw new IllegalArgumentException("format cannot be null");

        if (format == CommonConstants.DOCBOOK_45) {
            return buildDocbook45Doctype(rootElementName, entities);
        }

        if (format == CommonConstants.DOCBOOK_50) {
            return buildDocbook50Doctype(rootElementName, entities);
        }

        return "";
    }

    public static String buildDocbook50Doctype(final String rootElementName, final String entities) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final StringBuilder retValue = new StringBuilder();
        retValue.append("<!DOCTYPE " + rootElementName + " PUBLIC \"-//OASIS//DTD DocBook XML V5.0//EN\" \"http://www.oasis-open.org/docbook/xml/5.0/docbookx.dtd\" [\n");
        if (entities != null) {
            retValue.append(entities);
        }
        retValue.append("]>\n");
        return retValue.toString();
    }

    public static String buildDocbook45Doctype(final String rootElementName, final String entities) {
        if (rootElementName == null) throw new IllegalArgumentException("rootElementName cannot be null");
        final StringBuilder retValue = new StringBuilder();
        retValue.append("<!DOCTYPE " + rootElementName + " PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\" [\n");
        if (entities != null) {
            retValue.append(entities);
        }
        retValue.append("]>\n");
        return retValue.toString();
    }

    public static String buildXRefListItem(final String xref, final String role) {
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        return "<listitem><para><xref" + roleAttribute + " linkend=\"" + xref + "\"/></para></listitem>";
    }

    public static List<Element> buildXRef(final Document xmlDoc, final String xref) {
        return buildXRef(xmlDoc, xref, null);
    }

    public static List<Element> buildXRef(final Document xmlDoc, final String xref, final String xrefStyle) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);

        if (xrefStyle != null && !xrefStyle.isEmpty()) {
            xrefItem.setAttribute("xrefstyle", xrefStyle);
        }

        retValue.add(xrefItem);

        return retValue;
    }

    public static String buildXRef(final String xref) {
        return "<xref linkend=\"" + xref + "\" />";
    }

    public static String buildXRef(final String xref, final String xrefStyle) {
        return "<xref linkend=\"" + xref + "\" xrefstyle=\"" + xrefStyle + "\" />";
    }

    public static List<Element> buildULink(final Document xmlDoc, final String url, final String label) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element ulinkItem = xmlDoc.createElement("ulink");
        ulinkItem.setAttribute("url", url);

        final Text labelElement = xmlDoc.createTextNode(label);
        ulinkItem.appendChild(labelElement);

        retValue.add(ulinkItem);

        return retValue;
    }

    public static String buildULink(final String url, final String label) {
        return "<ulink url=\"" + url + "\">" + label + "</ulink>";
    }

    public static String buildULinkListItem(final String url, final String label) {
        return "<listitem><para><ulink url=\"" + url + "\">" + label + "</ulink></para></listitem>";
    }

    public static List<Element> buildEmphasisPrefixedXRef(final Document xmlDoc, final String prefix, final String xref) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element emphasis = xmlDoc.createElement("emphasis");
        emphasis.setTextContent(prefix);
        retValue.add(emphasis);

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);
        retValue.add(xrefItem);

        return retValue;
    }

    public static List<Element> buildEmphasisPrefixedULink(final Document xmlDoc, final String prefix, final String url,
            final String label) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element emphasis = xmlDoc.createElement("emphasis");
        emphasis.setTextContent(prefix);
        retValue.add(emphasis);

        final Element xrefItem = xmlDoc.createElement("ulink");
        xrefItem.setAttribute("url", url);
        retValue.add(xrefItem);

        final Text labelElement = xmlDoc.createTextNode(label);
        xrefItem.appendChild(labelElement);

        return retValue;
    }

    public static Node buildDOMXRefLinkListItem(final String xref, final String title, final Document xmlDoc) {
        final Element listItem = xmlDoc.createElement("listitem");

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element linkItem = xmlDoc.createElement("link");
        linkItem.setAttribute("linkend", xref);
        linkItem.setTextContent(title);
        paraItem.appendChild(linkItem);

        return listItem;
    }

    public static Node buildDOMLinkListItem(final List<Node> children, final Document xmlDoc) {
        final Element listItem = xmlDoc.createElement("listitem");

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        for (final Node node : children)
            paraItem.appendChild(node);

        return listItem;
    }

    public static Node buildDOMXRef(final String xref, final String title, final Document xmlDoc) {
        final Element linkItem = xmlDoc.createElement("link");
        linkItem.setAttribute("linkend", xref);
        linkItem.setTextContent(title);
        return linkItem;
    }

    public static Node buildDOMText(final String title, final Document xmlDoc) {
        final Node textNode = xmlDoc.createTextNode(title);
        return textNode;
    }

    public static String buildListItem(final String text) {
        return "<listitem><para>" + text + "</para></listitem>\n";
    }

    public static Element buildDOMListItem(final Document doc, final String text) {
        final Element listItem = doc.createElement("listitem");
        final Element para = doc.createElement("para");
        para.setTextContent(text);
        listItem.appendChild(para);
        return listItem;
    }

    public static String buildSection(final String contents, final String title) {
        return buildSection(contents, title, null, null, null);
    }

    public static String buildSection(final String contents, final String title, final String id) {
        return buildSection(contents, title, id, null, null);
    }

    public static String buildSection(final String contents, final String title, final String id, final String titleRole) {
        return buildSection(contents, title, id, titleRole, null);
    }

    public static String buildSection(final String contents, final String title, final String id, final String titleRole,
            final String xreflabel) {
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        final String xreflabelAttribute = xreflabel == null || xreflabel.length() == 0 ? "" : " xreflabel=\"" + xreflabel + "\"";
        final String titleRoleAttribute = titleRole == null || titleRole.length() == 0 ? "" : " role=\"" + titleRole + "\"";

        return "<section" + idAttribute + xreflabelAttribute + ">\n" +
                "<title" + titleRoleAttribute + ">" + title + "</title>\n" +
                contents + "\n" +
                "</section>\n";
    }

    public static String wrapInListItem(final String content) {
        return "<listitem>" + content + "</listitem>";
    }

    public static String wrapListItems(final List<String> listItems) {
        return wrapListItems(null, listItems, null, null);
    }

    public static String wrapListItems(final List<String> listItems, final String title) {
        return wrapListItems(null, listItems, title, null);
    }

    public static String wrapListItems(final DocBookVersion docBookVersion, final List<String> listItems, final String title,
            final String id) {
        final String idAttribute;
        if (docBookVersion == DocBookVersion.DOCBOOK_50) {
            idAttribute = id != null && id.length() != 0 ? " xml:id=\"" + id + "\" " : "";
        } else {
            idAttribute = id != null && id.length() != 0 ? " id=\"" + id + "\" " : "";
        }
        final String titleElement = title == null || title.length() == 0 ? "" : "<title>" + title + "</title>";

        final StringBuilder retValue = new StringBuilder("<itemizedlist" + idAttribute + ">" + titleElement);
        for (final String listItem : listItems)
            retValue.append(listItem);
        retValue.append("</itemizedlist>");

        return retValue.toString();
    }

    public static String wrapListItemsInPara(final String listItems) {
        if (listItems.length() != 0) {
            return "<para>" +
                    "<itemizedlist>\n" + listItems + "</itemizedlist>" +
                    "</para>";
        }

        return "";
    }

    public static String wrapInPara(final String contents) {
        return wrapInPara(contents, null, null);
    }

    public static String wrapInPara(final String contents, final String role) {
        return wrapInPara(contents, role, null);
    }

    public static String wrapInPara(final String contents, final String role, final String id) {
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        return "<para" + idAttribute + roleAttribute + ">" +
                contents +
                "</para>";
    }

    public static List<Element> wrapItemizedListItemsInPara(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element para = xmlDoc.createElement("para");

        final Element itemizedlist = xmlDoc.createElement("itemizedlist");
        para.appendChild(itemizedlist);

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            itemizedlist.appendChild(listitem);

            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }
        }

        retValue.add(para);

        return retValue;
    }

    public static List<Element> wrapOrderedListItemsInPara(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        final Element para = xmlDoc.createElement("para");

        final Element orderedlist = xmlDoc.createElement("orderedlist");
        para.appendChild(orderedlist);

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            orderedlist.appendChild(listitem);

            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }
        }

        retValue.add(para);

        return retValue;
    }

    public static List<Element> wrapItemsInListItems(final Document xmlDoc, final List<List<Element>> items) {
        final List<Element> retValue = new ArrayList<Element>();

        for (final List<Element> itemSequence : items) {
            final Element listitem = xmlDoc.createElement("listitem");
            final Element listItemPara = xmlDoc.createElement("para");
            listitem.appendChild(listItemPara);

            for (final Element item : itemSequence) {
                listItemPara.appendChild(item);
            }

            retValue.add(listitem);
        }

        return retValue;
    }

    public static String wrapInSimpleSect(final String contents) {
        return wrapInSimpleSect(contents, null, null);
    }

    public static String wrapInSimpleSect(final String contents, final String role) {
        return wrapInSimpleSect(contents, null, null);
    }

    public static String wrapInSimpleSect(final String contents, final String role, final String id) {
        final String roleAttribute = role == null || role.length() == 0 ? "" : " role=\"" + role + "\"";
        final String idAttribute = id == null || id.length() == 0 ? "" : " id=\"" + id + "\"";

        return "<simplesect" + idAttribute + roleAttribute + ">\n" +
                "\t<title></title>\n" +
                contents + "\n" +
                "</simplesect>";
    }

    public static Element wrapListItems(final Document xmlDoc, final List<Node> listItems) {
        return wrapListItems(xmlDoc, listItems, null);
    }

    public static Element wrapListItems(final Document xmlDoc, final List<Node> listItems, final String title) {
        final Element paraElement = xmlDoc.createElement("para");

        final Element itemizedlistElement = xmlDoc.createElement("itemizedlist");
        paraElement.appendChild(itemizedlistElement);

        if (title != null) {
            final Element titleElement = xmlDoc.createElement("title");
            itemizedlistElement.appendChild(titleElement);
            titleElement.setTextContent(title);
        }

        for (final Node listItem : listItems)
            itemizedlistElement.appendChild(listItem);

        return paraElement;
    }

    public static void insertNodeAfter(final Node reference, final Node insert) {
        final Node parent = reference.getParentNode();
        final Node nextSibling = reference.getNextSibling();

        if (parent == null) return;

        if (nextSibling != null) parent.insertBefore(insert, nextSibling);
        else parent.appendChild(insert);
    }

    public static Node createRelatedTopicXRef(final Document xmlDoc, final String xref, final Node parent) {
        final Element listItem = xmlDoc.createElement("listitem");
        if (parent != null) parent.appendChild(listItem);

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element xrefItem = xmlDoc.createElement("xref");
        xrefItem.setAttribute("linkend", xref);
        paraItem.appendChild(xrefItem);

        return listItem;
    }

    public static Node createRelatedTopicXRef(final Document xmlDoc, final String xref) {
        return createRelatedTopicXRef(xmlDoc, xref, null);
    }

    public static Node createRelatedTopicULink(final Document xmlDoc, final String url, final String title, final Node parent) {
        final Element listItem = xmlDoc.createElement("listitem");
        if (parent != null) parent.appendChild(listItem);

        final Element paraItem = xmlDoc.createElement("para");
        listItem.appendChild(paraItem);

        final Element xrefItem = xmlDoc.createElement("ulink");
        xrefItem.setAttribute("url", url);
        paraItem.appendChild(xrefItem);

        // Attempt to parse the title as XML. If this fails then just set the title as plain text.
        try {
            final Document doc = XMLUtilities.convertStringToDocument("<title>" + title + "</title>");
            final Node titleEle = xmlDoc.importNode(doc.getDocumentElement(), true);

            // Add the child elements to the ulink node
            final NodeList nodes = titleEle.getChildNodes();
            while (nodes.getLength() > 0) {
                xrefItem.appendChild(nodes.item(0));
            }
        } catch (Exception e) {
            final Text labelElement = xmlDoc.createTextNode(title);
            xrefItem.appendChild(labelElement);
        }

        return listItem;
    }

    public static Node createRelatedTopicULink(final Document xmlDoc, final String url, final String title) {
        return createRelatedTopicULink(xmlDoc, url, title, null);
    }

    public static Node createRelatedTopicItemizedList(final Document xmlDoc, final String title) {
        final Node itemizedlist = xmlDoc.createElement("itemizedlist");
        final Node itemizedlistTitle = xmlDoc.createElement("title");
        itemizedlistTitle.setTextContent(title);
        itemizedlist.appendChild(itemizedlistTitle);

        return itemizedlist;
    }

    public static Document wrapDocumentInSection(final Document doc) {
        return wrapDocument(doc, "section");
    }

    public static Document wrapDocumentInAppendix(final Document doc) {
        return wrapDocument(doc, "appendix");
    }

    public static Document wrapDocumentInLegalNotice(final Document doc) {
        return wrapDocument(doc, "legalnotice");
    }

    public static Document wrapDocumentInAuthorGroup(final Document doc) {
        return wrapDocument(doc, "authorgroup");
    }

    public static Document wrapDocument(final Document doc, final String elementName) {
        if (!doc.getDocumentElement().getNodeName().equals(elementName)) {
            final Element originalDocumentElement = doc.getDocumentElement();
            final Element newDocumentElement;
            if (doc.getDocumentElement().getNamespaceURI() != null) {
                newDocumentElement = doc.createElementNS(doc.getDocumentElement().getNamespaceURI(), elementName);
            } else {
                newDocumentElement = doc.createElement(elementName);
            }

            // Copy all children
            NodeList children = originalDocumentElement.getChildNodes();
            while (children.getLength() != 0) {
                newDocumentElement.appendChild(children.item(0));
            }

            // Copy all the attributes
            NamedNodeMap attrs = originalDocumentElement.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                final Attr attr = (Attr) attrs.item(i);
                originalDocumentElement.removeAttributeNode(attr);
                newDocumentElement.setAttributeNode(attr);
            }

            // Replace the original element
            doc.replaceChild(newDocumentElement, originalDocumentElement);

            return doc;
        } else {
            return doc;
        }
    }

    /**
     * Wrap a list of Strings in a {@code<row>} element. Each string
     * is also wrapped in a {@code<entry>} element.
     *
     * @param items The list of items to be set in the table row.
     * @return The strings wrapped in row and entry elements.
     */
    public static String wrapInTableRow(final List<String> items) {
        final StringBuilder output = new StringBuilder("<row>");
        for (final String entry : items) {
            output.append("<entry>" + entry + "</entry>");
        }

        output.append("</row>");
        return output.toString();
    }

    public static String wrapInTable(final String title, final List<List<String>> rows) {
        return wrapInTable(title, null, null, rows);
    }

    public static String wrapInTable(final String title, final List<String> headers, final List<List<String>> rows) {
        return wrapInTable(title, headers, null, rows);
    }

    public static String wrapInTable(final String title, final List<String> headers, final List<String> footers,
            final List<List<String>> rows) {
        if (rows == null) throw new IllegalArgumentException("rows cannot be null");

        final StringBuilder output = new StringBuilder("<table>\n");
        output.append("\t<title>" + title + "</title>\n");

        final int numColumns = headers == null ? (rows == null || rows.size() == 0 ? 0 : rows.get(0).size()) : Math.max(headers.size(),
                (rows == null || rows.size() == 0 ? 0 : rows.get(0).size()));
        output.append("\t<tgroup cols=\"" + numColumns + "\">\n");
        // Add the headers
        if (headers != null && !headers.isEmpty()) {
            output.append("\t\t<thead>\n");
            output.append("\t\t\t" + wrapInTableRow(headers));
            output.append("\t\t</thead>\n");
        }

        // Add the footer
        if (footers != null && !footers.isEmpty()) {
            output.append("\t\t<tfoot>\n");
            output.append("\t\t\t" + wrapInTableRow(footers));
            output.append("\t\t</tfoot>\n");
        }

        // Create the table body
        output.append("\t\t<tbody>\n");
        for (final List<String> row : rows) {
            output.append("\t\t\t" + wrapInTableRow(row));
        }
        output.append("\t\t</tbody>\n");
        output.append("\t</tgroup>\n");
        output.append("</table>\n");
        return output.toString();
    }

    public static String wrapInGlossTerm(final String glossTerm) {
        return "<glossterm>" + glossTerm + "</glossterm>";
    }

    /**
     * Creates a Glossary Definition element that contains an itemized list.
     * Each item specified in the items list is wrapped in a {@code<para>} and
     * {@code<listitem>} element and then added to the itemizedlist.
     *
     * @param title The title for the itemized list.
     * @param items The list of items that should be created in the list.
     * @return The {@code<glossdef>} wrapped list of items.
     */
    public static String wrapInItemizedGlossDef(final String title, final List<String> items) {
        final List<String> itemizedList = new ArrayList<String>();
        for (final String listItem : items) {
            itemizedList.add(wrapInListItem(wrapInPara(listItem)));
        }
        return "<glossdef>" + DocBookUtilities.wrapListItems(itemizedList) + "</glossdef>";
    }

    public static String wrapInGlossEntry(final String glossTerm, final String glossDef) {
        return "<glossentry>" + glossTerm + glossDef + "</glossentry>";
    }

    /**
     * Check to ensure that a table isn't missing any entries in its rows.
     *
     * @param table The DOM table node to be checked.
     * @return True if the table has the required number of entries, otherwise false.
     */
    public static boolean validateTableRows(final Element table) {
        assert table != null;
        assert table.getNodeName().equals("table") || table.getNodeName().equals("informaltable");

        final NodeList tgroups = table.getElementsByTagName("tgroup");
        for (int i = 0; i < tgroups.getLength(); i++) {
            final Element tgroup = (Element) tgroups.item(i);
            if (!validateTableGroup(tgroup)) return false;
        }

        return true;
    }

    /**
     * Check to ensure that a Docbook tgroup isn't missing an row entries, using number of cols defined for the tgroup.
     *
     * @param tgroup The DOM tgroup element to be checked.
     * @return True if the tgroup has the required number of entries, otherwise false.
     */
    public static boolean validateTableGroup(final Element tgroup) {
        assert tgroup != null;
        assert tgroup.getNodeName().equals("tgroup");

        final Integer numColumns = Integer.parseInt(tgroup.getAttribute("cols"));

        // Check that all the thead, tbody and tfoot elements have the correct number of entries.
        final List<Node> nodes = XMLUtilities.getDirectChildNodes(tgroup, "thead", "tbody", "tfoot");
        for (final Node ele : nodes) {
            // Find all child nodes that are a row
            final NodeList children = ele.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node node = children.item(i);
                if (node.getNodeName().equals("row") || node.getNodeName().equals("tr")) {
                    if (!validateTableRow(node, numColumns)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Check to ensure that a docbook row has the required number of columns for a table.
     *
     * @param row        The DOM row element to be checked.
     * @param numColumns The number of entry elements that should exist in the row.
     * @return True if the row has the required number of entries, otherwise false.
     */
    public static boolean validateTableRow(final Node row, final int numColumns) {
        assert row != null;
        assert row.getNodeName().equals("row") || row.getNodeName().equals("tr");

        if (row.getNodeName().equals("row")) {
            final List<Node> entries = XMLUtilities.getDirectChildNodes(row, "entry");
            final List<Node> entryTbls = XMLUtilities.getDirectChildNodes(row, "entrytbl");

            if ((entries.size() + entryTbls.size()) <= numColumns) {
                for (final Node entryTbl : entryTbls) {
                    if (!validateEntryTbl((Element) entryTbl)) return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            final List<Node> nodes = XMLUtilities.getDirectChildNodes(row, "td", "th");

            return nodes.size() <= numColumns;
        }
    }

    /**
     * Check to ensure that a Docbook entrytbl isn't missing an row entries, using number of cols defined for the entrytbl.
     *
     * @param entryTbl The DOM entrytbl element to be checked.
     * @return True if the entryTbl has the required number of entries, otherwise false.
     */
    public static boolean validateEntryTbl(final Element entryTbl) {
        assert entryTbl != null;
        assert entryTbl.getNodeName().equals("entrytbl");

        final Integer numColumns = Integer.parseInt(entryTbl.getAttribute("cols"));

        // Check that all the thead and tbody elements have the correct number of entries.
        final List<Node> nodes = XMLUtilities.getDirectChildNodes(entryTbl, "thead", "tbody");
        for (final Node ele : nodes) {
            // Find all child nodes that are a row
            final NodeList children = ele.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node node = children.item(i);
                if (node.getNodeName().equals("row") || node.getNodeName().equals("tr")) {
                    if (!validateTableRow(node, numColumns)) return false;
                }
            }
        }

        return true;
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition The condition regex to be tested against.
     * @param doc       The Document to check for conditional statements.
     */
    public static void processConditions(final String condition, final Document doc) {
        processConditions(condition, doc, "default");
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition        The condition regex to be tested against.
     * @param doc              The Document to check for conditional statements.
     * @param defaultCondition The default condition to allow a default block when processing conditions.
     */
    public static void processConditions(final String condition, final Document doc, final String defaultCondition) {
        processConditions(condition, doc, defaultCondition, true);
    }

    /**
     * Check the XML Document and it's children for condition
     * statements. If any are found then check if the condition
     * matches the passed condition string. If they don't match
     * then remove the nodes.
     *
     * @param condition           The condition regex to be tested against.
     * @param doc                 The Document to check for conditional statements.
     * @param defaultCondition    The default condition to allow a default block when processing conditions.
     * @param removeConditionAttr Remove the condition attribute from any matching/leftover nodes.
     */
    public static void processConditions(final String condition, final Document doc, final String defaultCondition,
            boolean removeConditionAttr) {
        final Map<Node, List<String>> conditionalNodes = getConditionNodes(doc.getDocumentElement());

        // Loop through each condition found and see if it matches
        for (final Map.Entry<Node, List<String>> entry : conditionalNodes.entrySet()) {
            final Node node = entry.getKey();
            final List<String> nodeConditions = entry.getValue();
            boolean matched = false;

            // Check to see if the condition matches
            for (final String nodeCondition : nodeConditions) {
                if (condition != null && nodeCondition.matches(condition)) {
                    matched = true;
                } else if (condition == null && defaultCondition != null && nodeCondition.matches(defaultCondition)) {
                    matched = true;
                }
            }

            // If there was no match then remove the node
            if (!matched) {
                final Node parentNode = node.getParentNode();
                if (parentNode != null) {
                    parentNode.removeChild(node);
                }
            } else if (removeConditionAttr) {
                // Remove the condition attribute so that it can't get processed by something else downstream
                ((Element) node).removeAttribute("condition");
            }
        }
    }

    /**
     * Collects any nodes that have the "condition" attribute in the
     * passed node or any of it's children nodes.
     *
     * @param node The node to collect condition elements from.
     * @return A mapping of nodes to their conditions.
     */
    public static Map<Node, List<String>> getConditionNodes(final Node node) {
        final Map<Node, List<String>> conditionalNodes = new HashMap<Node, List<String>>();
        getConditionNodes(node, conditionalNodes);
        return conditionalNodes;
    }

    /**
     * Collects any nodes that have the "condition" attribute in the
     * passed node or any of it's children nodes.
     *
     * @param node             The node to collect condition elements from.
     * @param conditionalNodes A mapping of nodes to their conditions
     */
    private static void getConditionNodes(final Node node, final Map<Node, List<String>> conditionalNodes) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Node attr = attributes.getNamedItem("condition");

            if (attr != null) {
                final String conditionStatement = attr.getNodeValue();

                final String[] conditions = conditionStatement.split("\\s*(;|,)\\s*");

                conditionalNodes.put(node, Arrays.asList(conditions));
            }
        }

        // Check the child nodes for condition attributes
        final NodeList elements = node.getChildNodes();
        for (int i = 0; i < elements.getLength(); ++i) {
            getConditionNodes(elements.item(i), conditionalNodes);
        }
    }

    /**
     * Get the Translatable Strings from an XML Document. This method will return of Translation strings to XML DOM nodes within
     * the XML Document. <br />
     * <br />
     * Note: This function has a flaw when breaking up strings if the Child Nodes contain translatable elements.
     *
     * @param xml             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    @Deprecated
    public static List<StringToNodeCollection> getTranslatableStringsV1(final Document xml, final boolean allowDuplicates) {
        if (xml == null) return null;

        final List<StringToNodeCollection> retValue = new ArrayList<StringToNodeCollection>();

        final NodeList nodes = xml.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            getTranslatableStringsFromNodeV1(node, retValue, allowDuplicates, new XMLProperties());
        }

        return retValue;
    }

    /**
     * Get the Translatable Strings from an XML Document. This method will return of Translation strings to XML DOM nodes within
     * the XML Document.
     *
     * @param xml             The XML to get the translatable strings from.
     * @param allowDuplicates If duplicate translation strings should be created in the returned list.
     * @return A list of StringToNodeCollection objects containing the translation strings and nodes.
     */
    public static List<StringToNodeCollection> getTranslatableStringsV2(final Document xml, final boolean allowDuplicates) {
        if (xml == null) return null;

        final List<StringToNodeCollection> retValue = new ArrayList<StringToNodeCollection>();

        final NodeList nodes = xml.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            getTranslatableStringsFromNodeV2(node, retValue, allowDuplicates, new XMLProperties());
        }

        return retValue;
    }

    /**
     * Check if a node has child translatable elements.
     *
     * @param node The node to check for child translatable elements.
     * @return True if the node has translatable child Elements.
     */
    @Deprecated
    private static boolean doesElementContainTranslatableContentV1(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            /* check to see if any of the children are translatable nodes */
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();

                /* this child node is itself translatable, so return true */
                if (TRANSLATABLE_ELEMENTS.contains(childName)) return true;
            }

            /*
             * now check to see if any of the child have children that are translatable
             */
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final NodeList grandChildren = child.getChildNodes();
                for (int k = 0; k < grandChildren.getLength(); ++k) {
                    final Node grandChild = grandChildren.item(k);
                    final boolean result = doesElementContainTranslatableContentV1(grandChild);
                    if (result) return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if a node has child translatable elements.
     *
     * @param node The node to check for child translatable elements.
     * @return True if the node has translatable child Elements.
     */
    private static boolean doesElementContainTranslatableContentV2(final Node node) {
        final NodeList children = node.getChildNodes();
        if (children != null) {
            // check to see if any of the children are translatable nodes
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                final String childName = child.getNodeName();

                if (TRANSLATABLE_ELEMENTS.contains(childName)) {
                    // This child node is itself translatable, so return true
                    return true;
                } else if (doesElementContainTranslatableContentV2(child)) {
                    // check if this child contains translatable nodes
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the Translatable String to Node collections from an XML DOM Node.
     *
     * @param node               The node to get the translatable elements from.
     * @param translationStrings The list of translation StringToNodeCollection objects to add to.
     * @param allowDuplicates    If duplicate translation strings should be created in the translationStrings list.
     * @param props              A set of XML Properties for the Node.
     */
    @Deprecated
    private static void getTranslatableStringsFromNodeV1(final Node node, final List<StringToNodeCollection> translationStrings,
            final boolean allowDuplicates, final XMLProperties props) {
        if (node == null || translationStrings == null) return;

        XMLProperties xmlProperties = new XMLProperties(props);

        final String nodeName = node.getNodeName();
        final String nodeParentName = node.getParentNode() != null ? node.getParentNode().getNodeName() : null;

        final boolean translatableElement = TRANSLATABLE_ELEMENTS.contains(nodeName);
        final boolean standaloneElement = TRANSLATABLE_IF_STANDALONE_ELEMENTS.contains(nodeName);
        final boolean translatableParentElement = TRANSLATABLE_ELEMENTS.contains(nodeParentName);
        if (!xmlProperties.isInline() && INLINE_ELEMENTS.contains(nodeName)) xmlProperties.setInline(true);
        if (!xmlProperties.isVerbatim() && VERBATIM_ELEMENTS.contains(nodeName)) xmlProperties.setVerbatim(true);

        /*
         * this element has translatable strings if:
         *
         * 1. a translatableElement
         *
         * OR
         *
         * 2. a standaloneElement without a translatableParentElement
         *
         * 3. not a standaloneElement and not an inlineElement
         */

        if ((translatableElement && ((standaloneElement && !translatableParentElement) || (!standaloneElement && !xmlProperties.isInline
                ())))) {
            final NodeList children = node.getChildNodes();
            final boolean hasChildren = children == null || children.getLength() != 0;

            /* dump the node if it has no children */
            if (!hasChildren) {
                final String nodeText = XMLUtilities.convertNodeToString(node, false);
                final String cleanedNodeText = cleanTranslationText(nodeText, true, true);

                if (xmlProperties.isVerbatim()) {
                    addTranslationToNodeDetailsToCollection(nodeText, node, allowDuplicates, translationStrings);
                } else if (!cleanedNodeText.isEmpty()) {
                    addTranslationToNodeDetailsToCollection(cleanedNodeText, node, allowDuplicates, translationStrings);
                }

            }
            /*
             * dump all child nodes until we hit one that itself contains a translatable element. in effect the translation
             * strings can contain up to one level of xml elements.
             */
            else {
                ArrayList<Node> nodes = new ArrayList<Node>();
                String translatableString = "";

                final int childrenLength = children.getLength();
                for (int i = 0; i < childrenLength; ++i) {
                    final Node child = children.item(i);

                    /*
                     * does this child have another level of translatable tags?
                     */
                    final boolean containsTranslatableTags = doesElementContainTranslatableContentV1(child);

                    /*
                     * if so, save the string we have been building up, process the child, and start building up a new string
                     */
                    if (containsTranslatableTags) {
                        if (nodes.size() != 0) {
                            /*
                             * We have found a child node that itself contains some translatable children. In this case we
                             * create a new translatable string. It is possible that the translatableString has some
                             * insignificant trailing whitespace, because the call to the cleanTranslationText function in the
                             * else statement below has assumed that the node being processed was not the last one in the
                             * translatable string, making the trailing whitespace important. So we clean up the trailing
                             * whitespace here.
                             */

                            final Matcher matcher = XMLUtilities.TRAILING_WHITESPACE_RE_PATTERN.matcher(translatableString);
                            if (matcher.matches()) translatableString = matcher.group("content");

                            addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                            translatableString = "";
                            nodes = new ArrayList<Node>();
                        }

                        getTranslatableStringsFromNodeV1(child, translationStrings, allowDuplicates, xmlProperties);
                    } else {
                        final String childName = child.getNodeName();
                        final String childText = XMLUtilities.convertNodeToString(child, true);

                        final String cleanedChildText = cleanTranslationText(childText, i == 0, i == childrenLength - 1);
                        final boolean isVerbatimNode = VERBATIM_ELEMENTS.contains(childName);

                        final String thisTranslatableString = isVerbatimNode || xmlProperties.isVerbatim() ? childText : cleanedChildText;

                        translatableString += thisTranslatableString;
                        nodes.add(child);
                    }
                }

                /* save the last translated string */
                if (nodes.size() != 0) {
                    addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                    translatableString = "";
                }
            }
        } else {
            /* if we hit a non-translatable element, process its children */
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node child = nodeList.item(i);
                getTranslatableStringsFromNodeV1(child, translationStrings, allowDuplicates, xmlProperties);
            }
        }
    }

    /**
     * Get the Translatable String to Node collections from an XML DOM Node.
     *
     * @param node               The node to get the translatable elements from.
     * @param translationStrings The list of translation StringToNodeCollection objects to add to.
     * @param allowDuplicates    If duplicate translation strings should be created in the translationStrings list.
     * @param props              A set of XML Properties for the Node.
     */
    private static void getTranslatableStringsFromNodeV2(final Node node, final List<StringToNodeCollection> translationStrings,
            final boolean allowDuplicates, final XMLProperties props) {
        if (node == null || translationStrings == null) return;

        XMLProperties xmlProperties = new XMLProperties(props);

        final String nodeName = node.getNodeName();
        final String nodeParentName = node.getParentNode() != null ? node.getParentNode().getNodeName() : null;

        final boolean translatableElement = TRANSLATABLE_ELEMENTS.contains(nodeName);
        final boolean standaloneElement = TRANSLATABLE_IF_STANDALONE_ELEMENTS.contains(nodeName);
        final boolean translatableParentElement = TRANSLATABLE_ELEMENTS.contains(nodeParentName);
        if (!xmlProperties.isInline() && INLINE_ELEMENTS.contains(nodeName)) xmlProperties.setInline(true);
        if (!xmlProperties.isVerbatim() && VERBATIM_ELEMENTS.contains(nodeName)) xmlProperties.setVerbatim(true);

        /*
         * this element has translatable strings if:
         *
         * 1. a translatableElement
         *
         * OR
         *
         * 2. a standaloneElement without a translatableParentElement
         *
         * 3. not a standaloneElement and not an inlineElement
         */

        if ((translatableElement && ((standaloneElement && !translatableParentElement) || (!standaloneElement && !xmlProperties.isInline
                ())))) {
            final NodeList children = node.getChildNodes();
            final boolean hasChildren = children == null || children.getLength() != 0;

            // dump the node if it has no children
            if (!hasChildren) {
                final String nodeText = XMLUtilities.convertNodeToString(node, false);
                final String cleanedNodeText = cleanTranslationText(nodeText, true, true);

                if (xmlProperties.isVerbatim()) {
                    addTranslationToNodeDetailsToCollection(nodeText, node, allowDuplicates, translationStrings);
                } else if (!cleanedNodeText.isEmpty() && !cleanedNodeText.matches("^\\s+$")) {
                    addTranslationToNodeDetailsToCollection(cleanedNodeText, node, allowDuplicates, translationStrings);
                }

            }
            /*
             * dump all child nodes until we hit one that itself contains a translatable element. in effect the translation
             * strings can contain up to one level of xml elements.
             */
            else {
                ArrayList<Node> nodes = new ArrayList<Node>();
                String translatableString = "";
                boolean removeWhitespaceFromStart = true;

                final int childrenLength = children.getLength();
                for (int i = 0; i < childrenLength; ++i) {
                    final Node child = children.item(i);
                    final String childNodeName = child.getNodeName();

                    // does this child have another level of translatable tags?
                    final boolean containsTranslatableTags = doesElementContainTranslatableContentV2(child);
                    final boolean childTranslatableElement = TRANSLATABLE_ELEMENTS.contains(childNodeName);
                    final boolean childInlineElement = INLINE_ELEMENTS.contains(childNodeName);

                    // if so, save the string we have been building up, process the child, and start building up a new string
                    if ((containsTranslatableTags || childTranslatableElement) && !childInlineElement) {
                        if (nodes.size() != 0) {
                            /*
                             * We have found a child node that itself contains some translatable children. In this case we
                             * create a new translatable string. It is possible that the translatableString has some
                             * insignificant trailing whitespace, because the call to the cleanTranslationText function in the
                             * else statement below has assumed that the node being processed was not the last one in the
                             * translatable string, making the trailing whitespace important. So we clean up the trailing
                             * whitespace here.
                             */

                            final Matcher matcher = XMLUtilities.TRAILING_WHITESPACE_RE_PATTERN.matcher(translatableString);
                            if (matcher.matches()) translatableString = matcher.group("content");

                            addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                            translatableString = "";
                            nodes = new ArrayList<Node>();
                            removeWhitespaceFromStart = true;
                        }

                        getTranslatableStringsFromNodeV2(child, translationStrings, allowDuplicates, xmlProperties);
                    } else {
                        final String childName = child.getNodeName();
                        final String childText = XMLUtilities.convertNodeToString(child, true);

                        final String cleanedChildText = cleanTranslationText(childText, removeWhitespaceFromStart, i == childrenLength - 1);
                        final boolean isVerbatimNode = VERBATIM_ELEMENTS.contains(childName);

                        final String thisTranslatableString = isVerbatimNode || xmlProperties.isVerbatim() ? childText : cleanedChildText;

                        if (!thisTranslatableString.isEmpty() && !thisTranslatableString.matches("^\\s+$")) {
                            translatableString += thisTranslatableString;
                            nodes.add(child);

                            /*
                             * We've processed the first element in the string so now we don't want to remove whitespace from
                             * the start of the String
                             */
                            removeWhitespaceFromStart = false;
                        }
                    }
                }

                // save the last translated string
                if (nodes.size() != 0) {
                    addTranslationToNodeDetailsToCollection(translatableString, nodes, allowDuplicates, translationStrings);

                    translatableString = "";
                }
            }
        } else {
            // if we hit a non-translatable element, process its children
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                final Node child = nodeList.item(i);
                getTranslatableStringsFromNodeV2(child, translationStrings, allowDuplicates, xmlProperties);
            }
        }
    }

    public static void replaceTranslatedStrings(final Document xml, final Map<String, String> translations,
            final List<StringToNodeCollection> stringToNodeCollections) {
        if (xml == null || translations == null || translations.size() == 0 || stringToNodeCollections == null || stringToNodeCollections
                .size() == 0)
            return;

        /*
         * We assume that the xml being provided here is either an exact match, or modified by Zanata in some predictable way
         * (i.e. some padding removed), as supplied to the getTranslatableStrings originally, which we then assume matches the
         * strings supplied as the keys in the translations parameter.
         */

        if (stringToNodeCollections == null || stringToNodeCollections.size() == 0) return;

        for (final StringToNodeCollection stringToNodeCollection : stringToNodeCollections) {
            final String originalString = stringToNodeCollection.getTranslationString();
            final ArrayList<ArrayList<Node>> nodeCollections = stringToNodeCollection.getNodeCollections();

            if (nodeCollections != null && nodeCollections.size() != 0) {
                // Zanata will remove any leading/trailing whitespace due to XML serialization. Here we account for any trimming that was
                // done.
                final TranslatedStringDetails fixedStringDetails = new TranslatedStringDetails(translations, originalString);

                if (fixedStringDetails.getFixedString() != null) {
                    final String translation = translations.get(fixedStringDetails.getFixedString());

                    if (translation != null && !translation.isEmpty()) {
                        // Build up the padding that Zanata removed
                        final StringBuilder leftTrimPadding = new StringBuilder();
                        final StringBuilder rightTrimPadding = new StringBuilder();

                        for (int i = 0; i < fixedStringDetails.getLeftTrimCount(); ++i)
                            leftTrimPadding.append(" ");

                        for (int i = 0; i < fixedStringDetails.getRightTrimCount(); ++i)
                            rightTrimPadding.append(" ");

                        // wrap the returned translation in a root element
                        final String wrappedTranslation = "<tempRoot>" + leftTrimPadding + translation + rightTrimPadding + "</tempRoot>";

                        // convert the wrapped translation into an XML document
                        Document translationDocument = null;
                        try {
                            translationDocument = XMLUtilities.convertStringToDocument(wrappedTranslation);
                        } catch (Exception ex) {
                            LOG.error("Unable to convert Translated String to a DOM Document", ex);
                        }

                        // was the conversion successful
                        if (translationDocument != null) {
                            for (final ArrayList<Node> nodes : nodeCollections) {
                                if (nodes != null && nodes.size() != 0) {
                                    // All nodes in a collection should share the same parent
                                    final Node parent = nodes.get(0).getParentNode();

                                    if (parent != null) {
                                        /*
                                         * Replace the old node with contents of the new node. To do this we need to iterate
                                         * over the children and place them from last to first after the node. This will ensure
                                         * the order of the nodes is kept. Also note that we can't just insert into the parent
                                         * at the start or end as there maybe more refined translations (ie an itemizedList) in
                                         * the middle of the content.
                                         */
                                        final Node importNode = xml.importNode(translationDocument.getDocumentElement(), true);
                                        final NodeList translatedChildren = importNode.getChildNodes();
                                        for (int i = translatedChildren.getLength() - 1; i >= 0; i--) {
                                            if (nodes.get(0).getNextSibling() == null) {
                                                parent.appendChild(translatedChildren.item(i));
                                            } else {
                                                parent.insertBefore(translatedChildren.item(i), nodes.get(0).getNextSibling());
                                            }
                                        }

                                        // remove the original node that the translated text came from
                                        for (final Node node : nodes) {
                                            if (parent == node.getParentNode()) parent.removeChild(node);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static StringToNodeCollection findExistingText(final String text, final List<StringToNodeCollection> translationStrings) {
        for (final StringToNodeCollection stringToNodeCollection : translationStrings) {
            if (stringToNodeCollection.getTranslationString().equals(text)) return stringToNodeCollection;
        }

        return null;
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final Node node, final boolean allowDuplicates,
            final List<StringToNodeCollection> translationStrings) {
        final ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(node);
        addTranslationToNodeDetailsToCollection(text, nodes, allowDuplicates, translationStrings);
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final ArrayList<Node> nodes,
            final boolean allowDuplicates, final List<StringToNodeCollection> translationStrings) {

        if (allowDuplicates) {
            translationStrings.add(new StringToNodeCollection(text).addNodeCollection(nodes));
        } else {
            final StringToNodeCollection stringToNodeCollection = findExistingText(text, translationStrings);

            if (stringToNodeCollection == null) translationStrings.add(new StringToNodeCollection(text).addNodeCollection(nodes));
            else stringToNodeCollection.addNodeCollection(nodes);
        }
    }

    /**
     * Cleans a string for presentation to a translator
     */
    private static String cleanTranslationText(final String input, final boolean removeWhitespaceFromStart,
            final boolean removeWhitespaceFromEnd) {
        String retValue = XMLUtilities.cleanText(input);

        final boolean hasStartWhiteSpace = XMLUtilities.PRECEEDING_WHITESPACE_SIMPLE_RE_PATTERN.matcher(input).matches();
        final boolean hasEndWhiteSpace = XMLUtilities.TRAILING_WHITESPACE_SIMPLE_RE_PATTERN.matcher(input).matches();

        retValue = retValue.trim();

        /*
         * When presenting the contents of a childless XML node to the translator, there is no need for white space padding.
         * When building up a translatable string from a succession of text nodes, whitespace becomes important.
         */
        if (!removeWhitespaceFromStart) {
            if (hasStartWhiteSpace) retValue = " " + retValue;
        }

        if (!removeWhitespaceFromEnd) {
            if (hasEndWhiteSpace) retValue += " ";
        }

        return retValue;
    }

    /**
     * Wraps the xml if required so that validation can be performed. An example of where this is required is if you are validating
     * against Abstracts, Author Groups or Legal Notices for DocBook 5.0.
     *
     * @param docBookVersion The DocBook version the document will be validated against.
     * @param xml            The xml that needs to be validated.
     * @return A {@link Pair} containing the root element name and the wrapped xml content.
     */
    public static Pair<String, String> wrapForValidation(final DocBookVersion docBookVersion, final String xml) {
        final String rootEleName = XMLUtilities.findRootElementName(xml);
        if (docBookVersion == DocBookVersion.DOCBOOK_50) {
            if (rootEleName.equals("abstract") || rootEleName.equals("legalnotice") || rootEleName.equals("authorgroup")) {
                final String preamble = XMLUtilities.findPreamble(xml);

                final StringBuilder buffer = new StringBuilder("<book><info><title />");
                if (preamble != null) {
                    buffer.append(xml.replace(preamble, ""));
                } else {
                    buffer.append(xml);
                }
                buffer.append("</info></book>");

                return new Pair<String, String>("book", DocBookUtilities.addDocBook50Namespace(buffer.toString()));
            } else if (rootEleName.equals("info")) {
                final String preamble = XMLUtilities.findPreamble(xml);

                final StringBuilder buffer = new StringBuilder("<book>");
                if (preamble != null) {
                    buffer.append(xml.replace(preamble, ""));
                } else {
                    buffer.append(xml);
                }
                buffer.append("</book>");

                return new Pair<String, String>("book", DocBookUtilities.addDocBook50Namespace(buffer.toString()));
            }
        }

        return new Pair<String, String>(rootEleName, xml);
    }

    /**
     * Pushing to Zanata will modify strings sent to it for translation due the to XML serialization. This class contains the info
     * necessary to take a string from Zanata and match it to the source XML.
     */
    protected static class TranslatedStringDetails {
        /**
         * The number of spaces that Zanata removed from the left
         */
        private final int leftTrimCount;
        /**
         * The number of spaces that Zanata removed from the right
         */
        private final int rightTrimCount;
        /**
         * The string that was matched to the one returned by Zanata. This will be null if there was no match.
         */
        private final String fixedString;

        TranslatedStringDetails(final Map<String, String> translations, final String originalString) {
        /*
         * Here we account for any trimming that is done by Zanata.
         */
            final String lTrimString = StringUtilities.ltrim(originalString);
            final String rTrimString = StringUtilities.rtrim(originalString);
            final String trimString = originalString.trim();

            final boolean containsExactMatch = translations.containsKey(originalString);
            final boolean lTrimMatch = translations.containsKey(lTrimString);
            final boolean rTrimMatch = translations.containsKey(rTrimString);
            final boolean trimMatch = translations.containsKey(trimString);

        /* remember the details of the trimming, so we can add the padding back */
            if (containsExactMatch) {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = originalString;
            } else if (lTrimMatch) {
                leftTrimCount = originalString.length() - lTrimString.length();
                rightTrimCount = 0;
                fixedString = lTrimString;
            } else if (rTrimMatch) {
                leftTrimCount = 0;
                rightTrimCount = originalString.length() - rTrimString.length();
                fixedString = rTrimString;
            } else if (trimMatch) {
                leftTrimCount = StringUtilities.ltrimCount(originalString);
                rightTrimCount = StringUtilities.rtrimCount(originalString);
                fixedString = trimString;
            } else {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = null;
            }
        }

        public int getLeftTrimCount() {
            return leftTrimCount;
        }

        public int getRightTrimCount() {
            return rightTrimCount;
        }

        public String getFixedString() {
            return fixedString;
        }
    }

    protected static class XMLProperties {
        private boolean verbatim = false;
        private boolean inline = false;

        public XMLProperties() {

        }

        public XMLProperties(final XMLProperties props) {
            if (props != null) {
                this.inline = props.isInline();
                this.verbatim = props.isVerbatim();
            }
        }

        public boolean isVerbatim() {
            return verbatim;
        }

        public void setVerbatim(boolean verbatim) {
            this.verbatim = verbatim;
        }

        public boolean isInline() {
            return inline;
        }

        public void setInline(boolean inline) {
            this.inline = inline;
        }
    }
}
