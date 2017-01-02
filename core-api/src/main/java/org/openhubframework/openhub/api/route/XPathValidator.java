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

package org.openhubframework.openhub.api.route;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.xml.InvalidXPathExpression;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.w3c.dom.NodeList;

import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.ValidationIntegrationException;


/**
 * XPath validator that checks existence of mandatory element values (only those which have text values, not other sub-elements).
 * If there is no specified element (also with any value) then {@link ValidationIntegrationException} is thrown.
 * <p/>
 * Input XML:
 * <pre>
     &lt;cus:setCustomerRequest>
        &lt;cus:customer>
           &lt;cus1:externalCustomerId>700034&lt;/cus1:externalCustomerId>
           &lt;cus1:customerNo>2&lt;/cus1:customerNo>
        &lt;/cus:customer>
     &lt;/cus:setCustomerRequest>      
 * </pre>
 * ParentElm: /cus:setCustomerRequest/cus:customer <br/>
 * elements: [cus1:externalCustomerId, cus1:customerNo]
 *
 * @author Petr Juza
 */
public class XPathValidator implements Processor {

    private Namespaces ns;

    private String parentElm;

    private String[] elements;

    /**
     * Creates new XPath validator.
     *
     * @param parentElm the parent element (if any)
     * @param ns the namespace(s)
     * @param elements the element names;
     */
    public XPathValidator(@Nullable String parentElm, Namespaces ns, String... elements) {
        Assert.notNull(ns, "the ns must not be null");
        Assert.notEmpty(elements, "the elements must not be empty");

        this.ns = ns;
        this.parentElm = parentElm;
        this.elements = elements;
    }

    /**
     * Creates new XPath validator with empty parent element.
     *
     * @param ns the namespace(s)
     * @param elements the elements
     */
    public XPathValidator(Namespaces ns, String... elements) {
        Assert.notNull(ns, "the ns must not be null");
        Assert.notEmpty(elements, "the elements must not be empty");

        this.ns = ns;
        this.elements = elements;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        List<String> wrongElms = new ArrayList<String>();

        for (String elm : elements) {
            String absElm = StringUtils.trimToEmpty(parentElm);
            if (!absElm.startsWith("/") && StringUtils.isNotEmpty(absElm)) {
                absElm = "/" + absElm;
            }

            absElm += "/" + elm;

            String xpathExp = absElm + "/text()";

            // example: /cusSer:setCustomerRequest/cusSer:customer/cus:customerNo/text()
            try {
                NodeList nodeList = ((NodeList)ns.xpath(xpathExp).evaluate(exchange));

                if (nodeList.getLength() == 0) {
                    // no mandatory element => error
                    wrongElms.add(absElm);
                }
            } catch (InvalidXPathExpression ex) {
                wrongElms.add(absElm);
            }
        }

        // throw exception if wrong
        if (!wrongElms.isEmpty()) {
            String msg = "The following mandatory elements aren't available or don't have any value: \n"
                    + StringUtils.join(wrongElms, ", ");

            throwException(msg);
        }
    }

    /**
     * Throws exception when validation failed.
     * Overrides this method when you need to throw different exception.
     *
     * @param msg the exception message
     */
    protected void throwException(String msg) {
        throw new ValidationIntegrationException(InternalErrorEnum.E110, msg);
    }
}
