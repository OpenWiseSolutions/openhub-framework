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

package org.openhubframework.openhub.admin.web.message.rest;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;
import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.admin.web.message.rpc.ActionRequestRpc;
import org.openhubframework.openhub.admin.web.message.rpc.ActionResultRpc;
import org.openhubframework.openhub.admin.web.message.rpc.MessageFilterRpc;
import org.openhubframework.openhub.admin.web.message.rpc.MessageCollectionWrapper;
import org.openhubframework.openhub.admin.web.message.rpc.MessageListItemRpc;
import org.openhubframework.openhub.admin.web.message.rpc.MessageRpc;
import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.core.common.asynch.msg.MessageOperationService;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.web.common.WebProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing operations with messages.
 *
 * @author Karel Kovarik
 * @since 2.0
 */
@RestController
@RequestMapping(value = MessageController.REST_URI)
public class MessageController extends AbstractOhfController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    public static final String REST_URI = BASE_PATH + "/messages";

    private static final String MESSAGE_ACTION_OK = "OK";

    private static final String MESSAGE_ACTION_ERROR = "ERROR";

    @ConfigurableValue(key = WebProps.MESSAGES_LIMIT)
    private ConfigurationItem<Long> messagesLimit;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageOperationService messageOperationService;

    /**
     * List messages, by given filter.
     *
     * @param messageFilter the filter to filter messages.
     * @return custom collection wrapper with message list elements.
     */
    @GetMapping(produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageCollectionWrapper list(final MessageFilterRpc messageFilter){
        Constraints.notNull(messageFilter.getReceivedFrom(), "The receivedFrom is mandatory.");
        Constraints.notNull(messagesLimit.getValue(), "the messagesLimit must be configured.");

        final MessageFilter filter =
                MessageFilterRpc.toMessageFilter().convert(messageFilter);
        LOG.trace("List messages by filter [{}].", filter);

        // fetch messages from messageService
        final List<Message> messageList = messageService.findMessagesByFilter(filter, messagesLimit.getValue());

        return new MessageCollectionWrapper(
                MessageListItemRpc.fromMessage(),
                messageList,
                messagesLimit.getValue(),
                messageList.size()
        );
    }

    /**
     * Get detail of message identified by its id.
     *
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

    /**
     * Perform action on message identified by its id.
     *
     * @param id the id of message to perform action on.
     * @param actionRequestRpc the action type.
     * @return ActionResultRpc entity.
     */
    @PostMapping(path = "/{id}/action", produces = {"application/xml", "application/json"})
    public ResponseEntity<ActionResultRpc> action(
            @PathVariable final Long id,
            @RequestBody @Valid final ActionRequestRpc actionRequestRpc) {

        try {
            switch(actionRequestRpc.getType()) {
                case CANCEL:
                    messageOperationService.cancelMessage(id);
                    return new ResponseEntity<>(
                            new ActionResultRpc(MESSAGE_ACTION_OK, "Successful message cancel."), HttpStatus.OK);
                case RESTART:
                    final JsonNode totalRestartNode = actionRequestRpc.getData().get(ActionRequestRpc.TOTAL_RESTART_FIELD);
                    return totalRestartNode != null ? restartMessage(id, totalRestartNode.asBoolean())
                                                    : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                default:
                    // unsupported action
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        // catch IllegalStateException, return it as 419 CONFLICT
        } catch (final IllegalStateException ex) {
            LOG.warn("Unable to perform action on message with id [{}], actionRequest [{}]:", id, actionRequestRpc, ex);
            return new ResponseEntity<>(
                    new ActionResultRpc(MESSAGE_ACTION_ERROR,
                            MessageFormat.format("Unable to perform action: {0}.", ex.getMessage())), HttpStatus.CONFLICT
            );
        }
    }

    // restartMessage, return OK ResponseEntity
    private ResponseEntity restartMessage(Long id, boolean totalRestart) {
        messageOperationService.restartMessage(id, totalRestart);
        return new ResponseEntity<>(
                new ActionResultRpc(MESSAGE_ACTION_OK, "Successful message restart."), HttpStatus.OK);
    }
}