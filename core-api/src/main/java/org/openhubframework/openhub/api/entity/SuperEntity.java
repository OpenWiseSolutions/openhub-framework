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

package org.openhubframework.openhub.api.entity;

import java.io.Serializable;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.openhubframework.openhub.api.common.HumanReadable;
import org.openhubframework.openhub.common.Strings;


/**
 * Basic entity with an identifier property.
 * Used as a base class for all persistent entities.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
public abstract class SuperEntity<ID extends Serializable> implements Serializable, Identifiable<ID>, HumanReadable {

    private static final long serialVersionUID = -3963280994783527490L;

    private ID id;

    /**
     * Creates new entity.
     *
     * @param id The unique identifier
     */
    protected SuperEntity(@Nullable ID id) {
        this.id = id;
    }

    @Override
    public void setId(@Nullable ID id) {
        this.id = id;
    }

    /**
     * Get unique ID of the entity.
     *
     * @return entity's ID
     */
    @Nullable
    @Override
    public ID getId() {
        return id;
    }

    /**
     * Return true if entity is new otherwise false.
     *
     * @return true if entity is new otherwise false.
     */
    public boolean isNew() {
        return (this.getId() == null);
    }

    /**
     * Two entities are equal if their ID is equal.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof SuperEntity) {
            SuperEntity en = (SuperEntity) obj;

            return new EqualsBuilder()
                    .append(getId(), en.getId())
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

    @Override
    public String toHumanString() {
        return Strings.fm("(id = {})", getId());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", getId())
                .toString();
    }
}