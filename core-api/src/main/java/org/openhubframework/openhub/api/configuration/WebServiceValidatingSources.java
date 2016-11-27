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

package org.openhubframework.openhub.api.configuration;

import org.springframework.core.io.Resource;


/**
 * Contract for definition of XSD schemas for header and payload validation.
 *
 * @author Petr Juza
 * @since 2.0
 */
public interface WebServiceValidatingSources {

    /**
     * Sets the schema resources to use for validation.
     *
     * @return array of schema resources
     */
    Resource[] getXsdSchemas();

    /**
     * Sets request root element names which will be ignored from validation checking.
     * <p>
     * Example: {@code {http://openhubframework.org/ws/HelloService-v1}syncHelloRequest}
     *
     * @return array of root element names
     */
    String[] getIgnoreRequests();

}
