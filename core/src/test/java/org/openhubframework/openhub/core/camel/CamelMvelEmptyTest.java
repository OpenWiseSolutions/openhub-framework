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

package org.openhubframework.openhub.core.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelMvelEmptyTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    /**
     * MVEL distributed by Camel 2.13 cannot handle numeric "== empty",
     * despite the fact that it mentions 0 value for number in language guide:
     * http://mvel.codehaus.org/Value+Emptiness, and RuntimeException error occurred.
     * Now this confusing behaviour is fixed.
     */
    @Test
    public void testMvelEmptyLongObject() {
        String result = producer.requestBody("direct:routeONE", new MyWrap<>(0L), String.class);
        assertEquals("FALSE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<>(1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<>(-1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrap<Long>(null), String.class);
        assertEquals("FALSE", result);
    }

    @Test
    public void testMvelEmptyLongPrimitive() {
        String result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(-1L), String.class);
        assertEquals("TRUE", result);

        result = producer.requestBody("direct:routeONE", new MyWrapPrimitive(0L), String.class);
        assertEquals("FALSE", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")

                    .choice()
                        .when().mvel("request.body.inner != empty")
                            .log(LoggingLevel.WARN, "Body is NOT EMPTY: ${body}")
                            .transform(constant("TRUE"))
                        .otherwise()
                            .log(LoggingLevel.WARN, "Body is EMPTY: ${body}")
                            .transform(constant("FALSE"))
                    .end();
            }
        };
    }

    public static class MyWrap <T> {
        private final T inner;

        public MyWrap(T someObject) {
            this.inner = someObject;
        }

        public T getInner() {
            return inner;
        }
    }

    public static class MyWrapPrimitive {
        private final long inner;

        public MyWrapPrimitive(long somePrimitive) {
            this.inner = somePrimitive;
        }

        public long getInner() {
            return inner;
        }
    }
}
