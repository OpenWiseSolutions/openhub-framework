/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.common;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;


/**
 * The common {@link String} utilities.
 *
 * @author Jan Loose
 */
public final class Strings {

    public static final String HTML_REMOVE_REGEX = "<[a-zA-Z\\/\\-!\"'][^>]*>";

    public static final Pattern HTML_REMOVE_PATTERN = Pattern.compile(HTML_REMOVE_REGEX);

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String NB_SPACE = "&nbsp;";

    public static final String HTTP_PREFIX = "http://";

    public static final String HTTPS_PREFIX = "https://";

    public static final String AMP = "&";

    public static final String AMP_XML = "&amp;";

    /**
     * Common string list delimiters.
     *
     * @see #tokenizeStringList(String)
     */
    public static final String LIST_DELIMITERS = ",; \t\n";

    /** The UTF-8 charset constant. */
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    /**
     * Removes the HTML sequences from an input string.
     *
     * @param str the input string
     * @return the char sequence without HTML
     */
    @Nullable
    public static CharSequence removeHtml(@Nullable String str) {
        if (!isEmpty(str)) {
            StringBuilder sb = new StringBuilder();

            Matcher m = HTML_REMOVE_PATTERN.matcher(str);

            int last = 0;
            while (m.find()) {
                sb.append(str.substring(last, m.start()).replace('"', '\''));
                last = m.end();
            }
            sb.append(str.substring(last));

            return sb;
        }

        return str;
    }

    /**
     * Prefixes url with 'http://' if not absolute url already.
     * null -> 'http://'.
     *
     * @param url url
     * @return url
     */
    @Nullable
    public static String toExternalUrl(@Nullable String url) {
        if (isAbsoluteUrl(url)) {
            return url;
        }
        return (url == null) ? HTTP_PREFIX : (HTTP_PREFIX + url);
    }

    /**
     * Returns true if url address starts with 'http://' or 'https://'.
     *
     * @param url url
     * @return boolean
     */
    public static boolean isAbsoluteUrl(@Nullable String url) {
        return (StringUtils.startsWith(url, HTTP_PREFIX) || StringUtils.startsWith(url, HTTPS_PREFIX));
    }

    /**
     * Convert a comma-separated list of numbers to a {@link List} of {@link Integer}s. Non-numbers are skipped.
     *
     * @param str the string to be parsed
     * @return the list of integers
     */
    public static List<Integer> convertCommaSeparatedNumbersToList(@Nullable String str) {
        List<Integer> result = new ArrayList<Integer>();
        str = StringUtils.replace(str, " ", EMPTY);
        if (isNotBlank(str)) {
            String[] rowsStr = str.split(",");
            for (String rowStr : rowsStr) {
                if (isNotBlank(rowStr)) {
                    try {
                        result.add(Integer.valueOf(rowStr));
                    } catch (NumberFormatException e) {
                        // skip it
                    }
                }
            }
        }
        return result;
    }

    /**
     * Convert a {@link List} to a comma-separated string.
     *
     * @param list the list
     * @return the comma-separated string or ""
     */
    public static String convertListToCommaSeparatedString(@Nullable List<?> list) {
        if (list == null || list.isEmpty()) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        String sep = EMPTY;
        for (Object object : list) {
            if (object != null) {
                sb.append(sep).append(object);
                sep = ",";
            }
        }
        return sb.toString();
    }

