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

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;


/**
 * Web security configuration.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@Configuration
@EnableGlobalMethodSecurity // allows AOP @PreAuthorize and some other annotations to be applied to methods
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PAGE_URL = "/web/admin/login";
    private static final String[] COOKIES_TO_DELETE = new String[]{"JSESSIONID"};

    @Override
    @SuppressWarnings("unchecked")
    protected void configure(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry
                = http.authorizeRequests();

        // permit all static resources
        urlRegistry.antMatchers("/webjars/**").permitAll();
        urlRegistry.antMatchers("/js/**").permitAll();
        urlRegistry.antMatchers("/img/**").permitAll();
        urlRegistry.antMatchers("/css/**").permitAll();

        // web services
        urlRegistry.antMatchers("/ws/**").hasRole(AuthRole.WS.getName())
                .and()
                .httpBasic();

        // web admin
        urlRegistry
                .antMatchers("/web/admin/homepage/**").permitAll()
                .antMatchers("/web/admin/login/**").permitAll()
                .antMatchers("/web/admin/**").hasRole(AuthRole.WEB.getName())
                .antMatchers("/web/admin/**/*").hasRole(AuthRole.WEB.getName())
                .antMatchers("/monitoring/**").hasRole(AuthRole.MONITORING.getName())
                .and()
                .formLogin()
                    .loginPage(LOGIN_PAGE_URL)
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/web/admin/console")
                    .failureUrl("/web/admin/login?error")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .invalidateHttpSession(true)
                    .deleteCookies(COOKIES_TO_DELETE)
                    .logoutSuccessUrl("/web/admin/homepage")
                    .and()
                .sessionManagement()
                    .maximumSessions(1)
                    .expiredUrl(LOGIN_PAGE_URL + "?expired");

        // deactivate CSRF
        urlRegistry.and().csrf().disable();

        // explicit deactivate HTTP basic auth
        urlRegistry.and().httpBasic().disable();

        // activate remember me functionality
        urlRegistry.and().rememberMe();
    }

    @Override
   	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //TODO PJUZA replace from properties
   		auth.inMemoryAuthentication().withUser("wsUser").password("wsPassword").roles(AuthRole.WS.getName());
   		auth.inMemoryAuthentication().withUser("webUser").password("webPassword").roles(AuthRole.WEB.getName(),
                AuthRole.WS.getName(), AuthRole.MONITORING.getName());
   		auth.inMemoryAuthentication().withUser("monUser").password("monPassword").roles(
   		        AuthRole.MONITORING.getName());
   	}

    private enum AuthRole {
        USER,
        WS,
        WEB,
        MONITORING;

        public String getName() {
            return name();
        }
    }
}


