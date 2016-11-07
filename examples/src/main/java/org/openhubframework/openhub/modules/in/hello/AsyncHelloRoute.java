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

package org.openhubframework.openhub.modules.in.hello;

import static org.openhubframework.openhub.common.jaxb.JaxbDataFormatHelper.jaxb;

import javax.xml.namespace.QName;

import org.openhubframework.openhub.api.asynch.AsynchResponseProcessor;
import org.openhubframework.openhub.api.asynch.AsynchRouteBuilder;
import org.openhubframework.openhub.api.asynch.model.CallbackResponse;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.api.route.XPathValidator;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.modules.ServiceEnum;
import org.openhubframework.openhub.modules.in.hello.model.AsyncHelloRequest;
import org.openhubframework.openhub.modules.in.hello.model.AsyncHelloResponse;

import org.apache.camel.Body;
import org.apache.camel.Expression;
import org.apache.camel.Handler;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.util.Assert;


/**
 * Route definition for "asyncHello" operation.
 *
 * @author Petr Juza
 */
@CamelConfiguration(value = AsyncHelloRoute.ROUTE_BEAN)
public class AsyncHelloRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "asyncHelloRouteBean";

    private static final String OPERATION_NAME = "asyncHello";

    public static final String ROUTE_ID_ASYNC_IN = getInRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String ROUTE_ID_ASYNC_OUT = getOutRouteId(ServiceEnum.HELLO, OPERATION_NAME);

    public static final String URI_ASYNC_HELLO_OUT = "direct:" + ROUTE_ID_ASYNC_OUT;

    private static final String URI_PRINT_GREETING = "direct:printGreeting";

    @Override
    protected void doConfigure() throws Exception {
        // asyncHello - input asynch message
        createRouteForAsyncHelloRouteIn();

        // asyncHello - process delivery to external systems
        createRouteForAsyncHelloRouteOut();
    }

    /**
     * Route for asynchronous <strong>asyncHello</strong> input operation.
     * <p/>
     * Prerequisite: none
     * <p/>
     * Output: {@link AsyncHelloResponse}
     */
    private void createRouteForAsyncHelloRouteIn() {
        Namespaces ns = new Namespaces("h", SyncHelloRoute.HELLO_SERVICE_NS);

        // note: mandatory parameters are set already in XSD, this validation is extra
        XPathValidator validator = new XPathValidator("/h:asyncHelloRequest", ns, "h:name");

        // note: only shows using but without any influence in this case
        Expression nameExpr = xpath("/h:asyncHelloRequest/h:name").namespaces(ns).stringResult();

        AsynchRouteBuilder.newInstance(ServiceEnum.HELLO, OPERATION_NAME,
                getInWsUri(new QName(SyncHelloRoute.HELLO_SERVICE_NS, "asyncHelloRequest")),
                new AsynchResponseProcessor() {
                    @Override
                    protected Object setCallbackResponse(CallbackResponse callbackResponse) {
                        AsyncHelloResponse res = new AsyncHelloResponse();
                        res.setConfirmAsyncHello(callbackResponse);
                        return res;
                    }
                }, jaxb(AsyncHelloResponse.class))

                .withValidator(validator)
                .withObjectIdExpr(nameExpr)
                .build(this);
    }

    /**
     * Route for <strong>asyncHello</strong> operation - process delivery to external systems.
     * <p/>
     * Prerequisite: none
     */
    private void createRouteForAsyncHelloRouteOut() {
        from(URI_ASYNC_HELLO_OUT)
                .routeId(ROUTE_ID_ASYNC_OUT)

                // xml -> AsyncHelloRequest
                .unmarshal(jaxb(AsyncHelloRequest.class))

                .to("extcall:message:" + URI_PRINT_GREETING);


        from(URI_PRINT_GREETING)
                .bean(this, "printGreeting");
    }

    @Handler
    public void printGreeting(@Body AsyncHelloRequest req) {
        Assert.notNull(req, "req must not be null");

        String greeting = "Hello " + req.getName();

        Log.debug("Greeting: " + greeting);
    }
}
