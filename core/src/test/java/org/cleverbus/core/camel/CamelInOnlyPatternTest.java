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

package org.cleverbus.core.camel;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelInOnlyPatternTest extends CamelTestSupport{
    @Produce
    ProducerTemplate producer;

    @Test
    public void testInOnlyPattern() {
        assertEquals("route ONE", producer.requestBody("direct:route1A", "original body", String.class));
        assertEquals("route TWO", producer.requestBody("direct:route2A", "original body", String.class));
        assertEquals("route THREE", producer.requestBody("direct:route3A", "original body", String.class));
        assertEquals("route FOUR", producer.requestBody("direct:route4A", "original body", String.class));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:route1A")
                        .to("direct:route1B");
                from("direct:route1B")
                        .transform(constant("route ONE"));

                from("direct:route2A")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:route2B?exchangePattern=InOnly");
                from("direct:route2B?exchangePattern=InOnly")
                        .transform(constant("route TWO"));

                from("direct:route3A")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .to("direct:route3B");
                from("direct:route3B")
                        .setExchangePattern(ExchangePattern.InOnly)
                        .transform(constant("route THREE"));

                from("direct:route4A")
                        .inOnly("direct:route4B?exchangePattern=InOnly");
                from("direct:route4B?exchangePattern=InOnly")
                        .transform(constant("route FOUR"));
            }
        };
    }
}
