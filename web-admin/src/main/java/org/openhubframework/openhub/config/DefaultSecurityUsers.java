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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Default users properties.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Component
@ConfigurationProperties(prefix = "security.user")
public class DefaultSecurityUsers {

    private String wsUser;

    private String wsPassword;

    private String webUser;

    private String webPassword;

    private String monitoringUser;

    private String monitoringPassword;

    public String getWsUser() {
        return wsUser;
    }

    public void setWsUser(String wsUser) {
        this.wsUser = wsUser;
    }

    public String getWsPassword() {
        return wsPassword;
    }

    public void setWsPassword(String wsPassword) {
        this.wsPassword = wsPassword;
    }

    public String getWebUser() {
        return webUser;
    }

    public void setWebUser(String webUser) {
        this.webUser = webUser;
    }

    public String getWebPassword() {
        return webPassword;
    }

    public void setWebPassword(String webPassword) {
        this.webPassword = webPassword;
    }

    public String getMonitoringUser() {
        return monitoringUser;
    }

    public void setMonitoringUser(String monitoringUser) {
        this.monitoringUser = monitoringUser;
    }

    public String getMonitoringPassword() {
        return monitoringPassword;
    }

    public void setMonitoringPassword(String monitoringPassword) {
        this.monitoringPassword = monitoringPassword;
    }
}
