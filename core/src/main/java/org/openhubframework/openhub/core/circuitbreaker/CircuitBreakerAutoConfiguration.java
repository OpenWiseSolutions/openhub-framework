package org.openhubframework.openhub.core.circuitbreaker;

import static org.openhubframework.openhub.common.OpenHubPropertyConstants.PREFIX;

import com.hazelcast.core.HazelcastInstance;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.spi.circuitbreaker.CircuitBreaker;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Circuit breaker AutoConfiguration.
 *
 * Can be enabled or disabled altogether via property {@link CircuitBreakerAutoConfiguration.CIRCUIT_BREAKER_ENABLED}.
 *
 * If enabled it does setup implementation of CircuitBreaker interface.
 * Either in-memory (default), or Hazelcast-based, based on configuration properties.
 *
 * @author Karel Kovarik
 * @since 2.2
 */
@AutoConfiguration
@AutoConfigureAfter({
        HazelcastAutoConfiguration.class
})
@ConditionalOnProperty(
        name = CircuitBreakerAutoConfiguration.CIRCUIT_BREAKER_ENABLED,
        havingValue = "true")
public class CircuitBreakerAutoConfiguration {

    /**
     * Enable or disable component at all.
     */
    public static final String CIRCUIT_BREAKER_ENABLED = PREFIX + "circuitbreaker.enabled";

    /**
     * Implementation of CircuitBreaker to wire.
     */
    public static final String CIRCUIT_BREAKER_IMPL = PREFIX + "circuitbreaker.impl";

    /**
     * Fully qualified name of in-memory implementation.
     */
    private static final String IN_MEMORY_CLASS_NAME
            = "org.openhubframework.openhub.core.circuitbreaker.CircuitBreakerInMemoryImpl";

    /**
     * Fully qualified name of Hazelcast based implementation.
     */
    private static final String HAZELCAST_CLASS_NAME
            = "org.openhubframework.openhub.core.circuitbreaker.CircuitBreakerHazelcastImpl";

    /**
     * Hazelcast based implementation.
     */
    @ConditionalOnProperty(name = CIRCUIT_BREAKER_IMPL,
            havingValue = HAZELCAST_CLASS_NAME)
    @ConditionalOnBean(HazelcastInstance.class)
    @Bean
    public CircuitBreaker circuitBreakerHazelCastImpl() {
        return new CircuitBreakerHazelcastImpl();
    }

    /**
     * Default implementation is in-memory.
     */
    @ConditionalOnMissingBean(CircuitBreaker.class)
    @ConditionalOnProperty(name = CIRCUIT_BREAKER_IMPL,
            havingValue = IN_MEMORY_CLASS_NAME,
            matchIfMissing = true)
    @Bean
    public CircuitBreaker circuitBreakerInMemoryImpl() {
        return new CircuitBreakerInMemoryImpl();
    }
}
