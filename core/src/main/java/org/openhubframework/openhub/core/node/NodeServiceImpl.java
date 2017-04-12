package org.openhubframework.openhub.core.node;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.exception.validation.ConfigurationException;
import org.openhubframework.openhub.core.common.dao.NodeDao;
import org.openhubframework.openhub.core.confcheck.ConfCheck;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;

/**
 * Implementation of the {@link NodeService} interface for manipulating with {@link Node}.
 *
 * @author Roman Havlicek
 * @see Node
 * @since 2.0
 */
@Service
public class NodeServiceImpl implements NodeService, ConfCheck {

    private static final Logger LOG = LoggerFactory.getLogger(NodeServiceImpl.class);

    private final TransactionTemplate transactionTemplate;

    @Autowired
    private NodeDao nodeDao;

    @ConfigurableValue(key = CoreProps.CLUSTER_ACTUAL_NODE_INSTANCE_CODE)
    private ConfigurationItem<String> actualNodeCode;

    /**
     * Contains actual node for this instance.
     */
    private Node actualNode;

    @Autowired
    public NodeServiceImpl(PlatformTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "the transactionManager must not be null");

        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional
    public Node insert(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.isNull(node.getNodeId(), "only new node can be insert");

        Node result = nodeDao.insert(node);

        LOG.debug("Inserted new node {}", result.toHumanString());

        return new ImmutableNode(result);
    }

    @Override
    @Transactional
    public Node update(Node node, ChangeNodeCallback changeNodeCallback) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(changeNodeCallback, "changeNode must not be null");

        ImmutableNode result = new ImmutableNode(nodeDao.update(node, changeNodeCallback));
        if (result.equals(getActualNode())) {
            synchronized (this) {
                actualNode = new ImmutableNode(result);
            }
        }

        LOG.debug("Updated node {}", result.toHumanString());

        return result;
    }

    @Override
    @Transactional
    public void delete(Node node) {
        Assert.notNull(node, "node must not be null");
        Assert.notNull(node.getNodeId(), "only new node can be delete");
        Assert.isTrue(!node.equals(getActualNode()),
                "Can not delete actual node for this application instance.");

        nodeDao.delete(node);
        LOG.debug("Deleted node {}", node.toHumanString());
    }

    @Override
    public List<Node> getAllNodes() {
        return ImmutableNode.createInstancesFromNodes(nodeDao.getAllNodes());
    }

    @Override
    public Node getNodeById(Long nodeId) {
        Assert.notNull(nodeId, "nodeId must not be null");

        return new ImmutableNode(nodeDao.getNodeById(nodeId));
    }

    @Override
    public Node getActualNode() {
        if (actualNode == null) {
            check();
        }
        return actualNode;
    }

    @Override
    public synchronized void check() throws ConfigurationException {
        //test if configuration value exist
        if (StringUtils.isBlank(actualNodeCode.getValue())) {
            throw new ConfigurationException("Configuration '" + CoreProps.CLUSTER_ACTUAL_NODE_INSTANCE_CODE
                    + "' has no value.");
        }

        //create default node for this instance if not exists
        actualNode = transactionTemplate.execute(new TransactionCallback<Node>() {
            @Override
            public Node doInTransaction(TransactionStatus transactionStatus) {
                LOG.info("Find node for this instance by code {}.", actualNodeCode.getValue());

                Node node = nodeDao.findNodeByCode(actualNodeCode.getValue());
                if (node == null) {
                    LOG.info("Node for this instance with code {} not found. It will be created.",
                            actualNodeCode.getValue());

                    return insert(new MutableNode(actualNodeCode.getValue(), actualNodeCode.getValue()));
                } else {
                    return node;
                }
            }
        });
    }
}
