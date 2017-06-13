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
 * Web configurer which is responsible for servlet registration of camel connectors.
 *
 * @author Tomas Hanus
 * @see WebSecurityConfig
 * @since 2.0
 */
@Configuration
public class WebServiceConfigurer {

    /**
     * The ID of webservice context.
     */
    public static final String WS_CONTEXT_ID = "WsContext";

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
        servlet.setContextId(WS_CONTEXT_ID);

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
