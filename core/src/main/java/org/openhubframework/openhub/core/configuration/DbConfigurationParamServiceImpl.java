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
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.api.exception.validation.ConfigurationException;
import org.openhubframework.openhub.common.Tools;


/**
 * Implementation of {@link DbConfigurationParamService} interface.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Service
@Transactional
public class DbConfigurationParamServiceImpl implements DbConfigurationParamService {

    private static final Logger LOG = LoggerFactory.getLogger(DbConfigurationParamServiceImpl.class);

    @Autowired
    private DbConfigurationParamDao paramDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public void update(DbConfigurationParam parameter) {
        Constraints.notNull(parameter, "the parameter must not be null");

        //TODO PJUZA when AOP will be configured then move conversionService into class DbConfigurationParam
        parameter.checkConsistency(conversionService);

        paramDao.update(parameter);

        LOG.debug("Parameter (code = {}) changed: {}", parameter.getCode(), parameter);
    }

    @Transactional(readOnly = true)
    @Override
    public DbConfigurationParam getParameter(String code) {
        return paramDao.findParameter(code).orElseThrow(
                () -> new ConfigurationException(Tools.fm("there is no conf. parameter with code = {}", code)));
    }

    @Transactional(readOnly = true)
    @Nullable
    @Override
    public Optional<DbConfigurationParam> findParameter(String code) {
        return paramDao.findParameter(code);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DbConfigurationParam> findAllParameters() {
        return paramDao.findAllParameters();
    }
}
