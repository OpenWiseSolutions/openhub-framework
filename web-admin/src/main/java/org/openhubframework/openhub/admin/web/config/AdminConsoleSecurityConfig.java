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

package org.openhubframework.openhub.admin.web.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.config.DefaultSecurityUsers;
import org.openhubframework.openhub.config.WebSecurityConfig;


/**
 * Admin console security configuration.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Configuration
@EnableWebSecurity
public class AdminConsoleSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PAGE_URL = WEB_URI_PREFIX + "login";

    private static final String[] COOKIES_TO_DELETE = new String[]{"JSESSIONID"};

    @Autowired
    private DefaultSecurityUsers defaultUsers;

    /**
     * Basic configuration for admin console, handled by {@link RouteConstants#WEB_URI_PREFIX}. 
     */
    @Configuration
    @Order(ConsoleSecurityConfig.ORDER)
    public static class ConsoleSecurityConfig extends WebSecurityConfigurerAdapter {

        /**
         * Order of this {@link ConsoleSecurityConfig}. Must be processed after {@link WebSecurityConfig.WsWebSecurityConfig}.
         */
        public static final int ORDER = WebSecurityConfig.WsWebSecurityConfig.ORDER + 1;

        @Override
        public void configure(WebSecurity web) throws Exception {
            // ignore security check for static resources
            web.ignoring().antMatchers("/webjars/**", "/js/**", "/img/**", "/css/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.csrf().disable() // HTTP with disabled CSRF
                    .authorizeRequests() //Authorize Request Configuration
                        .antMatchers(WEB_URI_PREFIX + "homepage/**").permitAll()
                        .antMatchers(WEB_URI_PREFIX + "login/**").permitAll()
                        .antMatchers(WEB_URI_PREFIX + "**").hasRole(AuthRole.WEB.name())
                        .antMatchers(WEB_URI_PREFIX + "**/*").hasRole(AuthRole.WEB.name())
                        .antMatchers("/monitoring/**").hasRole(AuthRole.MONITORING.name())
                        .and()
                    .formLogin()
                        .loginPage(LOGIN_PAGE_URL)
                        .loginProcessingUrl(LOGIN_PAGE_URL)
                        .defaultSuccessUrl(WEB_URI_PREFIX + "console")
                        .failureUrl(WEB_URI_PREFIX + "login?error")
                        .permitAll()
                        .and()
                    .logout()
                        .permitAll()
                        .invalidateHttpSession(true)
                        .deleteCookies(COOKIES_TO_DELETE)
                        .logoutUrl(WEB_URI_PREFIX + "logout")
                        .logoutSuccessUrl(WEB_URI_PREFIX + "homepage")
                        .and()
                    .sessionManagement()
                        .maximumSessions(1)
                        .expiredUrl(LOGIN_PAGE_URL + "?expired");
            // @formatter:on
        }
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(defaultUsers.getWebUser()).password(defaultUsers.getWebPassword())
                .roles(AuthRole.WEB.name(), AuthRole.WS.name(), AuthRole.MONITORING.name());
        auth.inMemoryAuthentication()
                .withUser(defaultUsers.getMonitoringUser()).password(defaultUsers.getMonitoringPassword())
                .roles(AuthRole.MONITORING.name());
    }

    private enum AuthRole {
        WS,
        WEB,
        MONITORING
    }
}


