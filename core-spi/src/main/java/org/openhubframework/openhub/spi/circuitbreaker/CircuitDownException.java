package org.openhubframework.openhub.spi.circuitbreaker;

/**
 * Exception to be thrown, when circuit is down (shortcuted).
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class CircuitDownException extends RuntimeException {

    /**
     * CircuitDownException with given message.
     *
     * @param message the message.
     */
    public CircuitDownException(String message) {
        super(message);
    }
}
