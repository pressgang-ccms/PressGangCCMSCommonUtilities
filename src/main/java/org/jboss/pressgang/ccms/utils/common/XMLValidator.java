package org.jboss.pressgang.ccms.utils.common;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import com.thaiopensource.relaxng.jaxp.XMLSyntaxSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
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
public class XMLValidator {
    private static final Logger LOG = LoggerFactory.getLogger(XMLValidator.class);

    private static final Pattern DOCTYPE_PATTERN = Pattern.compile(
            "^(\\s*(?<Preamble><\\?xml.*?\\?>))?\\s*<\\!DOCTYPE\\s+(?<Name>.*?)(\\s+((PUBLIC\\s+\".*?\"|SYSTEM)[ ]+\"(?<SystemId>.*?)\")"
                    + "\\s*)?((?<Entities>\\[(.|\n)*\\]\\s*))?>");

    protected boolean errorsDetected;
    private String errorText;
    private Map<String, byte[]> files = new HashMap<String, byte[]>();
    private final boolean logErrors;

    public XMLValidator(final boolean logErrors) {
        this.logErrors = logErrors;

        // Configure the RelaxNG schema factory
        System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI, XMLSyntaxSchemaFactory.class.getName());
    }

    public XMLValidator() {
        this(false);
    }

    /**
     * Validates some piece of XML, by firstly converting it to a string to ensure that it is valid.
     *
     * @param method   The validation method to use during validation.
     * @param doc      The XML DOM Document to be validated.
     * @param fileName The filename of the DTD/Schema data.
     * @param data     The DTD/Schema data to be used to validate against.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final Document doc, final String fileName, final byte[] data) {
        return validate(method, doc, fileName, data, null, null);
    }

    /**
     * Validates some piece of XML, by firstly converting it to a string to ensure that it is valid.
     *
     * @param method          The validation method to use during validation.
     * @param doc             The XML DOM Document to be validated.
     * @param fileName        The filename of the DTD/Schema data.
     * @param data            The DTD/Schema data to be used to validate against.
     * @param additionalFiles Any additional files that are needed during the validation.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final Document doc, final String fileName, final byte[] data,
            final Map<String, byte[]> additionalFiles) {
        return validate(method, doc, fileName, data, null, additionalFiles);
    }

    /**
     * Validates some piece of XML, by firstly converting it to a string to ensure that it is valid.
     *
     * @param method   The validation method to use during validation.
     * @param doc      The XML DOM Document to be validated.
     * @param fileName The filename of the DTD/Schema data.
     * @param data     The DTD/Schema data to be used to validate against.
     * @param entities The entity data to be used to validate against.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final Document doc, final String fileName, final byte[] data,
            final String entities) {
        return validate(method, doc, fileName, data, entities, null);
    }

    /**
     * Validates some piece of XML, by firstly converting it to a string to ensure that it is valid.
     *
     * @param method          The validation method to use during validation.
     * @param doc             The XML DOM Document to be validated.
     * @param fileName        The filename of the DTD/Schema data.
     * @param data            The DTD/Schema data to be used to validate against.
     * @param entities        The entity data to be used to validate against.
     * @param additionalFiles Any additional files that are needed during the validation.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final Document doc, final String fileName, final byte[] data,
            final String entities, final Map<String, byte[]> additionalFiles) {
        if (doc == null || doc.getDocumentElement() == null) {
            return false;
        } else {
            final String xml;
            if (doc.getXmlEncoding() == null) {
                xml = XMLUtilities.convertDocumentToString(doc, "UTF-8");
            } else {
                xml = XMLUtilities.convertDocumentToString(doc);
            }

            return validate(method, xml, fileName, data, entities, doc.getDocumentElement().getNodeName(), additionalFiles);
        }
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param method      The validation method to use during validation.
     * @param xml         The XML to be validated.
     * @param fileName    The filename of the DTD/Schema data.
     * @param data        The DTD/Schema data to be used to validate against.
     * @param rootEleName The name of the root XML Element.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final String xml, final String fileName, final byte[] data,
            final String rootEleName) {
        return validate(method, xml, fileName, data, rootEleName, (Map<String, byte[]>) null);
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param method          The validation method to use during validation.
     * @param xml             The XML to be validated.
     * @param fileName        The filename of the DTD/Schema data.
     * @param data            The DTD/Schema data to be used to validate against.
     * @param rootEleName     The name of the root XML Element.
     * @param additionalFiles Any additional files that are needed during the validation.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final String xml, final String fileName, final byte[] data,
            final String rootEleName, final Map<String, byte[]> additionalFiles) {
        return validate(method, xml, fileName, data, null, rootEleName, additionalFiles);
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param method      The validation method to use during validation.
     * @param xml         The XML to be validated.
     * @param fileName    The filename of the DTD data.
     * @param data        The DTD data to be used to validate against.
     * @param entities    The entity data to be used to validate against.
     * @param rootEleName The name of the root XML Element.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final String xml, final String fileName, final byte[] data,
            final String entities, final String rootEleName) {
        return validate(method, xml, fileName, data, entities, rootEleName, null);
    }

    /**
     * Validates some piece of XML to ensure that it is valid.
     *
     * @param method          The validation method to use during validation.
     * @param xml             The XML to be validated.
     * @param fileName        The filename of the DTD data.
     * @param data            The DTD data to be used to validate against.
     * @param entities        The entity data to be used to validate against.
     * @param rootEleName     The name of the root XML Element.
     * @param additionalFiles Any additional files that are needed during the validation.
     * @return True if the XML is valid, otherwise false.
     */
    public boolean validate(final ValidationMethod method, final String xml, final String fileName, final byte[] data,
            final String entities, final String rootEleName, final Map<String, byte[]> additionalFiles) {
        if (xml == null || fileName == null || data == null || rootEleName == null) return false;

        files = new HashMap<String, byte[]>();
        files.put(fileName, data);
        if (additionalFiles != null) {
            files.putAll(additionalFiles);
        }

        String encoding = XMLUtilities.findEncoding(xml);
        if (encoding == null) encoding = "UTF-8";

        try {
            final Resolver resolver = new Resolver(files);
            if (method == ValidationMethod.DTD) {
                final byte[] xmlData = setXmlPreambleAndDTD(xml, fileName, entities, rootEleName).getBytes(encoding);
                validateDTD(resolver, xmlData);
            } else {
                final byte[] xmlData = setXmlPreambleAndDTD(xml, null, entities, rootEleName).getBytes(encoding);
                validateSchema(method, resolver, data, xmlData);
            }
        } catch (SAXParseException e) {
            handleError(e);
            return false;
        } catch (Exception e) {
            LOG.error("An error occurred validating the XML", e);
            return false;
        }
        return true;
    }

    protected void validateDTD(final Resolver resolver,
            final byte[] xmlData) throws SAXException, ParserConfigurationException, IOException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        final SAXParser parser = factory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        reader.setEntityResolver(resolver);
        reader.setErrorHandler(new ErrorHandler());
        reader.parse(new InputSource(new ByteArrayInputStream(xmlData)));
    }

    protected void validateSchema(final ValidationMethod method, final Resolver resolver, final byte[] schemaData,
            byte[] xmlData) throws SAXException, IOException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(method.schemaLanguage);
        schemaFactory.setResourceResolver(resolver);
        final Schema schema = schemaFactory.newSchema(new StreamSource(new ByteArrayInputStream(schemaData)));

        final Validator validator = schema.newValidator();
        validator.setErrorHandler(new ErrorHandler());
        validator.setResourceResolver(resolver);
        validator.validate(new StreamSource(new ByteArrayInputStream(xmlData)));
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    protected boolean handleError(final SAXParseException error) {
        errorsDetected = true;
        errorText = error.getMessage();
        if (logErrors) LOG.error(errorText);
        return true;
    }

    /**
     * Sets the DTD for an xml file. If there are any entities then they are removed. This function will also add the preamble to the XML
     * if it doesn't exist.
     *
     * @param xml            The XML to add the DTD for.
     * @param dtdFileName    The file/url name of the DTD.
     * @param dtdRootEleName The name of the root element in the XML that is inserted into the {@code<!DOCTYPE >} node.
     * @return The xml with the dtd added.
     */
    private String setXmlPreambleAndDTD(final String xml, final String dtdFileName, final String entities, final String dtdRootEleName) {
        // Check if the XML already has a DOCTYPE. If it does then replace the values and remove entities for processing
        final Matcher matcher = DOCTYPE_PATTERN.matcher(xml);
        if (matcher.find()) {
            String preamble = matcher.group("Preamble");
            String name = matcher.group("Name");
            String systemId = matcher.group("SystemId");
            String declaredEntities = matcher.group("Entities");
            String doctype = matcher.group();
            String newDoctype = doctype.replace(name, dtdRootEleName);
            if (systemId != null) {
                newDoctype = newDoctype.replace(systemId, dtdFileName);
            }
            if (declaredEntities != null) {
                newDoctype = newDoctype.replace(declaredEntities, " [\n" + entities + "\n]");
            } else {
                newDoctype = newDoctype.substring(0, newDoctype.length() - 1) + " [\n" + entities + "\n]>";
            }
            if (preamble == null) {
                final StringBuilder output = new StringBuilder();
                output.append("<?xml version='1.0' encoding='UTF-8' ?>\n");
                output.append(xml.replace(doctype, newDoctype));
                return output.toString();
            } else {
                return xml.replace(doctype, newDoctype);
            }
        } else {
            // The XML doesn't have any doctype so add it
            final String preamble = XMLUtilities.findPreamble(xml);
            if (preamble != null) {
                final StringBuilder doctype = new StringBuilder();
                doctype.append(preamble);
                appendDoctype(doctype, dtdRootEleName, dtdFileName, entities);
                return xml.replace(preamble, doctype.toString());
            } else {
                final StringBuilder output = new StringBuilder();
                output.append("<?xml version='1.0' encoding='UTF-8' ?>\n");
                appendDoctype(output, dtdRootEleName, dtdFileName, entities);
                output.append(xml);
                return output.toString();
            }
        }
    }

    protected void appendDoctype(final StringBuilder output, final String dtdRootEleName, final String dtdFileName, final String entities) {
        if (dtdFileName != null || entities != null) {
            output.append("<!DOCTYPE ").append(dtdRootEleName);
            if (dtdFileName != null) {
                output.append(" SYSTEM \"").append(dtdFileName).append("\"");
            }
            if (entities != null) {
                output.append(" [\n");
                output.append(entities);
                output.append("\n]");
            }
            output.append(">\n");
        }
    }

    public static enum ValidationMethod {
        DTD(XMLConstants.XML_DTD_NS_URI), XSD(XMLConstants.W3C_XML_SCHEMA_NS_URI), RELAXNG(XMLConstants.RELAXNG_NS_URI);

        private String schemaLanguage;

        ValidationMethod(final String schemaLanguage) {
            this.schemaLanguage = schemaLanguage;
        }
    }

    protected static class Resolver implements EntityResolver, LSResourceResolver {
        private final Map<String, byte[]> files;
        private DOMImplementationLS impl;

        public Resolver(final Map<String, byte[]> files) {
            this.files = files;
            try {
                impl = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
            } catch (final Exception ex) {
                LOG.debug("Unable to resolve external resource", ex);
            }
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
            final InputSource source = new InputSource();
            for (final Map.Entry<String, byte[]> file : files.entrySet()) {
                if (systemId.endsWith(file.getKey())) {
                    source.setByteStream(new ByteArrayInputStream(file.getValue()));
                    return source;
                }
            }

            return null;
        }

        @Override
        public LSInput resolveResource(final String type, final String namespace, final String publicId, final String systemId,
                final String baseURI) {
            if (files.containsKey(systemId)) {
                final LSInput source = impl.createLSInput();
                source.setByteStream(new ByteArrayInputStream(files.get(systemId)));
                return source;
            } else {
                return null;
            }
        }
    }

    protected static class ErrorHandler implements org.xml.sax.ErrorHandler {
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
    }
}