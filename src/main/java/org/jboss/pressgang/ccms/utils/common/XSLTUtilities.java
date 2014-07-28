/*
  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.utils.common;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSLTUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(XSLTUtilities.class);
    private static Map<String, Templates> templates = new ConcurrentHashMap<String, Templates>();

    public static String transformXML(final String xml, final String xsl, final String xslSystemId,
            final Map<String, byte[]> resources) throws TransformerException {
        return transformXML(xml, xsl, xslSystemId, resources, new HashMap<String, String>());
    }

    public static String transformXML(final String xml, final String xsl, final String xslSystemId, final Map<String, byte[]> resources,
            final Map<String, String> globalParameters) throws TransformerException {
        if (xml == null || xml.trim().length() == 0) return null;

        if (xsl == null || xsl.trim().length() == 0) return null;

        if (resources == null) return null;

        try {
            final ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            final ByteArrayInputStream xslStream = new ByteArrayInputStream(xsl.getBytes("UTF-8"));
            final ByteArrayOutputStream retValueStream = new ByteArrayOutputStream();

            // http://xml.apache.org/xalan-j/usagepatterns.html#basic
            Templates template = null;
            synchronized (templates) {
                if (templates.containsKey(xslSystemId)) {
                    template = templates.get(xslSystemId);
                } else {
                    System.out.println("Initialising Templates for " + xslSystemId);

                    /*
                     * Instantiate a TransformerFactory. make sure to get a
                     * org.apache.xalan.processor.TransformerFactoryImpl instead
                     * of the default
                     * org.apache.xalan.xsltc.trax.TransformerFactoryImpl. The
                     * latter doesn't work for docbook xsl.
                     */
                    System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
                    final TransformerFactory transformerFactory = TransformerFactory.newInstance();

                    /*
                     * Set the URIResolver that will handle request to external
                     * resources
                     */
                    transformerFactory.setURIResolver(new XSLTResolver(resources));

                    /*
                     * see http://nlp.stanford.edu/nlp/javadoc/xalan-docs/
                     * extensionslib .html#nodeinfo
                     */
                    transformerFactory.setAttribute(TransformerFactoryImpl.FEATURE_SOURCE_LOCATION, Boolean.TRUE);
                    // transformerFactory.setAttribute(TransformerFactoryImpl.FEATURE_INCREMENTAL,
                    // Boolean.TRUE);

                    final StreamSource xslStreamSource = new StreamSource(xslStream);
                    xslStreamSource.setSystemId(xslSystemId);

                    /* save the template */
                    templates.put(xslSystemId, transformerFactory.newTemplates(xslStreamSource));

                    System.out.println("Done Initialising Templates for " + xslSystemId);
                }

                template = templates.get(xslSystemId);
            }

            /*
             * Use the TransformerFactory to process the stylesheet Source and
             * generate a Transformer.
             */
            final Transformer transformer = template.newTransformer();

            /* set the global variables */
            if (globalParameters != null) for (final Entry<String, String> paramEntry : globalParameters.entrySet())
                transformer.setParameter(paramEntry.getKey(), paramEntry.getValue());

            /*
             * Use the Transformer to transform an XML Source and send the
             * output to a Result object.
             */
            transformer.transform(new StreamSource(xmlStream), new StreamResult(retValueStream));

            return retValueStream.toString();

        } catch (final TransformerException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error("Unable to transform the XML content", ex);
        }

        return null;
    }

    /**
     * A class to get the various xsl resources that might be imported.
     */
    private static class XSLTResolver implements URIResolver {
        private static final Logger LOG = LoggerFactory.getLogger(XSLTResolver.class);
        private Map<String, byte[]> resources;

        public XSLTResolver(final Map<String, byte[]> resources) {
            this.resources = resources;
        }

        public Source resolve(final String href, final String base) throws TransformerException {
            try {
                String fileLocation = "";

                if (base != null) {
                    final URL baseUrl = new URL(base);
                    final URL absoluteUrl = new URL(baseUrl, href);
                    fileLocation = absoluteUrl.toExternalForm();
                } else {
                    fileLocation = href;
                }

                if (resources != null && resources.containsKey(fileLocation)) {
                    final StreamSource source = new StreamSource(new ByteArrayInputStream(resources.get(fileLocation)));
                    source.setSystemId(fileLocation);
                    return source;
                }
            } catch (final Exception ex) {
                LOG.debug("Unable to resolve external resource via an internal resource", ex);
            }

            System.out.println("Did not find resource. href: \"" + href + "\" base: \"" + base + "\"");
            throw new TransformerException("Could not find the resource " + href);
        }
    }
}
