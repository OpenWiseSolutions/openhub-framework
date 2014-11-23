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

package org.cleverbus.modules.in.hello;

import static org.cleverbus.common.jaxb.JaxbDataFormatHelper.jaxb;

import javax.xml.namespace.QName;

import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.modules.ServiceEnum;
import org.cleverbus.modules.in.hello.model.SyncHelloRequest;
import org.cleverbus.modules.in.hello.model.SyncHelloResponse;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.springframework.util.Assert;


/**
 * Route definition for "syncHello" operation.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = SyncHelloRoute.ROUTE_BEAN)
public class SyncHelloRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "syncHelloRouteBean";

    private static final String OPERATION_NAME = "syncHello";

    public static final String ROUTE_ID_SYNC_HELLO = getRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String HELLO_SERVICE_NS = "http://cleverbus.org/ws/HelloService-v1";

    @Override
    protected void doConfigure() throws Exception {
        from(getInWsUri(new QName(HELLO_SERVICE_NS, "syncHelloRequest")))
                .routeId(ROUTE_ID_SYNC_HELLO)

                .policy("roleWsAuthPolicy")

                .to("throttling:sync:" + OPERATION_NAME)

                .unmarshal(jaxb(SyncHelloRequest.class))

                .log(LoggingLevel.DEBUG, "Calling hello service with name: ${body.name}")

                .bean(this, "composeGreeting")

                .marshal(jaxb(SyncHelloResponse.class));
    }

    @Handler
    public SyncHelloResponse composeGreeting(@Body SyncHelloRequest req) {
        Assert.notNull(req, "req must not be null");

        String greeting = "Hello " + req.getName();

        SyncHelloResponse res = new SyncHelloResponse();
        res.setGreeting(greeting);
        return res;
    }
}
