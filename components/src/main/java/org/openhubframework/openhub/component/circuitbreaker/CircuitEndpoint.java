package org.openhubframework.openhub.component.circuitbreaker;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultEndpoint;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;


/**
 * Circuit breaker endpoint.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
public class CircuitEndpoint extends DefaultEndpoint {

    private String circuitName;
    private String targetUri;

    public CircuitEndpoint(String endpointUri, CircuitComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new CircuitProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Circuit breaker does not support consumer endpoint.");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Get circuit name.
     */
    public String getCircuitName() {
        return circuitName;
    }

    /**
     * Set circuit name.
     */
    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }

    /**
     * Get target uri.
     */
    public String getTargetUri() {
        return targetUri;
    }

    /**
     * Set target uri.
     */
    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    /**
     * Get circuit breaker instance.
     */
    protected CircuitBreaker getCircuitBreaker() {
        return ((CircuitComponent) getComponent()).getCircuitBreaker();
    }

    /**
     * Get producer template instance.
     */
    protected ProducerTemplate getProducerTemplate() {
        return ((CircuitComponent) getComponent()).getProducerTemplate();
    }
}
