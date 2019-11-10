package org.openhubframework.openhub.spi.circuitbreaker;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Configuration of single circuit.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class CircuitConfiguration implements Serializable {
    static final long serialVersionUID = 1L;

    /**
     * If set to false, circuit breaker is disabled, meaning all calls will
     * be invoked "live".
     */
    private boolean enabled = true;
    /**
     * Unique name of circuit.
     */
    private String circuitName;
    /**
     * Threshold in percentage of failed requests needed to break circuit.
     */
    private int thresholdPercentage;
    /**
     * Sliding window size for checking threshold.
     */
    private long windowSizeInMillis;
    /**
     * Minimal count of requests in window.
     */
    private long minimalCountInWindow;
    /**
     * Sleep in millis, after circuit breaker is switched.
     */
    private long sleepInMillis;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCircuitName() {
        return circuitName;
    }

    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    public int getThresholdPercentage() {
        return thresholdPercentage;
    }

    public void setThresholdPercentage(int thresholdPercentage) {
        this.thresholdPercentage = thresholdPercentage;
    }

    public long getWindowSizeInMillis() {
        return windowSizeInMillis;
    }

    public void setWindowSizeInMillis(long windowSizeInMillis) {
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public long getMinimalCountInWindow() {
        return minimalCountInWindow;
    }

    public void setMinimalCountInWindow(long minimalCountInWindow) {
        this.minimalCountInWindow = minimalCountInWindow;
    }

    public long getSleepInMillis() {
        return sleepInMillis;
    }

    public void setSleepInMillis(long sleepInMillis) {
        this.sleepInMillis = sleepInMillis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("enabled", enabled)
                .append("circuitName", circuitName)
                .append("thresholdPercentage", thresholdPercentage)
                .append("windowSizeInMillis", windowSizeInMillis)
                .append("minimalCountInWindow", minimalCountInWindow)
                .append("sleepInMillis", sleepInMillis)
                .toString();
    }
}
