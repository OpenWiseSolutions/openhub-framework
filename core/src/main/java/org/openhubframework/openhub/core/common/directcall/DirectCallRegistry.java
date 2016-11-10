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

/**
 * Contract of registry calls between sibling Spring application contexts, specifically from web application context
 * to Spring WS (Camel) application context.
 * <p/>
 * This registry serves for saving call parameters. Registry is initialized in root application context
 * and therefore it's accessible from both child (sibling) application contexts.
 * <p/>
 * There is the following procedure:
 * <ol>
 *     <li>client (web controller) generates unique call identifier
 *     <li>client {@link #addParams(String, DirectCallParams) inserts to registry call parameters under unique identifier}
 *     <li>client calls Camel route in Spring WS (Camel) application context - unique identifier is transferred
 *     <li>server accepts incoming call, gets unique call identifier that use
 *          for {@link #getParams(String) getting call parameters}
 *     <li>server calls external system
 *     <li>server returns response from external system
 *     <li>client {@link #removeParams(String) removes parameters for the call}
 * </ol>
 *
 * Initializes implementation of this interface in root application context because it's necessary
 * to have this instance available from both child contexts.
 *
 * @author Petr Juza
 */
public interface DirectCallRegistry {

    /**
     * Adds new parameters under specified call ID.
     *
     * @param callId the call identifier
     * @param params call parameters
     */
    void addParams(String callId, DirectCallParams params);

    /**
     * Gets call parameters.
     *
     * @param callId the call identifier
     * @return call parameters
     * @throws IllegalArgumentException when there are no call parameters for specified call ID
     */
    DirectCallParams getParams(String callId);

    /**
     * Removes call parameters for specified call ID.
     *
     * @param callId the call identifier
     */
    void removeParams(String callId);

}
