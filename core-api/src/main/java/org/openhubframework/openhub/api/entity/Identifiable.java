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

package org.openhubframework.openhub.api.entity;

import java.io.Serializable;
import javax.annotation.Nullable;


/**
 * Interface serves for unified assess to entities with ID identifier.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
public interface Identifiable<T extends Serializable> {

    /**
     * Gets unique identifier of the entity.
     *
     * @return unique identifier
     */
    @Nullable
    T getId();

    /**
     * Sets unique identifier of the entity.
     *
     * @param id unique identifier
     */
    void setId(@Nullable T id);

}
