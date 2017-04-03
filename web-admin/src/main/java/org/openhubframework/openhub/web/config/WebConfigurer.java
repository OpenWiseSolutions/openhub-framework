/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.web.config;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter;


/**
 * Web configurer which is responsible for configuration of web runtime layer of OpenHub, for example servlet
 * registration of camel connectors.
 *
 * @author Tomas Hanus
 * @see WebContextConfig
 * @since 2.0
 */
@Configuration
public class WebConfigurer {

    /**
     * Configures servlet for HTTP communication.
     *
     * @return registration bean of {@link CamelHttpTransportServlet}.
     */
    @Bean(name = RouteConstants.CAMEL_SERVLET)
    @ConditionalOnMissingBean(name = RouteConstants.CAMEL_SERVLET)
    public ServletRegistrationBean camelHttpServlet() {
        CamelHttpTransportServlet servlet = new CamelHttpTransportServlet();
        servlet.setServletName(RouteConstants.CAMEL_SERVLET);

        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, RouteConstants.HTTP_URI_PREFIX_MAPPING);
        bean.setName(servlet.getServletName());

        return bean;
    }

    /**
     * Configures servlet to dispatching of Web service messages.
     *
     * @param applicationContext to map dispatcher to current context
     * @return registration bean of {@link MessageDispatcherServlet}.
     * @see #messageReceiverHandlerAdapter
     */
    @Bean(name = RouteConstants.WS_SERVLET)
    @ConditionalOnMissingBean(name = RouteConstants.WS_SERVLET)
    public ServletRegistrationBean dispatcherWsRegistration(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        
        return new ServletRegistrationBean(servlet, RouteConstants.WS_URI_PREFIX_MAPPING);
    }

    /**
     * Register Web service receiver adapter.
     *
     * @return {@link WebServiceMessageReceiverHandlerAdapter}
     */
    @Bean(name = MessageDispatcherServlet.DEFAULT_MESSAGE_RECEIVER_HANDLER_ADAPTER_BEAN_NAME)
    @ConditionalOnMissingBean(name = MessageDispatcherServlet.DEFAULT_MESSAGE_RECEIVER_HANDLER_ADAPTER_BEAN_NAME)
    public WebServiceMessageReceiverHandlerAdapter messageReceiverHandlerAdapter() {
        return new ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter();
    }
}
