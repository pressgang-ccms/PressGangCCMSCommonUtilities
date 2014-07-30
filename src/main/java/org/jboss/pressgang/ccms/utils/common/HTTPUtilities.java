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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(HTTPUtilities.class);

    /**
     * Used to send arbitrary data to the user (i.e. to download files) with a
     * MIME type of "application/octet-stream"
     *
     * @param data     The contents of the file
     * @param filename The name of the file to send to the browser
     */
    public static void writeOutContent(final byte[] data, final String filename) {
        writeOutContent(data, filename, "application/octet-stream");
    }

    /**
     * Used to send arbitrary data to the user (i.e. to download files)
     *
     * @param data     The contents of the file
     * @param filename The name of the file to send to the browser
     * @param mime     The MIME type of the file
     */
    public static void writeOutContent(final byte[] data, final String filename, final String mime) {
        writeOutContent(data, filename, mime, true);
    }

    /**
     * Used to send data through the browser. It is up to the browser to either
     * display or download the data.
     *
     * @param data The contents of the file
     * @param mime The MIME type of the file
     */
    public static void writeOutToBrowser(final byte[] data, final String mime) {
        writeOutContent(data, null, mime, false);
    }

    /**
     * Used to send arbitrary to the user.
     *
     * @param data         The contents of the file to send
     * @param filename     The name of the file. This is only useful if asAttachment
     *                     is true
     * @param mime         The MIME type of the content
     * @param asAttachment If true, the file will be downloaded. If false, it is
     *                     up to the browser to decide whether to display or download the
     *                     file
     */
    private static void writeOutContent(final byte[] data, final String filename, final String mime, final boolean asAttachment) {
        try {
            final HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            response.setContentType(mime);

            if (asAttachment) response.addHeader("Content-Disposition", "attachment;filename=" + filename);

            response.setContentLength(data.length);

            final OutputStream writer = response.getOutputStream();

            writer.write(data);

            writer.flush();
            writer.close();

            FacesContext.getCurrentInstance().responseComplete();
        } catch (final Exception ex) {
            LOG.error("Unable to write content to HTTP Output Stream", ex);
        }
    }


}
