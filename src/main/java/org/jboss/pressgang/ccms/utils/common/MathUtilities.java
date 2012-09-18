package org.jboss.pressgang.ccms.utils.common;

import java.util.Date;

public final class MathUtilities
{
	public static boolean generateRandomBoolean()
	{
		return Math.random() >= 0.5;
	}
	
	public static int generateRandomInt(final int max)
	{
		return (int)(Math.random() * max);
	}
	
	public static Date generateRandomDate()
	{
		return new Date((long)Math.random() * Long.MAX_VALUE);
	}
}
