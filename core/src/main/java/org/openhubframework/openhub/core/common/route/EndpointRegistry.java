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

import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.Nullable;


/**
 * Contract for getting endpoints overview.
 *
 * @author Petr Juza
 */
public interface EndpointRegistry {

    /**
     * Gets endpoint URIs which match specified pattern.
     * <p>
     * Example: method with the following pattern {@code ^(spring-ws|servlet).*$} will return URIs which starts
     * by spring-ws or servlet.
     *
     * @param includePattern {@link Pattern pattern} for filtering endpoints URI - only whose URIs will
     *                       match specified pattern will be returned
     * @return collection of endpoint URIs
     */
    Collection<String> getEndpointURIs(@Nullable String includePattern);

}
