/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.common.rpc;

import java.io.Serializable;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.entity.Identifiable;


/**
 * Reference-able read-only RPC entity.
 * Override {@link #init()} method for custom initialization needs.
 * <p>
 * If you need to create/update RPC then use {@link ChangeableRpc}.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 * @see ChangeableRpc
 */
public abstract class BaseRpc<T extends Identifiable<ID>, ID extends Serializable> implements Identifiable<ID> {

    private ID id;

    /**
     * Empty constructor for deserialization from XML/JSON.
     */
    protected BaseRpc() {
        init();
    }

    /**
     * Creates RPC from specified entity.
     *
     * @param entity The entity
     */
    protected BaseRpc(T entity) {
        Assert.notNull(entity, "entity can not be null");

        init();

        this.id = entity.getId();
    }

    @Nullable
    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(@Nullable final ID id) {
        this.id = id;
    }

    /**
     * Inits RPC => override it for specified needs.
     */
    protected void init() {
        // nothing to implement by default
    }

    /**
     * Two entities are equal if their key values are equal.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BaseRpc) {
            BaseRpc en = (BaseRpc) obj;

            return new EqualsBuilder()
                    .append(id, en.id)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .toString();
    }
}
