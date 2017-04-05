package org.openhubframework.openhub.core.common.dao;

import java.util.List;
import javax.annotation.Nullable;

import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;

/**
 * Dao interface for operation with {@link MutableNode}.
 *
 * @author Roman Havlicek
 * @since 2.0
 */
public interface NodeDao {

    /**
     * Insert new {@link MutableNode}.
     *
     * @param node node
     */
    Node insert(Node node);

    /**
     * Update existing {@link MutableNode}.
     *
     * @param node               node
     * @param changeNodeCallback callback function in which can be changed all node attributes
     */
    Node update(Node node, ChangeNodeCallback changeNodeCallback);

    /**
     * Delete existing {@link MutableNode}.
     *
     * @param node node
     */
    void delete(Node node);

    /**
     * Get all {@link MutableNode}s.
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
     * Finds node by identifier.
     *
     * @param nodeId node identifier
     * @return found node, {@code NULL} - node not found by identifier
     */
    @Nullable
    Node findNodeById(Long nodeId);

    /**
     * Find node by {@link MutableNode#getCode()}.
     *
     * @param code code
     * @return found node, {@code NULL} - no node found by code
     */
    @Nullable
    Node findNodeByCode(String code);
}
