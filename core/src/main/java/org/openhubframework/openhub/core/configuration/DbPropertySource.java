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

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.common.OpenHubPropertyConstants;


/**
 * {@link PropertySource} implementation that reads properties from an underlying database table "configuration"
 * via {@link DbConfigurationParamService}.
 * <p>
 * Use '{@value CoreProps#PROPERTY_INCLUDE_PATTERN}' property to define pattern for which property names should
 * be loaded from DB. If there is no defined then all property names which starts with
 * '{@value OpenHubPropertyConstants#PREFIX}' will be loaded from DB.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class DbPropertySource extends PropertySource<DbConfigurationParamService> {

    private static final Logger LOG = LoggerFactory.getLogger(DbPropertySource.class);

    private static final String DEFAULT_INCLUDE_PATTERN = "^ohf\\..*$";

    private Pattern includePattern;

	/**
	 * Create a new {@code DbPropertySource} with the given name and the given
	 * {@code DbConfigurationParamService}.
	 */
	DbPropertySource(String name, DbConfigurationParamService paramService, @Nullable String includePatternStr) {
		super(name, paramService);

        if (StringUtils.isNotEmpty(includePatternStr)) {
		    this.includePattern = Pattern.compile(includePatternStr);
        } else {
            this.includePattern = Pattern.compile(DEFAULT_INCLUDE_PATTERN);
        }
	}

	@Override
	public Object getProperty(String name) {
        Constraints.notNull(name, "name must not be null");

        Object value =  null;

        if (isDbProperty(name)) {
            Optional<DbConfigurationParam> param = this.source.findParameter(name);
            if (param.isPresent()) {
                value = param.get().getValue();
            }

            LOG.debug("Get DB property value for name '{}': {}", name, value);
        } else {
            LOG.trace("Getting non-DB property value 'null' for name '{}'", name);
        }

        return value;
	}

	private boolean isDbProperty(String name) {
        Matcher matcher = includePattern.matcher(name);
        return matcher.matches();
    }
}