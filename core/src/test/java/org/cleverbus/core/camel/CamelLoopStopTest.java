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

import static org.hamcrest.CoreMatchers.instanceOf;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test suite that verifies stopping loop iteration.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class CamelLoopStopTest extends CamelTestSupport {

    @Produce
    private ProducerTemplate producer;

    private boolean reachedRouteTWO;

    @Test
    public void testStoppingLoop() throws Exception {
        reachedRouteTWO = false;

        producer.requestBody("direct:routeLoop", "any body");
    }

    @Test
    public void testStoppingLoopWithCopy() throws Exception {
        reachedRouteTWO = false;

        try {
            producer.requestBody("direct:routeLoopCopy", "any body");
            fail();
        } catch (Exception ex) {
            assertThat(ex.getCause(), instanceOf(IllegalStateException.class));
        }
    }

    @Test
    public void testLoopCopyBody() throws Exception {
        reachedRouteTWO = false;

        producer.requestBody("direct:routeLoopCopyBody", "any body");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeLoop")
                    .log(LoggingLevel.WARN, "routeLoop reached")

                    .transform().constant("routeLoop")

                    .loop(2)
                        .to("direct:routeTWO")
                    .end()

                    .log(LoggingLevel.WARN, "end of routeLoop");


                from("direct:routeLoopCopy")
                    .log(LoggingLevel.WARN, "routeLoop reached")

                    .transform().constant("routeLoop")

                    .loop(2).copy()
                        .to("direct:routeTWO")
                    .end()

                    .log(LoggingLevel.WARN, "end of routeLoop")

                    .to("mock:test");


                from("direct:routeTWO")
                    .log(LoggingLevel.WARN, "routeTWO reached (index: ${property." + Exchange.LOOP_INDEX + "})")

                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Integer index = (Integer)exchange.getProperty(Exchange.LOOP_INDEX);

                            if (reachedRouteTWO && index > 0) {
                                throw new IllegalStateException("stop in the previous route haven't had effect");
                            }

                            reachedRouteTWO = true;
                        }
                    })

                    .transform().constant("routeTWO")
                    .stop();


                from("direct:routeLoopCopyBody")
                        .log(LoggingLevel.WARN, "routeLoopCopyBody reached")

                        .transform().constant("routeLoopCopyBody")

                        .setHeader("myBody", body())

                        .loop(2)
                            .setBody(header("myBody"))

                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    String body = exchange.getIn().getBody(String.class);

                                    if (!body.equals("routeLoopCopyBody")) {
                                        throw new IllegalStateException("body has changed during previous loop cycle");
                                    }
                                }
                            })

                            .to("direct:routeChangeBody")
                        .end()

                        .log(LoggingLevel.WARN, "end of routeLoopCopyBody");

                from("direct:routeChangeBody")
                    .log(LoggingLevel.WARN, "routeChangeBody reached")
                    .transform().constant("change body");
            }
        };
    }
}
