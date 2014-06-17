package org.jboss.pressgang.ccms.utils.common;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TopicUtilities {
    public static Document convertXMLStringToDocument(final String xml, final Integer format) throws SAXException {
        return convertXMLStringToDocument(xml, true, true, format);
    }

    public static Document convertXMLStringToDocument(final String xml, final boolean preserveEntities, final Integer format) throws
            SAXException {
        return convertXMLStringToDocument(xml, preserveEntities, true, format);
    }

    public static Document convertXMLStringToDocument(final String xml, final boolean preserveEntities, final boolean restoreEntities,
            final Integer format) throws SAXException {
        if (format == CommonConstants.DOCBOOK_50) {
            return XMLUtilities.convertStringToDocument(DocBookUtilities.addDocBook50Namespace(xml), preserveEntities, restoreEntities);
        } else {
            return XMLUtilities.convertStringToDocument(xml, preserveEntities, restoreEntities);
        }
    }
}
