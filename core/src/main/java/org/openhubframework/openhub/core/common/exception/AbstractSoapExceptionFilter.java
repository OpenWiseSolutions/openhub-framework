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

package org.openhubframework.openhub.core.common.exception;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebFault;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Node;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.exception.IntegrationException;


/**
 * Filters exception thrown by the external system via web services.
 * <p>
 *
 * If {@link SoapFaultClientException SOAP exception} occurs and there is fault detail defined
 * then new exception is thrown by {@link #createException(QName, Node)} - if {@link #isAsynchMessage()} returns true.
 * Otherwise (for synchronous messages) is changed exception in the exchange {@link Exchange#EXCEPTION_CAUGHT}
 * and processing is redirected to {@link ExceptionTranslator}.
 * <p>
 *
 * If there is no fault detail defined or new exception wasn't thrown then exchange property
 * {@link AsynchConstants#EXCEPTION_ERROR_CODE} would contain error code
 * that is defined by {@link #getErrorCodeForException(Exception)}
 * and processing is redirected to {@link ExceptionTranslator} for synchronous messages
 * ({@link #isAsynchMessage()} returns false).
 * <p>
 *
 * Filter accepts SOAP standards 1.1 and 1.2.
 *
 * @author Petr Juza
 */
//TODO (juza) applicant for moving to API
public abstract class AbstractSoapExceptionFilter implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSoapExceptionFilter.class);

    private boolean asynchMessage;

    private boolean redirectToExTranslator = true;

    private ExceptionTranslator exceptionTranslator = ExceptionTranslator.getInstance();

    /**
     * Creates exception filter.
     *
     * @param asynchMessage {@code true} when it's asynchronous processing, otherwise {@code false}
     */
    protected AbstractSoapExceptionFilter(boolean asynchMessage) {
        this.asynchMessage = asynchMessage;
    }

    @Override
    public final void process(Exchange exchange) throws Exception {
        Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (ex instanceof SoapFaultClientException) {
            SoapFaultClientException soapEx = (SoapFaultClientException) ex;

            Exception faultException = null;

            if (soapEx.getSoapFault() != null) {
                // there is fault detail
                faultException = getFaultException(soapEx.getSoapFault().getFaultDetail());

                if (faultException != null) {
                    LOG.debug("get new exception (asynchMessage = " + asynchMessage + "): " + faultException);

                    if (asynchMessage) {
                        // throw exception for asynchronous processing - parent routes will catch it
                        throw faultException;
                    } else {
                        // change exception in the exchange
                        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, faultException);
                    }
                }
            }

            // at least save error code
            exchange.setProperty(AsynchConstants.EXCEPTION_ERROR_CODE, getErrorCodeForException(faultException));

            if (!asynchMessage && redirectToExTranslator) {
                // redirects to ExceptionTranslator
                exceptionTranslator.process(exchange);
            }
        }
    }

    /**
     * Returns {@link ErrorExtEnum error code} for the specified exception.
     * If there is no specified input exception then returns error code for common error for specific external system.
     *
     * @param faultException the exception
     * @return error code
     */
    protected abstract ErrorExtEnum getErrorCodeForException(@Nullable Exception faultException);

    /**
     * Gets exception from fault detail.
     * <p/>
     * If there is no fault detail then no exception is returned.
     * If there is no supported exception in fault detail then {@link IntegrationException} is returned.
     *
     * @param faultDetail the fault detail
     * @return exception
     */
    @Nullable
    private Exception getFaultException(@Nullable SoapFaultDetail faultDetail) {
        if (faultDetail != null) {
            DOMSource detailSource = (DOMSource) faultDetail.getSource();
            Node detailNode = detailSource.getNode();

            QName exName = getExceptionName(detailNode);

            Exception exception = createException(exName, detailNode);
            if (exception == null) {
                // throws common exception with specified exception
                exception = new IntegrationException(getErrorCodeForException(null), exName.getLocalPart());
            }

            return exception;
        }

        return null;
    }

    /**
     * Creates exception from SOAP detail node (= element detail/Detail).
     * <p>
     * Example of the generated SOAP fault response for SOAP 1.1:
     * <pre>
        &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt;
        &lt;SOAP-ENV:Header/&gt;
        &lt;SOAP-ENV:Body&gt;
           &lt;SOAP-ENV:Fault&gt;
              &lt;faultcode&gt;SOAP-ENV:Server&lt;/faultcode&gt;
              &lt;faultstring xml:lang="en"&gt;E102: the validation error&lt;/faultstring&gt;
              &lt;detail&gt;
                &lt;errorCode xmlns="http://openhubframework.org"&gt;E102&lt;/errorCode&gt;
              &lt;/detail&gt;
           &lt;/SOAP-ENV:Fault&gt;
        &lt;/SOAP-ENV:Body&gt;
        &lt;/SOAP-ENV:Envelope&gt;
        </pre>
     * <p>
     * Example of the generated SOAP fault response for SOAP 1.2:
     * <pre>
        &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope"&gt;
        &lt;SOAP-ENV:Header/&gt;
        &lt;SOAP-ENV:Body&gt;
           &lt;SOAP-ENV:Fault&gt;
              &lt;SOAP-ENV:Code&gt;&lt;SOAP-ENV:Value&gt;&lt;SOAP-ENV:Server&lt;/SOAP-ENV:Value&gt;&lt;/SOAP-ENV:Code&gt;
              &lt;SOAP-ENV:Reason xml:lang="en"&gt;E102: the validation error&lt;/SOAP-ENV:Reason&gt;
              &lt;SOAP-ENV:Detail&gt;
                &lt;errorCode xmlns="http://openhubframework.org"&gt;E102&lt;/errorCode&gt;
              &lt;/SOAP-ENV:Detail&gt;
           &lt;/SOAP-ENV:Fault&gt;
        &lt;/SOAP-ENV:Body&gt;
        &lt;/SOAP-ENV:Envelope&gt;
        </pre>
     *
     * @param exName the fully qualified exception name (e.g.: ValidityMismatch)
     * @param detailNode the XML node that represents fault detail
     * @return exception or {@code null} if specified exception is not supported.
     */
    @Nullable
    protected abstract Exception createException(QName exName, Node detailNode);

    /**
     * Gets the exception name.
     *
     * @param detailNode the fault detail node
     * @return exception fully qualified name
     */
    protected QName getExceptionName(Node detailNode) {
        Node exNode = getFirstElm(detailNode);

        return new QName(exNode.getNamespaceURI(), exNode.getLocalName());
    }

    /**
     * Gets first child element (node with type {@link Node#ELEMENT_NODE}.
     *
     * @param node the node
     * @return element
     */
    protected Node getFirstElm(Node node) {
        node = node.getFirstChild();

        while (node.getNodeType() != Node.ELEMENT_NODE) {
            node = node.getNextSibling();
        }

        return node;
    }

    public boolean isAsynchMessage() {
        return asynchMessage;
    }

    /**
     * Gets the SOAP fault name from the class with {@link WebFault @WebFault}.
     *
     * @param clazz the class that represents fault
     * @return fault name
     */
    protected static String getExFaultName(Class clazz) {
        WebFault annotation = AnnotationUtils.findAnnotation(clazz, WebFault.class);
        return annotation.name();
    }

    public boolean isRedirectToExTranslator() {
        return redirectToExTranslator;
    }

    /**
     * Sets whether to redirect response to {@link ExceptionTranslator}.
     * Valid only if {@link #isAsynchMessage()} is {@code true}.
     *
     * @param redirectToExTranslator {@code true} for redirecting otherwise {@code false}
     */
    public void setRedirectToExTranslator(boolean redirectToExTranslator) {
        this.redirectToExTranslator = redirectToExTranslator;
    }
}
