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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for JavaMelody.
 *
 * @author Tomas Hanus
 */
@Configuration
@ConfigurationProperties(prefix = JavaMelodyConfigurationProperties.PREFIX)
public class JavaMelodyConfigurationProperties {
    
    /**
     * Prefix of properties names.
     */
    public static final String PREFIX = "javamelody";

    private boolean enabled = true;
    private Map<String, String> initParameters = new HashMap<>();

    /**
     * Returns if JavaMelody should be enabled within the application.
     *
     * @return {@code true} if JavaMelody should be enabled, otherwise {@code false}. Default: {@code true}
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether JavaMelody should be enabled within the application.
     *
     * @param enabled {@code true} if JavaMelody should be enabled, otherwise {@code false}.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns a map of initialization parameters to be passed to the JavaMelody monitoring filter.
     *
     * @return Initialization parameters for the JavaMelody monitoring filter.
     */
    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    /**
     * Sets a map of initialization parameters to be passed to the JavaMelody monitoring filter.
     *
     * @param initParameters Initialization parameters for the JavaMelody monitoring filter.
     */
    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("enabled", enabled)
                .append("initParameters", initParameters)
                .toString();
    }
}
