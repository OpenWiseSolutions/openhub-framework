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

package org.openhubframework.openhub.core.reqres;

import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.api.entity.Response;


/**
 * Service for manipulation with {@link Request requests} and {@link Response responses}.
 *
 * @author Petr Juza
 * @since 0.4
 */
public interface RequestResponseService {

    /**
     * Inserts new request.
     *
     * @param request the request
     */
    void insertRequest(Request request);


    /**
     * Inserts new response.
     *
     * @param response the response
     */
    void insertResponse(Response response);


    /**
     * Gets last request specified by target URI and response-join ID.
     * <p/>
     * Note: there can be more requests for one message and external system because of reprocessing the message.
     * Therefore last request is used.
     *
     * @param uri the target URI
     * @param responseJoinId the identifier for pairing/joining request and response together
     * @return request
     */
    @Nullable
    Request findLastRequest(String uri, String responseJoinId);


    /**
     * Finds requests which matches the criteria filter.
     *
     * @param from       the timestamp from
     * @param to         the timestamp to
     * @param subUri     the substring of URI
     * @param subRequest the substring of request content
     * @return list of {@link Request}
     */
    List<Request> findByCriteria(Date from, Date to, @Nullable String subUri, @Nullable String subRequest);
}
