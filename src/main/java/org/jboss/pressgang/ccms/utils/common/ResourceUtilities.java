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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * A set of utilities to read from the local application resources.
 */
public class ResourceUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceUtilities.class);

    /**
     * Reads a resource file and converts it into a String
     *
     * @param location The location of the resource file.
     * @param fileName The name of the file.
     * @return The string that represents the contents of the file or null if an error occurred.
     */
    public static String resourceFileToString(final String location, final String fileName) {
        if (location == null || fileName == null) return null;

        final InputStream in = ResourceUtilities.class.getResourceAsStream(location + fileName);
        if (in == null) return null;

        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        final StringBuilder output = new StringBuilder("");
        try {
            while ((line = br.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.error("Failed to close the InputStream", e);
            }
            try {
                br.close();
            } catch (IOException e) {
                LOG.error("Failed to close the BufferedReader", e);
            }
        }
        return output.toString();
    }

    /**
     * Reads a resource file and converts it into a XML based Document object.
     *
     * @param location The location of the resource file.
     * @param fileName The name of the file.
     * @return The Document that represents the contents of the file or null if an error occurred.
     */
    public static Document resourceFileToXMLDocument(final String location, final String fileName) {
        if (location == null || fileName == null) return null;

        final InputStream in = ResourceUtilities.class.getResourceAsStream(location + fileName);
        if (in == null) return null;

        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(in);
        } catch (Exception e) {
            LOG.error("Failed to parse the resource as XML.", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.error("Failed to close the InputStream", e);
            }
        }
        return doc;
    }

    /**
     * Reads a resource file and converts it into a String
     *
     * @param location The location of the resource file.
     * @param fileName The name of the file.
     * @return The string that represents the contents of the file or null if an error occurred.
     */
    public static byte[] resourceFileToByteArray(final String location, final String fileName) {
        if (location == null || fileName == null) return null;

        final InputStream in = ResourceUtilities.class.getResourceAsStream(location + fileName);
        if (in == null) return null;

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Create the byte array to hold the data
            final byte[] buffer = new byte[1024];

            // Read in the bytes
            int numRead = 0;
            while((numRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numRead);
            }
        } catch (final Exception ex) {
            LOG.error("An error occurred while reading the file contents", ex);
        } finally {
            try {
                in.close();
            } catch (final Exception ex) {
                LOG.error("Failed to close the InputStream", ex);
            }
        }
        return out.toByteArray();
    }
}
