package org.openhubframework.openhub.admin.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX_MAPPING;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.web.RequestResponseLoggingFilter;

/**
 * Web configurer which is responsible for configuration of web admin client.
 *
 * @author Tomas Hanus
 * @see AdminMvcConfig
 * @see AdminSecurityConfig
 * @see CustomFreemarkerConfig
 * @since 2.0
 */
@Configuration
public class WebAdminConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(WebAdminConfigurer.class);

    /**
     * The ID of web context.
     */
    public static final String WEB_CONTEXT_ID = "WebContext";

    /**
     * Registers {@link RequestResponseLoggingFilter}.
     */
    @Bean
    public FilterRegistrationBean loggingRest() {
        LOG.info("REQ/RES logging initialization");

        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();
        filter.setLogUnsupportedContentType(false);
        final FilterRegistrationBean bean = new FilterRegistrationBean(filter);
        // we use logging filter only for administration endpoints to avoid duplication log events
        bean.addUrlPatterns(WEB_URI_PREFIX_MAPPING);

        return bean;
    }

    /**
     * Defines localized messages for admin console.
     */
    @Bean
    @ConditionalOnMissingBean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Defines default {@link LocaleResolver}.
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * Configures servlet for admin interface of OpenHub.
     *
     * @param context           current context
     * @param dispatcherServlet as actually registered dispatcher
     * @return registration bean of {@link DispatcherServlet} to handling {@link RouteConstants#WEB_URI_PREFIX_MAPPING}.
     */
    @Bean(name = WEB_CONTEXT_ID)
    @ConditionalOnMissingBean(name = WEB_CONTEXT_ID)
    public ServletRegistrationBean adminServlet(
            ConfigurableApplicationContext context,
            DispatcherServlet dispatcherServlet) {

        ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet);
        // sets corresponding ID (name) of web context
        context.setId(WEB_CONTEXT_ID);
        bean.addUrlMappings(WEB_URI_PREFIX_MAPPING);

        return bean;
    }
}
