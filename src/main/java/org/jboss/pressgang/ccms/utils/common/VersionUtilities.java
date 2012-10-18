package org.jboss.pressgang.ccms.utils.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionUtilities {
    /**
     * Get the Version Information from a properties file in the Application Classpath.
     * 
     * @param resourceFileName The name of the properties file.
     * @param versionProperty The name of the version property in the properties file.
     * @return The Version number or "unknown" if the version couldn't be found.
     */
    public static String getVersionInfo(final String resourceFileName, final String versionProperty) {
        final Properties props = new Properties();
        final URL url = ClassLoader.getSystemResource(resourceFileName);
        if (url != null) {
            try {
                props.load(url.openStream());
            } catch (IOException ex) {
                ExceptionUtilities.handleException(ex);
            }
        }

        String version = props.getProperty(versionProperty, "unknown");

        return version;
    }

    /**
     * Get the Version Information for a specific class. The archive the class belows to must have been built with maven and the
     * "Implementation-Version" must exist in the MANIFEST.MF. This can be set by adding the following to the pom:
     * 
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
     * 
     * Thanks to the Zanata Team for this method, it has just been edited for this libraries needs.
     * 
     * @param clazz The class to find the Version Information for.
     * @return The Version number or "unknown" if the version couldn't be found.
     */
    public static String getVersionInfo(final Class<?> clazz) {
        Attributes atts = null;
        String version = null;
        try {
            atts = getJarAttributesForClass(clazz);
        } catch (IOException ex) {
            ExceptionUtilities.handleException(ex);
        }

        if (atts != null) {
            version = atts.getValue("Implementation-Version");
        }

        // if we can't get version from the jar, try for the package version
        if (version == null) {
            Package pkg = clazz.getPackage();
            if (pkg != null)
                version = pkg.getImplementationVersion();
        }
        if (version == null)
            version = "unknown";
        return version;
    }

    /**
     * Get the Manifest Attributes from a JAR Archive for a specific class.
     * 
     * Thanks to the Zanata Team for this method, it has just been edited for this libraries needs.
     * 
     * @param clazz The class to find the version information for.
     * @return The Attributes that exist in the Jars Manifest for the spcified class.
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Attributes getJarAttributesForClass(final Class<?> clazz) throws MalformedURLException, IOException {
        // thanks to
        // http://stackoverflow.com/questions/1272648/need-to-read-own-jars-manifest-and-not-root-classloaders-manifest/1273432#1273432
        final String className = clazz.getSimpleName() + ".class";
        final String classPath = clazz.getResource(className).toString();
        final String manifestPath;
        if (classPath.startsWith("vfszip:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf(".jar/") + ".jar/".length()) + "META-INF/MANIFEST.MF";
        } else if (classPath.startsWith("vfs:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf(".jar/") + ".jar/".length()) + "META-INF/MANIFEST.MF";
        } else if (classPath.startsWith("jar:")) {
            manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + "!".length()) + "/META-INF/MANIFEST.MF";
        } else {
            return null;
        }

        final Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        final Attributes attr = manifest.getMainAttributes();
        return attr;
    }
}
