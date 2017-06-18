/*
 * Copyright 2012-2017 the original author or authors.
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

package org.openhubframework.openhub.admin.web.message.rpc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * Action result rpc object.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class ActionResultRpc {
    private final String result;
    private final String resultDescription;

    public ActionResultRpc(String result, String resultDescription) {
        this.result = result;
        this.resultDescription = resultDescription;
    }

    public String getResult() {
        return result;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("result", result)
                .append("resultDescription", resultDescription)
                .toString();
    }
}
