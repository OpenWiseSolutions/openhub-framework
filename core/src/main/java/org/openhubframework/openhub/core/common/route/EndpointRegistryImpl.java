/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link EndpointRegistry}.
 *
 * @author Petr Juza
 */
public class EndpointRegistryImpl implements EndpointRegistry, CamelContextAware {

    private CamelContext camelContext;

    @Override
    public Collection<String> getEndpointURIs(String includePattern) {
        // gets endpoints
        Collection<Endpoint> endpoints = camelContext.getEndpoints();

        Collection<String> endpointURIs = new ArrayList<String>(endpoints.size());

        // compile pattern
        Pattern pattern = null;
        if (StringUtils.isNotEmpty(includePattern)) {
            pattern = Pattern.compile(includePattern);
        }

        // go through all endpoints and filter URIs
        for (Endpoint endpoint : endpoints) {
            String uri = endpoint.getEndpointUri();

            if (filter(uri, pattern)) {
                endpointURIs.add(uri);
            }
        }

        return endpointURIs;
    }

    /**
     * Returns {@code true} if specified URI matches specified pattern.
     *
     * @param endpointURI the endpoint URI
     * @param pattern pattern
     * @return {@code true} if specified URI matches at least one of specified patterns otherwise {@code false}
     */
    private boolean filter(String endpointURI, @Nullable Pattern pattern) {
        Assert.hasText(endpointURI, "the endpointURI must be defined");

        if (pattern == null) {
            return true;
        }

        Matcher matcher = pattern.matcher(endpointURI);
        if (matcher.matches()) {
            return true;
        }

        return false;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }
}
