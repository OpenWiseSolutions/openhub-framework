package org.openhubframework.openhub.api.entity;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract node with base methods for get {@link NodeState} information.
 *
 * @author Roman Havlicek
 * @see Node
 * @see MutableNode
 * @since 2.0
 */
public abstract class AbstractNode implements Node {

    @Nullable
    @Override
    public Long getId() {
        return getNodeId();
    }

    @Override
    public void setId(@Nullable Long id) {
        throw new UnsupportedOperationException("It is not possible to update unique identifier");
    }

    @Override
    public boolean isStopped() {
        return getState().equals(NodeState.STOPPED);
    }

    @Override
    public boolean isAbleToHandleNewMessages() {
        return getState().isAbleToHandleNewMessages();
    }

    @Override
    public boolean isAbleToHandleExistingMessages() {
        return getState().isAbleToHandleExistingMessages();
    }

    //--------------------------------------------- TOSTRING / HASH / EQUALS -------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return new EqualsBuilder()
                .append(getNodeId(), node.getNodeId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getNodeId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("nodeId", getNodeId())
                .append("code", getCode())
                .append("name", getName())
                .append("description", StringUtils.substring(getDescription(), 100))
                .append("state", getState())
                .toString();
    }

    @Override
    public String toHumanString() {
        return "(code = " + getCode() + ")";
    }
}
