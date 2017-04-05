package org.openhubframework.openhub.spi.node;

import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;

/**
 * Callback function for update {@link Node} in {@link NodeService#update(Node, ChangeNodeCallback)}.
 * In method {@link #updateNode(MutableNode)} we can change all node attributes.
 *
 * @author Roman Havlicek
 * @see NodeService#update(Node, ChangeNodeCallback)
 * @see Node
 * @see MutableNode
 * @since 2.0
 */
public interface ChangeNodeCallback {

    /**
     * Update attributes in {@link MutableNode}.
     *
     * @param node mutable node in which can be changed all attributes
     */
    void updateNode(MutableNode node);
}
