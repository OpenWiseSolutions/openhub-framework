package org.openhubframework.openhub.api.entity;

import javax.annotation.Nullable;
import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Contains information about one node in cluster.
 * Attributes in this entity can be changed.
 *
 * @author Roman Havlicek
 * @see Node
 * @since 2.0
 */
@Entity
@Table(name = "node",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_node_code", columnNames = {"code"}),
                @UniqueConstraint(name = "uq_node_name", columnNames = {"name"})})
public class MutableNode extends AbstractNode {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    /**
     * Identifier.
     */
    @Id
    @Column(name = "node_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long nodeId;

    /**
     * Unique node code.
     */
    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    /**
     * Unique node name.
     */
    @Column(name = "name", length = 256, nullable = false, unique = true)
    private String name;

    /**
     * Description.
     */
    @Column(name = "description", length = 2056, nullable = true)
    private String description;

    /**
     * Node state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 64, nullable = false)
    private NodeState state;

    /**
     * New instance only for JPA.
     */
    protected MutableNode() {
    }

    /**
     * New instance with {@link NodeState#RUN} state.
     *
     * @param code code
     * @param name name
     */
    public MutableNode(String code, String name) {
        this(code, name, NodeState.RUN);
    }

    /**
     * New instance.
     *
     * @param code  code
     * @param name  name
     * @param state state
     */
    public MutableNode(String code, String name, NodeState state) {
        Assert.hasText(code, "code must not be empty");
        Assert.hasText(name, "name must not be empty");
        Assert.notNull(state, "state must not be null");

        this.code = code;
        this.name = name;
        this.state = state;
    }

    //-------------------------------------------------- STATE ---------------------------------------------------------

    /**
     * Node handles all messages (existing and new) {@link NodeState#RUN}.
     */
    public void setRunState() {
        setState(NodeState.RUN);
    }

    /**
     * Node handles only existing messages (new message will be rejected)
     * {@link NodeState#HANDLES_EXISTING_MESSAGES}.
     */
    public void setHandleOnlyExistingMessageState() {
        setState(NodeState.HANDLES_EXISTING_MESSAGES);
    }

    /**
     * Node is stopped (new message will be rejected and existing is not processing)
     * {@link NodeState#STOPPED}.
     */
    public void setStoppedState() {
        setState(NodeState.STOPPED);
    }

    //--------------------------------------------------- SET / GET ----------------------------------------------------

    @Override
    @Nullable
    public Long getNodeId() {
        return nodeId;
    }

    @Override
    public String getCode() {
        return code;
    }

    /**
     * Sets code of this node.
     *
     * @param code code
     */
    public void setCode(String code) {
        Assert.hasText(code, "code must not be empty");

        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets name ot this node.
     *
     * @param name name
     */
    public void setName(String name) {
        Assert.hasText(name, "name must not be empty");

        this.name = name;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Sets description
     *
     * @param description description, {@code NULL} - node has no description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Override
    public NodeState getState() {
        return state;
    }

    /**
     * Sets state of this node.
     *
     * @param state state
     */
    private void setState(NodeState state) {
        Assert.notNull(state, "state must not be null");

        this.state = state;

        LOG.info("State of node {} was changed to {}", toHumanString(), getState());
    }
}
