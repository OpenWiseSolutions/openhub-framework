package org.openhubframework.openhub.admin.web.message.rpc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.api.entity.Message;
import org.springframework.core.convert.converter.Converter;


/**
 * Message list item RPC, only subset of message attributes are used.
 *
 * @author Karel Kovarik
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
