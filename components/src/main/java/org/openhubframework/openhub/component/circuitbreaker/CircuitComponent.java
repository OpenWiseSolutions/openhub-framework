package org.openhubframework.openhub.component.circuitbreaker;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.StringHelper;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Custom component for simple use of circuit breaker.
 *
 * Usage:
 * .to("circuit:<circuit-name>:<uri-to-call>"),
 * where:
 *  <circuit-name> is unique name of circuit, recommended granularity
 *  is one circuit per target system.
 *  <uri-to-call> is uri to be invoked if circuit is up.
 * Configuration:
 *  {@link CircuitConfiguration} in Exchange property {@link CircuitBreaker#CONFIGURATION_PROPERTY}
 *
 * @author Karel Kovarik
 * @see CircuitBreaker
 * @since 2.2
 */
public class CircuitComponent extends DefaultComponent {

    /**
     * CircuitBreaker interface implementation.
     * If none is provided, component cannot be used.
     */
    @Autowired
    private CircuitBreaker circuitBreaker;

    /**
     * Camel producer template.
     */
    @Produce
    private ProducerTemplate producerTemplate;

    /**
     * CamelContext.
     */
    @Autowired
    private CamelContext camelContext;


    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        final CircuitEndpoint endpoint = new CircuitEndpoint(uri, this);

        final String endpointURI = StringHelper.after(uri, ":");
        Assert.hasText(endpointURI, "the endpointURI must not be empty");

        final String name = StringHelper.before(endpointURI, ":");
        Assert.hasText(name, "the circuitName must not be empty.");
        final String trimmedName = name.replaceAll("/", "").trim();
        Assert.hasText(trimmedName, "the trimmed circuitName must not be empty.");
        final String targetUri = StringHelper.after(endpointURI, ":");
        Assert.hasText(targetUri, "the targetUri must not be empty.");

        endpoint.setCircuitName(trimmedName);
        endpoint.setTargetUri(targetUri);
        return endpoint;
    }

    /**
     * Get instance of CircuitBreaker implementation.
     */
    protected CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    /**
     * Get instance of producerTemplate.
     */
    protected ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        // do nothing, do not call validation from parent - DefaultComponent
    }
}
