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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A collection of static methods to create and manipulate collections
 *
 * @author Matthew Casperson
 */
public class CollectionUtilities {
    public static <T extends Comparable<? super T>> List<T> sortAndReturn(final List<T> list) {
        Collections.sort(list);
        return list;
    }

    /**
     * @param array A collection of elements to be included in the returned ArrayList
     * @return An ArrayList that includes all the elements passed in via the array parameter
     */
    public static <T> ArrayList<T> toArrayList(final T... array) {
        final ArrayList<T> retValue = new ArrayList<T>();
        for (final T item : array)
            retValue.add(item);
        return retValue;
    }

    /**
     * @param items The Collection of items to be converted to an ArrayList
     * @return An ArrayList containing the elements in the set
     */
    public static <T> ArrayList<T> toArrayList(final Collection<T> items) {
        final ArrayList<T> retValue = new ArrayList<T>();
        for (final T item : items)
            retValue.add(item);
        return retValue;
    }

    /**
     * @param array The elements to be included in the returned ArrayList
     * @return An ArrayList containing the String representation of the elements passed in via the array parameter
     */
    public static ArrayList<String> toStringArrayList(final Object... array) {
        final ArrayList<String> retValue = new ArrayList<String>();
        for (final Object item : array)
            retValue.add(item.toString());
        return retValue;
    }

    /**
     * @param input       The original collection of Strings
     * @param originalRE  The regular expression to match
     * @param replacement The String to be replace any instance of <code>originalRE</code> with
     * @return A collection of strings where <code>originalRE</code> has been replaced with </code>replacement</code>
     */
    public static List<String> replaceStrings(final List<String> input, final String originalRE, final String replacement) {
        final List<String> retValue = new ArrayList<String>();
        for (final String element : input) {
            retValue.add(element.replaceAll(originalRE, replacement));
        }
        return retValue;
    }

    /**
     * Merge arrays
     *
     * @param array The source arrays
     * @return A collection that contains the contents of all the arrays passed to <code>array</code>
     */
    public static <T> ArrayList<T> mergeLists(final T... array) {
        final ArrayList<T> retValue = new ArrayList<T>();
        Collections.addAll(retValue, array);
        return retValue;
    }

    public static <T> int addAll(final T[] source, final Collection<T> destination) {
        int count = 0;
        for (final T sourceItem : source) {
            destination.add(sourceItem);
            ++count;
        }

        return count;
    }

    public static <T> int addAllThatDontExist(final Collection<T> source, final Collection<T> destination) {
        int count = 0;
        for (final T sourceItem : source) {
            if (!destination.contains(sourceItem)) {
                destination.add(sourceItem);
                ++count;
            }
        }

        return count;
    }

    public static <T> String toSeperatedString(final Collection<T> list) {
        final StringBuffer stringBuffer = new StringBuffer();
        for (final T element : list) {
            if (stringBuffer.length() != 0) stringBuffer.append(",");
            stringBuffer.append(element.toString());
        }
        return stringBuffer.toString();
    }

    public static <T> String toSeperatedString(final Collection<T> list, final String separator) {
        final StringBuffer stringBuffer = new StringBuffer();
        for (final T element : list) {
            if (stringBuffer.length() != 0) stringBuffer.append(separator);
            stringBuffer.append(element.toString());
        }
        return stringBuffer.toString();
    }

    /**
     * Merges two arrays together
     *
     * @param first  The first source array
     * @param second The second source array
     * @return An array that combines the two arrays
     */
    public static <T> T[] concat(final T[] first, final T[] second) {
        /* deal with null inputs */
        if (first == null && second == null) return null;
        if (first == null) return second;
        if (second == null) return first;

        final T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Provides an easy way to compare two possibly null comparable objects
     *
     * @param first  The first object to compare
     * @param second The second object to compare
     * @return < 0 if the first object is less than the second object, 0 if they are equal, and > 0 otherwise
     */
    public static <T extends Comparable<? super T>> Integer getSortOrder(final T first, final T second) {
        if (first == null && second == null) return null;

        if (first == null && second != null) return -1;

        if (first != null && second == null) return 1;

        return first.compareTo(second);
    }

    /**
     * Provides an easy way to see if two possibly null objects are equal
     *
     * @param first  The first object to test
     * @param second The second to test
     * @return true if both objects are equal, false otherwise
     */
    public static <T> boolean isEqual(final T first, final T second) {
        /* test to see if they are both null, or both reference the same object */
        if (first == second) return true;

        if (first == null && second != null) return false;

        return first.equals(second);
    }

    public static List<Integer> toAbsIntegerList(final Collection<Integer> array) {
        final ArrayList<Integer> retValue = new ArrayList<Integer>();
        for (final Integer item : array)
            retValue.add(Math.abs(item));
        return retValue;
    }

    /**
     * Trims an array of Strings to remove the whitespace. If the string is empty then its removed from the array.
     *
     * @param input The array of strings to be trimmed
     * @return The same array of strings but all elements have been trimmed of whitespace
     */
    public static String[] trimStringArray(final String[] input) {
        final ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < input.length; i++) {
            String s = input[i].trim();
            if (!s.equals("")) output.add(s);
        }
        return output.toArray(new String[0]);
    }
}
