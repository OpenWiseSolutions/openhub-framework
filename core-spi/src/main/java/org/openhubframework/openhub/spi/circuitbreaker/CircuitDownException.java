package org.openhubframework.openhub.spi.circuitbreaker;

import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;

/**
 * Exception to be thrown, when circuit is down (shortcuted).
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class CircuitDownException extends IntegrationException {

    /**
     * CircuitDownException with given message.
     *
     * @param message the message.
     */
    public CircuitDownException(String message) {
        super(InternalErrorEnum.E123, message);
    }
}
