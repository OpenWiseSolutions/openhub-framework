package org.openhubframework.openhub.common.datasource;

import java.lang.annotation.*;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * Alias for {@link javax.annotation.Resource} annotation to autowire bean with {@value #BEAN_NAME} name.
 *
 * Note that we cannot use {@link Autowired} with {@link Qualifier}, as if only one {@link DataSource} is does not inject by bean name.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@Target({ ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
@Qualifier(OpenHubDataSource.BEAN_NAME)
public @interface OpenHubDataSource {

    /**
     * Bean name of {@link DataSource}.
     */
    String BEAN_NAME = "OpenHubDataSource";
}