    /**
     * Converts an array to the string.
     *
     * @param array the array that has to be converted
     * @return the array as a string
     */
    public static String arrayToString(Object[] array) {
        if (array == null) {
            return "null";
        }
        int i;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (i = 0; i < array.length; i++) {
            if (array[i] != null) {
                sb.append(array[i].toString());
            } else {
                sb.append("null");
            }
            sb.append(',');
        }
        if (i > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Substitutes all places of the message using a values. See {@link MessageFormatter} for more detail.
     *
     * @param message the message with places defined as {}
     * @param values an array of values that are used to substitute formatting places of the message
     * @return the final string
     */
    public static String fm(String message, Object... values) {
        if (isBlank(message) || ArrayUtils.isEmpty(values)) {
            return message;
        }
        return MessageFormatter.arrayFormat(message, values).getMessage();
    }

    /**
     * Removes last character ',' from a builder if exists.
     *
     * @param sb string builder
     * @return original string builder
     */
    public static StringBuilder removeLastComma(StringBuilder sb) {
        return removeLastCharacter(sb, ',');
    }

    /**
     * Removes last character from a builder if exists.
     *
     * @param sb string builder
     * @param c the the character to be removed
     * @return original string builder
     */
    public static StringBuilder removeLastCharacter(StringBuilder sb, char c) {
        if (sb == null) {
            return null;
        }
        int len = sb.length();
        if (len == 0) {
            return sb;
        }
        if (sb.charAt(len - 1) == c) {
            sb.setLength(len - 1);
        }
        return sb;
    }

    /**
     * This code was copied from Apache Wicket.
     * Converts a Java String to an HTML markup string, but does not convert normal spaces to
     * non-breaking space entities (&lt;nbsp&gt;).
     *
     * @param s the string to escape
     * @return The escaped string
     * @see #escapeMarkup(CharSequence, boolean)
     */
    public static CharSequence escapeMarkup(CharSequence s) {
        return escapeMarkup(s, false);
    }

    /**
     * This code was copied from Apache Wicket.
     * Converts a Java String to an HTML markup String by replacing illegal characters with HTML
     * entities where appropriate. Spaces are converted to non-breaking spaces (&lt;nbsp&gt;) if
     * escapeSpaces is true, tabs are converted to four non-breaking spaces, less than signs are
     * converted to &amp;lt; entities and greater than signs to &amp;gt; entities.
     *
     * @param s the string to escape
     * @param escapeSpaces true to replace ' ' with nonbreaking space
     * @return The escaped string
     */
    public static CharSequence escapeMarkup(CharSequence s, boolean escapeSpaces) {
        return escapeMarkup(s, escapeSpaces, false);
    }

    /**
     * This code was copied from Apache Wicket.
     * Converts a Java String to an HTML markup String by replacing illegal characters with HTML
     * entities where appropriate. Spaces are converted to non-breaking spaces (&lt;nbsp&gt;) if
     * escapeSpaces is true, tabs are converted to four non-breaking spaces, less than signs are
     * converted to &amp;lt; entities and greater than signs to &amp;gt; entities.
     *
     * @param s the string to escape
     * @param escapeSpaces true to replace ' ' with nonbreaking space
     * @param convertToHtmlUnicodeEscapes true to convert non-7 bit characters to unicode HTML (&#...)
     * @return the escaped string
     */
    public static CharSequence escapeMarkup(CharSequence s, boolean escapeSpaces, boolean convertToHtmlUnicodeEscapes) {
        if (s == null) {
            return null;
        } else {
            int len = s.length();
            final StringBuilder sb = new StringBuilder((int) (len * 1.1));

            for (int i = 0; i < len; i++) {
                final char c = s.charAt(i);

                switch (c) {
                    case '\t':
                        if (escapeSpaces) {
                            // Assumption is four space tabs (sorry, but that's
                            // just how it is!)
                            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                        } else {
                            sb.append(c);
                        }
                        break;

                    case ' ':
                        if (escapeSpaces) {
                            sb.append("&nbsp;");
                        } else {
                            sb.append(c);
                        }
                        break;

                    case '<':
                        sb.append("&lt;");
                        break;

                    case '>':
                        sb.append("&gt;");
                        break;

                    case '&':

                        sb.append(AMP_XML);
                        break;

                    case '"':
                        sb.append("&quot;");
                        break;

                    case '\'':
                        sb.append("&#039;");
                        break;

                    default:

                        int ci = 0xffff & c;
                        if (ci < 0x20) {
                            // http://en.wikipedia.org/wiki/Valid_characters_in_XML
                            if ((ci != 0x09) && (ci != 0x0A) && (ci != 0x0D)) {
                                sb.append("&#");
                                sb.append(Integer.toString(ci));
                                sb.append(';');
                                break;
                            }
                        }

                        if (convertToHtmlUnicodeEscapes) {
                            if (ci < 160) {
                                // nothing special only 7 Bit
                                sb.append(c);
                            } else {
                                // Not 7 Bit use the unicode system
                                sb.append("&#");
                                sb.append(Integer.toString(ci));
                                sb.append(';');
                            }
                        } else {
                            sb.append(c);
                        }

                        break;
                }
            }

            return sb;
        }
    }

    /**
     * Tokenizes the given string list into an array of strings.
     *
     * @param list the string list to be tokenized
     * @return the tokenized list (an empty array in case of a {@code null} list)
     * @see #LIST_DELIMITERS
     */
    public static String[] tokenizeStringList(@Nullable String list) {
        if (list == null) {
            return new String[0];
        } else {
            return tokenizeToStringArray(list, LIST_DELIMITERS);
        }
    }

    /**
     * @param input the input
     * @param text the searched text
     * @return the index or -1
     */
    public static int indexOf(CharSequence input, String text) {
        if (input instanceof String) {
            return ((String) input).indexOf(text);
        }
        if (input instanceof StringBuilder) {
            return ((StringBuilder) input).indexOf(text);
        }
        if (input instanceof StringBuffer) {
            return ((StringBuffer) input).indexOf(text);
        }
        return input.toString().indexOf(text);
    }

    /**
     * @param input the input
     * @param c the searched character
     * @return the index or -1
     */
    public static int indexOf(CharSequence input, char c) {
        if (input instanceof String) {
            return ((String) input).indexOf(c);
        }
        for (int i = 0; i < input.length(); i++) {
            if (c == input.charAt(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param input the {@link CharSequence} to be trimmed
     * @return the trimmed char sequence
     */
    @Nullable
    public static CharSequence trim(@Nullable CharSequence input) {
        if (input == null) {
            return null;
        }

        if (input.length() == 0) {
            return input;
        }

        if (input instanceof String) {
            return ((String) input).trim();
        }

        int count = input.length();
        int len = count;
        int st = 0;
        int off = 0;

        while ((st < len) && (input.charAt(off + st) <= ' ')) {
            st++;
        }
        while ((st < len) && (input.charAt((off + len) - 1) <= ' ')) {
            len--;
        }

        if ((st == 0) && (input instanceof StringBuilder)) {
            ((StringBuilder) input).setLength(len);
            return input;
        }

        return ((st > 0) || (len < count)) ? input.subSequence(st, len) : input;
    }

    public static boolean isEmpty(@Nullable  CharSequence c) {
        return (c == null) || (c.length() == 0);
    }

    public static boolean isNotEmpty(@Nullable CharSequence c) {
        return !isEmpty(c);
    }

    public static boolean isBlank(@Nullable CharSequence c) {
        return (c == null) || (c.length() == 0) || (trim(c).length() == 0);
    }

    public static boolean isNotBlank(@Nullable CharSequence c) {
        return !isBlank(c);
    }

    @Nullable
    public static String toString(@Nullable CharSequence c) {
        return c == null ? null : c.toString();
    }

    /**
     * Replace all occurrences of one string replaceWith another string.
     *
     * @param s
     *            The string to process
     * @param searchFor
     *            The value to search for
     * @param replaceWith
     *            The value to searchFor replaceWith
     * @return The resulting string with searchFor replaced with replaceWith
     */
    public static CharSequence replaceAll(CharSequence s, CharSequence searchFor, CharSequence replaceWith) {
        if (s == null) {
            return null;
        }

        // If searchFor is null or the empty string, then there is nothing to
        // replace, so returning s is the only option here.
        if ((searchFor == null) || EMPTY.equals(searchFor)) {
            return s;
        }

        // If replaceWith is null, then the searchFor should be replaced with
        // nothing, which can be seen as the empty string.
        if (replaceWith == null) {
            replaceWith = EMPTY;
        }

        String searchString = searchFor.toString();
        // Look for first occurrence of searchFor
        int matchIndex = search(s, searchString, 0);
        if (matchIndex == -1) {
            // No replace operation needs to happen
            return s;
        } else {
            // Allocate a AppendingStringBuffer that will hold one replacement
            // with a
            // little extra room.
            int size = s.length();
            final int replaceWithLength = replaceWith.length();
            final int searchForLength = searchFor.length();
            if (replaceWithLength > searchForLength) {
                size += (replaceWithLength - searchForLength);
            }
            final StringBuilder sb = new StringBuilder(size + 16);

            int pos = 0;
            do {
                // Append text up to the match`
                append(sb, s, pos, matchIndex);

                // Add replaceWith text
                sb.append(replaceWith);

                // Find next occurrence, if any
                pos = matchIndex + searchForLength;
                matchIndex = search(s, searchString, pos);
            } while (matchIndex != -1);

            // Add tail of s
            sb.append(s.subSequence(pos, s.length()));

            // Return processed buffer
            return sb;
        }
    }

    private static int search(final CharSequence s, String searchString, int pos) {
        if (s == null) {
            return -1;
        }

        int matchIndex = -1;
        if (s instanceof String) {
            matchIndex = ((String) s).indexOf(searchString, pos);
        } else if (s instanceof StringBuffer) {
            matchIndex = ((StringBuffer) s).indexOf(searchString, pos);
        } else if (s instanceof StringBuilder) {
            matchIndex = ((StringBuilder) s).indexOf(searchString, pos);
        } else {
            matchIndex = s.toString().indexOf(searchString, pos);
        }

        return matchIndex;
    }

    private static void append(StringBuilder buffer, CharSequence s, int from, int to) {
        if (s instanceof StringBuilder) {
            buffer.append(s, from, to);
        } else if (s instanceof StringBuffer) {
            buffer.append(s, from, to);
        } else {
            buffer.append(s.subSequence(from, to));
        }
    }

    /**
     * Checks an input - it escapes the input for HTML and JavaScript.
     *
     * @param str the string to be checked
     * @return the checked string
     */
    @Nullable
    public static String checkInput(@Nullable String str) {
        return str == null ? str : escapeMarkup(StringEscapeUtils.escapeEcmaScript(str)).toString();
    }

    private Strings() {
        // prevents instantiation
    }
}
