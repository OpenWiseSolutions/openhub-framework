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

package org.openhubframework.openhub.admin.web.auth.rpc;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.Authentication;


/**
 * RPC for {@link Principal user} entity.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public class AuthInfoRpc {

    private final String fullName;
    private final List<RoleInfoRpc> roles;

    /**
     * Default constructor.
     *
     * @param fullName of authenticated user.
     * @param roles    of authenticated user.
     */
    public AuthInfoRpc(final String fullName, final List<RoleInfoRpc> roles) {
        this.fullName = fullName;
        this.roles = Collections.unmodifiableList(roles);
    }

    /**
     * Default constructor.
     *
     * @param authentication of authenticated user.
     */
    public AuthInfoRpc(final Authentication authentication) {
        this.fullName = authentication.getName();
        final List<RoleInfoRpc> roles = authentication.getAuthorities().stream()
                .map(e -> new RoleInfoRpc(e.getAuthority())).collect(Collectors.toList());
        this.roles = Collections.unmodifiableList(roles);
    }

    /**
     * Gets full name of user.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets roles.
     *
     * @return roles
     */
    public List<RoleInfoRpc> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AuthInfoRpc)) {
            return false;
        }

        final AuthInfoRpc authInfoRpc = (AuthInfoRpc) o;

        return new EqualsBuilder()
                .append(getFullName(), authInfoRpc.getFullName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getFullName())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fullName", fullName)
                .toString();
    }
}
