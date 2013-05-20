package org.jboss.pressgang.ccms.utils.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZipUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(ZipUtilities.class);
    private static int BUFFER_SIZE = 2048;

    public static byte[] createZip(final HashMap<String, byte[]> files) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ZipOutputStream zipfile = new ZipOutputStream(bos);
            for (final Entry<String, byte[]> fileEntry : files.entrySet()) {
                final ZipEntry zipentry = new ZipEntry(fileEntry.getKey());
                zipfile.putNextEntry(zipentry);
                zipfile.write(fileEntry.getValue());
            }
            zipfile.close();
            return bos.toByteArray();
        } catch (final Exception ex) {
            LOG.error("Unable to create ZIP", ex);
        }

        return null;
    }

    public static void createZipMap(final File path, final String absolutePathPrefix, final HashMap<String, byte[]> fileMap) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (final File file : files) {
                if (file.isDirectory()) {
                    createZipMap(file, absolutePathPrefix, fileMap);
                } else {
                    final String absolutePath = file.getAbsolutePath();
                    final String relativePath = absolutePath.replace(absolutePathPrefix, "");

                    try {
                        fileMap.put(relativePath, FileUtilities.readFileContents(file).getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        // UTF-8 is a valid format so this should exception should never get thrown
                        LOG.debug("", e);
                    }
                }
            }
        }
    }

    /**
     * Takes a ZIP file (as a byte array), and extracts its contents into a map of filename to file contents
     */
    public static void mapZipFile(final byte[] zipFile, final Map<String, byte[]> output, final String keyPrefix,
            final String keyUnPrefix) {
        try {
            final String fixedKeyPrefix = keyPrefix == null ? "" : keyPrefix;
            final String fixedKeyUnPrefix = keyUnPrefix == null ? "" : keyUnPrefix;
            final ZipInputStream zf = new ZipInputStream(new ByteArrayInputStream(zipFile));

            ZipEntry ze = null;
            while ((ze = zf.getNextEntry()) != null) {
                final String name = ze.getName();
                final long size = ze.getSize();

                // see if we are working with a file or a directory
                if (size != 0) {
                    byte[] fileContents = new byte[0];
                    final byte[] fileBuffer = new byte[BUFFER_SIZE];

                    int bytesRead = 0;
                    while ((bytesRead = zf.read(fileBuffer, 0, BUFFER_SIZE)) != -1) fileContents = ArrayUtils.addAll(fileContents,
                            bytesRead == BUFFER_SIZE ? fileBuffer : ArrayUtils.subarray(fileBuffer, 0, bytesRead));

                    output.put(fixedKeyPrefix + name.replace(fixedKeyUnPrefix, ""), fileContents);
                }
            }

        } catch (final IOException ex) {
           LOG.error("Unable to read file contents", ex);
        }
    }

    public static void mapZipFile(final byte[] zipFile, final Map<String, byte[]> output, final String keyPrefix) {
        mapZipFile(zipFile, output, keyPrefix, "");
    }

    public static void mapZipFile(final byte[] zipFile, final Map<String, byte[]> output) {
        mapZipFile(zipFile, output, "", "");
    }

    public static Map<String, byte[]> mapZipFile(final byte[] zipFile, final String keyPrefix, final String keyUnPrefix) {
        final Map<String, byte[]> retValue = new HashMap<String, byte[]>();
        mapZipFile(zipFile, retValue, keyPrefix, keyUnPrefix);
        return retValue;
    }

    public static Map<String, byte[]> mapZipFile(final byte[] zipFile, final String keyPrefix) {
        return mapZipFile(zipFile, keyPrefix, "");
    }

    public static Map<String, byte[]> mapZipFile(final byte[] zipFile) {
        return mapZipFile(zipFile, "", "");
    }

    /**
     * Unzips a Zip File in byte array format to a specified directory.
     *
     * @param zipBytes  The zip file.
     * @param directory The directory to unzip the file to.
     * @return true if the file was successfully unzipped otherwise false.
     */
    public static boolean unzipFileIntoDirectory(final File zipFile, final String directory) {
        final Map<ZipEntry, byte[]> zipEntries = new HashMap<ZipEntry, byte[]>();
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
        } catch (FileNotFoundException ex) {
            LOG.error("Unable to find specified file", ex);
            return false;
        }
        ZipEntry entry = null;
        byte[] buffer = new byte[1024];
        try {
            int read;
            while ((entry = zis.getNextEntry()) != null) {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((read = zis.read(buffer)) > 0) {
                    bos.write(buffer, 0, read);
                }
                zipEntries.put(entry, bos.toByteArray());
            }
        } catch (IOException ex) {
            LOG.error("Unable to write ZIP file to directory", ex);
            return false;
        }
        return unzipFileIntoDirectory(zipEntries, directory);
    }

    /**
     * @param zipFile
     * @param directory
     */
    private static boolean unzipFileIntoDirectory(final Map<ZipEntry, byte[]> zipEntries, final String directory) {
        // Check the directory specified is a directory
        final File dir = new File(directory);
        if (!dir.isDirectory()) return false;

        File file = null;
        FileOutputStream fos = null;

        for (final Entry<ZipEntry, byte[]> entry : zipEntries.entrySet()) {
            final ZipEntry zipEntry = entry.getKey();
            try {
                file = new File(dir.getAbsolutePath() + File.separator + stripZipFileName(zipEntry.getName()));

                // If the ZipEntry is a directory then create it
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }
                // Else the ZipEntry is a file then make sure its directory exists
                // and then create the file
                else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                // Write the contents of the zip entry into the File
                fos = new FileOutputStream(file);
                fos.write(entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static String stripZipFileName(final String name) {
        return name.replaceFirst("^[^/]*/", "");
    }
}
