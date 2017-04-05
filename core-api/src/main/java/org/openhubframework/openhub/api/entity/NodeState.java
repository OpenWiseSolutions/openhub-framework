package org.openhubframework.openhub.api.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Contains all states for {@link Node}
 *
 * @author Roman Havlicek
 * @see Node#getState()
 * @since 2.0
 */
public enum NodeState {

    /**
     * Node handles new and existing (saved messages in ESB) messages.
     */
    RUN(true, true),

    /**
     * Node handles only existing messages. New messages/requests are rejected.
     */
    HANDLES_EXISTING_MESSAGES(false, true),

    /**
     * Node not handles existing messages and new messages.
     */
    STOPPED(false, false);

    /**
     * {@code true} node handles new incoming messages, {@code false} - otherwise.
     */
    private final boolean handleNewMessages;

    /**
     * {@code true} node handles existing saved messages, {@code false} - otherwise.
     */
    private final boolean handleExistingMessages;

    /**
     * New instance.
     *
     * @param handleNewMessages      {@code true} node handles new incoming messages, {@code false} - otherwise
     * @param handleExistingMessages {@code true} node handles existing saved messages, {@code false} - otherwise
     */
    NodeState(boolean handleNewMessages, boolean handleExistingMessages) {
        this.handleNewMessages = handleNewMessages;
        this.handleExistingMessages = handleExistingMessages;
    }

    /**
     * If {@link Node} in this state handles new incoming messages.
     *
     * @return {@code true} node handles new incoming messages, {@code false} - oterwise
     */
    public boolean isAbleToHandleNewMessages() {
        return handleNewMessages;
    }

    /**
     * If {@link Node} in this state handles messages existing saved in ESB (from SEDA or
     * {@link MsgStateEnum#PARTLY_FAILED}, {@link MsgStateEnum#POSTPONED}, etc.).
     *
     * @return {@code true} node handles existing saved messages, {@code false} - otherwise
     */
    public boolean isAbleToHandleExistingMessages() {
        return handleExistingMessages;
    }

    //--------------------------------------------- TOSTRING / HASH / EQUALS -------------------------------------------

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("handleNewMessages", handleNewMessages)
                .append("handleExistingMessages", handleExistingMessages)
                .toString();
    }
}
