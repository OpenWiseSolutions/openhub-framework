/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.contextcall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ReflectionUtils;


/**
 * Helper class for calling target service via reflection.
 *
 * @author Petr Juza
 */
public final class ReflectionCallUtils {

    private ReflectionCallUtils() {
    }

    /**
     * Invokes target method.
     *
     * @param params the parameters of the call
     * @param beanFactory the Spring bean factory
     * @return response
     */
    public static Object invokeMethod(ContextCallParams params, BeanFactory beanFactory) {
        // find target service
        Object targetService = beanFactory.getBean(params.getTargetType());

        // determine method's argument types
        List<Class> argTypes = new ArrayList<Class>();
        for (Object arg : params.getMethodArgs()) {
            argTypes.add(arg.getClass());
        }

        // exist method?
        Method method = ReflectionUtils.findMethod(params.getTargetType(), params.getMethodName(),
                argTypes.toArray(new Class[argTypes.size()]));
        if (method == null) {
            throw new IllegalStateException("there is no method '" + params.getMethodName()
                    + "' on target type '" + params.getTargetType().getSimpleName() + "'");
        }

        // invoke method
        return ReflectionUtils.invokeMethod(method, targetService, params.getMethodArgs().toArray());
    }
}
