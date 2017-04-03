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

import java.util.List;
import java.util.Optional;

import org.openhubframework.openhub.api.configuration.DbConfigurationParam;


/**
 * DAO for {@link DbConfigurationParam} entity.
 *
 * @author Petr Juza
 * @since 2.0
 */
public interface DbConfigurationParamDao {

    /**
     * Inserts new parameter.
     *
     * @param parameter The configuration parameter
     */
    void insert(DbConfigurationParam parameter);

    /**
     * Updates existing parameter.
     *
     * @param parameter The configuration parameter
     */
    void update(DbConfigurationParam parameter);

    /**
     * Finds an existing external call for the specified operation and entityId.
     *
     * @param code The parameter code
     * @return the parameter or null if there is no parameter with specified code
     */
    Optional<DbConfigurationParam> findParameter(String code);

    /**
     * Finds all parameters.
     *
     * @return list of parameters sorted by categoryCode and code
     */
    List<DbConfigurationParam> findAllParameters();

}
