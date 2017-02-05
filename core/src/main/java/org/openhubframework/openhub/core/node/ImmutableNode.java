package org.openhubframework.openhub.core.node;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.AbstractNode;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;

/**
 * Base implementation of {@link Node} that has immutable attribute values.
 *
 * @author Roman Havlicek
 * @see Node
 * @see MutableNode
 * @since 2.0
 */
class ImmutableNode extends AbstractNode {

    /**
     * Identifier.
     */
    private final Long nodeId;

    /**
     * Unique node code.
     */
    private final String code;

    /**
     * Unique node name.
     */
    private final String name;

    /**
     * Description.
     */
    private final String description;

    /**
     * Node state.
     */
    private final NodeState state;

    /**
     * Create instance from {@link Node}.
     *
     * @param node node
     */
    ImmutableNode(Node node) {
        Assert.notNull(node, "node must not be null");

        this.nodeId = node.getNodeId();
        this.code = node.getCode();
        this.name = node.getName();
        this.description = node.getDescription();
        this.state = node.getState();
    }

    /**
     * New instance.
     *
     * @param nodeId      node id
     * @param code        code
     * @param name        name
     * @param description description
     * @param state       state
     */
    ImmutableNode(Long nodeId, String code, String name, String description, NodeState state) {
        Assert.notNull(nodeId, "nodeId must not be null");
        Assert.hasText(code, "code must not be empty");
        Assert.hasText(description, "description must not be empty");
        Assert.notNull(state, "state must not be null");

        this.nodeId = nodeId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.state = state;
    }

    //--------------------------------------------------- SET / GET ----------------------------------------------------

    @Nullable
    @Override
    public Long getNodeId() {
        return nodeId;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public NodeState getState() {
        return state;
    }

    //----------------------------------------------- STATIC -----------------------------------------------------------

    /**
     * Create instance of {@link ImmutableNode}s from {@link Node}s
     *
     * @param nodes nodes
     * @return new instance of {@link ImmutableNode}s
     */
    static List<Node> createInstancesFromNodes(List<? extends Node> nodes) {
        Assert.notNull(nodes, "nodes must not be null");

        List<Node> result = new ArrayList<>(nodes.size());
        for (Node node : nodes) {
            result.add(new ImmutableNode(node));
        }
        return result;
    }
}
