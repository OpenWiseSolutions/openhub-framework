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

package org.openhubframework.openhub.core.common.ws;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;


/**
 * Implementation of {@link WsdlRegistry} based on Spring capabilities that supposes the following prerequisites:
 * <ul>
 *     <li>all WSDLs are defined/published via {@link SimpleWsdl11Definition}
 *     <li>Spring bean IDs will contain the WSDL name
 * </ul>
 *
 * @author Petr Juza
 */
public class WsdlRegistrySpringImpl implements WsdlRegistry {

    @Autowired
    private Map<String, SimpleWsdl11Definition> wsdls;

    @Override
    public Collection<String> getWsdls() {
        return wsdls.keySet();
    }
}
