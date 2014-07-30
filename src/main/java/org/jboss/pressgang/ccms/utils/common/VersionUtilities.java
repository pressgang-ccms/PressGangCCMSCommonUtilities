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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(VersionUtilities.class);

    private static final Map<Class<?>, String> versionMap = new WeakHashMap<Class<?>, String>();
    private static final Map<Class<?>, String> buildDateMap = new WeakHashMap<Class<?>, String>();

    protected static String getVersion(final Class<?> interfaceClazz) {
        if (!versionMap.containsKey(interfaceClazz)) {
            versionMap.put(interfaceClazz, VersionUtilities.getAPIVersion(interfaceClazz));
        }

        return versionMap.get(interfaceClazz);
    }

    /**
     * Get the Version Number from a properties file in the Application Classpath.
     *
     * @param resourceFileName The name of the properties file.
     * @param versionProperty  The name of the version property in the properties file.
     * @return The Version number or "unknown" if the version couldn't be found.
     */
    public static String getAPIVersion(final String resourceFileName, final String versionProperty) {
        final Properties props = new Properties();
        final URL url = ClassLoader.getSystemResource(resourceFileName);
        if (url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                props.load(is);
            } catch (IOException ex) {
                LOG.debug("Unable to open resource file", ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        String version = props.getProperty(versionProperty, "unknown");

        return version;
    }

    /**
     * Get the Build Date Information from a properties file in the Application Classpath.
     *
     * @param resourceFileName  The name of the properties file.
     * @param buildDateProperty The name of the version property in the properties file.
     * @return The build timestamp or "unknown" if the build timestamp couldn't be found.
     */
    public static String getAPIBuildTimestamp(final String resourceFileName, final String buildDateProperty) {
        final Properties props = new Properties();
        final URL url = ClassLoader.getSystemResource(resourceFileName);
        if (url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                props.load(is);
            } catch (IOException ex) {
                LOG.debug("Unable to open resource file", ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {

                    }
                }
            }
        }

        String version = props.getProperty(buildDateProperty, "unknown");

        return version;
    }

    /**
     * Get the Version Number for a specific class. The archive the class belongs to must have been built with maven and the
     * "Implementation-Version" must exist in the MANIFEST.MF. This can be set by adding the following to the pom:
     * <p/>
     * <pre>
     * {@code
     * <build>
     *     <plugins>
     *         <plugin>
     *             <artifactId>maven-jar-plugin</artifactId>
     *             <configuration>
     *                 <archive>
     *                     <manifest>
     *                         <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
     *                         <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
     *                     </manifest>
     *                 </archive>
     *             </configuration>
     *         </plugin>
     *     </plugins>
     * </build>}
     * </pre>
     * <p/>
     * Thanks to the Zanata Team for this method, it has just been edited for this libraries needs.
     *
     * @param clazz The class to find the Version Information for.
     * @return The Version number or "unknown" if the version couldn't be found.
     */
    public static String getAPIVersion(final Class<?> clazz) {
        if (versionMap.containsKey(clazz)) {
            return versionMap.get(clazz);
        }

        Attributes atts = null;
        String version = null;
        try {
            atts = getAttributesForClass(clazz);
        } catch (IOException ex) {
            LOG.debug("Unable to open Manifest stream for class: " + clazz.getName(), ex);
        }

        if (atts != null) {
            version = atts.getValue("Implementation-Version");
        }

        // if we can't get version from the jar, try for the package version
        if (version == null) {
            Package pkg = clazz.getPackage();
            if (pkg != null) version = pkg.getImplementationVersion();
        }
        if (version == null) version = "unknown";

        versionMap.put(clazz, version);

        return version;
    }

    /**
     * Get the Build Date for a specific class. The archive the class belongs to must have been built with maven and the
     * "Implementation-Build" must exist in the MANIFEST.MF. This can be set by adding the following to the pom:
     * <p/>
     * <pre>
     * {@code
     * <build>
     *     <plugins>
     *         <plugin>
     *             <artifactId>maven-jar-plugin</artifactId>
     *             <configuration>
     *                 <archive>
     *                     <manifestEntries>
     *                          <Implementation-Build>}${maven.build.timestamp}{@code</Implementation-Build>
     *                     </manifestEntries>
     *                 </archive>
     *             </configuration>
     *         </plugin>
     *     </plugins>
     * </build>}
     * </pre>
     * <p/>
     * Thanks to the Zanata Team for this method, it has just been edited for this libraries needs.
     *
     * @param clazz The class to find the Version Information for.
     * @return The build timestamp or "unknown" if the timestamp couldn't be found.
     */
    public static String getAPIBuildTimestamp(final Class<?> clazz) {
        if (buildDateMap.containsKey(clazz)) {
            return buildDateMap.get(clazz);
        }

        Attributes atts = null;
        String buildTimestamp = null;
        try {
            atts = getAttributesForClass(clazz);
        } catch (IOException ex) {
            LOG.debug("Unable to open Manifest stream for class: " + clazz.getName(), ex);
        }

        if (atts != null) {
            buildTimestamp = atts.getValue("Implementation-Build");
        }

        if (buildTimestamp == null) buildTimestamp = "unknown";

        buildDateMap.put(clazz, buildTimestamp);

        return buildTimestamp;
    }

    /**
     * Get the Manifest Attributes from a JAR Archive for a specific class.
     * <p/>
     * Thanks to the Zanata Team for this method, it has just been edited for this libraries needs.
     *
     * @param clazz The class to find the version information for.
     * @return The Attributes that exist in the Jars Manifest for the specified class.
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Attributes getAttributesForClass(final Class<?> clazz) throws IOException {
        // thanks to
        // http://stackoverflow.com/questions/1272648/need-to-read-own-jars-manifest-and-not-root-classloaders-manifest/1273432#1273432
        final String className = clazz.getSimpleName() + ".class";
        final String classPath = clazz.getResource(className).toString();

        final String type = classPath.contains(".war") ? ".war/" : ".jar/";

        final String manifestPath;
        if (classPath.startsWith("vfszip:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf(type) + type.length()) + "META-INF/MANIFEST.MF";
        } else if (classPath.startsWith("vfs:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf(type) + type.length()) + "META-INF/MANIFEST.MF";
        } else if (classPath.startsWith("jar:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + "!".length()) + "/META-INF/MANIFEST.MF";
        } else {
            return null;
        }


        InputStream is = null;
        try {
            is = new URL(manifestPath).openStream();
            final Manifest manifest = new Manifest(is);
            return manifest.getMainAttributes();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
