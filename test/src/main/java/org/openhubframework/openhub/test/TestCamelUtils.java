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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.ToDefinition;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLAssert;
import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.asynch.model.CallbackResponse;
import org.openhubframework.openhub.api.asynch.model.ConfirmationTypes;


/**
 * Handy methods for testing Apache Camel.
 *
 * @author Petr Juza
 */
public final class TestCamelUtils {

    private TestCamelUtils() {
    }

    /**
     * Replaces calling TO {@link AsynchConstants#URI_ASYNCH_IN_MSG} with processor that creates OK response.
     * <p>
     * Useful when you want to test input (IN) route of asynchronous process.
     *
     * @param builder the advice builder
     */
    public static void replaceToAsynch(AdviceWithRouteBuilder builder) {
        // remove AsynchInMessageRoute.URI_ASYNCH_IN_MSG
        builder.weaveByType(ToDefinition.class).replace().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                // creates OK response
                CallbackResponse callbackResponse = new CallbackResponse();
                callbackResponse.setStatus(ConfirmationTypes.OK);

                exchange.getIn().setBody(callbackResponse);
            }
        });
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
     * Returns processor that throws specified exception.
     *
     * @param exc the exception
     * @return processor that throws specified exception
     */
    public static Processor throwException(final Exception exc) {
        Assert.notNull(exc, "exc must not be null");

        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw exc;
            }
        };
    }
}
