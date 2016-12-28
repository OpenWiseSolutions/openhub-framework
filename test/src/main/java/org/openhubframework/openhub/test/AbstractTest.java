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

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestContextLoaderTestExecutionListener;
import org.custommonkey.xmlunit.*;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.joda.time.ReadableInstant;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.common.Profiles;


/**
 * Parent class for all tests with Apache Camel.
 *
 * @author Petr Juza
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TestConfig.class)
@ActiveProfiles(profiles = Profiles.TEST)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners(
        listeners = CamelSpringTestContextLoaderTestExecutionListener.class,
        mergeMode = MERGE_WITH_DEFAULTS
)
public abstract class AbstractTest {

    @Autowired
    private ModelCamelContext camelContext;

    @Autowired
    private ApplicationContext ctx;

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
    protected final ApplicationContext getApplicationContext() {
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
     * Same as {@link XMLAssert#assertXMLEqual(String, String)},
     * but with the specified node verified as XML using the same similarity technique,
     * not as text (which would have to match completely).
     *
     * @param control               the expected XML
     * @param test                  the actual XML being verified
     * @param innerXmlXpathLocation the XPath location of the element, that should be verified as XML, not as text
     * @throws org.xml.sax.SAXException can be thrown when creating {@link Diff} (e.g., if the provided XML is invalid)
     * @throws java.io.IOException      can be thrown when creating {@link Diff}
     */
    public static void assertXMLEqualWithInnerXML(String control, String test, final String innerXmlXpathLocation)
            throws SAXException, IOException {

        Diff myDiff = new Diff(control, test);
        myDiff.overrideDifferenceListener(new DifferenceListener() {
            @Override
            public int differenceFound(Difference difference) {
                if (innerXmlXpathLocation.equals(difference.getTestNodeDetail().getXpathLocation())) {
                    //use a custom verification for the encoded XML payload:
                    try {
                        Diff innerDiff = new Diff(
                                difference.getControlNodeDetail().getValue(),
                                difference.getTestNodeDetail().getValue());
                        if (innerDiff.identical()) {
                            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                        } else if (innerDiff.similar()) {
                            return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
                        } else {
                            fail("Inner encoded XML node at " + innerXmlXpathLocation + " doesn't match\n" + innerDiff);
                        }
                    } catch (Exception e) {
                        //shouldn't have a problem creating a Diff instance at this point,
                        // so just ignore and acknowledge the difference
                    }
                }
                // the default behavior is to acknowledge the difference:
                return RETURN_ACCEPT_DIFFERENCE;
            }

            @Override
            public void skippedComparison(Node control, Node test) {
            }
        });

        assertXMLEqual(myDiff, true);
    }

    /**
     * Shorthand for {@link CoreMatchers#is(org.hamcrest.Matcher) is}
     * ({@link #equalDateTime(org.joda.time.ReadableInstant) equalDateTime}(expected)).
     */
    public static <T extends ReadableInstant> Matcher<T> isDateTime(final T expected) {
        return is(equalDateTime(expected));
    }

    /**
     * Creates a matcher that compares JodaTime {@link ReadableInstant} objects based on their millis only,
     * not Chronology as the default equals() does.
     * This comparison uses {@link ReadableInstant#isEqual(org.joda.time.ReadableInstant)}
     * instead of default {@link ReadableInstant#equals(Object)}.
     *
     * @param expected the expected ReadableInstant
     * @param <T>      any class implementing ReadableInstant
     * @return matcher for the usual Hamcrest matcher chaining
     * @see CoreMatchers#equalTo(Object)
     * @see ReadableInstant#equals(Object)
     * @see ReadableInstant#isEqual(org.joda.time.ReadableInstant)
     */
    public static <T extends ReadableInstant> Matcher<T> equalDateTime(final T expected) {
        return new IsEqual<T>(expected) {
            @Override
            public boolean matches(Object actualValue) {
                if (expected == null) {
                    return actualValue == null;
                } else {
                    return actualValue instanceof ReadableInstant
                            && expected.isEqual((ReadableInstant) actualValue);
                }
            }
        };
    }

    /**
     * Returns processor that throws specified exception.
     *
     * @param exc the exception
     * @return processor that throws specified exception
     */
    protected static Processor throwException(final Exception exc) {
        Assert.notNull(exc, "exc must not be null");

        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw exc;
            }
        };
    }

    /**
     * Mocks a hand-over-type endpoint (direct, direct-vm, seda or vm)
     * by simply providing the other (consumer=From) side connected to a mock.
     * <p/>
     * There should be no consumer existing, i.e., the consumer route should not be started.
     *
     * @param uri the URI a new mock should consume from
     * @return the mock that is newly consuming from the URI
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