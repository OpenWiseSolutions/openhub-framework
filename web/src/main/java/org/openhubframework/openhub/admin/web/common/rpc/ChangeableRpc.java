/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.common.rpc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.entity.Identifiable;
import org.openhubframework.openhub.api.exception.validation.IllegalDataException;
import org.openhubframework.openhub.api.exception.validation.InputValidationException;
import org.openhubframework.openhub.api.exception.validation.ValidationException;


/**
 * RPC that can be created or updated. {@link BaseRpc} is for readonly RPCs.
 * <p>
 * There is the following lifecycle when entity is <strong>created</strong> from RPC -
 * calling {@link #createEntity()} or {@link #createEntity(BindingResult)} or {@link #createEntity(BindingResult, Map)}:
 * </p>
 * <ol>
 *     <li>validates attributes and state of the RPC - {@link #validate(BindingResult, Identifiable)}
 *     <li>creates new entity instance - {@link #createEntityInstance(Map)}
 *     <li>updates attributes - {@link #updateAttributes(Identifiable, boolean)}, where second parameter is {@code true}
 * </ol>
 *
 * <p>
 * There is the following lifecycle when entity is <strong>updated</strong> from RPC -
 * calling {@link #updateEntity(Identifiable)} or {@link #updateEntity(Identifiable, BindingResult)}:
 * </p>
 * <ol>
 *     <li>validates attributes and state of the RPC - {@link #validate(BindingResult, Identifiable)}
 *     <li>updates attributes - {@link #updateAttributes(Identifiable, boolean)}, where second parameter is {@code false}
 * </ol>
 *
 *
 * @author Petr Juza
 * @since 2.0
 */
public abstract class ChangeableRpc<T extends Identifiable<ID>, ID extends Serializable> extends BaseRpc<T, ID> {

    /**
     * Empty constructor for deserialization from XML/JSON.
     */
    protected ChangeableRpc() {
    }

    /**
     * Creates RPC from specified entity.
     *
     * @param entity The entity
     */
    protected ChangeableRpc(T entity) {
        super(entity);
    }

    /**
     * Validates RPC.
     *
     * @param errors Binding/validation errors
     * @param updateEntity {@code null} if new entity is created otherwise entity that is updated
     * @throws ValidationException when there is error in input data
     */
    private void doValidation(BindingResult errors, @Nullable T updateEntity) throws ValidationException {
        Assert.notNull(errors, "errors can not be null");

        try {
            validate(errors, updateEntity);
        } catch (Exception ex) {
            if (!(ex instanceof ValidationException)) {
                // no validation exception => wrap it by ValidationException
                throw new IllegalDataException("validation of the following object failed: " + toString(), ex);
            }
        }

        if (errors.hasErrors()) {
            throw new InputValidationException(errors);
        }
    }

    /**
     * Validates RPC (mandatory attributes, correct object state etc.).
     * <p>
     * There are the following possibilities how to make validation:
     * <ul>
     *     <li>add errors to {@link BindingResult errors} (e.g. use {@link ValidationUtils}).
     *          If it has errors then {@link InputValidationException} will be thrown
     *     <li>throw {@link ValidationException} itself (e.g. use {@link Constraints} for asserts)
     *     <li>if exception is thrown and it's not type of {@link ValidationException} then
     *          original exception will be wrapped by {@link IllegalDataException}
     *     <li>use <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#validation-beanvalidation-overview">
     *         JSR-303 Bean Validation API</a>
     * </ul>
     *
     * @param errors Binding/validation errors
     * @param updateEntity {@code null} if new entity is created otherwise entity that is updated
     * @throws ValidationException when there is error in input data
     */
    protected abstract void validate(BindingResult errors, @Nullable T updateEntity) throws ValidationException;

    /**
     * Creates new entity instance from RPC.
     *
     * @param params Input parameters from client for creating new instance
     * @return new entity instance
     */
    protected abstract T createEntityInstance(Map<String, ?> params);

    /**
     * Creates new entity from RPC.
     *
     * @return new entity
     */
    public final T createEntity() {
        return createEntity(createBeanPropertyBindingResult(), new HashMap<>());
    }

    /**
   	 * Create the {@link AbstractPropertyBindingResult} instance using standard JavaBean property access.
   	 */
   	protected AbstractPropertyBindingResult createBeanPropertyBindingResult() {
   	    return new BeanPropertyBindingResult(this, getObjectName());
    }

    /**
     * Gets object's name.
     * Override it or {@link Class#getSimpleName()} simple class name} will be returned.
     *
     * @return object's name
     */
    @JsonIgnore
    public String getObjectName() {
        String name = this.getClass().getSimpleName();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Creates new entity from RPC.
     *
     * @param bindingResult Binding results
     * @return new entity
     */
    public final T createEntity(BindingResult bindingResult) {
        return createEntity(bindingResult, new HashMap<>());
    }

    /**
     * Creates new entity from RPC.
     *
     * @param bindingResult Binding results
     * @param params Input parameters from client for creating new instance
     * @return new entity
     */
    public final T createEntity(BindingResult bindingResult, Map<String, ?> params) {
        Assert.notNull(bindingResult, "bindingResult can not be null");
        Assert.notNull(params, "params can not be null");

        doValidation(bindingResult,null);

        T entity = createEntityInstance(params);

        Assert.notNull(entity, "entity can not be null");

        updateAttributes(entity, true);

        return entity;
    }

    /**
     * Updates existing entity from RPC.
     *
     * @param entity The existing entity
     */
    public final void updateEntity(T entity) {
        updateEntity(entity, createBeanPropertyBindingResult());
    }

    /**
     * Updates existing entity from RPC.
     *
     * @param entity The existing entity
     * @param bindingResult Binding results
     */
    public final void updateEntity(T entity, BindingResult bindingResult) {
        Assert.notNull(bindingResult, "bindingResult can not be null");
        Assert.notNull(entity, "entity can not be null");

        doValidation(bindingResult, entity);

        updateAttributes(entity, false);
    }

    /**
     * Updates attributes of specified entity from RPC values.
     *
     * @param entity The entity
     * @param created {@code true} if new entity is created or {@code false} if entity is updated
     */
    protected void updateAttributes(T entity, boolean created) {
        // nothing to implement by default
    }
}
