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

package org.cleverbus.core.common.ws.component;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ws.InvalidXmlException;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.DefaultTransportContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;
import org.springframework.ws.transport.support.TransportUtils;


/**
 * Spring WS error handler that returns SOAP fault message when there is error during XML parsing
 * in incoming message.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ErrorAwareWebServiceMessageReceiverHandlerAdapter extends WebServiceMessageReceiverHandlerAdapter {

    @Override
    protected void handleInvalidXmlException(HttpServletRequest req, HttpServletResponse res, Object handler,
                                             InvalidXmlException ex) throws Exception {

        WebServiceConnection connection = new MyHttpServletConnection(req, res);

        TransportContext previousTransportContext = TransportContextHolder.getTransportContext();
        TransportContextHolder.setTransportContext(new DefaultTransportContext(connection));

        try {
            MessageContext messageContext = new DefaultMessageContext(getMessageFactory().createWebServiceMessage(),
                    getMessageFactory());

            SoapBody soapBody = ((SoapMessage) messageContext.getResponse()).getSoapBody();
            soapBody.addServerOrReceiverFault(getFaultString(ex), Locale.ENGLISH);
            connection.send(messageContext.getResponse());
        } finally {
            TransportUtils.closeConnection(connection);
            TransportContextHolder.setTransportContext(previousTransportContext);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nothing to check - messageFactory will be set later in MessageDispatcherServlet
    }

    protected String getFaultString(InvalidXmlException ex) {
        return ex.getLocalizedMessage();
    }
}