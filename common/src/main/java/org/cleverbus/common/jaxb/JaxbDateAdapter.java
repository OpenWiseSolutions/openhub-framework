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

package org.cleverbus.common.jaxb;

import javax.annotation.Nullable;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


/**
 * JAXB adapter for automatic conversion between {@link XMLGregorianCalendar} to {@link DateTime}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
 */
public class JaxbDateAdapter {

    private static final DateTimeFormatter DATE_PRINT_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-ddZZ");

    private JaxbDateAdapter() {
    }

    @Nullable
    public static DateTime parseDate(@Nullable String dateStr) {
        if (dateStr == null) {
            return null;
        }

        //TODO (juza) check adding DateTimeZone
        return new DateTime(DatatypeConverter.parseDate(dateStr), DateTimeZone.getDefault());
    }

    @Nullable
    public static String printDate(@Nullable DateTime dt) {
        if (dt == null) {
            return null;
        }

        return DATE_PRINT_FORMATTER.print(dt);
    }

    @Nullable
    public static DateTime parseDateTime(@Nullable String dtStr) {
        if (dtStr == null) {
            return null;
        }

        return DateTime.parse(dtStr).withZone(DateTimeZone.getDefault());
    }

    @Nullable
    public static String printDateTime(@Nullable DateTime dt) {
        if (dt == null) {
            return null;
        }

        return dt.withZone(DateTimeZone.getDefault()).toString(ISODateTimeFormat.dateTime());
    }
}