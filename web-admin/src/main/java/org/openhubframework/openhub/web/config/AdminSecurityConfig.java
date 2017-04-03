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

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;
import static org.openhubframework.openhub.web.config.GlobalSecurityConfig.DEFAULT_PATH_PATTERN;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;


/**
 * Admin security configuration.
 *
 * @author Tomas Hanus
 * @since 2.0
 * @see GlobalSecurityConfig
 * @see UiSecurityConfig
 * @see WebSecurityConfig
 */
@EnableWebSecurity
// Allows AOP @PreAuthorize and some other annotations to be applied to methods
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Basic configuration for admin, handled by {@link RouteConstants#WEB_URI_PREFIX}.
     */
    @Configuration
    @Order(UiSecurityConfig.ORDER)
    public static class UiSecurityConfig extends AdminSecurityConfig {

        /**
         * Order of this {@link UiSecurityConfig}. Must be processed after
         * {@link WebSecurityConfig.WsSecurityConfig}.
         */
        public static final int ORDER = WebSecurityConfig.WsSecurityConfig.ORDER + 5;

        private static final String LOGIN_PAGE_URL = WEB_URI_PREFIX + "login";
        private static final String[] COOKIES_TO_DELETE = new String[]{"JSESSIONID"};

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
                        .antMatchers(WEB_URI_PREFIX + "homepage/**")
                            .permitAll()
                        .antMatchers(WEB_URI_PREFIX + "login/**")
                            .permitAll()
                        .antMatchers(WEB_URI_PREFIX + DEFAULT_PATH_PATTERN)
                            .hasRole(GlobalSecurityConfig.AuthRole.WEB.name())
                        .antMatchers(WEB_URI_PREFIX + "**/*")
                            .hasRole(GlobalSecurityConfig.AuthRole.WEB.name())
                        .antMatchers("/monitoring/" + DEFAULT_PATH_PATTERN)
                            .hasRole(GlobalSecurityConfig.AuthRole.MONITORING.name())
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
}


