/*
 * Copyright 2002-2017 the original author or authors.
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

package org.openhubframework.openhub.web.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WS_AUTH_POLICY;
import static org.openhubframework.openhub.api.route.RouteConstants.WS_URI_PREFIX;
import static org.openhubframework.openhub.web.config.GlobalSecurityConfig.DEFAULT_PATH_PATTERN;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.config.CamelConfig;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;


/**
 * Web security configuration.
 *
 * @author Petr Juza
 * @see GlobalSecurityConfig
 * @see WsSecurityConfig
 * @since 2.0
 */
@EnableWebSecurity
@AutoConfigureBefore(value = CamelConfig.class)
// own custom access rules
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Basic configuration for Web services, handled by {@link RouteConstants#WS_URI_PREFIX}. 
     */
    @Configuration
    @Order(WsSecurityConfig.ORDER)
    public static class WsSecurityConfig extends WebSecurityConfig {

        @Autowired
        private SecurityProperties securityProperties;

        /**
         * Order of this {@link WsSecurityConfig}. Must be processed before standard 
         * {@link WebSecurityConfigurerAdapter}.
         */
        public static final int ORDER = 1;

        /**
         * WWW-Authenticate header name.
         */
        static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";

        @Override
        protected void configure(HttpSecurity http) throws Exception {


            // @formatter:off
            http.csrf().disable() // HTTP with disabled CSRF
                    .antMatcher(WS_URI_PREFIX + DEFAULT_PATH_PATTERN)
                    .authorizeRequests()
                        .anyRequest().hasAnyRole(GlobalSecurityConfig.AuthRole.WS.name())
                        .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(basicAuthenticationEntryPoint())
                    .and()
                    .httpBasic()
            ;
            // @formatter:on
        }

        // Basic authentication entry point, does return 401 UNAUTHORIZED with WWW-Authenticate header set.
        //
        // Note: custom entry point is used, because the original spring BasicAuthenticationEntryPoint does set unauthorized
        // by invoking HttpResponse.sendError. With current openhub configuration however, it is not handled correctly
        // in standalone deployment. With "manually" setting status on HttpResponse, it works in both scenarios
        // (standalone & deployment to servlet container).
        private AuthenticationEntryPoint basicAuthenticationEntryPoint() {
            return (request, response, authException) -> {
                if (authException != null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    Assert.notNull(securityProperties, "the securityProperties must be set");
                    Assert.notNull(securityProperties.getBasic(), "the security.basic must be set");
                    final String realmName = securityProperties.getBasic().getRealm();
                    response.addHeader(
                            WWW_AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");
                }
            };
        }
    }

    /**
     * Configures access decision manager.
     */
    @Bean
    @ConditionalOnMissingBean
    public AffirmativeBased accessDecisionManager() {
        AffirmativeBased accessManager = new AffirmativeBased(
                Collections.<AccessDecisionVoter<? extends Object>>singletonList(new RoleVoter()));
        accessManager.setAllowIfAllAbstainDecisions(true);

        return accessManager;
    }

    /**
     * Configures authorization policy for Camel.
     */
    @Bean(name = WS_AUTH_POLICY)
    @ConditionalOnMissingBean(name = WS_AUTH_POLICY)
    public SpringSecurityAuthorizationPolicy authorizationPolicy(AccessDecisionManager accessDecisionManager,
            AuthenticationManager authManager) {
        SpringSecurityAuthorizationPolicy authPolicy = new SpringSecurityAuthorizationPolicy();
        authPolicy.setAccessDecisionManager(accessDecisionManager);
        authPolicy.setAuthenticationManager(authManager);
        authPolicy.setUseThreadSecurityContext(true);
        authPolicy.setSpringSecurityAccessPolicy(
                new SpringSecurityAccessPolicy(GlobalSecurityConfig.AuthRole.WS.name()));
        return authPolicy;
    }
}


