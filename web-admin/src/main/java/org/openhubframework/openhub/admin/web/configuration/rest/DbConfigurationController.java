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

import static org.springframework.util.StringUtils.hasText;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.common.rpc.paging.PagingWrapper;
import org.openhubframework.openhub.admin.web.configuration.rpc.DbConfigurationParamRpc;
import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.configuration.DbConfigurationParamService;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.api.exception.validation.ConfigurationException;
import org.openhubframework.openhub.api.exception.validation.ValidationException;


/**
 * REST controller with operations of DB configuration parameters
 *
 * @author Petr Juza
 * @since 2.0
 */
@RestController
@RequestMapping(value = DbConfigurationController.REST_URI)
public class DbConfigurationController extends AbstractOhfController {

    public static final String REST_URI = BASE_PATH + "/config-params";

    @Autowired
    private DbConfigurationParamService paramService;

    /**
     * Gets list of all configuration parameters.
     *
     * @return list of conf. parameters
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PagingWrapper<DbConfigurationParamRpc> findAll(Pageable pageable) {
        Assert.notNull(pageable, "pageable cannot be null");

        List<DbConfigurationParam> params = paramService.findAllParameters();

        Page<DbConfigurationParam> paramsPage = new PageImpl<>(pageContentList(params, pageable), pageable, params.size());
        Page<DbConfigurationParamRpc> paramsRpcPage = paramsPage.map(
                new DbConfigurationParamRpc.DbConfigurationParamConverter());
        return PagingWrapper.withMetadata(paramsRpcPage);
    }

    /**
     * Gets configuration parameter by code.
     *
     * @param code The configuration parameter code
     * @return configuration parameter
     * @throws NoDataFoundException if entity not found
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DbConfigurationParamRpc getByCode(@PathVariable final String code) throws NoDataFoundException {
        return new DbConfigurationParamRpc(getParam(code));
    }

    /**
     * Updates existing configuration parameter.
     *
     * @param paramRpc The paramRpc
     * @throws ValidationException if input object has wrong values
     * @throws NoDataFoundException entity not found for update
     */
    // .+ is workaround how to match everything after / as code of config parameters, otherwise last .* is removed as
    // file extension
    @RequestMapping(value = "/{code:.+}", method = RequestMethod.PUT, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void update(@PathVariable final String code, @RequestBody @Valid final DbConfigurationParamRpc paramRpc,
            BindingResult errors) throws ValidationException, NoDataFoundException {

        Assert.notNull(paramRpc, "paramRpc can not be null");
        if (hasText(paramRpc.getCode())) {
            Constraints.state(code.equals(paramRpc.getCode()), "codes must be equal");
        } else {
            paramRpc.setCode(code);
        }

        // get entity to update it
        DbConfigurationParam dbParam = getParam(code);
        paramRpc.updateEntity(dbParam, errors);

        // save entity
        paramService.update(dbParam);
    }

    private DbConfigurationParam getParam(String paramCode) {
        Assert.notNull(paramCode, "paramCode can not be null");

        try {
            return paramService.getParameter(paramCode);
        } catch (ConfigurationException ex) {
            throw new NoDataFoundException("Configuration parameter not found by code '" + paramCode + "'.", ex);
        }
    }
}
