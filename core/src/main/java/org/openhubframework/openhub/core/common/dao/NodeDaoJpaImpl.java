package org.openhubframework.openhub.core.common.dao;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.MultipleDataFoundException;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;

/**
 * JPA implementation of {@link NodeDao} operation with {@link MutableNode}.
 *
 * @author Roman Havlicek
 * @since 2.0
 */
@Repository
public class NodeDaoJpaImpl implements NodeDao {

    @PersistenceContext(unitName = DbConst.UNIT_NAME)
    private EntityManager em;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Node insert(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.isNull(node.getNodeId(), "only new node can be insert");

        MutableNode result = new MutableNode(node.getCode(), node.getName(), node.getState());
        result.setDescription(node.getDescription());

        em.persist(result);

        return result;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Node update(Node node, ChangeNodeCallback changeNodeCallback) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(node.getNodeId(), "only new node can be update");
        Assert.notNull(changeNodeCallback);

        MutableNode nodeToUpdate = em.find(MutableNode.class, node.getNodeId());
        if (nodeToUpdate == null) {
            throw new NoDataFoundException(Node.class.getSimpleName() + " not found by identifier '"
                    + node.getNodeId() + "'.");
        }
        changeNodeCallback.updateNode(nodeToUpdate);

        return em.merge(nodeToUpdate);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(node.getNodeId(), "only new node can be delete");

        MutableNode nodeToDelete = em.find(MutableNode.class, node.getNodeId());
        if (nodeToDelete != null) {
            em.remove(nodeToDelete);
        }
    }

    @Override
    public List<Node> getAllNodes() {
        String sqlQuery = "SELECT n FROM " + Node.class.getName() + " n ORDER BY n.name";
        TypedQuery<MutableNode> query = em.createQuery(sqlQuery, MutableNode.class);
        return new ArrayList<Node>(query.getResultList());
    }

    @Override
    public Node getNodeById(Long nodeId) {
        Assert.notNull(nodeId, "nodeId must not be null");

        Node result = findNodeById(nodeId);
        if (result == null) {
            throw new NoDataFoundException(Node.class.getSimpleName() + " not found by identifier '" + nodeId + "'.");
        }
        return result;
    }

    @Nullable
    @Override
    public Node findNodeById(Long nodeId) {
        Assert.notNull(nodeId, "nodeId must not be null");

        return em.find(MutableNode.class, nodeId);
    }

    @Nullable
    @Override
    public Node findNodeByCode(String code) {
        Assert.hasText(code, "code must not be empty");

        String sqlQuery = "SELECT n FROM " + Node.class.getName() + " n WHERE n.code = :code";
        TypedQuery<MutableNode> query = em.createQuery(sqlQuery, MutableNode.class);
        query.setParameter("code", code);

        List<MutableNode> result = query.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            throw new MultipleDataFoundException("For code " + code + " found more then one nodes ("
                    + result.size() + ").");
        }
    }
}
