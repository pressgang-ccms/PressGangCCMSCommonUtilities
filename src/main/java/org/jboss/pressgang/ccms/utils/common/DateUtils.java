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

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Note: These methods are pulled directly from the Apache Commons Lang 3.2-SNAPSHOT at http://svn.apache
 * .org/viewvc/commons/proper/lang/trunk/src/main/java/org/apache/commons/lang3/time/DateUtils
 * .java?revision=1388806&view=co&pathrev=1388806 and should be replaced by the Commons Lang 3.2 version when it is released.
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {
    //-----------------------------------------------------------------------

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     * <p/>
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * The parser will be lenient toward the parsed date.
     *
     * @param str           the date to parse, not null
     * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException           if none of the date patterns were suitable (or there were none)
     */
    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        return parseDate(str, null, parsePatterns);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers,
     * using the default date format symbols for the given locale.</p>
     * <p/>
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * The parser will be lenient toward the parsed date.
     *
     * @param str           the date to parse, not null
     * @param locale        the locale whose date format symbols should be used. If <code>null</code>,
     *                      the system locale is used (as per {@link #parseDate(String, String...)}).
     * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException           if none of the date patterns were suitable (or there were none)
     * @since 3.2
     */
    public static Date parseDate(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, true);
    }

    // ------------------------------------------------------------------------------------

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     * <p/>
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * The parser parses strictly - it does not allow for dates such as "February 942, 1996".
     *
     * @param str           the date to parse, not null
     * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException           if none of the date patterns were suitable
     * @since 2.5
     */
    public static Date parseDateStrictly(String str, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, null, parsePatterns, false);
    }

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     *
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     * The parser parses strictly - it does not allow for dates such as "February 942, 1996".
     *
     * @param str  the date to parse, not null
     * @param locale        the locale whose date format symbols should be used. If <code>null</code>,
     *                      the system locale is used (as per {@link #parseDate(String, String...)}).
     * @param parsePatterns  the date format patterns to use, see SimpleDateFormat, not null
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException if none of the date patterns were suitable
     * @since 3.2
     */
    public static Date parseDateStrictly(String str, Locale locale, String... parsePatterns) throws ParseException {
        return parseDateWithLeniency(str, locale, parsePatterns, false);
    }

    /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     * <p/>
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     *
     * @param str           the date to parse, not null
     * @param locale        the locale to use when interpretting the pattern, can be null in which
     *                      case the default system locale is used
     * @param parsePatterns the date format patterns to use, see SimpleDateFormat, not null
     * @param lenient       Specify whether or not date/time parsing is to be lenient.
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException           if none of the date patterns were suitable
     * @see java.util.Calender#isLenient()
     */
    public static Date parseDateWithLeniency(String str, Locale locale, String[] parsePatterns, boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        SimpleDateFormat parser;
        if (locale == null) {
            parser = new SimpleDateFormat();
        } else {
            parser = new SimpleDateFormat("", locale);
        }

        parser.setLenient(lenient);
        ParsePosition pos = new ParsePosition(0);
        for (String parsePattern : parsePatterns) {

            String pattern = parsePattern;

            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }

            parser.applyPattern(pattern);
            pos.setIndex(0);

            String str2 = str;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePattern.endsWith("ZZ")) {
                str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
            }

            Date date = parser.parse(str2, pos);
            if (date != null && pos.getIndex() == str2.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }
}
