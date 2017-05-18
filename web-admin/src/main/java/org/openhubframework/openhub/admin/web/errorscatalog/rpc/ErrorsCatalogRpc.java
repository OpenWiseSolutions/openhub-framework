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

package org.openhubframework.openhub.admin.web.errorscatalog.rpc;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * RPC object that holds error catalog.
 * 
 * @author Tomas Hanus
 * @see 2.0
 */
public class ErrorsCatalogRpc {

    private final String name;
    private final List<ErrorCodeRpc> codes;

    public ErrorsCatalogRpc(String name, List<ErrorCodeRpc> codes) {
        this.name = name;
        this.codes = Collections.unmodifiableList(codes);
    }

    public String getName() {
        return name;
    }

    public List<ErrorCodeRpc> getCodes() {
        return codes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("codes", codes)
                .toString();
    }
}
