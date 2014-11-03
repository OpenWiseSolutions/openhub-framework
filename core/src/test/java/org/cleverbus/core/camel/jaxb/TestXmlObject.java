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

package org.cleverbus.core.camel.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"testDate"})
@XmlRootElement(name = "testXmlObject")
public class TestXmlObject {
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(ExceptionThrowingDateTimeAdapter.class)
    @XmlSchemaType(name = "date")
    protected DateTime testDate;

    public DateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(DateTime testDate) {
        this.testDate = testDate;
    }
}
