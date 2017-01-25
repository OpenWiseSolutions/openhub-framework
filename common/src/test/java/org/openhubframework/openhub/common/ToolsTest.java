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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.openhubframework.openhub.common.Tools.fm;
import static org.openhubframework.openhub.common.Tools.joinNonEmpty;

import javax.xml.namespace.QName;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test suite for {@link Tools} class.
 * 
 * @author Petr Juza
 */
@RunWith(JUnit4.class)
public class ToolsTest{

    /**
     * Test toString method.
     * 
     * When input is null, it should return null, otherwise the input.toString method
     */
    @Test
    public void testToString() {
        
        Integer i = null;
        assertNull(Tools.toString(i));
        
        i = 2;
        
        assertEquals("2", Tools.toString(i));
    }
    
    /**
     * Test joinNonEmpty method.
     * 
     * Method merges the input strings and each string should be separated by enrich character. If string before, after
     * next one is null or empty, the enrich character is not inserted.
     */
    @Test
    public void testJoinNonEmpty() {
        assertEquals("Separate this character",
                joinNonEmpty(new String[]{"", "Separate", "this", null, "character", "", null}, ' '));

        assertEquals("",
                joinNonEmpty(new String[]{}, 'x'));

        assertEquals("//",
                joinNonEmpty(new String[]{"//"}, 'x'));

        assertEquals("//",
                joinNonEmpty(new String[]{null, "//", null}, 'x'));

        assertEquals("///",
                joinNonEmpty(new String[]{"/", "", "/", null}, '/'));

        assertEquals("/,2",
                joinNonEmpty(new String[]{"/", "   ", "2", null}, ','));
    }

    @Test
    public void testFm() {
        assertThat(fm(""), is(""));
        assertThat(fm("empty"), is("empty"));
        assertThat(fm("{} > {}", 2, 1), is("2 > 1"));
    }

    @Test
    public void testUtc() {
        DateTime localDate = new DateTime(2013, 10, 5, 23, 0,
                DateTimeZone.getDefault());
        DateTime utcDate = Tools.toUTC(localDate); // 2012-10-05 21:00 (UTC time zone)

        // converts back to local time
        DateTime localDt = Tools.fromUTC(utcDate);
        // utcDate: 2013-10-04T20:00:00.000Z, localDate: 2013-10-05T00:00:00.000+02:00, localDt: 2013-10-05T00:00:00.000+02:00
        assertThat(localDt.isEqual(localDate), is(true));
        assertThat(localDt.toInstant(), is(localDate.toInstant()));

        localDt = Tools.fromUTC(utcDate.getMillis());
        assertThat(localDt.isEqual(localDate), is(true));
    }

    @Test
    public void testMarshalFromXml() {
        HelloRequest req = new HelloRequest();
        req.setName("Peter");

        String xml = Tools.marshalToXml(req, HelloRequest.class);
        assertThat(xml, CoreMatchers.containsString("<helloRequest>"));
        assertThat(xml, CoreMatchers.containsString("</helloRequest>"));
        assertThat(xml, CoreMatchers.containsString("<name>"));
        assertThat(xml, CoreMatchers.containsString("Peter"));

        xml = Tools.marshalToXml(req, QName.valueOf("hello"));
        assertThat(xml, CoreMatchers.containsString("<hello>"));
        assertThat(xml, CoreMatchers.containsString("</hello>"));
        assertThat(xml, CoreMatchers.containsString("<name>"));
        assertThat(xml, CoreMatchers.containsString("Peter"));
    }

    @Test
    public void testUnmarshalFromXml() {
        String xml = "<helloRequest>"
                + "    <name>Mr. Parker</name>"
                + "</helloRequest>";

        HelloRequest req = Tools.unmarshalFromXml(xml, HelloRequest.class);
        assertThat(req.getName(), is("Mr. Parker"));
    }
}
