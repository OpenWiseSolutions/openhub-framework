/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.errorscatalog.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.services.ErrorCatalogService;
import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.errorscatalog.rpc.ErrorCodeRpc;
import org.openhubframework.openhub.admin.web.errorscatalog.rpc.ErrorsCatalogRpc;
import org.openhubframework.openhub.api.entity.ErrorsCatalog;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;


/**
 * REST controller to operate over errors catalog.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@RestController
@RequestMapping(value = ErrorsCatalogController.REST_URI)
public class ErrorsCatalogController extends AbstractOhfController {

    public static final String REST_URI = BASE_PATH + "/errors-catalog";

    @Autowired
    private ErrorCatalogService errorCatalogService;

    /**
     * Serves errors catalog.
     *
     * @return list of {@link ErrorsCatalogRpc}
     */
    @RequestMapping(method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ErrorsCatalogRpc> errorsCatalog() {
        final List<ErrorsCatalog> errorsCatalogList = errorCatalogService.getErrorCatalog();

        final List<ErrorsCatalogRpc> result = new ArrayList<>();
        for (ErrorsCatalog errorsCatalog : errorsCatalogList) {
            final ErrorExtEnum[] codes = errorsCatalog.getCodes();
            final List<ErrorCodeRpc> resultCodes = new ArrayList<>();
            for (ErrorExtEnum code : codes) {
                resultCodes.add(new ErrorCodeRpc(code.getErrorCode(), code.getErrDesc(), null));
            }
            result.add(new ErrorsCatalogRpc(errorsCatalog.getName(), resultCodes));
        }
        
        return result;
    }
}
