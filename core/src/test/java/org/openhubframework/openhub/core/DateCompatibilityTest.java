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
import static org.junit.Assume.assumeThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
        DateTimeZone.setDefault(DateTimeZone.forID(TIMEZONE_PRAGUE));
    }

    @Test
    public void testXmlConversion() {
        assumeThat(DateTimeZone.getDefault().getID(), is(TIMEZONE_PRAGUE));

        DateTime xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000+02:00");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000"); // default time zone - CEST
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00Z");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T02:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDate("2013-10-05");
        assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+02:00"));
    }

    @Test
    public void testConversionToUTC() {
        assumeThat(DateTimeZone.getDefault().getID(), is(TIMEZONE_PRAGUE));

        DateTime xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000+02:00");

        // change time zone to UTC (instant is still the same)
        DateTime utcDate = xmlDate.withZone(DateTimeZone.UTC);

        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));
        assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-05T00:00:00.000+02:00"));
        assertThat(utcDate.isEqual(xmlDate), is(true));
        assertThat(utcDate.toDate().equals(xmlDate.toDate()), is(true));

        // converts to UTC
        utcDate = Tools.toUTC(xmlDate);
        assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T22:00:00.000+02:00"));
        assertThat(utcDate.isEqual(xmlDate), is(false));
        assertThat(utcDate.isBefore(xmlDate), is(true));
        assertThat(utcDate.toDate().equals(xmlDate.toDate()), is(false));

        // converts back to local time
        DateTime localDt = Tools.fromUTC(utcDate);
        assertThat(localDt.isEqual(xmlDate), is(true));

        localDt = Tools.fromUTC(utcDate.getMillis());
        assertThat(localDt.isEqual(xmlDate), is(true));
    }

    @Test
    public void testDateConversionToUTC() {
        assumeThat(DateTimeZone.getDefault().getID(), is("Europe/Prague"));

        // with timezone
        DateTime xmlDate = JaxbDateAdapter.parseDate("2013-10-05+02:00");
        assertNotNull(xmlDate);

        // change time zone to UTC (instant is still the same)
        DateTime utcDate = xmlDate.withZone(DateTimeZone.UTC);

        assertThat(JaxbDateAdapter.printDate(xmlDate), is("2013-10-05+02:00"));
        assertThat(JaxbDateAdapter.printDate(utcDate), is("2013-10-04+00:00")); // ???
        assertThat(utcDate.isEqual(xmlDate), is(true));
        assertThat(utcDate.toDate().equals(xmlDate.toDate()), is(true));

        // without timezone
        xmlDate = JaxbDateAdapter.parseDate("2013-10-05");

        assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+02:00"));

        // converts to UTC
        DateTime localDate = new DateTime(2013, 10, 5, 0, 0, DateTimeZone.forID(TIMEZONE_PRAGUE));
        utcDate = Tools.toUTC(localDate); // 2012-10-05 22:00 (UTC time zone)
        assertThat(JaxbDateAdapter.printDateTime(utcDate), is("2013-10-04T22:00:00.000+02:00")); // default timezone
        assertThat(utcDate.isEqual(xmlDate), is(false));
        assertThat(utcDate.isBefore(xmlDate), is(true));
        assertThat(utcDate.isBefore(localDate), is(true));
        assertThat(utcDate.toDate().equals(xmlDate.toDate()), is(false));

        // converts back to local time
        DateTime localDt = Tools.fromUTC(utcDate);
        // utcDate: 2013-10-04T20:00:00.000Z, localDate: 2013-10-05T00:00:00.000+02:00, localDt: 2013-10-05T00:00:00.000+02:00
        assertThat(localDt.isEqual(localDate), is(true));
        assertThat(localDt.toInstant(), is(localDate.toInstant()));

        localDt = Tools.fromUTC(utcDate.getMillis());
        assertThat(localDt.isEqual(localDate), is(true));
    }
}
