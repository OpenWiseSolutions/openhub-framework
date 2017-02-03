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

package org.openhubframework.openhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;

import org.openhubframework.openhub.common.AutoConfiguration;


/**
 * Global <strong>default</strong> security configuration of OpenHub.
 * <p>
 * To override that it is necessary to create own global security configuration with higher precedence 
 * or better use {@link AutoConfigureBefore} declaration with relation to this configuration.
 * 
 * @author Tomas Hanus
 * @since 2.0
 */
@AutoConfiguration
@ConditionalOnBean(GlobalAuthenticationConfigurerAdapter.class)
public class GlobalSecurityConfig extends GlobalAuthenticationConfigurerAdapter {

    /**
     * Default path separator: {@code **}.
     */
    public static final String DEFAULT_PATH_PATTERN = "**";

    @Autowired
    private DefaultSecurityUsers defaultUsers;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
        auth.inMemoryAuthentication()
                .withUser(defaultUsers.getWsUser()).password(defaultUsers.getWsPassword())
                    .roles(AuthRole.WS.name());
        auth.inMemoryAuthentication()
                .withUser(defaultUsers.getWebUser()).password(defaultUsers.getWebPassword())
                    .roles(AuthRole.WEB.name(), AuthRole.WS.name(), AuthRole.MONITORING.name());
        auth.inMemoryAuthentication()
                .withUser(defaultUsers.getMonitoringUser()).password(defaultUsers.getMonitoringPassword())
                    .roles(AuthRole.MONITORING.name());        
        // @formatter:on
    }

    /**
     * Default authentication roles definition.
     */
    public enum AuthRole {
        WS,
        WEB,
        MONITORING
    }
}

