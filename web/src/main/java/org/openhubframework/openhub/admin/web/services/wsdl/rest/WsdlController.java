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

package org.openhubframework.openhub.admin.web.services.wsdl.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.common.rpc.CollectionWrapper;
import org.openhubframework.openhub.admin.web.services.wsdl.rpc.WsdlInfoRpc;
import org.openhubframework.openhub.api.route.RouteConstants;
import org.openhubframework.openhub.core.common.ws.WsdlRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller serving list of exposed webservices contracts - wsdl.
 *
 * @author Karel Kovarik
 */
@RestController(WsdlController.BEAN_NAME)
@RequestMapping(value = WsdlController.REST_URI)
public class WsdlController extends AbstractOhfController {

    /**
     * Base REST uri for the controller.
     */
    public static final String REST_URI = BASE_PATH + "/services/wsdl";

    /**
     * Suffix for all wsdl files.
     */
    private static final String WSDL_SUFFIX = ".wsdl";

    /**
     * Custom bean name.
     */
    public static final String BEAN_NAME = "WsdlRestController";

    @Autowired
    private WsdlRegistry wsdlRegistry;

    /**
     * Get list of exposed wsdl contracts.
     * @param request the input httpServletRequest.
     * @return list of {@link WsdlInfoRpc} objects wrapped in the {@link CollectionWrapper}.
     */
    @GetMapping(produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CollectionWrapper<WsdlInfoRpc> list(final HttpServletRequest request) {
        return new CollectionWrapper<>(
                wsdlRegistry.getWsdls().stream()
                        // sort by beanName, mainly to have stable order
                        .sorted(Comparator.naturalOrder())
                        // map to Rpc object
                        .map(beanName -> new WsdlInfoRpc(beanName, wsdlUrl(request, beanName)))
                        .collect(Collectors.toList()));
    }

    // create url of wsdl for webservice
    private static String wsdlUrl(final HttpServletRequest request, final String beanName) {
        return getBaseUrl(request) + RouteConstants.WS_URI_PREFIX + beanName + WSDL_SUFFIX;
    }

    // baseUrl of the server - like http://localhost:8080
    private static String getBaseUrl(final HttpServletRequest request) {
        URL requestURL;
        try {
            requestURL = new URL(request.getRequestURL().toString());
        } catch (final MalformedURLException e) {
            // should not happen, request is expected to have valid requestURL
            throw new RuntimeException("Not valid request.requestURL for parsing:", e);
        }
        final String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
        return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
    }
}
