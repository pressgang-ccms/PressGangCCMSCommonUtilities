package org.jboss.pressgang.ccms.utils.common;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyUtils.class);

    public static String getProperty(final String fileName, final String property, final Class<?> classWithResources) {
        final Properties properties = new Properties();
        try {
            final InputStream inputStream = classWithResources.getResourceAsStream(fileName);
            properties.load(inputStream);
        } catch (final Exception ex) {
            LOG.debug("Unable to find resource file", ex);
        }

        return properties.getProperty(property);
    }
}
