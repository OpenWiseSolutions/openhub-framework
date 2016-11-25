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

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.support.DefaultConversionService;

import org.openhubframework.openhub.common.time.Seconds;


/**
 * Test that all converters is registered and working properly.
 *
 * @author Petr Juza
 * @since 2.0
 */
@RunWith(Parameterized.class)
public class ConvertersTest {

    private final Object from;
    private final Class<?> targetClass;
    private final Object expected;

    /**
     * Create parametrized classes.
     *
     * @param from object to convert
     * @param targetClass target class to convert into
     * @param expected expected converted object.
     */
    public ConvertersTest(Object from, Class<?> targetClass, Object expected) {
        this.from = from;
        this.targetClass = targetClass;
        this.expected = expected;
    }

    /**
     * Data for parametrized test.
     *
     * @return the data for constructor
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "105", Seconds.class, Seconds.of(105) },
                { "PT107S", Duration.class, Duration.ofSeconds(107) },
                { 108, Seconds.class, Seconds.of(108) },
                { 1099L, Duration.class, Duration.ofMillis(1099) },
                { "PT72.345S", Duration.class, Duration.ofMillis(72345) },

                { Seconds.of(123), Integer.class, 123 },
                { Seconds.of(177), String.class, "177" },
                { Duration.ofMillis(1099), Long.class, 1099L },
                { Duration.ofMillis(45230), String.class, "PT45.23S" }
        });
    }

    @Test
    public void testConvert() {
        DefaultConversionService registry = new DefaultConversionService();

        Converters.registerConverters(registry);

        Object res = registry.convert(from, targetClass);
        assertEquals(expected, res);
    }
}
