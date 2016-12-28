/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.spi.alerts;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openhubframework.openhub.api.common.HumanReadable;
import org.springframework.util.Assert;


/**
 * Alert entity.
 *
 * @author Petr Juza
 * @since 0.4
 */
public class AlertInfo implements HumanReadable {

    private String id;

    private long limit;

    private String sql;

    private boolean enabled = true;

    private String notificationSubject;

    private String notificationBody;


    /**
     * Creates new alert.
     *
     * @param id the alert unique identification
     * @param limit limit that must be exceeded to activate alert
     * @param sql SQL query that returns count of items for comparison with limit value
     * @param enabled if specified alert is enabled or disabled
     * @param notificationSubject the (mail, sms) subject
     * @param notificationBody the (mail, sms) body
     */
    public AlertInfo(String id, long limit, String sql, boolean enabled, @Nullable String notificationSubject,
            @Nullable String notificationBody) {
        Assert.hasText(id, "the id must not be empty");
        Assert.hasText(sql, "the sql must not be empty");

        this.id = id;
        this.limit = limit;
        this.sql = sql;
        this.enabled = enabled;
        this.notificationSubject = notificationSubject;
        this.notificationBody = notificationBody;
    }

    /**
     * Gets alert unique identification.
     *
     * @return alert identification
     */
    public String getId() {
        return id;
    }

    /**
     * Gets limit.
     *
     * @return limit
     */
    public long getLimit() {
        return limit;
    }

    /**
     * Sets limit.
     *
     * @param limit the limit
     */
    public void setLimit(long limit) {
        this.limit = limit;
    }

    /**
     * Gets SQL expression.
     *
     * @return SQL expression
     */
    public String getSql() {
        return sql;
    }

    /**
     * Is this alert enabled?
     *
     * @return {@code true} if enabled otherwise {@code false}
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables/disables this alert.
     *
     * @param enabled {@code true} for enabling otherwise {@code false}
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets notification (email, sms) subject.
     *
     * @return subject
     */
    @Nullable
    public String getNotificationSubject() {
        return notificationSubject;
    }

    /**
     * Gets notification (email, sms) body.
     *
     * @return body
     */
    @Nullable
    public String getNotificationBody() {
        return notificationBody;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof AlertInfo) {
            AlertInfo en = (AlertInfo) obj;

            return new EqualsBuilder()
                    .append(id, en.id)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toHumanString() {
        return id + "(" + enabled + ")";
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("limit", limit)
                .append("sql", sql)
                .append("enabled", enabled)
                .toString();
    }
}
