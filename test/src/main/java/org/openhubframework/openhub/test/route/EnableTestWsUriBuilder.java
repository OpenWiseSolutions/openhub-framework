package org.openhubframework.openhub.test.route;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Annotation to enable {@link TestWsUriBuilder} for tests.
 * Please refer to its javadoc for more info.
 *
 * @author Karel Kovarik
 * @since 2.1.0
 * @see TestWsUriBuilder
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Import(TestWsUriBuilderConfiguration.class)
public @interface EnableTestWsUriBuilder {
}
