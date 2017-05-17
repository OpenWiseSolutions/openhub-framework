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

package org.openhubframework.openhub.api.route;

import java.util.Set;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.apache.camel.*;
import org.apache.camel.processor.DefaultExchangeFormatter;
import org.apache.camel.spi.EventNotifier;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.util.MessageHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.ExternalSystemExtEnum;
import org.openhubframework.openhub.api.entity.ServiceExtEnum;
import org.openhubframework.openhub.api.exception.*;


/**
 * Parent route definition that defines common route rules, e.g. exception policy, error handling etc.
 *
 * @author Petr Juza
 */
public abstract class AbstractBasicRoute extends SpringRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBasicRoute.class);

    /**
     * Suffix for synchronous routes.
     */
    public static final String ROUTE_SUFFIX = "_route";


    /**
     * Suffix for asynchronous incoming routes.
     */
    public static final String IN_ROUTE_SUFFIX = "_in_route";

    /**
     * Suffix for asynchronous outbound routes.
     */
    public static final String OUT_ROUTE_SUFFIX = "_out_route";

    /**
     * Suffix for outbound routes with external systems.
     */
    public static final String EXTERNAL_ROUTE_SUFFIX = "_external_route";

    /**
     * Delimiter in generated route id for service and operation name.
     *
     * @see #getRouteId(ServiceExtEnum, String)
     * @see #getInRouteId(ServiceExtEnum, String)
     * @see #getOutRouteId(ServiceExtEnum, String)
     * @see #getExternalRouteId(ExternalSystemExtEnum, String)
     */
    public static final String ROUTE_ID_DELIMITER = "_";

    // note: I prefer using this before calling repeatedly lookup method for getting bean implementation
    @Autowired(required = false)
    private WebServiceUriBuilder wsUriBuilder;

    private DefaultExchangeFormatter historyFormatter;

    protected AbstractBasicRoute() {
        // setup exchange formatter to be used for message history dump
        historyFormatter = new DefaultExchangeFormatter();
        historyFormatter.setShowExchangeId(true);
        historyFormatter.setMultiline(true);
        historyFormatter.setShowHeaders(true);
        historyFormatter.setStyle(DefaultExchangeFormatter.OutputStyle.Fixed);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void configure() throws Exception {
        doErrorHandling();

        doConfigure();
    }

    /**
     * Defines global (better to say semi-global because it's scoped for one route builder) exception policy
     * and common error handling.
     * <p>
     * Default implementation catches common {@link Exception} and if it's synchronous message
     * (see {@link AsynchConstants#ASYNCH_MSG_HEADER}) then redirect to {@link AsynchConstants#URI_EX_TRANSLATION}.
     * If it's asynchronous message then determines according to exception's type if redirect to
     * {@link AsynchConstants#URI_ERROR_FATAL} or {@link AsynchConstants#URI_ERROR_HANDLING} route URI.
     *
     * @throws Exception can be thrown during configuration
     */
    @SuppressWarnings("unchecked")
    protected void doErrorHandling() throws Exception {
        // note: IO exceptions must be handled where is call to external system

        onException(Exception.class)
            .handled(true)
            .log(LoggingLevel.ERROR,
                    "exception caught (asynch = '${header." + AsynchConstants.ASYNCH_MSG_HEADER + "}') "
                            + "- ${property." + Exchange.EXCEPTION_CAUGHT + ".message}")

            .bean(this, "printMessageHistory")

            .choice()
                .when().method(this, "isAsynch")
                    // process exception and redirect message to next route
                    .routingSlip(method(this, "exceptionHandling"))
                .end()

                .otherwise()
                    // synchronous
                    .to(AsynchConstants.URI_EX_TRANSLATION)
            .end();
    }

    @Handler
    public void printMessageHistory(Exchange exchange) {
        // print message history
        String routeStackTrace = MessageHelper.dumpMessageHistoryStacktrace(exchange, historyFormatter, false);
        LOG.debug(routeStackTrace);
    }

    @Handler
    public boolean isAsynch(@Header(AsynchConstants.ASYNCH_MSG_HEADER) Boolean asynch) {
        return BooleanUtils.isTrue(asynch);
    }

    /**
     * Handles specified exception.
     *
     * @param ex the thrown exception
     * @param asynch {@code true} if it's asynchronous message processing otherwise synchronous processing
     * @return next route URI
     */
    @Handler
    public String exceptionHandling(Exception ex, @Header(AsynchConstants.ASYNCH_MSG_HEADER) Boolean asynch) {
        Assert.notNull(ex, "the ex must not be null");
        Assert.isTrue(BooleanUtils.isTrue(asynch), "it must be asynchronous message");

        String nextUri;

        if (ExceptionUtils.indexOfThrowable(ex, org.apache.camel.ValidationException.class) >= 0
                || ExceptionUtils.indexOfThrowable(ex, ValidationException.class) >= 0) {
            LOG.warn("Validation error, no further processing - " + ex.getMessage());
            nextUri = AsynchConstants.URI_ERROR_FATAL;

        } else if (ExceptionUtils.indexOfThrowable(ex, BusinessException.class) >= 0) {
            LOG.warn("Business exception, no further processing.");
            nextUri = AsynchConstants.URI_ERROR_FATAL;

        } else if (ExceptionUtils.indexOfThrowable(ex, NoDataFoundException.class) >= 0) {
            LOG.warn("No data found, no further processing.");
            nextUri = AsynchConstants.URI_ERROR_FATAL;

        } else if (ExceptionUtils.indexOfThrowable(ex, MultipleDataFoundException.class) >= 0) {
            LOG.warn("Multiple data found, no further processing.");
            nextUri = AsynchConstants.URI_ERROR_FATAL;

        } else if (ExceptionUtils.indexOfThrowable(ex, LockFailureException.class) >= 0) {
            LOG.warn("Locking exception.");
            nextUri = AsynchConstants.URI_ERROR_HANDLING;

        } else {
            LOG.error("Unspecified exception - " + ex.getClass().getSimpleName() + " (" + ex.getMessage() + ")");
            nextUri = AsynchConstants.URI_ERROR_HANDLING;
        }

        return nextUri;
    }

    /**
     * Defines routes and route specific configuration.
     *
     * @throws Exception can be thrown during configuration
     */
    protected abstract void doConfigure() throws Exception;

    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.1</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    protected String getOutWsUri(String connectionUri, String messageSenderRef, String soapAction) {
        return wsUriBuilder.getOutWsUri(connectionUri, messageSenderRef, soapAction);
    }

    /**
     * Shorthand for {@link #getOutWsUri(String, String, String)}.
     *
     * @param connectionUri the URI connection
     * @param messageSenderRef the reference of message sender
     * @return URI of WS compliant with SOAP 1.1 version
     */
    protected String getOutWsUri(String connectionUri, String messageSenderRef) {
        return getOutWsUri(connectionUri, messageSenderRef, null);
    }

    /**
     * Shorthand for {@link #getOutWsSoap12Uri(String, String, String)}.
     * 
     * @param connectionUri the URI connection
     * @param messageSenderRef the reference of message sender
     * @return URI of WS compliant with SOAP 1.2 version
     */
    protected String getOutWsSoap12Uri(String connectionUri, String messageSenderRef) {
        return wsUriBuilder.getOutWsSoap12Uri(connectionUri, messageSenderRef, null);
    }

    /**
     * Constructs a "to" URI for sending WS messages to external systems,
     * i.e., Camel Web Service Endpoint URI for contacting an external system via <strong>SOAP 1.2</strong>.
     *
     * @param connectionUri the URI to connect to the external system, e.g.: http://localhost:8080/vfmock/ws/mm7
     * @param messageSenderRef the message sender ref (bean id/name in Spring context)
     * @param soapAction the SOAP action to be invoked,
     *                   can be {@code null} for implicit handling of SOAP messages by the external system
     * @return the Camel Endpoint URI for producing (sending via To) SOAP messages to external system
     */
    protected String getOutWsSoap12Uri(String connectionUri, String messageSenderRef, String soapAction) {
        return wsUriBuilder.getOutWsSoap12Uri(connectionUri, messageSenderRef, soapAction);
    }

    /**
     * Gets "from" URI for handling incoming WS messages with default '{@value RouteConstants#ENDPOINT_MAPPING_BEAN}' bean.
     *
     * @return from URI
     * @param qName the operation QName (namespace + local part)
     */
    protected String getInWsUri(QName qName) {
        return wsUriBuilder.getInWsUri(qName, RouteConstants.ENDPOINT_MAPPING_BEAN, null);
    }

    /**
     * Gets "from" URI for handling incoming WS messages with default '{@value RouteConstants#ENDPOINT_MAPPING_BEAN}' bean.
     *
     * @return from URI
     * @param qName the operation QName (namespace + local part)
     * @param params the endpoint URI parameters (without leading signs ? or &amp;)
     */
    protected String getInWsUri(QName qName, @Nullable String params) {
        return wsUriBuilder.getInWsUri(qName, RouteConstants.ENDPOINT_MAPPING_BEAN, params);
    }

    /**
     * Gets route ID for synchronous routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getInRouteId(ServiceExtEnum, String)
     * @see #getOutRouteId(ServiceExtEnum, String)
     * @see #getExternalRouteId(ExternalSystemExtEnum, String)
     */
    public static String getRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + ROUTE_ID_DELIMITER + operationName + ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous incoming routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getOutRouteId(ServiceExtEnum, String)
     */
    public static String getInRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + ROUTE_ID_DELIMITER + operationName + IN_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for asynchronous outbound routes.
     *
     * @param service       the service name
     * @param operationName the operation name
     * @return route ID
     * @see #getInRouteId(ServiceExtEnum, String)
     */
    public static String getOutRouteId(ServiceExtEnum service, String operationName) {
        Assert.notNull(service, "the service must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return service.getServiceName() + ROUTE_ID_DELIMITER + operationName + OUT_ROUTE_SUFFIX;
    }

    /**
     * Gets route ID for routes which communicates with external systems.
     *
     * @param system        the external system
     * @param operationName the operation name
     * @return route ID
     * @see #getRouteId(ServiceExtEnum, String)
     */
    public static String getExternalRouteId(ExternalSystemExtEnum system, String operationName) {
        Assert.notNull(system, "the system must not be null");
        Assert.hasText(operationName, "the operationName must not be empty");

        return system.getSystemName() + ROUTE_ID_DELIMITER + operationName + EXTERNAL_ROUTE_SUFFIX;
    }

    /**
     * Adds new event notifier.
     * <p>
     * Use manual adding via this method or use {@link EventNotifier} annotation
     * for automatic registration. Don't use both.
     *
     * @param eventNotifier the event notifier
     */
    protected final void addEventNotifier(EventNotifier eventNotifier) {
        Assert.notNull(eventNotifier, "the eventNotifier must not be null");

        getContext().getManagementStrategy().addEventNotifier(eventNotifier);
    }

    /**
     * Returns bean by its type from registry.
     *
     * @param type the type of the registered bean
     * @param <T> as class of type
     * @return bean of specified type
     */
    protected final <T> T getBean(Class<T> type) {
        Set<T> beans = getContext().getRegistry().findByType(type);

        Assert.state(beans.size() == 1, "there is more beans of type " + type);

        return beans.iterator().next();
    }

    /**
     * Returns class name of the route implementation class.
     * <p>
     * This is because of using {@code bean(this, "createResponseForGetCounterData")} - if there is no toString()
     * method then {@link StackOverflowError} is thrown.
     *
     * @return class name
     */
    @Override
    public String toString() {
        return getClass().getName();
    }
}
