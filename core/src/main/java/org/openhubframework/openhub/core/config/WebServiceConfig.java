/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.adapter.MessageEndpointAdapter;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHandlerAdapter;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import org.openhubframework.openhub.api.config.WebServiceValidatingSources;
import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.common.route.SpringWsUriBuilder;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareSoapExceptionResolver;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter;
import org.openhubframework.openhub.core.common.ws.HeaderAndPayloadValidatingInterceptor;


/**
 * Web services configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@EnableWs
@Configuration
@AutoConfigureBefore(value = CamelConfig.class)
public class WebServiceConfig extends WsConfigurerAdapter {

    private static final ClassPathResource XSD_COMMON_RESOURCE = new ClassPathResource(
            "org/openhubframework/openhub/api/modules/in/common/commonTypes-v1.0.xsd");

    @Autowired(required = false)
    private List<WebServiceValidatingSources> xsdSources;

    @Bean
   	public ServletRegistrationBean dispatcherWsRegistration(ApplicationContext applicationContext) {
   		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
   		servlet.setApplicationContext(applicationContext);
   		return new ServletRegistrationBean(servlet, RouteConstants.WS_URI_PREFIX + "*");
   	}

    @Bean
    //http://stackoverflow.com/questions/31048389/no-adapter-for-endpoint-exception-apache-camel-with-spring-boot-spring-ws
    public EndpointAdapter messageEndpointAdapter() {
        return new MessageEndpointAdapter();
    }

    @Bean
    public HeaderAndPayloadValidatingInterceptor validatingInterceptor() {
        HeaderAndPayloadValidatingInterceptor validatingInterceptor = new HeaderAndPayloadValidatingInterceptor();
        validatingInterceptor.setFaultStringOrReason("E101: the request message is not valid against XSD schema");
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);

        // get all schema resources
        // the order of XSDs is important: commons -> entity XSD -> service XSD
        List<Resource> schemas = new ArrayList<>();
        List<String> ignoreRequests = new ArrayList<>();
        schemas.add(XSD_COMMON_RESOURCE);
        if (xsdSources != null) {
            for (WebServiceValidatingSources xsdSource : xsdSources) {
                schemas.addAll(Arrays.asList(xsdSource.getXsdSchemas()));
                ignoreRequests.addAll(Arrays.asList(xsdSource.getIgnoreRequests()));
            }
        }
        validatingInterceptor.setSchemas(schemas.toArray(new Resource[]{}));
        validatingInterceptor.setIgnoreRequests(ignoreRequests);

        return validatingInterceptor;
    }

    @Bean
    public SoapEnvelopeLoggingInterceptor loggingInterceptor() {
        return new SoapEnvelopeLoggingInterceptor();
    }

   	@Bean(name = RouteConstants.ENDPOINT_MAPPING_BEAN)
    public EndpointMapping endpointMapping(SoapEnvelopeLoggingInterceptor loggingInterceptor,
            HeaderAndPayloadValidatingInterceptor validatingInterceptor) {
        CamelEndpointMapping mapping = new CamelEndpointMapping();
        mapping.setInterceptors(new EndpointInterceptor[] {loggingInterceptor, validatingInterceptor});
        return mapping;
    }

   	@Bean
    public ErrorCodeAwareSoapExceptionResolver endpointExceptionResolver() {
        return new ErrorCodeAwareSoapExceptionResolver();
    }

   	@Bean(name = MessageDispatcherServlet.DEFAULT_MESSAGE_RECEIVER_HANDLER_ADAPTER_BEAN_NAME)
    public WebServiceMessageReceiverHandlerAdapter messageReceiverHandlerAdapter() {
        return new ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter();
    }

   	@Bean(name = SpringWsUriBuilder.MESSAGE_FACTORY_SOAP11)
    public SoapMessageFactory messageFactorySOAP11() {
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_11);
        return factory;
    }

    @Bean(name = SpringWsUriBuilder.MESSAGE_FACTORY_SOAP12)
    public SoapMessageFactory messageFactorySOAP12() {
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_12);
        return factory;
    }

   	@Bean(name = "commonTypes-v1.0")
   	public XsdSchema commonTypes() {
        return new SimpleXsdSchema(XSD_COMMON_RESOURCE);
   	}
}
