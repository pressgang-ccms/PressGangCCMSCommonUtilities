/*
  Copyright 2011-2014 Red Hat

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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A collection of static methods to make running native commands easier.
 *
 * @author Matthew Casperson
 */
public class ExecUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(ExecUtilities.class);

    /**
     * Run a Process, and read the various streams so there is not a buffer
     * overrun.
     *
     * @param p      The Process to be executed
     * @param output The Stream to receive the Process' output stream
     * @return true if the Process returned 0, false otherwise
     */
    public static boolean runCommand(final Process p, final OutputStream output) {
        return runCommand(p, output, output, new ArrayList<String>());
    }

    /**
     * Run a Process, and read the various streams so there is not a buffer
     * overrun.
     *
     * @param p                 The Process to be executed
     * @param output            The Stream to receive the Process' output stream
     * @param doNotPrintStrings A collection of strings that should not be
     *                          dumped to std.out
     * @return true if the Process returned 0, false otherwise
     */
    public static boolean runCommand(final Process p, final OutputStream output, final List<String> doNotPrintStrings) {
        return runCommand(p, output, output, doNotPrintStrings);
    }

    /**
     * Run a Process, and read the various streams so there is not a buffer
     * overrun.
     *
     * @param p                 The Process to be executed
     * @param output            The Stream to receive the Process' output stream
     * @param error             The Stream to receive the Process' error stream
     * @param doNotPrintStrings A collection of strings that should not be
     *                          dumped to std.out
     * @return true if the Process returned 0, false otherwise
     */
    public static boolean runCommand(final Process p, final OutputStream output, final OutputStream error,
            final List<String> doNotPrintStrings) {
        final StreamRedirector errorStream = new StreamRedirector(p.getErrorStream(), error, doNotPrintStrings);
        final StreamRedirector outputStream = new StreamRedirector(p.getInputStream(), output, doNotPrintStrings);

        errorStream.start();
        outputStream.start();

        try {
            final boolean retValue = p.waitFor() == 0;

            // give the threads time to collect the final output from the Process
            errorStream.join();
            outputStream.join();

            return retValue;
        } catch (final InterruptedException ex) {
            LOG.debug("Command execution Intercepted", ex);
            return false;
        }
    }

    /**
     * @return the current environment variables as an array in the format
     *         "VARIABLE=value"
     */
    public static String[] getEnvironmentVars() {
        final Map<String, String> env = System.getenv();
        final String[] envArray = new String[env.size()];

        int i = 0;

        for (final Entry<String, String> entry : env.entrySet()) {
            envArray[i] = entry.getKey() + "=" + entry.getValue();
            ++i;
        }

        return envArray;
    }
}
