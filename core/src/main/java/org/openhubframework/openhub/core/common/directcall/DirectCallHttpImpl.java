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

package org.openhubframework.openhub.core.common.directcall;

import static org.openhubframework.openhub.api.configuration.CoreProps.SERVER_LOCALHOST_URI;
import static org.openhubframework.openhub.api.route.RouteConstants.HTTP_URI_PREFIX;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;


/**
 * Implementation of {@link DirectCall} interface with HTTP client that calls {@link DirectCallWsRoute}.
 *
 * @author Petr Juza
 * @see DirectCallWsRoute
 */
@Service
public class DirectCallHttpImpl implements DirectCall {

    /**
     * URI of this localhost application, including port number.
     */
    @ConfigurableValue(key = SERVER_LOCALHOST_URI)
    private ConfigurationItem<String> localhostUri;

    @Override
    public String makeCall(String callId) throws IOException {
        Assert.hasText(callId, "callId must not be empty");
        Assert.hasText(localhostUri.getValue(), "localhostUri must not be empty");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(localhostUri.getValue() + HTTP_URI_PREFIX
                    + DirectCallWsRoute.SERVLET_URL + "?" + DirectCallWsRoute.CALL_ID_HEADER + "=" + callId);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                        // successful response
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new IOException(EntityUtils.toString(response.getEntity()));
                    }
                }
            };

            return httpClient.execute(httpGet, responseHandler);
        }
    }
}
