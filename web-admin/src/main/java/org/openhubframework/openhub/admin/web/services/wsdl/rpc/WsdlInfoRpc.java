/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.services.wsdl.rpc;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Info about exposed webservice.
 *
 * @author Karel Kovarik
 */
@XmlRootElement
public class WsdlInfoRpc {

    private final String name;
    private final String wsdl;

    /**
     * All argument constructor.
     * @param name the name of webservice.
     * @param wsdl the link to wsdl.
     */
    public WsdlInfoRpc(String name, String wsdl) {
        this.name = name;
        this.wsdl = wsdl;
    }

    public String getName() {
        return name;
    }

    public String getWsdl() {
        return wsdl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("wsdl", wsdl)
                .toString();
    }
}
