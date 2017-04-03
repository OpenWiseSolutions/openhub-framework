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

import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.core.runtime.Assert;
import org.springframework.core.convert.converter.Converter;

import org.openhubframework.openhub.admin.web.common.rpc.BaseRpc;
import org.openhubframework.openhub.api.configuration.DbConfigurationParam;
import org.openhubframework.openhub.api.exception.ValidationIntegrationException;


/**
 * RPC of {@link DbConfigurationParam} entity.
 *
 * @author Petr Juza
 * @since 2.0
 */
@XmlRootElement
public class DbConfigurationParamRpc extends BaseRpc<DbConfigurationParam, String> {

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
    private String currentValue;

    /**
     * defaultValue: true (string) - default value (used if current value is not filled)
     */
    private String defaultValue;

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
        this.currentValue = entity.getCurrentValue();
        this.defaultValue = entity.getDefaultValue();
        this.dataType = entity.getDataType().name();
        this.mandatory = entity.isMandatory();
        this.description = entity.getDescription();
        this.validationRegEx = entity.getValidationRegEx();
    }

    @Override
    protected void validate() throws ValidationIntegrationException {
        //TODO PJUZA make validation
    }

    @Override
    protected DbConfigurationParam createEntityInstance(Map<String, ?> params) {
        throw new UnsupportedOperationException("Configuration parameter can be updated only, not created");
    }

    @Override
    protected void updateAttributes(DbConfigurationParam param, boolean created) {
        super.updateAttributes(param, created);

        Assert.isTrue(!created, "Configuration parameter can be updated only");

        param.setCurrentValue(getCurrentValue());
        param.setDefaultValue(getDefaultValue());
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
    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(@Nullable String currentValue) {
        this.currentValue = currentValue;
    }

    @Nullable
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(@Nullable String defaultValue) {
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
