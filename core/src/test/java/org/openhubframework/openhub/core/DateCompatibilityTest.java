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

package org.openhubframework.openhub.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import org.openhubframework.openhub.api.common.jaxb.JaxbDateAdapter;
import org.openhubframework.openhub.common.Tools;


/**
 * Test suite with compatibility checks between UTC and local dates.
 *
 * @author Petr Juza
 */
public class DateCompatibilityTest {

    private static final String TIMEZONE_PRAGUE = "Europe/Prague";

    @Before
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_PRAGUE));
    }

    @Test
    public void testXmlConversion() {
        assumeThat(TimeZone.getDefault().getID(), is(TIMEZONE_PRAGUE));

        // xs:datetime - http://books.xmlschemata.org/relaxng/ch19-77049.html
        //Valid values for xsd:dateTime include: 2001-10-26T21:32:52, 2001-10-26T21:32:52+02:00, 2001-10-26T19:32:52Z,
        //  2001-10-26T19:32:52+00:00, -2001-10-26T21:32:52, or 2001-10-26T21:32:52.12679.

        //The following values are invalid: 2001-10-26 (all the parts must be specified),
        // 2001-10-26T21:32 (all the parts must be specified), 2001-10-26T25:32:52+02:00 (the hours part—25—is out of range),
        // or 01-10-26T21:32 (all the parts must be specified).

        assertValidXmlDateTime("2013-10-05T00:00:00.000+02:00", "2013-10-05T00:00:00+02:00");
        assertValidXmlDateTime("2013-10-05T00:00:00Z", "2013-10-05T00:00:00Z");

        if (isSummerTime()) {
            assertValidXmlDateTime("2013-10-05T00:00:00", "2013-10-05T00:00:00+02:00");
            assertValidXmlDateTime("2001-10-26T21:32:52.12679", "2001-10-26T21:32:52.12679+02:00");
            assertValidXmlDateTime("2001-10-26T21:32", "2001-10-26T21:32:00+02:00");

            OffsetDateTime xmlDate = JaxbDateAdapter.parseDate("2013-10-05");
            assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+02:00"));
        } else {
            assertValidXmlDateTime("2013-10-05T00:00:00", "2013-10-05T00:00:00+01:00");
            assertValidXmlDateTime("2001-10-26T21:32:52.12679", "2001-10-26T21:32:52.12679+01:00");
            assertValidXmlDateTime("2001-10-26T21:32", "2001-10-26T21:32:00+01:00");

            OffsetDateTime xmlDate = JaxbDateAdapter.parseDate("2013-10-05");
            assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+01:00"));
        }

        assertWrongXmlDateTime("2013-10-05");
        assertWrongXmlDateTime("2001-10-26T25:32:52+02:00");
    }

    private boolean isSummerTime() {
        return TimeZone.getDefault().inDaylightTime(new Date());
    }

    private void assertValidXmlDateTime(String xml, String printedDate) {
        OffsetDateTime xmlDate = JaxbDateAdapter.parseDateTime(xml);
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is(printedDate));
    }

    private void assertWrongXmlDateTime(String xml) {
        try {
            JaxbDateAdapter.parseDateTime(xml);
            fail("Date '" + xml + "' is not valid according to xs:datetime specification");
        } catch (DateTimeParseException ex) {
            // it's ok
        }
    }

    @Test
    public void testConversionToUTC() {
        assumeThat(TimeZone.getDefault().getID(), is(TIMEZONE_PRAGUE));

        OffsetDateTime xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000+02:00");

        // change time zone to UTC (instant is still the same)
        OffsetDateTime utcDate = xmlDate.withOffsetSameInstant(ZoneOffset.UTC);

        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00+02:00"));
        assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T22:00:00Z"));
        assertThat(utcDate.toInstant().equals(xmlDate.toInstant()), is(true));
        assertThat(utcDate.equals(xmlDate), is(false));

        // converts to UTC
        utcDate = Tools.toUTC(xmlDate);
        assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T22:00:00Z"));
        assertThat(utcDate.toInstant().equals(xmlDate.toInstant()), is(true));

        // converts back to local time
        OffsetDateTime localDt = Tools.fromUTC(utcDate);
        assertThat(localDt.isEqual(xmlDate), is(true));

        localDt = Tools.fromUTC(utcDate.toInstant().toEpochMilli());
        assertThat(localDt.isEqual(xmlDate), is(true));
    }

    @Test
    public void testDateConversionToUTC() {
        assumeThat(TimeZone.getDefault().getID(), is(TIMEZONE_PRAGUE));

        // with timezone
        OffsetDateTime xmlDate = JaxbDateAdapter.parseDate("2013-10-05+02:00");
        assertNotNull(xmlDate);

        // change time zone to UTC (instant is still the same)
        OffsetDateTime utcDate = xmlDate.withOffsetSameInstant(ZoneOffset.UTC);

        assertThat(JaxbDateAdapter.printDate(xmlDate), is("2013-10-05+02:00"));
        assertThat(JaxbDateAdapter.printDate(utcDate), is("2013-10-04Z"));
        assertThat(utcDate.isEqual(xmlDate), is(true));
        assertThat(utcDate.toInstant().equals(xmlDate.toInstant()), is(true));

        // without timezone
        xmlDate = JaxbDateAdapter.parseDate("2013-10-05");

        if (isSummerTime()) {
            assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+02:00"));
        } else {
            assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+01:00"));
        }

        // converts to UTC
        OffsetDateTime localDate = OffsetDateTime.of(2013, 10, 5, 0, 0, 0, 0, ZoneOffset.from(ZonedDateTime.now()));
        utcDate = Tools.toUTC(localDate); // 2013-10-04 23:00 (UTC time zone)
        if (isSummerTime()) {
            assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T22:00:00Z")); // default timezone
        } else {
            assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T23:00:00Z")); // default timezone
        }
        assertThat(utcDate.toInstant().equals(localDate.toInstant()), is(true));
        assertThat(utcDate.toInstant().equals(xmlDate.toInstant()), is(true));

        // converts back to local time
        OffsetDateTime localDt = Tools.fromUTC(utcDate);
        // utcDate: 2013-10-04T20:00:00.000Z, localDate: 2013-10-05T00:00:00.000+02:00, localDt: 2013-10-05T00:00:00.000+02:00
        assertThat(localDt.isEqual(localDate), is(true));
        assertThat(localDt.toInstant(), is(localDate.toInstant()));

        localDt = Tools.fromUTC(utcDate.toInstant().toEpochMilli());
        assertThat(localDt.isEqual(localDate), is(true));
    }
}
