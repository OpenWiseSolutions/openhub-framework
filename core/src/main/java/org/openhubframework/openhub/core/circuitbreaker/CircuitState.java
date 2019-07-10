package org.openhubframework.openhub.core.circuitbreaker;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * CircuitState domain object, does store circuit state.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class CircuitState implements Serializable {
    static final long serialVersionUID = 1L;

    private long lastShortcutTimestamp;
    private List<Long> successCallList = new CopyOnWriteArrayList<>();
    private List<Long> failedCallList = new CopyOnWriteArrayList<>();

    public long getLastShortcutTimestamp() {
        return lastShortcutTimestamp;
    }

    public void setLastShortcutTimestamp(long lastShortcutTimestamp) {
        this.lastShortcutTimestamp = lastShortcutTimestamp;
    }

    public List<Long> getSuccessCallList() {
        return successCallList;
    }

    public void resetSuccessCallList() {
        this.successCallList = new CopyOnWriteArrayList<>();
    }

    public List<Long> getFailedCallList() {
        return failedCallList;
    }

    public void resetFailedCallList() {
        this.failedCallList = new CopyOnWriteArrayList<>();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("lastShortcutTimestamp", lastShortcutTimestamp)
                .append("successCallList", successCallList)
                .append("failedCallList", failedCallList)
                .toString();
    }
}
