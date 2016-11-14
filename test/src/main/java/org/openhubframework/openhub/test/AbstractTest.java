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
import static org.springframework.core.annotation.AnnotationUtils.findAnnotationDeclaringClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.route.CamelConfiguration;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.joda.time.ReadableInstant;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Parent class for all tests with Apache Camel.
 *
 * @author Petr Juza
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test_camel.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractTest {

    private static boolean initAllRoutes = false;

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
     * Sets {@code true} if all routes should be initialized (=added into Camel context).
     * By default the value is {@code false} - only routes defined in {@link ActiveRoutes} annotation are initialized.
     * <p/>
     * Use {@link BeforeClass} annotation for setting this parameter.
     *
     * @param initAllRoutes {@code true} for initialization of all routes, otherwise {@code false}
     */
    public static void setInitAllRoutes(boolean initAllRoutes) {
        AbstractTest.initAllRoutes = initAllRoutes;
    }

    /**
     * Initializes selected routes for specific test.
     * Active route definitions are defined via {@link ActiveRoutes} annotation.
     *
     * @throws Exception when init fails
     */
    @Before
    public void initRoutes() throws Exception {
        getCamelContext().getShutdownStrategy().setTimeout(1);// no shutdown timeout:
        getCamelContext().getShutdownStrategy().setTimeUnit(TimeUnit.NANOSECONDS);
        getCamelContext().getShutdownStrategy().setShutdownNowOnTimeout(true);// no pending exchanges

        Map<String, Class<RoutesBuilder>> beans = new HashMap<>();

        String[] beanNames = getApplicationContext().getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class beanClass = getApplicationContext().getType(beanName);

            if (beanClass.isAnnotationPresent(CamelConfiguration.class)) {
                beans.put(beanName, beanClass);
            }
        }

        Set<Class> activeRoutesClasses = getActiveRoutes();

        for (Map.Entry<String, Class<RoutesBuilder>> entry : beans.entrySet()) {

            for (Class routesClass : activeRoutesClasses) {

                if (entry.getValue().isAssignableFrom(routesClass)) {
                    getCamelContext().addRoutes((RoutesBuilder)getApplicationContext().getBean(entry.getKey()));
                }
            }
        }
    }

    /**
     * Gets set of active route definitions which should be added into Camel context.
     *
     * @return set of classes
     */
    private Set<Class> getActiveRoutes() {
        Set<Class> routeClasses = new HashSet<>();

        Class<ActiveRoutes> annotationType = ActiveRoutes.class;

        Class<?> testClass = this.getClass();

        Class<?> declaringClass = AnnotationUtils.findAnnotationDeclaringClass(annotationType, testClass);
        if (declaringClass == null) {
            return routeClasses;
        }

        while (declaringClass != null) {
            ActiveRoutes activeRoutes = declaringClass.getAnnotation(annotationType);

            routeClasses.addAll(Arrays.asList(activeRoutes.classes()));

            declaringClass = findAnnotationDeclaringClass(annotationType, declaringClass.getSuperclass());
        }

        return routeClasses;
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
        return new Processor(){
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
        org.junit.Assert.assertThat(uri,
                anyOf(
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
     * @param error the actual error code
     * @param expError the expected error code
     */
    protected void assertErrorCode(ErrorExtEnum error, ErrorExtEnum expError) {
        assertThat(error.getErrorCode(), CoreMatchers.is(expError.getErrorCode()));
    }
}