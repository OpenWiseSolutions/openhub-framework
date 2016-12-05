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

package org.openhubframework.openhub.admin.web.config;

import java.util.Arrays;

import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import org.openhubframework.openhub.core.common.route.SpringWsUriBuilder;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareSoapExceptionResolver;
import org.openhubframework.openhub.core.common.ws.ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter;
import org.openhubframework.openhub.core.common.ws.HeaderAndPayloadValidatingInterceptor;


/**
 * Web services configuration.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
   	public ServletRegistrationBean dispatcherWsRegistration(ApplicationContext applicationContext) {
   		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
   		servlet.setApplicationContext(applicationContext);
   		return new ServletRegistrationBean(servlet, "/ws/*");
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
        validatingInterceptor.setSchemas(
                // the order of XSDs is important: commons -> entity XSD -> service XSD
                new ClassPathResource("org/openhubframework/openhub/api/modules/in/common/commonTypes-v1.0.xsd"),
                new ClassPathResource("org/openhubframework/openhub/modules/in/hello/ws/v1_0/helloOperations-v1.0.xsd"));
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.setIgnoreRequests(
                Arrays.asList("{http://openhubframework.org/ws/HelloService-v1}syncHelloRequest"));

        return validatingInterceptor;
    }

    @Bean
    public SoapEnvelopeLoggingInterceptor loggingInterceptor() {
        return new SoapEnvelopeLoggingInterceptor();
    }

   	@Bean(name = "endpointMapping")
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
   		return new SimpleXsdSchema(new ClassPathResource(
   		        "org/openhubframework/openhub/api/modules/in/common/commonTypes-v1.0.xsd"));
   	}

   	@Bean(name = "helloOperations-v1.0")
   	public XsdSchema helloOperations() {
   		return new SimpleXsdSchema(new ClassPathResource(
   		        "org/openhubframework/openhub/modules/in/hello/ws/v1_0/helloOperations-v1.0.xsd"));
   	}

    @Bean(name = "hello")
   	public SimpleWsdl11Definition helloWsdl() {
        SimpleWsdl11Definition wsdl = new SimpleWsdl11Definition();
        wsdl.setWsdl(new ClassPathResource(
                "org/openhubframework/openhub/modules/in/hello/ws/v1_0/hello-v1.0.wsdl"));
   		return wsdl;
   	}
}
