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

package org.openhubframework.openhub.admin.web.configuration.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createJson;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createPagePair;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.json.JsonObject;
import javax.transaction.Transactional;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;
import org.openhubframework.openhub.api.configuration.DataTypeEnum;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.core.configuration.DbConfigurationParamDao;


/**
 * Test suite that verifies {@link DbConfigurationController} (REST API).
 *
 * @author Petr Juza
 * @since 2.0
 */
@Transactional
public class DbConfigurationControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = DbConfigurationController.REST_URI;

    private DbConfigurationParam param;

    @Autowired
    private DbConfigurationParamDao paramDao;

    @Autowired
    private DbConfigurationParamService paramService;

    @Before
    public void prepareData() {
        param = new DbConfigurationParam("ohf.async.concurrentConsumers", "desc", "async", "1",
                null, DataTypeEnum.INT, true, null);

        // note: we use DAO service because DbConfigurationParamService doesn't allow to insert new parameters
        paramDao.insert(param);
    }

    @Test
    public void testFindAll() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI)
                .addParameters(createPagePair(1, 100));

        // performs GET: /api/config-params?p=1&s=100
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("paging.first", is(true)))
                    .andExpect(jsonPath("paging.last", is(true)))
                    .andExpect(jsonPath("paging.totalPages", is(1)))
                    .andExpect(jsonPath("paging.number", is(1)))
                    .andExpect(jsonPath("data[0].code", is(param.getCode())))
                    .andExpect(jsonPath("data[0].currentValue", is(1)))
                    .andExpect(jsonPath("data[0].dataType", is(param.getDataType().toString())));
    }

    @Test
    public void testGet() throws Exception {
        paramDao.insert(new DbConfigurationParam("ohf.mail.from", "desc", "core.mail", "openhub@openwise.cz",
                null, DataTypeEnum.STRING, true, null));

        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/ohf.mail.from");

        // performs GET: /api/config-params/ohf.mail.from
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is("ohf.mail.from")))
                .andExpect(jsonPath("code", is("ohf.mail.from")))
                .andExpect(jsonPath("categoryCode", is("core.mail")))
                .andExpect(jsonPath("currentValue", is("openhub@openwise.cz")))
                .andExpect(jsonPath("defaultValue", is(nullValue())))
                .andExpect(jsonPath("dataType", is("STRING")))
                .andExpect(jsonPath("mandatory", is(true)))
                .andExpect(jsonPath("description", is("desc")))
                .andExpect(jsonPath("validationRegEx", is(nullValue())))
        ;
    }


    @Test
    public void testGet_boolean() throws Exception {
        paramDao.insert(new DbConfigurationParam("ohf.requestSaving.enable", "desc", "core.requestSaving", "true",
                "true", DataTypeEnum.BOOLEAN, true, null));

        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/ohf.requestSaving.enable");

        // performs GET: /api/config-params/ohf.requestSaving.enable
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is("ohf.requestSaving.enable")))
                .andExpect(jsonPath("code", is("ohf.requestSaving.enable")))
                .andExpect(jsonPath("categoryCode", is("core.requestSaving")))
                .andExpect(jsonPath("currentValue", is(true)))
                .andExpect(jsonPath("defaultValue", is(true)))
                .andExpect(jsonPath("dataType", is("BOOLEAN")))
                .andExpect(jsonPath("mandatory", is(true)))
                .andExpect(jsonPath("description", is("desc")))
                .andExpect(jsonPath("validationRegEx", is(nullValue())))
        ;
    }

    @Test
    public void testUpdate() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + param.getCode());

        JsonObject request = createJson()
                .add("code", param.getCode())
                .add("categoryCode", param.getCategoryCode())
                .add("dataType", param.getDataType().name())
                .add("currentValue", 222)
                .add("defaultValue", 10)
                .build();

        // performs PUT: /api/config-params/ohf.async.concurrentConsumers
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));

        // check updated property
        DbConfigurationParam dbParam = paramService.getParameter(param.getId());
        assertThat(Integer.valueOf(dbParam.getCurrentValue()), is(222));
        assertThat(Integer.valueOf(dbParam.getDefaultValue()), is(10));
        assertThat(param.getDataType(), is(DataTypeEnum.INT));
        assertThat(param.isMandatory(), is(true));
    }

    @Test
    public void testUpdate_boolean() throws Exception {
        paramDao.insert(new DbConfigurationParam("ohf.requestSaving.enable", "desc", "core.requestSaving", "true",
        "true", DataTypeEnum.BOOLEAN, true, null));

        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/ohf.requestSaving.enable");

        JsonObject request = createJson()
                .add("code", "ohf.requestSaving.enable")
                .add("categoryCode", "core.requestSaving")
                .add("dataType", "BOOLEAN")
                .add("currentValue", false)
                .add("defaultValue", true)
                .build();

        // performs PUT: /api/config-params/ohf.requestSaving.enable
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));

        // check updated property
        DbConfigurationParam dbParam = paramService.getParameter("ohf.requestSaving.enable");
        assertThat(dbParam.getCurrentValueAsObject(), is(false));
        assertThat(dbParam.getDefaultValueAsObject(), is(true));
    }


    @Test
    public void testUpdate_wrongValueType() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + param.getCode());

        JsonObject request = createJson()
                .add("code", param.getCode())
                .add("categoryCode", param.getCategoryCode())
                .add("dataType", param.getDataType().name())
                .add("currentValue", "abc")
                .build();

        // performs PUT: /api/config-params
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode", is("E122")))
                .andExpect(jsonPath("type", is("ConfigurationException")))
                .andExpect(jsonPath("message",
                        is("Current value 'abc' can't be converted to target type INT")))
                .andExpect(jsonPath("httpStatus", is(400)))
                .andExpect(jsonPath("httpDesc", is("Bad Request")))
                .andExpect(content().string(
                        containsString("Current value 'abc' can't be converted to target type INT")));
    }

    // test also JSR-303 Bean Validation API
    @Test
    public void testUpdate_beanValidation() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/" + param.getCode());

        JsonObject request = createJson()
                .add("code", param.getCode())
                .add("categoryCode", param.getCategoryCode())
                .add("currentValue", "abc")
                .build();

        // performs PUT: /api/config-params
        mockMvc.perform(put(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode", is("E109")))
                .andExpect(jsonPath("type", is("InputValidationException")))
                .andExpect(jsonPath("message",
                        is("Input validation error in object 'dbConfigurationParamRpc'")))
                .andExpect(jsonPath("httpStatus", is(400)))
                .andExpect(jsonPath("httpDesc", is("Bad Request")))

                // there is no dataType => one inputField
                .andExpect(jsonPath("inputFields[0].objectName", is("dbConfigurationParamRpc")))
                .andExpect(jsonPath("inputFields[0].field", is("dataType")))
                .andExpect(jsonPath("inputFields[0].code", is("NotNull")))
                .andExpect(jsonPath("inputFields[0].message", is("may not be null")));
    }
}
