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

package org.openhubframework.openhub.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WS_AUTH_POLICY;
import static org.openhubframework.openhub.api.route.RouteConstants.WS_URI_PREFIX;

import java.util.Collections;

import org.apache.camel.component.spring.security.SpringSecurityAccessPolicy;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.config.CamelConfig;


/**
 * Web security configuration.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Configuration
@EnableGlobalMethodSecurity // allows AOP @PreAuthorize and some other annotations to be applied to methods
@EnableWebSecurity
@AutoConfigureBefore(value = CamelConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DefaultSecurityUsers defaultUsers;

    /**
     * Basic configuration for Web services, handled by {@link RouteConstants#WS_URI_PREFIX}. 
     */
    @Configuration
    @Order(WsWebSecurityConfig.ORDER)
    public static class WsWebSecurityConfig extends WebSecurityConfigurerAdapter {

        /**
         * Order of this {@link WsWebSecurityConfig}. Must be processed before standard 
         * {@link WebSecurityConfigurerAdapter}.
         */
        public static final int ORDER = 1;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.csrf().disable() // HTTP with disabled CSRF
                    .antMatcher(WS_URI_PREFIX + "**")
                    .authorizeRequests()
                        .anyRequest().hasAnyRole(AuthRole.WS.name())
                        .and()
                    .httpBasic();
            // @formatter:on
        }
    }


    @Autowired
   	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
   		auth.inMemoryAuthentication().withUser(defaultUsers.getWsUser())
                .password(defaultUsers.getWsPassword()).roles(AuthRole.WS.name());
   		auth.inMemoryAuthentication().withUser(defaultUsers.getWebUser())
                .password(defaultUsers.getWebPassword()).roles(AuthRole.WEB.name(), AuthRole.WS.name(), AuthRole.MONITORING.name());
   		auth.inMemoryAuthentication().withUser(defaultUsers.getMonitoringUser())
                .password(defaultUsers.getMonitoringPassword()).roles(AuthRole.MONITORING.name());
   	}

    /**
     * Configures access decision manager.
     */
    @Bean
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
    public SpringSecurityAuthorizationPolicy authorizationPolicy(AccessDecisionManager accessDecisionManager,
            AuthenticationManager authManager) {
        SpringSecurityAuthorizationPolicy authPolicy = new SpringSecurityAuthorizationPolicy();
        authPolicy.setAccessDecisionManager(accessDecisionManager);
        authPolicy.setAuthenticationManager(authManager);
        authPolicy.setUseThreadSecurityContext(true);
        authPolicy.setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy(AuthRole.WS.name()));
        return authPolicy;
    }


    private enum AuthRole {
        USER,
        WS,
        WEB,
        MONITORING;
    }
}


