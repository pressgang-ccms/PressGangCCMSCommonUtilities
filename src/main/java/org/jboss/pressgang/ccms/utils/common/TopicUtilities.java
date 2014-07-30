/*
  Copyright 2011-2014 Red Hat, Inc, Inc

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
