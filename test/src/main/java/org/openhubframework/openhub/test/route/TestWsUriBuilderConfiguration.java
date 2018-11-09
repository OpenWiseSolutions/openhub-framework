package org.openhubframework.openhub.test.route;

import org.openhubframework.openhub.api.route.WebServiceUriBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuration of {@link TestWsUriBuilder}, should be used indirectly
 * via annotation {@link EnableTestWsUriBuilder}.
 *
 * @author Karel Kovarik
 * @since 2.1.0
 * @see EnableTestWsUriBuilder
 */
public class TestWsUriBuilderConfiguration {

    @Primary
    @Bean
    public WebServiceUriBuilder testWebServiceUriBuilder() {
        return new TestWsUriBuilder();
    }
}
