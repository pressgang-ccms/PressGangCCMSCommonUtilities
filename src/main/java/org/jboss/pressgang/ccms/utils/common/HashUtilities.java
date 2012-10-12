package org.jboss.pressgang.ccms.utils.common;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

/**
 * A collection of static methods for working with has codes.
 * @author Matthew Casperson
 */
public class HashUtilities
{
	/**
	 * Generates a MD5 Hash for a specific string
	 * 
	 * @param input The string to be converted into an MD5 hash.
	 * @return The MD5 Hash string of the input string.
	 */
	public static String generateMD5(final String input) {
		try
		{
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			byte[] digest = messageDigest.digest(input.getBytes("UTF-8"));
			return new String(Hex.encodeHex(digest));
		}
		catch (Exception e)
		{
			ExceptionUtilities.handleException(e);
			return null;
		}
	}
}
