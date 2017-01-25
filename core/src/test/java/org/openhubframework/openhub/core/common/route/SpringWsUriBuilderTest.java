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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test suite for {@link SpringWsUriBuilder}.
 *
 * @author Petr Juza
 * @since 2.0
 */
@RunWith(JUnit4.class)
public class SpringWsUriBuilderTest {

    private SpringWsUriBuilder uriBuilder = new SpringWsUriBuilder();

    @Test
    public void testGetOutWsUri() {
        assertThat(uriBuilder.getOutWsUri("uri", "senderRef", "action"),
                is("spring-ws:uri?messageSender=#senderRef&messageFactory=#messageFactorySOAP11&soapAction=action"));
    }

    @Test
    public void testGetOutWsSoap12Uri() {
        assertThat(uriBuilder.getOutWsSoap12Uri("uri", "senderRef", null),
                is("spring-ws:uri?messageSender=#senderRef&messageFactory=#messageFactorySOAP12"));
    }

    @Test
    public void testGetInWsUri() {
        assertThat(uriBuilder.getInWsUri(QName.valueOf("rootElm"), "mappingRef", null),
                is("spring-ws:rootqname:rootElm?endpointMapping=#mappingRef"));
    }
}
