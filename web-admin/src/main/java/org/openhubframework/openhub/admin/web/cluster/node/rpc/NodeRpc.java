/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.cluster.node.rpc;

import static org.springframework.util.StringUtils.hasText;

import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.openhubframework.openhub.admin.web.common.rpc.ChangeableRpc;
import org.openhubframework.openhub.api.entity.MutableNode;
import org.openhubframework.openhub.api.entity.Node;
import org.openhubframework.openhub.api.entity.NodeState;
import org.openhubframework.openhub.api.exception.validation.IllegalDataException;
import org.openhubframework.openhub.api.exception.validation.InputValidationException;
import org.openhubframework.openhub.api.exception.validation.ValidationException;
import org.openhubframework.openhub.spi.node.ChangeNodeCallback;


/**
 * RPC of {@link Node} entity.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@XmlRootElement
@Validated
public class NodeRpc extends ChangeableRpc<Node, Long> {

    /**
     * Converts {@link Node} to {@link NodeRpc}.
     */
    public static class NodeRpcConverter implements Converter<Node, NodeRpc> {

        @Override
        public NodeRpc convert(Node expr) {
            return new NodeRpc(expr);
        }
    }

    /**
     * Execution callback used to update entity.
     */
    public static class ChangeCallback implements ChangeNodeCallback {

        private final BindingResult bindingResult;
        private final NodeRpc nodeRpc;

        /**
         * Creates change callback for {@link NodeRpc}.
         *
         * @param nodeRpc to be updated in database
         */
        public ChangeCallback(BindingResult bindingResult, NodeRpc nodeRpc) {
            this.bindingResult = bindingResult;
            this.nodeRpc = nodeRpc;
        }

        @Override
        public void updateNode(final MutableNode node) {

            node.setName(nodeRpc.getName());
            node.setDescription(nodeRpc.getDescription());

            // condition translation
            if (hasText(nodeRpc.getState())) {
                final NodeState nodeState = NodeState.valueOf(nodeRpc.getState());
                switch (nodeState) {
                    case RUN:
                        node.setRunState();
                        break;
                    case HANDLES_EXISTING_MESSAGES:
                        node.setHandleOnlyExistingMessageState();
                        break;
                    case STOPPED:
                        node.setStoppedState();
                        break;
                }
            }

            // validation phase
            try {
                nodeRpc.preSaveValidation(bindingResult, node);
            } catch (Exception ex) {
                if (!(ex instanceof ValidationException)) {
                    // no validation exception => wrap it by ValidationException
                    throw new IllegalDataException("validation of the following object failed: " + toString(), ex);
                }
            }

            if (bindingResult.hasErrors()) {
                throw new InputValidationException(bindingResult);
            }
        }
    }

    /**
     * Identifier.
     */
    private Long id;

    /**
     * Unique node code.
     */
    private String code;

    /**
     * Unique node name.
     */
    @NotEmpty
    private String name;

    /**
     * Description.
     */
    private String description;

    /**
     * state (enum[string]): state of node
     * + Members
     * + `RUN`
     * + `HANDLES_EXISTING_MESSAGES`
     * + `STOPPED`
     */
    @NotEmpty
    private String state;

    protected NodeRpc() {
        // for XML NodeRpc
    }

    /**
     * Creates {@link NodeRpc} based upon {@link Node}.
     *
     * @param entity of node
     */
    public NodeRpc(Node entity) {
        super(entity);

        this.id = entity.getId();
        this.code = entity.getCode();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.state = entity.getState().name();
    }

    @Override
    protected void validate(BindingResult errors, @Nullable Node updateEntity) throws ValidationException {
        // nothing to validate
    }

    /**
     * Validates RPC (mandatory attributes, correct object state etc.).
     *
     * @param errors       Binding/validation errors
     * @param updateEntity {@code null} if new entity is created otherwise entity that is updated
     * @throws ValidationException when there is error in input data
     */
    public final void preSaveValidation(BindingResult errors, @Nullable Node updateEntity) throws ValidationException {
        validate(errors, updateEntity);
    }

    @Override
    protected MutableNode createEntityInstance(Map<String, ?> params) {
        throw new UnsupportedOperationException("Node entity can be updated only, not created");
    }

    @Override
    protected void updateAttributes(Node param, boolean created) {
        throw new UnsupportedOperationException("Node entity can be updated only via MutableNode");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        Assert.hasText(state, "State must not be empty");
        this.state = state;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("code", code)
                .append("name", name)
                .append("description", description)
                .append("state", state)
                .toString();
    }
}
