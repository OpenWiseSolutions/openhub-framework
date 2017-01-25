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

package org.openhubframework.openhub.core.common.route;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.openhubframework.openhub.core.AbstractCoreTest;
import org.openhubframework.openhub.test.route.ActiveRoutes;


/**
 * Test suite for {@link CamelEndpointRegistryImpl}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@ActiveRoutes(classes = PingRoute.class)
public class CamelEndpointRegistryTest extends AbstractCoreTest {

    @Autowired
    private EndpointRegistry endpointRegistry;

    @Test
    public void testAllEndpoints() throws Exception {
        Collection<String> endpointURIs = endpointRegistry.getEndpointURIs(null);
        // ExceptionTranslationRoute + PingRoute
        assertThat(endpointURIs.size(), is(2));
    }

    @Test
    public void testFilterEndpoints() throws Exception {
        Collection<String> endpointURIs = endpointRegistry.getEndpointURIs("^(servlet).*$");
        assertThat(endpointURIs.size(), is(1));
        assertThat(endpointURIs.iterator().next(), containsString("servlet:/ping"));
    }
}
