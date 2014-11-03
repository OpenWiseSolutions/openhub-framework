
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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class ExceptionThrowingDateTimeAdapter
    extends XmlAdapter<String, DateTime>
{
    public DateTime unmarshal(String value) {
        throw new NumberFormatException(
                String.format("Mock exception while unmarshalling String value %s as DateTime", value));
    }

    public String marshal(DateTime value) {
        throw new NumberFormatException(
                String.format("Mock exception while marshalling DateTime value %s as String", value));
    }

}
