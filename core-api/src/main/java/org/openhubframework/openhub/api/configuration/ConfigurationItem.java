/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.api.configuration;

import org.openhubframework.openhub.api.exception.validation.ConfigurationException;


/**
 * Contract of configuration holder, which is wrapping one config item allowing to manage that.
 *
 * @param <T> represents type of a configuration item (allowed types are determined by backing repository)
 * @author Tomas Hanus
 * @since 2.0
 */
public interface ConfigurationItem<T> {

    /**
     * Gets required configuration item.
     *
     * @return Configuration item loaded from repository
     * @throws ConfigurationException When configuration item is not found in the repository
     *                                    (i.e. application requires to have this configuration item)
     */
    T getValue() throws ConfigurationException;

    /**
     * Gets configuration item and specifies a default value in case configuration item is not found.
     *
     * @param defaultValue as default value for item
     * @return Configuration item loaded from repository or {@code defaultValue} in case item
     * is not found in repository
     */
    T getValue(T defaultValue);

    /**
     * Sets a new value for the configuration item.
     *
     * @param newValue for configuration item
     */
    void setValue(T newValue);
}
