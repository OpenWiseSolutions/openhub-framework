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

package org.openhubframework.openhub.core.configuration;

import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.exception.validation.ConfigurationException;


/**
 * Default implementation of {@link ConfigurationItem}.
 *
 * @param <T> represents type of a configuration item (allowed types are determined by configuration service)
 * @author Tomas Hanus
 * @since 2.0
 */
public class ConfigurationItemImpl<T> implements ConfigurationItem<T> {

    private final ConfigurationService configurationService;
    private final Class<T> clazz;
    private final String key;

    /**
     * Default all-args constructor to create {@link ConfigurationItem wrapper}.
     *
     * @param clazz                this represents the wrapped type class; it has to be specified here as due to type
     *                             erasure mechanisms in java generics, it
     *                             cannot be inferred in runtime from declared type parameter
     * @param key                  represents identification of configured item in configuration storage/service
     * @param configurationService to operate with {@link ConfigurationItem}
     */
    public ConfigurationItemImpl(Class<T> clazz, String key, ConfigurationService configurationService) {
        this.clazz = clazz;
        this.key = key;
        this.configurationService = configurationService;
    }

    @Override
    public T getValue() throws ConfigurationException {
        T value = configurationService.getValue(clazz, key);
        if (value == null) {
            throw new ConfigurationException(String.format("Required configuration item with key [%s] not found.", key), key);
        }

        return value;
    }

    @Override
    public T getValue(T defaultValue) {
        T value = configurationService.getValue(clazz, key);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    @Override
    public void setValue(T newValue) {
        configurationService.setValue(clazz, this.key, newValue);
    }

    @Override
    public String toString() {
        // value cannot be null because of internal behaviour of getValue() method
        return getValue().toString();
    }
}
