/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.common.converter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import org.junit.Test;


/**
 * Test suite for {@link DataTypeConverter} implementations.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class DataTypeConverterTest {

    private void assertConvertFrom(TypeConverterEnum type, String value, Object expectedValue) throws ParseException {
        Object res = type.convertToObject(null);
        assertThat(res, nullValue());

        res = type.convertToObject(value);
        assertThat(res, instanceOf(type.getTypeClass()));
        assertThat(res, is(expectedValue));
    }

    private void assertConvertToString(TypeConverterEnum type, Object value, String expectedValue) throws ParseException {
        String res = type.convertToString(null);
        assertThat(res, nullValue());

        res = type.convertToString(value);
        assertThat(res, is(expectedValue));
    }

    @Test
    public void testString() throws ParseException {
        assertConvertFrom(TypeConverterEnum.STRING, "some str", "some str");
        assertConvertToString(TypeConverterEnum.STRING, "some str", "some str");
    }

    @Test
    public void testInt() throws ParseException {
        assertConvertFrom(TypeConverterEnum.INT, "13", 13);
        assertConvertToString(TypeConverterEnum.INT, 13, "13");
    }

    @Test
    public void testFloat() throws ParseException {
        assertConvertFrom(TypeConverterEnum.FLOAT, "13.5", 13.5f);
        assertConvertToString(TypeConverterEnum.FLOAT, 13.5f, "13.5");
    }

    @Test
    public void testDate() throws ParseException {
        assertConvertFrom(TypeConverterEnum.DATE, "2016-01-13", LocalDate.of(2016, 1, 13));
        assertConvertToString(TypeConverterEnum.DATE, LocalDate.of(2016, 1, 13), "2016-01-13");
    }

    @Test
    public void testBoolean() throws ParseException {
        assertConvertFrom(TypeConverterEnum.BOOLEAN, "true", true);
        assertConvertToString(TypeConverterEnum.BOOLEAN, false, "false");
    }

    @Test
    public void testFile() throws ParseException {
        assertConvertFrom(TypeConverterEnum.FILE, "", new File(""));
        String fileStr = File.pathSeparator + "Volumes" + File.pathSeparator + "Obelix" + File.pathSeparator
                + "projects";
        assertConvertFrom(TypeConverterEnum.FILE, fileStr, new File(fileStr));
        assertConvertToString(TypeConverterEnum.FILE, new File(fileStr), fileStr);
    }

    @Test
    public void testPattern() throws ParseException {
        String patternStr = "^(spring-ws|servlet).*$";
        Pattern pattern = Pattern.compile(patternStr);

        // I must use own comparison because Pattern has no changed equal() - instances are compared
        Object res = TypeConverterEnum.PATTERN.convertToObject(null);
        assertThat(res, nullValue());

        res = TypeConverterEnum.PATTERN.convertToObject("");
        assertThat(res, instanceOf(TypeConverterEnum.PATTERN.getTypeClass()));
        assertThat(((Pattern)res).pattern(), is(""));

        res = TypeConverterEnum.PATTERN.convertToObject(patternStr);
        assertThat(res, instanceOf(TypeConverterEnum.PATTERN.getTypeClass()));
        assertThat(((Pattern)res).pattern(), is(pattern.pattern()));

        assertConvertToString(TypeConverterEnum.PATTERN, pattern, patternStr);
    }
}
