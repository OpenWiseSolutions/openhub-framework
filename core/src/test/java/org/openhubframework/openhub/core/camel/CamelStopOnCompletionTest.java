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
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test suite for checking behaviour of stop and <a href="http://camel.apache.org/oncompletion.html">onComplete</a>
 * functionality in Camel.
 * <p/>
 * Note: behaviour changed in 2.12.1 version - when the route is stopped then it's not possible to send something
 */
public class CamelStopOnCompletionTest extends CamelTestSupport {

    @Produce
    private ProducerTemplate producer;

    private boolean completed = false;

    @Test
    public void testStopOnCompletion() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:test");
        mock.expectedMessageCount(0);

        String result = producer.requestBody("direct:routeONE", "any body", String.class);
        assertEquals("any body-routeONE-routeTWO", result);
        assertTrue(completed);

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onCompletion()
                        .transform(body().append("-OnCompletion"))
                        .log(LoggingLevel.WARN, "OnCompletion")
                        .to("mock:test");

                from("direct:routeONE")
                    .onCompletion()
                        .transform(body().append("-OnCompletionInRoute"))
                        .log(LoggingLevel.WARN, "OnCompletionInRoute")
                        .to("mock:test")
                    .end()

                    .setProperty("TestProperty", constant("Test"))
                    .transform(body().append("-routeONE"))
                    .log(LoggingLevel.WARN, "routeONE")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            exchange.getUnitOfWork().addSynchronization(new Synchronization() {
                                @Override
                                public void onComplete(Exchange exchange) {
                                    exchange.getIn().setBody(exchange.getIn().getBody(String.class) + "-Complete");
                                    log.warn("Complete");

                                    completed = true;

                                    // camel 2.12.1 - doesn't have effect
                                    producer.send(getMockEndpoint("mock:test"), exchange);
                                }

                                @Override
                                public void onFailure(Exchange exchange) {
                                    exchange.getIn().setBody(exchange.getIn().getBody(String.class) + "-Failure");
                                    log.warn("Failure");
                                    producer.send(getMockEndpoint("mock:test"), exchange);
                                }
                            });
                        }
                    })

                    .doTry()
                        .to("direct:routeTWO")
                        .transform(body().append("-continued after stop"))
                        .log(LoggingLevel.WARN, "continued after stop")
                    .doCatch(Throwable.class)
                        .transform(body().append("-caught exception"))
                        .log(LoggingLevel.ERROR, "Error: ${property." + Exchange.EXCEPTION_CAUGHT + "}")
                    .doFinally()
                        .transform(body().append("-entered finally"))
                        .log(LoggingLevel.WARN, "entered finally");

                from("direct:routeTWO")
                    .onCompletion()
                        .transform(body().append("-OnCompletionInRoute2"))
                        .log(LoggingLevel.WARN, "OnCompletionInRoute2")
                        .to("mock:test")
                    .end()
                    .transform(body().append("-routeTWO"))
                    .log(LoggingLevel.WARN, "routeTWO")
                    .stop();
            }
        };
    }
}
