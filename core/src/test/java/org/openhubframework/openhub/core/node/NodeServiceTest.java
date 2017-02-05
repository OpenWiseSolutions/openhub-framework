package org.openhubframework.openhub.core.node;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.openhubframework.openhub.api.configuration.CoreProps;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;
import org.openhubframework.openhub.api.exception.NoDataFoundException;
import org.openhubframework.openhub.core.AbstractCoreDbTest;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;
import org.openhubframework.openhub.spi.node.NodeService;

/**
 * Test suite for {@link NodeService}.
 *
 * @author Roman Havlicek
 * @see NodeService
 * @see NodeServiceImpl
 * @since 2.0
 */
public class NodeServiceTest extends AbstractCoreDbTest {

    @Autowired
    private NodeService nodeService;

    @Value("${" + CoreProps.CLUSTER_ACTUAL_NODE_INSTANCE_CODE + "}")
    private String actualNodeCode;

    /**
     * Test method {@link NodeService#insert(Node)}.
     */
    @Test
    public void testInsert() {
        MutableNode firstNode = new MutableNode("codeFirst", "nameFirst");
        MutableNode secondNode = new MutableNode("codeSecond", "nameSecond", NodeState.STOPPED);
        secondNode.setDescription("descriptionSecond");

        Node persistFirstNode = nodeService.insert(firstNode);
        Node persistSecondNode = nodeService.insert(secondNode);

        //with actual node create after start is three
        assertThat(nodeService.getAllNodes().size(), is(2));

        Node testNode = nodeService.getNodeById(persistFirstNode.getNodeId());
        assertThat(testNode.getCode(), is("codeFirst"));
        assertThat(testNode.getName(), is("nameFirst"));
        assertThat(testNode.getDescription(), nullValue());
        assertThat(testNode.getState(), is(NodeState.RUN));

        testNode = nodeService.getNodeById(persistSecondNode.getNodeId());
        assertThat(testNode.getCode(), is("codeSecond"));
        assertThat(testNode.getName(), is("nameSecond"));
        assertThat(testNode.getDescription(), is("descriptionSecond"));
        assertThat(testNode.getState(), is(NodeState.STOPPED));
    }

    /**
     * Test method {@link NodeService#update(Node, ChangeNodeCallback)}.
     */
    @Test
    public void testUpdate() {
        MutableNode node = new MutableNode("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        //insert node
        Node persistedNode = nodeService.getNodeById(nodeService.insert(node).getNodeId());

        nodeService.update(persistedNode, new ChangeNodeCallback() {
            @Override
            public void updateNode(MutableNode node) {
                node.setCode("codeUpdated");
                node.setName("nameUpdated");
                node.setDescription("descriptionUpdated");
                node.setRunState();
            }
        });

        Node testNode = nodeService.getNodeById(persistedNode.getNodeId());
        assertThat(testNode.getCode(), is("codeUpdated"));
        assertThat(testNode.getName(), is("nameUpdated"));
        assertThat(testNode.getDescription(), is("descriptionUpdated"));
        assertThat(testNode.getState(), is(NodeState.RUN));
    }

    /**
     * Test method {@link NodeService#delete(Node)}.
     */
    @Test
    @Transactional
    public void testDelete() {
        MutableNode node = new MutableNode("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        //insert node
        Node persistNode = nodeService.insert(node);

        //test if found this node
        Node persistedNode = nodeService.getNodeById(persistNode.getNodeId());

        //delete node
        nodeService.delete(persistedNode);

        try {
            nodeService.getNodeById(persistedNode.getNodeId());
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(NoDataFoundException.class));
        }
    }

    /**
     * Test method {@link NodeService#getAllNodes()}.
     */
    @Test
    public void testGetAllNodes() {
        MutableNode firstNode = new MutableNode("CodeFirst", "NameFirst");
        MutableNode secondNode = new MutableNode("CodeSecond", "NameSecond");

        nodeService.insert(firstNode);
        nodeService.insert(secondNode);

        List<Node> allNodes = nodeService.getAllNodes();

        assertThat(allNodes.size(), is(2));

        assertThat(allNodes.get(0), instanceOf(ImmutableNode.class));
        assertThat(allNodes.get(0).getCode(), is("CodeFirst"));
        assertThat(allNodes.get(0).getName(), is("NameFirst"));
        assertThat(allNodes.get(0).getDescription(), nullValue());
        assertThat(allNodes.get(0).getState(), is(NodeState.RUN));

        assertThat(allNodes.get(1), instanceOf(ImmutableNode.class));
        assertThat(allNodes.get(1).getCode(), is("CodeSecond"));
        assertThat(allNodes.get(1).getName(), is("NameSecond"));
        assertThat(allNodes.get(1).getDescription(), nullValue());
        assertThat(allNodes.get(1).getState(), is(NodeState.RUN));
    }

    /**
     * Test method {@link NodeService#getNodeById(Long)}.
     */
    @Test
    public void testGetNodeById() {
        MutableNode node = new MutableNode("code", "name", NodeState.STOPPED);
        node.setDescription("description");

        Node persistNode = nodeService.insert(node);

        Node testNode = nodeService.getNodeById(persistNode.getNodeId());
        assertThat(testNode, instanceOf(ImmutableNode.class));
        assertThat(testNode.getNodeId(), is(persistNode.getNodeId()));
        assertThat(testNode.getCode(), is("code"));
        assertThat(testNode.getName(), is("name"));
        assertThat(testNode.getDescription(), is("description"));
        assertThat(testNode.getState(), is(NodeState.STOPPED));
    }

    /**
     * Test method {@link NodeService#getActualNode()}.
     */
    @Test
    public void testGetActualNode() {
        //actual node for tests is defined in configuration file

        Node testNode = nodeService.getActualNode();
        assertThat(testNode, instanceOf(ImmutableNode.class));
        assertThat(testNode.getCode(), is(actualNodeCode));
        assertThat(testNode.getName(), is(actualNodeCode));
        assertThat(testNode.getDescription(), nullValue());
        assertThat(testNode.getState(), is(NodeState.RUN));
    }
}
