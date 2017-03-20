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

package org.openhubframework.openhub.test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.Assert.assertThat;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

import java.lang.reflect.Field;
import java.util.Locale;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestContextLoaderTestExecutionListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.common.Profiles;


/**
 * Parent class for all tests with Apache Camel.
 *
 * @author Petr Juza
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles(profiles = Profiles.TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners(
        listeners = CamelSpringTestContextLoaderTestExecutionListener.class,
        mergeMode = MERGE_WITH_DEFAULTS
)
public abstract class AbstractTest {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Autowired
    private ModelCamelContext camelContext;

    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setDefaultLocale() {
        // see https://docs.oracle.com/cd/E19455-01/806-0169/overview-9/index.html
        // Large Number as 4,294,967,295.00
        Locale.setDefault(DEFAULT_LOCALE);
    }

    @Before
    public void configureXmlUnit() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @After
    public void validateMockito() {
        Mockito.validateMockitoUsage();
    }

    /**
     * Gets Camel context.
     *
     * @return Camel context
     */
    protected final ModelCamelContext getCamelContext() {
        return camelContext;
    }

    /**
     * Gets Spring context.
     *
     * @return Spring context
     */
    protected final ApplicationContext getApplContext() {
        return ctx;
    }

    /**
     * Sets value of private field.
     *
     * @param target the target object
     * @param name   the field name
     * @param value  the value for setting to the field
     */
    public static void setPrivateField(Object target, String name, Object value) {
        Field countField = ReflectionUtils.findField(target.getClass(), name);
        ReflectionUtils.makeAccessible(countField);
        ReflectionUtils.setField(countField, target, value);
    }

    /**
     * Mocks a hand-over-type endpoint (direct, direct-vm, seda or vm)
     * by simply providing the other (consumer=From) side connected to a mock.
     * <p>
     * There should be no consumer existing, i.e., the consumer route should not be started.
     *
     * @param uri the URI a new mock should consume from
     * @return the mock that is newly consuming from the URI
     * @throws Exception if error occurs during mocking required route
     */
    protected MockEndpoint mockDirect(final String uri) throws Exception {
        return mockDirect(uri, null);
    }

    /**
     * Same as {@link #mockDirect(String)}, except with route ID to be able to override an existing route with the mock.
     *
     * @param uri     the URI a new mock should consume from
     * @param routeId the route ID for the new mock route
     *                (existing route with this ID will be overridden by this new route)
     * @return the mock that is newly consuming from the URI
     * @throws Exception if error occurs during mocking required route
     */
    protected MockEndpoint mockDirect(final String uri, final String routeId) throws Exception {
        // precaution: check that URI can be mocked by just providing the other side:
        org.junit.Assert.assertThat(uri, anyOf(
                CoreMatchers.startsWith("direct:"),
                CoreMatchers.startsWith("direct-vm:"),
                CoreMatchers.startsWith("seda:"),
                CoreMatchers.startsWith("vm:")));

        // create the mock:
        final MockEndpoint createCtidMock = getCamelContext().getEndpoint("mock:" + uri, MockEndpoint.class);
        // redirect output to this mock:
        getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                RouteDefinition routeDef = from(uri);

                if (routeId != null) {
                    routeDef.routeId(routeId);
                }

                routeDef.to(createCtidMock);
            }
        });

        return createCtidMock;
    }

    /**
     * Asserts {@link ErrorExtEnum error codes}.
     *
     * @param error    the actual error code
     * @param expError the expected error code
     */
    protected void assertErrorCode(ErrorExtEnum error, ErrorExtEnum expError) {
        assertThat(error.getErrorCode(), CoreMatchers.is(expError.getErrorCode()));
    }
}