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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

import org.openhubframework.openhub.common.log.Log;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Parses log files based on date and returns lines which follow specific patterns.
 */
@Component
public class LogParser {

    // log file format: logFile_%d{yyyy-MM-dd}_%i.log
    public static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final String FILE_EXTENSION = "log";

    /**
     * Absolute path to the folder with log files.
     */
    @Value("${log.folder.path}")
    private String logFolderPath;

    public File[] getLogFiles(final DateTime date) throws FileNotFoundException {
        File logFolder = new File(logFolderPath);
        if (!logFolder.exists() || !logFolder.canRead()) {
            throw new FileNotFoundException("there is no readable log folder - " + logFolderPath);
        }

        final String logDateFormatted = FILE_DATE_FORMAT.print(date);
        final long dateMillis = date.getMillis();

        File[] files = logFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                return name.endsWith(FILE_EXTENSION) // it's a log file
                        && name.contains(logDateFormatted) // it contains the date in the name
                        && file.lastModified() >= dateMillis; // and it's not older than the search date
            }
        });

        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);

        if (files.length == 0) {
            Log.debug("No log files ending with {}, containing {}, modified after {}, at {}",
                    FILE_EXTENSION, logDateFormatted, date, logFolderPath);
        } else {
            Log.debug("Found log files for {}: {}", date, files);
        }

        return files;
    }

    public Iterator<LogEvent> getLogEventIterator(LogParserConfig config, Collection<File> files) throws IOException {
        return new LogEventParsingIterator(this, config, files);
    }


    /**
     * Processes the next log line by either appending it to the last log event,
     * if it's not a valid log line and last log event wasn't ignored (appendTo != null);
     * or by parsing it into a new event (parseTo).
     *
     * @param line     the log line to process
     * @param parseTo  the next log event to parse line into
     * @param appendTo the log event to append non-event lines to
     * @param config   log parser config
     * @return appendTo event, if the line was appended to it; parseTo event if the line was fully parsed into this event;
     * or null if the line was ignored
     */
    LogEvent parseLine(String line, LogEvent parseTo, LogEvent appendTo, LogParserConfig config) {
        Matcher dateMatcher = config.getDatePattern().matcher(line);
        if (!dateMatcher.lookingAt()) {
            return parseFailed(line, appendTo);
        }

        DateTime eventDate = getDate(dateMatcher.group(1), config);
        if (eventDate == null) {
            return parseFailed(line, appendTo);
        }

        // line might still not match properties and therefore not be a new log event,
        // so don't stop just yet, even if the date is wrong
        boolean skipLogLine = eventDate.isBefore(config.getFromDate());
        if (skipLogLine && appendTo == null) {
            return null; // no point continuing, since this line wouldn't be appended anyway
        }

        parseTo.setDate(eventDate);
        String unmatched = line.substring(0, dateMatcher.start())
                + line.substring(dateMatcher.end(), line.length());

        // date matches, but is the line a new log event line?
        Matcher propertiesMatcher = config.getPropertiesPattern().matcher(unmatched);
        if (!propertiesMatcher.lookingAt()) {
            return parseFailed(line, appendTo);
        }

        if (skipLogLine || !parseEventProperties(propertiesMatcher, parseTo, config)) {
            return null;
        }

        if (unmatched != null && config.getMsg() != null && !unmatched.contains(config.getMsg())) {
            return null;
        }

        unmatched = unmatched.substring(0, propertiesMatcher.start())
                + unmatched.substring(propertiesMatcher.end(), unmatched.length());

        parseTo.setMessage(unmatched);
        return parseTo;
    }

    private LogEvent parseFailed(String line, LogEvent appendTo) {
        if (appendTo != null) {
            appendTo.appendMessage("\n" + line);
        }
        return appendTo;
    }

    /**
     * Parses a log line into a new LogEvent,
     * verifying fields against required values specified by {@link LogParserConfig#getFilter()}.
     *
     * @param matcher the matcher generated based on {@link LogParserConfig#getPropertiesPattern()}
     * @return true if log event properties were parsed from the matcher; false otherwise
     */
    private boolean parseEventProperties(Matcher matcher, LogEvent logEvent, LogParserConfig config) {
        assert matcher.groupCount() >= config.getPropertyCount();
        for (int propertyIndex = 0; propertyIndex < logEvent.getPropertyCount(); propertyIndex++) {
            String propertyValue = matcher.group(propertyIndex + 1);
            if (!config.isMatchesFilter(propertyValue, propertyIndex)) {
                return false;
            }
            logEvent.getProperties()[propertyIndex] = propertyValue;
        }
        return true;
    }

    private DateTime getDate(String dateString, LogParserConfig config) {
        try {
            return config.getDateFormat().parseDateTime(dateString);
        } catch (IllegalArgumentException exc) {
            // the date is not in the correct format - ignore
            return null;
        }
    }

    public String getLogFolderPath() {
        return logFolderPath;
    }

    public void setLogFolderPath(String logFolderPath) {
        this.logFolderPath = logFolderPath;
    }
}
