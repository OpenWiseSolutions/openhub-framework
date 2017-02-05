package org.openhubframework.openhub.spi.node;

import java.util.List;

import org.openhubframework.openhub.api.entity.Node;

/**
 * Service for manipulating with {@link Node}.
 *
 * @author Roman Havlicek
 * @see Node
 * @see ChangeNodeCallback
 * @since 2.0
 */
public interface NodeService {

    /**
     * Insert new {@link Node}.
     *
     * @param node node
     * @return saved {@link Node}
     */
    Node insert(Node node);

    /**
     * Update existing {@link Node}.
     *
     * @param node       node that will be updated
     * @param changeNodeCallback callback function in which can be changed all node attributes
     * @return updated {@link Node}
     */
    Node update(Node node, ChangeNodeCallback changeNodeCallback);

    /**
     * Update existing {@link Node}.
     *
     * @param node node
     */
    void delete(Node node);

    /**
     * Get all {@link Node}s.
     *
     * @return all nodes
     */
    List<Node> getAllNodes();

    /**
     * Gets node by identifier.
     *
     * @param nodeId node identifier
     * @return found node
     */
    Node getNodeById(Long nodeId);

    /**
     * Get actual node for this application server instance.
     *
     * @return node for this server instance
     */
    Node getActualNode();
}
