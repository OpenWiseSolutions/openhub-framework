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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;


/**
 * {@link PropertySource} implementation that reads properties from an underlying database table "configuration"
 * via {@link DbConfigurationParamService}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class DbPropertySource extends PropertySource<DbConfigurationParamService> {

    private static final Logger LOG = LoggerFactory.getLogger(DbPropertySource.class);

	/**
	 * Create a new {@code DbPropertySource} with the given name and the given
	 * {@code DbConfigurationParamService}.
	 */
	DbPropertySource(String name, DbConfigurationParamService paramService) {
		super(name, paramService);
	}

	@Override
	public Object getProperty(String name) {
        Constraints.notNull(name, "name must not be null");

        DbConfigurationParam param = this.source.findParameter(name);
        Object value = param != null ? param.getValue() : null;

        LOG.debug("Get property value for name '{}': {}", name, value);

        return value;
	}
}