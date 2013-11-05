package org.jboss.pressgang.ccms.utils.common;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * An XML Validator Utility to validate XML using SAX. SAX is significantly faster then DOM so if you aren't required to check elements in
 * the xml then this should be a lot faster then a DOM Validator.
 *
 * @author lnewson
 */
public class SAXXMLValidator implements ErrorHandler, EntityResolver {
    private static final Pattern DOCTYPE_PATTERN = Pattern.compile(
            "^\\s*<\\?xml.*?\\?>\\s*<\\!DOCTYPE\\s+(?<Name>.*?)\\s+((PUBLIC\\s+\".*?\"|SYSTEM)[ ]+\"(?<SystemId>.*?)\")\\s*(" + "" +
                    "(?<Entities>\\[(.|\n)*\\]\\s*))?>");

    protected boolean errorsDetected;
    private String errorText;
    private Map<String, byte[]> files = new HashMap<String, byte[]>();
    final private boolean showErrors;

    public SAXXMLValidator(final boolean showErrors) {
        this.showErrors = showErrors;
    }

    public SAXXMLValidator() {
        this.showErrors = false;
    }

    /**
     * Validates some piece of XML, by firstly converting it to a string to ensure that it is valid.
     *
     * @param doc         The XML DOM Document to be validated.
     * @param dtdFileName The filename of the DTD data.
     * @param dtdData     The DTD data to be used to validate against.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validateXML(final Document doc, final String dtdFileName, final byte[] dtdData) {
        if (doc == null || doc.getDocumentElement() == null) {
            return false;
        } else {
            return validateXML(doc, dtdFileName, dtdData, doc.getDocumentElement().getNodeName());
        }
    }

    public boolean validateXML(final Document doc, final String dtdFileName, final byte[] dtdData, final String rootEleName) {
        return validateXML(doc, dtdFileName, dtdData, null, null, rootEleName);
    }

    public boolean validateXML(final Document doc, final String dtdFileName, final byte[] dtdData, final String entityFileName,
            final byte[] entityData) {
        if (doc == null || doc.getDocumentElement() == null) {
            return false;
        } else {
            return validateXML(doc, dtdFileName, dtdData, entityFileName, entityData, doc.getDocumentElement().getNodeName());
        }
    }

    public boolean validateXML(final Document doc, final String dtdFileName, final byte[] dtdData, final String entityFileName,
            final byte[] entityData, final String rootEleName) {
        if (doc == null || doc.getDocumentElement() == null) {
            return false;
        } else {
            final String xml;
            if (doc.getXmlEncoding() == null) {
                xml = XMLUtilities.convertDocumentToString(doc, "UTF-8");
            } else {
                xml = XMLUtilities.convertDocumentToString(doc);
            }

            return validateXML(xml, dtdFileName, dtdData, entityFileName, entityData, rootEleName);
        }
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param xml         The XML to be validated.
     * @param dtdFileName The filename of the DTD data.
     * @param dtdData     The DTD data to be used to validate against.
     * @param rootEleName The name of the root XML Element.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validateXML(final String xml, final String dtdFileName, final byte[] dtdData, final String rootEleName) {
        return validateXML(xml, dtdFileName, dtdData, null, null, rootEleName);
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param xml         The XML to be validated.
     * @param dtdFileName The filename of the DTD data.
     * @param dtdData     The DTD data to be used to validate against.
     * @param rootEleName The name of the root XML Element.
     * @return
     */
    public boolean validateXML(final String xml, final String dtdFileName, final byte[] dtdData, final String entityFileName,
            final byte[] entityData, final String rootEleName) {
        if (xml == null || dtdFileName == null || dtdData == null || rootEleName == null) return false;

        this.files = new HashMap<String, byte[]>();
        files.put(dtdFileName, dtdData);
        if (entityData != null && entityFileName != null) {
            files.put(entityFileName, entityData);
        }

        String encoding = XMLUtilities.findEncoding(xml);
        if (encoding == null) encoding = "UTF-8";

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setValidating(true);
            factory.setNamespaceAware(false);
            final SAXParser parser = factory.newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(this);
            reader.setErrorHandler(this);
            reader.parse(
                    new InputSource(new ByteArrayInputStream(setXmlDtd(xml, dtdFileName, entityFileName, rootEleName).getBytes(encoding))));
        } catch (SAXParseException e) {
            handleError(e);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * A function that will resolve the dtd file location to the dtd byte[] data specified, if the System ID matches the dtd filename.
     * Otherwise a null InputSource will be returned.
     *
     * @param publicId The Public ID of the DTD to be resolved.
     * @param systemId The System ID of the DTD to be resolved.
     * @return An input stream that will read from the passed dtd byte array.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        final InputSource dtdsource = new InputSource();
        for (final Map.Entry<String, byte[]> file : files.entrySet()) {
            if (systemId.endsWith(file.getKey())) {
                dtdsource.setByteStream(new ByteArrayInputStream(file.getValue()));
                return dtdsource;
            }
        }

        return null;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public void error(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXParseException {
        throw ex;
    }

    @Override
    public void warning(SAXParseException ex) throws SAXParseException {
        // Do nothing if its a warning
    }

    public boolean handleError(final SAXParseException error) {
        errorsDetected = true;
        errorText = error.getMessage();
        if (showErrors) System.out.println("XMLValidator.handleError() " + errorText);
        return true;
    }

    /**
     * Sets the DTD for an xml file. If there are any entities then they are removed. This function will also add the preamble to the XML
     * if it doesn't exist.
     *
     * @param xml            The XML to add the DTD for.
     * @param dtdFileName    The file/url name of the DTD.
     * @param dtdFileName    The file/url name of the Entity File or null if none exists.
     * @param dtdRootEleName The name of the root element in the XML that is inserted into the {@code<!DOCTYPE >} node.
     * @return The xml with the dtd added.
     */
    private String setXmlDtd(final String xml, final String dtdFileName, final String entityFileName, final String dtdRootEleName) {
        String output = null;

        // Check if the XML already has a DOCTYPE. If it does then replace the values and remove entities for processing
        final Matcher matcher = DOCTYPE_PATTERN.matcher(xml);
        while (matcher.find()) {
            String name = matcher.group("Name");
            String systemId = matcher.group("SystemId");
            String entities = matcher.group("Entities");
            String doctype = matcher.group();
            String newDoctype = doctype.replace(name, dtdRootEleName).replace(systemId, dtdFileName);
            if (entities != null) {
                newDoctype = newDoctype.replace(entities, "");
            }
            output = xml.replace(doctype, newDoctype);
            return output;
        }

        // The XML doesn't have any doctype so add it
        final String preamble = XMLUtilities.findPreamble(xml);
        if (preamble != null) {
            output = xml.replace(preamble,
                    preamble + "\n" + "<!DOCTYPE " + dtdRootEleName + " SYSTEM \"" + dtdFileName + "\" [\n" + (entityFileName == null ?
                            "" : "<!ENTITY % BOOK_ENTITIES SYSTEM \"" + entityFileName + "\">\n" + "%BOOK_ENTITIES;") + "\n]>");
        } else {
            output = "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                    "<!DOCTYPE " + dtdRootEleName + " SYSTEM \"" + dtdFileName + "\"[\n" +
                    (entityFileName == null ? "" : "<!ENTITY % BOOK_ENTITIES SYSTEM \"" + entityFileName + "\">\n" + "%BOOK_ENTITIES;") +
                    "\n]>\n" + xml;
        }

        return output;
    }
}