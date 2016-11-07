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

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelMulticastPipelineTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testCamelMulticastPipeline() {
        String result = producer.requestBody("direct:routeONE", "start", String.class);
        assertEquals("start->routeONE->routeTWO + start->routeONE->routeTHREE + start->routeONE->routeFOUR->routeFIVE", result);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .log(LoggingLevel.WARN, "routeONE reached")
                    .transform(body().append("->routeONE"))
                    .multicast(
                            new AggregationStrategy() {
                                @Override
                                public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                                    if (oldExchange == null) {
                                        return newExchange;
                                    } else {
                                        oldExchange.getIn().setBody(oldExchange.getIn().getBody(String.class)
                                                + " + "
                                                + newExchange.getIn().getBody(String.class));
                                        return oldExchange;
                                    }
                                }
                            })
                        .to("direct:routeTWO", "direct:routeTHREE")
                        .pipeline()
                            .to("direct:routeFOUR")
                            .to("direct:routeFIVE")
                        .end()
                    .end();

                from("direct:routeTWO")
                    .log(LoggingLevel.WARN, "routeTWO reached with body ${body}")
                    .transform(body().append("->routeTWO"));

                from("direct:routeTHREE")
                    .log(LoggingLevel.WARN, "routeTHREE reached with body ${body}")
                    .transform(body().append("->routeTHREE"));

                from("direct:routeFOUR")
                    .log(LoggingLevel.WARN, "routeFOUR reached with body ${body}")
                    .transform(body().append("->routeFOUR"));

                from("direct:routeFIVE")
                    .log(LoggingLevel.WARN, "routeFIVE reached with body ${body}")
                    .transform(body().append("->routeFIVE"));
            }
        };
    }
}
