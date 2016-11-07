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

public class CamelFilterTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testCamelStopMulticast() {
        String result = producer.requestBody("direct:routeONE", "any body", String.class);
        assertEquals("routeTHREE", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .log(LoggingLevel.WARN, "routeONE reached")
                    .transform().constant("routeONE")
                    .filter(constant(false))
                        .to("direct:routeTWO")
                    .end()
                    .to("direct:routeTHREE");

                from("direct:routeTWO")
                    .log(LoggingLevel.WARN, "routeTWO reached")
                    .transform().constant("routeTWO");

                from("direct:routeTHREE")
                    .log(LoggingLevel.ERROR, "routeTHREE reached")
                    .transform().constant("routeTHREE");
            }
        };
    }
}
