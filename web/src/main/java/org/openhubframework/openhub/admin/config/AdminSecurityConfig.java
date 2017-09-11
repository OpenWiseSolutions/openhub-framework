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

package org.openhubframework.openhub.admin.config;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;
import static org.openhubframework.openhub.web.config.GlobalSecurityConfig.DEFAULT_PATH_PATTERN;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.web.common.WebProps;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.web.config.GlobalSecurityConfig;
import org.openhubframework.openhub.web.config.WebSecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


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

        @ConfigurableValue(key = WebProps.SESSION_CONCURRENCY_LIMIT)
        private ConfigurationItem<Integer> sessionConcurrencyLimit;

        /**
         * Order of this {@link UiSecurityConfig}. Must be processed after
         * {@link WebSecurityConfig.WsSecurityConfig}.
         */
        public static final int ORDER = WebSecurityConfig.WsSecurityConfig.ORDER + 5;

        private static final String LOGIN_PAGE_URL = WEB_URI_PREFIX + "login";
        private static final String[] COOKIES_TO_DELETE = new String[]{"JSESSIONID"};

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            Constraints.notNull(sessionConcurrencyLimit.getValue(), "the sessionConcurrencyLimit must be configured.");

            // @formatter:off
            http.csrf().disable() // HTTP with disabled CSRF
                    .authorizeRequests() //Authorize Request Configuration
                        .antMatchers(WEB_URI_PREFIX + "console/**")
                            .permitAll()
                        .antMatchers(WEB_URI_PREFIX + "login/**")
                            .permitAll()
                        .antMatchers(WEB_URI_PREFIX + "mgmt/info")
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
                        .successHandler(authenticationSuccessHandler())
                        // on failure, return 403 FORBIDDEN
                        .failureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                        .permitAll()
                        .and()
                    .logout()
                        .permitAll()
                        .invalidateHttpSession(true)
                        .deleteCookies(COOKIES_TO_DELETE)
                        .logoutUrl(WEB_URI_PREFIX + "logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .and()
                    .sessionManagement()
                        .maximumSessions(sessionConcurrencyLimit.getValue())
                        .expiredUrl(WEB_URI_PREFIX + "console/")
            ;
            // @formatter:on
        }
    }

    // simple authenticationSuccessHandler, results in 200 OK response.
    private static AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request,
                    HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                clearAuthenticationAttributes(request);
            }
        };
    }

    // unauthorized entry point, does return 401 UNAUTHORIZED.
    private static AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            if (authException != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }
}
