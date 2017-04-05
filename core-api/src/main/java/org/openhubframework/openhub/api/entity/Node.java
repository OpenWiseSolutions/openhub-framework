package org.openhubframework.openhub.api.entity;

import javax.annotation.Nullable;

import org.openhubframework.openhub.api.common.HumanReadable;

/**
 * Contains information about one node in cluster.
 * <p>
 * Value of attributes in this entity can not be changed.
 * If you want to change value of attributes (like code, name...), use {@link MutableNode}.
 * </p>
 *
 * @author Roman Havlicek
 * @see MutableNode
 * @since 2.0
 */
public interface Node extends HumanReadable {

    /**
     * Is node for this instance stopping?
     *
     * @return {@code true} if node is in "stopping mode" otherwise {@code false}
     * @see NodeState#STOPPED
     */
    boolean isStopped();

    /**
     * Is node handles new messages.
     *
     * @return {@code true} node handles new messages, {@code false} - otherwise
     * @see NodeState#RUN
     */
    boolean isAbleToHandleNewMessages();

    /**
     * Is node handles existing message (from SEDA or {@link MsgStateEnum#PARTLY_FAILED},
     * {@link MsgStateEnum#POSTPONED}, etc.).
     *
     * @return {@code true} handles existing message, {@code false} - otherwise
     * @see NodeState#HANDLES_EXISTING_MESSAGES
     */
    boolean isAbleToHandleExistingMessages();

    //--------------------------------------------------- SET / GET ----------------------------------------------------

    /**
     * Gets node identifier.
     *
     * @return node identifier, {@code NULL} - node is not persisted
     */
    @Nullable
    Long getNodeId();

    /**
     * Gets code of this node.
     *
     * @return code
     */
    String getCode();

    /**
     * Gets name of this node.
     *
     * @return name
     */
    String getName();

    /**
     * Gets description.
     *
     * @return description, {@code NULL} - node has no description
     */
    @Nullable
    String getDescription();

    /**
     * Gets state of this node.
     *
     * @return state
     */
    NodeState getState();
}
