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

package org.openhubframework.openhub.api.common.jaxb;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import javax.annotation.Nullable;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * JAXB adapter for automatic conversion between {@link XMLGregorianCalendar} to {@link OffsetDateTime}.
 *
 * @author Petr Juza
 * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
 */
public class JaxbDateAdapter {

    // time is set midnight (00:00:00)
    private static final DateTimeFormatter ISO_OFFSET_DATE_MIDNIGHT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_OFFSET_DATE)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .toFormatter();

    private JaxbDateAdapter() {
    }

    @Nullable
    public static OffsetDateTime parseDate(@Nullable String dateStr) {
        if (dateStr == null) {
            return null;
        }

        // parsing has two steps:
        //  1) parse date with offset "2013-10-05+02:00"
        //  2) parse date without offset "2013-10-05"
        try {
            return OffsetDateTime.from(ISO_OFFSET_DATE_MIDNIGHT.parse(dateStr));
        } catch (DateTimeParseException ex) {
            TemporalAccessor dt = DateTimeFormatter.ISO_DATE.parseBest(dateStr, OffsetDateTime::from, LocalDate::from);
            if (dt instanceof OffsetDateTime) {
                return (OffsetDateTime) dt;
            } else {
                LocalDateTime ldt = LocalDateTime.of((LocalDate)dt, LocalTime.now());
                return OffsetDateTime.of(ldt.truncatedTo(ChronoUnit.DAYS), ZoneOffset.from(ZonedDateTime.now()));
            }
        }
    }

    @Nullable
    public static String printDate(@Nullable OffsetDateTime dt) {
        if (dt == null) {
            return null;
        }

        return dt.format(DateTimeFormatter.ISO_DATE);
    }

    @Nullable
    public static OffsetDateTime parseDateTime(@Nullable String dtStr) {
        if (dtStr == null) {
            return null;
        }

        // An unspecified time zone is exactly that - unspecified. No more, no less. It's not saying it's in UTC,
        // nor is it saying it's in the local time zone or any other time zone, it's just saying that the time
        // read from some clock somewhere was that time
        // see http://stackoverflow.com/questions/20670041/what-is-the-default-time-zone-for-an-xml-schema-datetime-if-not-specified
        // => we use default time zone to the current time

        TemporalAccessor dt = DateTimeFormatter.ISO_DATE_TIME.parseBest(dtStr, OffsetDateTime::from, LocalDateTime::from);
        if (dt instanceof OffsetDateTime) {
            return (OffsetDateTime) dt;
        } else {
            return OffsetDateTime.of((LocalDateTime)dt, ZoneOffset.from(ZonedDateTime.now()));
        }
    }

    @Nullable
    public static String printDateTime(@Nullable OffsetDateTime dt) {
        if (dt == null) {
            return null;
        }

        return dt.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}