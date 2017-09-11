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

package org.openhubframework.openhub.core.configuration;

import java.io.Serializable;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;


/**
 * Implementation of {@link ConfigurationItem} that allows to set/get fixed value of configuration item.
 * Useful for tests.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class FixedConfigurationItem<T extends Serializable> implements ConfigurationItem<T> {

    private final T value;

    /**
     * Constructor to create {@link ConfigurationItem wrapper} with fixed value.
     *
     * @param value Value of configuration item
     */
    public FixedConfigurationItem(T value) {
        Constraints.notNull(value, "value must not be null");

        this.value = value;
    }

    @Override
    public T getValue()  {
        return value;
    }

    @Override
    public T getValue(T defaultValue) {
        return getValue();
    }

    @Override
    public void setValue(T newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        // value cannot be null because of internal behaviour of getValue() method
        return getValue().toString();
    }
}
