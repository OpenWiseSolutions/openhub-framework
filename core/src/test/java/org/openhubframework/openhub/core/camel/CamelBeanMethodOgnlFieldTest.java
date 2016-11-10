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

import static org.hamcrest.CoreMatchers.is;

import org.apache.camel.Handler;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test suite for checking correct bean binding,
 * see <a href="https://issues.apache.org/jira/browse/CAMEL-6687">CAMEL-6687</a>.
 */
public class CamelBeanMethodOgnlFieldTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producer;

    @Test
    public void testBothValues() {
        ExamplePojo fooBar = new ExamplePojo();
        fooBar.setFoo("foo1");
        fooBar.setBar("bar2");

        String result = producer.requestBody("direct:routeONE", fooBar, String.class);
        assertThat(result, is("foo: foo1; bar: bar2"));
    }

    @Test
    public void testNullValue() {
        ExamplePojo fooBar = new ExamplePojo();
        fooBar.setFoo(null);
        fooBar.setBar("test");

        String result = producer.requestBody("direct:routeONE", fooBar, String.class);
        assertThat(result, is("foo: null; bar: test"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:routeONE")
                    .bean(new ExampleBean(), "doWithFooBar(${body.foo}, ${body.bar})");
            }
        };
    }

    public static class ExampleBean {
        @Handler
        public String doWithFooBar(String foo, String bar) {
            return String.format("foo: %s; bar: %s", foo, bar);
        }
    }

    public static class ExamplePojo {
        private String foo;
        private String bar;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
    }
}
