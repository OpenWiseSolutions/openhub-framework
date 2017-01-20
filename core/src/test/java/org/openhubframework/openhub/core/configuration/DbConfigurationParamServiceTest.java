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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.configuration.DataTypeEnum;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.api.exception.ConfigurationException;
import org.openhubframework.openhub.core.AbstractCoreDbTest;


/**
 * Test suite for {@link DbConfigurationParamService}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Transactional
public class DbConfigurationParamServiceTest extends AbstractCoreDbTest {

    private DbConfigurationParam param;

    @Autowired
    private DbConfigurationParamService paramService;

    @Autowired
    private DbConfigurationParamDao paramDao;

    @Autowired
    private ConversionService conversionService;

    @Before
    public void prepareConfParam() {
        param = new DbConfigurationParam("ohf.async.concurrentConsumers", "async", "1",
                null, DataTypeEnum.INT, true, null);

        paramDao.insert(param);
    }

    @Test
    public void testGetParam() {
        DbConfigurationParam dbParam = paramService.getParameter(param.getCode());
        assertThat(dbParam, is(param));
        assertThat(dbParam.getDataType(), is(DataTypeEnum.INT));
    }

    @Test
    public void testUpdateParam_currentValue() {
        DbConfigurationParam dbParam = paramService.getParameter(param.getCode());
        dbParam.setCurrentValue("23");
        assertThat(dbParam.getValue(), is("23"));
        paramService.update(dbParam);

        assertThat(dbParam.getCurrentValue(), is(paramService.getParameter(param.getCode()).getCurrentValue()));
    }

    @Test(expected = ConfigurationException.class)
    public void testGetValue_wrongType() {
        param.setCurrentValue("true");
        param.checkConsistency(conversionService);
    }

    @Test(expected = ConfigurationException.class)
    public void testConsistency_wrongType() {
        param.setCurrentValue("true");
        param.checkConsistency(conversionService);
    }

    @Test(expected = ConfigurationException.class)
    public void testConsistency_wrongValidationRegEx() {
        param.setValidationRegEx("!?6");
        param.checkConsistency(conversionService);
    }

    @Test
    public void testConsistency_validationRegEx() {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
       		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        param.setCurrentValue("pjuza@openwise.com");
        param.setValidationRegEx(EMAIL_PATTERN);
        param.setDataType(DataTypeEnum.STRING);

        param.checkConsistency(conversionService);
    }

    @Test
    public void testUpdateParam_dataType() {
        DbConfigurationParam dbParam = paramService.getParameter(param.getCode());
        dbParam.setCurrentValue("true");
        dbParam.setDataType(DataTypeEnum.BOOLEAN);
        dbParam.setDefaultValue("false");
        assertThat(dbParam.getValue(), is("true"));
        paramService.update(dbParam);

        DbConfigurationParam dbParam2 = paramService.getParameter(param.getCode());
        assertThat(dbParam.getCurrentValue(), is(dbParam2.getCurrentValue()));
        assertThat(dbParam.getDefaultValue(), is(dbParam2.getDefaultValue()));
        assertThat(dbParam.getDataType(), is(dbParam2.getDataType()));
    }

    @Test
    public void testConsistency_notMandatory() {
        param.setMandatory(false);
        param.setDefaultValue(null);
        param.setCurrentValue(null);
        param.checkConsistency(conversionService);
    }

    @Test
    public void testDataType_nullFile() {
        param.setCurrentValue(null);
        param.setDefaultValue(null);
        param.setMandatory(false);
        param.setDataType(DataTypeEnum.FILE);
        param.checkConsistency(conversionService);
        assertThat(param.getValue(), nullValue());
    }

    @Test
    public void testDataType_file() {
        param.setCurrentValue("");
        param.setDefaultValue(null);
        param.setMandatory(false);
        param.setDataType(DataTypeEnum.FILE);
        param.checkConsistency(conversionService);
    }
}
