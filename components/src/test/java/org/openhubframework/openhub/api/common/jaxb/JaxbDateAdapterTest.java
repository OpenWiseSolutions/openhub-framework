/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.common.jaxb;

import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test suite for {@link JaxbDateAdapter}.
 *
 * @author Tomas Hanus
 */
public class JaxbDateAdapterTest {

    public static final String DATE = "2013-10-05+02:00";
    public static final String DATE_TIME = "2013-10-05T16:02:55+02:00";

    @Test
    public void parseDate() throws Exception {
        final OffsetDateTime result = JaxbDateAdapter.parseDate(DATE);
        
        assertThat(result.getYear(), is(2013));
        assertThat(result.getMonthValue(), is(10));
        assertThat(result.getDayOfMonth(), is(5));
        assertThat(result.getOffset(), is(ZoneOffset.ofHours(2)));
    }

    @Test
    public void printDate() throws Exception {
        final String result = JaxbDateAdapter.printDate(OffsetDateTime.of(2013, 10, 5, 0, 0, 0, 0, ZoneOffset.ofHours(2)));

        assertThat(result, is("2013-10-05+02:00"));
    }

    @Test
    public void parseDateTime() throws Exception {
        final OffsetDateTime result = JaxbDateAdapter.parseDateTime(DATE_TIME);

        assertThat(result.getYear(), is(2013));
        assertThat(result.getMonthValue(), is(10));
        assertThat(result.getDayOfMonth(), is(5));
        assertThat(result.getHour(), is(16));
        assertThat(result.getMinute(), is(2));
        assertThat(result.getSecond(), is(55));
        assertThat(result.getOffset(), is(ZoneOffset.ofHours(2)));
    }

    @Test
    public void printDateTime() throws Exception {
        final String result = JaxbDateAdapter.printDateTime(OffsetDateTime.of(2013, 10, 5, 16, 2, 55, 0, ZoneOffset.ofHours(2)));
        assertThat(result, is("2013-10-05T16:02:55+02:00"));

        final String resultUTC = JaxbDateAdapter.printDateTime(OffsetDateTime.of(2013, 10, 5, 16, 2, 55, 0, ZoneOffset.UTC));
        assertThat(resultUTC, is("2013-10-05T16:02:55Z"));
    }

}