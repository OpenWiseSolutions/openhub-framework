package org.openhubframework.openhub.api.route;

/**
 * Type of route.
 * To identify new type of route implements {@link RouteTypeResolver}.
 *
 * @author Roman Havlicek
 * @see RouteTypeResolver
 * @see RouteTypeInfo
 * @since 2.0
 */
public interface RouteType {

    /**
     * Gets name of route type.
     *
     * @return name
     */
    String getName();
}
