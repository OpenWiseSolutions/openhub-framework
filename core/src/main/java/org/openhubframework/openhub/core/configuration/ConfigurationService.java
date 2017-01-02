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

import javax.annotation.Nullable;


/**
 * Contract configuration service to provide uniform type-safe access to configuration properties. 
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public interface ConfigurationService {

    /**
     * Gets the type-safe value of configuration item.
     *
     * @param clazz as type
     * @param key   as item ID
     * @param <T>   as generic type of expected value type
     * @return type-safe value
     */
    @Nullable
    <T> T getValue(Class<T> clazz, String key);

    /**
     * Sets the value of the configuration item.
     *
     * @param clazz    as type
     * @param key      as item ID
     * @param <T>      as generic type of updated value type
     * @param newValue to be saved
     */
    <T> void setValue(Class<T> clazz, String key, Object newValue);
}
