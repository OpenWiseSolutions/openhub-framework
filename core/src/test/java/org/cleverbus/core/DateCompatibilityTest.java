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

package org.cleverbus.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.cleverbus.common.Tools;
import org.cleverbus.common.jaxb.JaxbDateAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;


/**
 * Test suite with compatibility checks between UTC and local dates.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class DateCompatibilityTest {

    @Before
    public void setDefaultTimeZone() {
        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Prague"));
    }

    @Test
    public void testXmlConversion() {
        assumeThat(DateTimeZone.getDefault().getID(), is("Europe/Prague"));

        DateTime xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000+02:00");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00.000"); // default time zone - CEST
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05T00:00:00Z");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T02:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDateTime("2013-10-05");
        assertThat(JaxbDateAdapter.printDateTime(xmlDate), is("2013-10-05T00:00:00.000+02:00"));

        xmlDate = JaxbDateAdapter.parseDate("2013-10-05");
        // printed date depends on location/timezone
        assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+"));
    }

    @Test
    public void testConversionToUTC() {
        assumeThat(DateTimeZone.getDefault().getID(), is("Europe/Prague"));

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

        // printed date depends on location/timezone
        assertThat(JaxbDateAdapter.printDate(xmlDate), startsWith("2013-10-05+"));

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
}
