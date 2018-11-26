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

package org.openhubframework.openhub.admin.web.configuration.rpc;

import static org.springframework.util.StringUtils.hasText;

import java.util.Map;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;

import org.openhubframework.openhub.admin.web.common.rpc.ChangeableRpc;
import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.exception.validation.ValidationException;


/**
 * RPC of {@link DbConfigurationParam} entity.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Validated
public class DbConfigurationParamRpc extends ChangeableRpc<DbConfigurationParam, String> {

    /**
     * Converts {@link DbConfigurationParam} to {@link DbConfigurationParamRpc}.
     */
    public static class DbConfigurationParamConverter implements Converter<DbConfigurationParam, DbConfigurationParamRpc> {

        @Override
        public DbConfigurationParamRpc convert(DbConfigurationParam expr) {
            return new DbConfigurationParamRpc(expr);
        }
    }

    /**
     * code: ohf.server.localhostUri.check (string) - unique code of one configuration parameter
     */
    private String code;

    /**
     * categoryCode: core.server (string) - unique code for specific configuration scope/category
     */
    private String categoryCode;

    /**
     * currentValue: true (string) - current value
     */
    private Object currentValue;

    /**
     * defaultValue: true (string) - default value (used if current value is not filled)
     */
    private Object defaultValue;

    /**
     * dataType (enum[string]): data type of current and default value
         + Members
             + `STRING`,
             + `INT`
             + `FLOAT`
             + `DATE`
             + `BOOL`
             + `FILE`
     */
    @NotNull
    private String dataType;

    /**
     * mandatory: true (boolean) - Is this configuration parameter mandatory?
     * In other worlds must be at least one current or default value defined?
     */
    private boolean mandatory;

    /**
     * description: enable/disable checking of localhostUri (string) - description of the parameter
     */
    private String description;

    /**
     * validationRegEx: `^(spring-ws|servlet).*$` (string)
     * - regular expression for checking if current or default value is valid
     */
    private String validationRegEx;

    protected DbConfigurationParamRpc() {
        // for XML deserialization
    }

    public DbConfigurationParamRpc(DbConfigurationParam entity) {
        super(entity);

        this.code = entity.getCode();
        this.categoryCode = entity.getCategoryCode();
        this.currentValue = entity.getCurrentValueAsObject();
        this.defaultValue = entity.getDefaultValueAsObject();
        this.dataType = entity.getDataType().name();
        this.mandatory = entity.isMandatory();
        this.description = entity.getDescription();
        this.validationRegEx = entity.getValidationRegEx();
    }

    @Override
    protected void validate(BindingResult errors, @Nullable DbConfigurationParam updateEntity) throws ValidationException {
        if (updateEntity != null) {
            ValidationUtils.rejectIfEmpty(errors, "code", "field.required");
            if (hasText(getCode())) {
                Constraints.state(getCode().equals(updateEntity.getCode()), "codes must be equal");
            }
        }
    }

    @Override
    protected DbConfigurationParam createEntityInstance(Map<String, ?> params) {
        throw new UnsupportedOperationException("Configuration parameter can be updated only, not created");
    }

    @Override
    protected void updateAttributes(DbConfigurationParam param, boolean created) {
        super.updateAttributes(param, created);

        Assert.isTrue(!created, "Configuration parameter can be updated only");

        param.setCurrentValueAsObject(getCurrentValue());
        param.setDefaultValueAsObject(getDefaultValue());
        param.setValidationRegEx(getValidationRegEx());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    @Nullable
    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(@Nullable Object currentValue) {
        this.currentValue = currentValue;
    }

    @Nullable
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(@Nullable Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public String getValidationRegEx() {
        return validationRegEx;
    }

    public void setValidationRegEx(@Nullable String validationRegEx) {
        this.validationRegEx = validationRegEx;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("code", code)
                .append("categoryCode", categoryCode)
                .append("currentValue", currentValue)
                .append("defaultValue", defaultValue)
                .append("dataType", dataType)
                .append("mandatory", mandatory)
                .append("description", description)
                .append("validationRegEx", validationRegEx)
                .toString();
    }
}
