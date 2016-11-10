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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.util.Assert;


/**
 * Encapsulates context call parameters.
 *
 * @author Petr Juza
 * @see ContextCall
 */
public class ContextCallParams {

    private Class<?> targetType;
    private String methodName;
    private List<Object> methodArgs;

    // technical parameter
    private DateTime creationTimestamp;

    public ContextCallParams(Class<?> targetType, String methodName, Object... methodArgs) {
        Assert.notNull(targetType, "the targetType must not be null");
        Assert.hasText(methodName, "the methodName must be defined");

        this.targetType = targetType;
        this.methodName = methodName;
        this.methodArgs = new ArrayList<Object>(Arrays.asList(methodArgs));
        this.creationTimestamp = DateTime.now();
    }

    /**
     * Gets class of target service.
     *
     * @return the class of target service
     */
    public Class<?> getTargetType() {
        return targetType;
    }

    /**
     * Gets name of calling method on target service.
     *
     * @return name of calling method on target service
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets method arguments (if any).
     *
     * @return method arguments, can be empty
     */
    public List<Object> getMethodArgs() {
        return Collections.unmodifiableList(methodArgs);
    }

    /**
     * Gets timestamp when these params were created.
     *
     * @return timestamp
     */
    public DateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("targetType", targetType)
                .append("methodName", methodName)
                .toString();
    }
}
