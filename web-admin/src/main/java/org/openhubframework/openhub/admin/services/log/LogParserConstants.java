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

package org.openhubframework.openhub.admin.services.log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

public class LogParserConstants {
    public static final String VIEW_REQUEST_PARAMETER = "view";
    public static final String GROUP_BY_REQUEST_PARAMETER = "groupBy";
    public static final String GROUP_SIZE_REQUEST_PARAMETER = "groupSize";

    public static final int MAX_RESULT_LIMIT = 500;
    public static final int DEFAULT_GROUP_SIZE = 10;
    public static final List<String> DEFAULT_GROUP_BY_PROPERTY = Collections.singletonList("REQUEST_ID");

    /**
     * Date pattern is used for seeking functionality
     * - rapidly going through log in search of a specific date.
     * It should contain a single group which contains the whole date found.
     */
    public static final Pattern LOG_LINE_DATE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}[,.]\\d{3})");
    /**
     * Matches the log line with each value being in a separate matching group.
     * See the logback.xml config for up-to-date log appender pattern:
     * <p/>
     * %d{ISO8601} [${MACHINE}, %thread, %X{REQUEST_URI}, %X{REQUEST_ID}, %X{SESSION_ID}, %X{SOURCE_SYSTEM}, %X{CORRELATION_ID}] %-5level %logger{36} - %msg%n
     */
    public static final Pattern LOG_LINE_PROPERTIES_PATTERN = Pattern.compile(
            // [serverId, MACHINE, thread, REQUEST_URI, REQUEST_ID, SESSION_ID, SOURCE_SYSTEM, CORRELATION_ID] level logger -
            "\\s+\\[(.*?), (.*?), (.*?), (.*?), (.*?), (.*?), (.*?), (.*?)\\]\\s+(\\S+)\\s+(\\S+)\\s+-\\s+");
    /**
     * Group names in the same order they are present in {@link #LOG_LINE_PROPERTIES_PATTERN}.
     */
    public static final List<String> LOG_LINE_PROPERTIES = Collections.unmodifiableList(Arrays.asList("serverId",
            "MACHINE", "thread", "REQUEST_URI", "REQUEST_ID", "SESSION_ID", "SOURCE_SYSTEM", "CORRELATION_ID", "level", "logger"));
    /**
     * Printer that can print logback ISO8601 "yyyy-MM-dd HH:mm:ss,SSS" (with space)
     * as opposed to printing standard ISO8601 "yyyy-MM-dd'T'HH:mm:ss,SSS" (with T).
     * <p/>
     * Time zones are neither printed, nor parsed.
     */
    public static final DateTimeFormatter LOGBACK_ISO8601_FORMAT = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.date())
            .appendLiteral(' ')
            .append(ISODateTimeFormat.hourMinuteSecondFraction())
            .toFormatter();
    /**
     * Parser that can parse logback ISO8601 "yyyy-MM-dd HH:mm:ss,SSS" (with space)
     * as opposed to parsing standard ISO8601 "yyyy-MM-dd'T'HH:mm:ss,SSS" (with T)
     */
    private static final DateTimeParser LOGBACK_ISO8601_OPTIONAL_TIME_PARSER = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.dateElementParser())
            .appendOptional(new DateTimeFormatterBuilder()
                    .appendLiteral(' ')
                    .append(ISODateTimeFormat.timeElementParser())
                    .toParser()
            ).toParser();
    /**
     * Formatter that prints full ISO8601 date with 'T',
     * but can parse full date and time with either 'T' or space as separator,
     * and time being optionally only partial (such as just hours, hours and minutes, etc.).
     * <p/>
     * Time zones are neither printed, nor parsed.
     */
    public static final DateTimeFormatter LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT = new DateTimeFormatterBuilder()
            .append(LOGBACK_ISO8601_FORMAT.getPrinter(), new DateTimeParser[]{
                    LOGBACK_ISO8601_OPTIONAL_TIME_PARSER,
                    ISODateTimeFormat.dateOptionalTimeParser().getParser()
            }).toFormatter();
}
