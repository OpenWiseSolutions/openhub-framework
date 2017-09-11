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
import org.openhubframework.openhub.api.entity.Message;
import org.springframework.core.convert.converter.Converter;


/**
 * Message list item RPC, only subset of message attributes are used.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
public class MessageListItemRpc extends MessageBaseRpc {

    /**
     * Convert MessageListRpc from Message entity.
     * @return filled in rpc object.
     */
    public static Converter<Message, MessageListItemRpc> fromMessage() {
        return source -> fromMessage(new MessageListItemRpc()).convert(source);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .toString();
    }
}
