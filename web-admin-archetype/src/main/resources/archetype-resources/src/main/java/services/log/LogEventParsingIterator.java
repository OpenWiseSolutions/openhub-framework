#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.services.log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.cleverbus.common.log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * Iterates over provided log files, parsing and returning {@link LogEvent}s on demand.
 */
class LogEventParsingIterator implements Iterator<LogEvent>, Closeable {
    private final LogParser parser;
    private final LogParserConfig config;
    private final Queue<File> logFiles;

    private LineIterator lineIterator;

    private LogEvent parsedEvent; // event to be returned by this iterator on next()
    private LogEvent preParsedEvent; // event that was preParsed from a single line, but might have more lines

    private List<String> groupKey = null;

    private int totalCount = 0; // events found total
    private int fileEventsFound = -1; // events per file, resets to 0 on new file
    private int groupCount = 0; // events in current group, resets to 0 on new group

    LogEventParsingIterator(LogParser parser, LogParserConfig config, Collection<File> logFiles) throws IOException {
        this.parser = parser;
        this.config = config;
        this.logFiles = new LinkedList<File>(logFiles);
        nextEvent();
    }

    /**
     * Ensures that either {@link ${symbol_pound}parsedEvent} is not null, or the end is reached.
     *
     * @throws IOException if there's a problem opening a new file, while advancing to the next event
     */
    private void nextEvent() throws IOException {
        if (totalCount >= config.getLimit()) {
            Log.debug("Reached {} events limit - stopping", config.getLimit());
            close();
            return;
        }

        if (parsedEvent != null) {
            return;
        }

        boolean haveMore = !reachedEnd();
        while (parsedEvent == null && haveMore) {
            haveMore = seekToNextEvent();
            if (preParsedEvent != null && config.getGroupBy() != null && config.getGroupLimit() != null) {
                // grouping enabled - check group:
                List<String> nextGroupKey = getGroupKey(preParsedEvent, config);
                if (!nextGroupKey.equals(groupKey)) {
                    groupKey = nextGroupKey; // this event starts a new group
                    groupCount = 1; // reset group counter
                } else if (groupCount >= config.getGroupLimit()) {
                    preParsedEvent = null; // discard the pre-parsed event, as its group is full
                } else {
                    groupCount++;
                }
            }
        }

        if (parsedEvent != null) {
            fileEventsFound++;
            totalCount++;
        }
    }

    /**
     * Advance to the position where next line is available,
     * regardless of whether it passes filter requirements or not.
     * On the way to this position any lines that are not log events
     * will be appended to the previous log event.
     * <p/>
     * If this call is successful, either {@link ${symbol_pound}parsedEvent} will be a new event,
     * or {@link ${symbol_pound}preParsedEvent} will not be null; or both.
     *
     * @return true, if there might be more events; false otherwise
     * @throws IOException if there's a problem opening a new file
     */
    private boolean seekToNextEvent() throws IOException {
        LogEvent nextEvent = config.createLogEvent();

        LineIterator iterator;
        while ((iterator = getLineIterator()) != null) {
            while (iterator.hasNext()) {
                // process the line:
                LogEvent event = parser.parseLine(iterator.next(), nextEvent, preParsedEvent, config);
                // check what to do next:
                if (preParsedEvent != null && event != preParsedEvent) {
                    // there is pre-parsed event, but the line was NOT appended to it
                    // => pre-parsed event will not get any more lines, it can be considered fully parsed
                    parsedEvent = preParsedEvent; // graduate pre-parsed event - it'll be the next event
                    preParsedEvent = event; // next event that was found (if any) is now pre-parsed
                    return true; // found full pre-parsed event => success
                } else if (event == nextEvent) {
                    // nextEvent is now pre-parsed, but previous pre-parsed event is null, so no event graduated
                    preParsedEvent = nextEvent;
                    return true;
                }
                // otherwise line was ignored or added to the pre-parsed event, nothing really changed
            }
        }
        return false; // failure
    }

    private boolean reachedEnd() throws IOException {
        return getLineIterator() == null;
    }

    /**
     * Returns a line iterator to process next/current file, opening the next file, if necessary.
     *
     * @return line iterator for the current/next file; or null if no files left
     */
    private LineIterator getLineIterator() throws IOException {
        if (lineIterator != null && lineIterator.hasNext()) {
            return lineIterator;
        }

        // discard current line iterator
        LineIterator.closeQuietly(lineIterator);
        lineIterator = null;

        if (fileEventsFound == 0) {
            Log.debug("No events in the last file, closing prematurely");
            close(); // optimize: last file had no events, no point continuing
        } else if (!logFiles.isEmpty()) {
            File file = logFiles.poll();
            Log.debug("Opening {}", file);
            lineIterator = FileUtils.lineIterator(file);
            fileEventsFound = 0; // restart per-file counter
        }

        return lineIterator;
    }


    private List<String> getGroupKey(LogEvent event, LogParserConfig config) {
        List<String> groupKey = new ArrayList<String>(config.getGroupBy().size());
        for (int propertyIndex = 0; propertyIndex < event.getPropertyCount(); propertyIndex++) {
            if (config.getGroupBy().contains(event.getPropertyNames().get(propertyIndex))) {
                groupKey.add(String.valueOf(event.getProperties()[propertyIndex]));
            }
        }
        return groupKey;
    }

    @Override
    public boolean hasNext() {
        return parsedEvent != null;
    }

    @Override
    public LogEvent next() {
        LogEvent result = parsedEvent;
        parsedEvent = null;

        try {
            nextEvent();
        } catch (IOException exc) {
            throw new IllegalStateException("Error prefetching next event", exc);
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        LineIterator.closeQuietly(lineIterator);
        lineIterator = null;
        logFiles.clear();
        Log.debug("Closed");
    }
}
