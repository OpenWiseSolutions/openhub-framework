package org.openhubframework.openhub.admin.web.message.rest;

import java.util.List;
import java.util.Optional;

import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.message.rpc.MessageFilterRpc;
import org.openhubframework.openhub.admin.web.message.rpc.MessageCollectionWrapper;
import org.openhubframework.openhub.admin.web.message.rpc.MessageListItemRpc;
import org.openhubframework.openhub.admin.web.message.rpc.MessageRpc;
import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.common.OpenHubPropertyConstants;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing operation with messages.
 *
 * @author Karel Kovarik
 */
@RestController
@RequestMapping(value = MessageController.REST_URI)
public class MessageController extends AbstractOhfController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    public static final String REST_URI = BASE_PATH + "/messages";

    private static final String MESSAGES_LIMIT_PROPERTY = OpenHubPropertyConstants.PREFIX + "admin.console.messages.limit";

    @ConfigurableValue(key = MESSAGES_LIMIT_PROPERTY)
    private ConfigurationItem<Long> messagesLimit;

    @Autowired
    private MessageService messageService;

    /**
     * List messages, by given filter.
     * @param messageFilter the filter to filter messages.
     * @return custom collection wrapper with messagelist elemenets.
     */
    @GetMapping(produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageCollectionWrapper<MessageListItemRpc> list(final MessageFilterRpc messageFilter){
        Constraints.notNull(messageFilter.getReceivedFrom(), "The receivedFrom is mandatory.");

        final MessageFilter filter =
                MessageFilterRpc.toMessageFilter().convert(messageFilter);
        LOG.trace("List messages by filter [{}].", filter);

        // fetch messages from messageService
        final List<Message> messageList = messageService.findMessagesByFilter(filter);

        return new MessageCollectionWrapper<>(
                MessageListItemRpc.fromMessage(),
                messageList,
                messagesLimit.getValue(0L),
                messageList.size()
        );
    }

    /**
     * Get detail of message identified by its id.
     * @param id the id of message.
     * @return message detail.
     */
    @GetMapping(path = "/{id}", produces = {"application/xml", "application/json"})
    public ResponseEntity<MessageRpc> detail(@PathVariable final Long id) {
        LOG.trace("Fetch detail of message with id [{}].", id);

        return Optional.ofNullable(messageService.findEagerMessageById(id))
                .map(message -> new ResponseEntity<>(
                        MessageRpc.fromMessage().convert(message), HttpStatus.OK)
                )
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // action TBA
}
