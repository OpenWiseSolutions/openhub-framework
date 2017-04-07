package org.openhubframework.openhub.common.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Filter for
 * {@link ReflectionUtils#doWithMethods(Class, ReflectionUtils.MethodCallback, ReflectionUtils.MethodFilter)} that
 * return only method that contains at least one {@link Annotation}s from defined list {@link #getAnnotationTypes()}.
 *
 * @author Roman Havlicek
 * @see ReflectionUtils.MethodFilter
 * @see ReflectionUtils#doWithMethods(Class, ReflectionUtils.MethodCallback, ReflectionUtils.MethodFilter)
 * @see AnnotationUtils
 * @since 2.0
 */
public class AnnotationMethodFilter implements ReflectionUtils.MethodFilter {

    /**
     * All {@link Annotation}s for filtering method.
     */
    private final Set<Class<? extends Annotation>> annotationTypes = new HashSet<>();

    /**
     * New instance.
     *
     * @param annotationType {@link Annotation} which method must have
     */
    public AnnotationMethodFilter(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "annotationType must not be null");

        annotationTypes.add(annotationType);
    }

    /**
     * New instance.
     *
     * @param annotationTypes {@link Annotation}s which method must have least one
     */
    public AnnotationMethodFilter(Class<? extends Annotation>... annotationTypes) {
        Assert.notEmpty(annotationTypes, "annotationTypes must not be empty");

        this.annotationTypes.addAll(Arrays.asList(annotationTypes));
    }

    /**
     * New instance.
     *
     * @param annotationTypes {@link Annotation}s which method must have least one
     */
    public AnnotationMethodFilter(Collection<Class<? extends Annotation>> annotationTypes) {
        Assert.notEmpty(annotationTypes, "annotationTypes must not be empty");

        this.annotationTypes.addAll(annotationTypes);
    }

    @Override
    public boolean matches(Method method) {
        Assert.notNull(method, "method must not be null");

        for (Class<? extends Annotation> annotation : this.annotationTypes) {
            if (AnnotationUtils.findAnnotation(method, annotation) != null) {
                return true;
            }
        }
        return false;
    }

    //------------------------------------------------ SET / GET -------------------------------------------------------

    /**
     * Gets all {@link Annotation}s.
     *
     * @return {@link Annotation}s
     */
    public List<Class<? extends Annotation>> getAnnotationTypes() {
        return new ArrayList<>(annotationTypes);
    }
}
