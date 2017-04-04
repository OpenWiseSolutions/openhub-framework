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

package org.openhubframework.openhub.admin.web.auth.rest;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.web.auth.rpc.AuthInfoRpc;
import org.openhubframework.openhub.admin.web.common.AbstractOhfController;


/**
 * REST controller to provide authenticated user and related operations.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@RestController
@RequestMapping(value = AuthController.REST_URI)
public class AuthController extends AbstractOhfController {

    public static final String REST_URI = BASE_PATH + "/auth";

    /**
     * Serves information about authenticated user.
     *
     * @param principal object
     * @return {@link AuthInfoRpc} response
     */
    @RequestMapping(method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AuthInfoRpc getInfo(Principal principal) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return new AuthInfoRpc(authentication);
    }

}
