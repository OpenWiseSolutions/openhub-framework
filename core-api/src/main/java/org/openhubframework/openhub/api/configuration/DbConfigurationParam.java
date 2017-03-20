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

package org.openhubframework.openhub.api.configuration;

import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import org.openhubframework.openhub.api.common.Constraints;
import org.openhubframework.openhub.api.entity.SuperEntity;
import org.openhubframework.openhub.api.exception.ConfigurationException;
import org.openhubframework.openhub.common.Tools;


/**
 * Entity represents one configuration parameter in database.
 * </p>
 * Call {@link #checkConsistency(ConversionService)} to check that parameter is consistent state.
 *
 * @author Petr Juza
 * @since 2.0
 */
@Entity
@Table(name = "configuration_item")
public class DbConfigurationParam extends SuperEntity<String> {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "category_code", length = 100, nullable = false)
    private String categoryCode;

    @Column(name = "current_value", length = 1000)
    private String currentValue;

    @Column(name = "default_value", length = 1000)
    private String defaultValue;

    @Column(name = "data_type", length = 20, nullable = false)
    @Access(AccessType.PROPERTY)
    private String dataTypeInternal;

    @Transient
    private DataTypeEnum dataType;

    @Column(name = "mandatory")
    private boolean mandatory;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "validation", length = 100)
    private String validationRegEx;


    /** Default public constructor. */
    public DbConfigurationParam() {
        super(null);
    }

    /**
     * Creates new configuration parameter.
     *
     * @param code           Unique code of one configuration item, e.g. ohf.asyncThread.processing.count.name
     * @param description    Description of the parameter (in english).
     * @param categoryCode   Unique code for specific configuration scope, e.g. "dataSource" for data source settings
     * @param currentValue   Current (valid) value
     * @param defaultValue   Default value if there is no value defined
     * @param dataType       Data type of current and default value
     * @param mandatory      Is this configuration item mandatory?
     *                       In other worlds must be at least one current or default value defined?
     * @param validationRegEx     Regular expression for checking if current value or default value is valid
     */
    public DbConfigurationParam(String code, String description, String categoryCode, @Nullable String currentValue,
            @Nullable String defaultValue, DataTypeEnum dataType, boolean mandatory, @Nullable String validationRegEx) {
        super(code);

        Constraints.hasText(code, "code must not be empty");
        Constraints.hasText(description, "description must not be empty");
        Constraints.hasText(categoryCode, "categoryCode must not be empty");
        Constraints.notNull(dataType, "dataType must not be null");

        this.code = code;
        this.description = description;
        this.categoryCode = categoryCode;
        this.currentValue = currentValue;
        this.defaultValue = defaultValue;
        this.dataTypeInternal = dataType.name();
        this.dataType = dataType;
        this.mandatory = mandatory;
        this.validationRegEx = validationRegEx;
    }

    /**
     * Creates new configuration parameter.
     *
     * @param code           Unique code of one configuration item, e.g. ohf.asyncThread.processing.count.name
     * @param description    Description of the parameter (in english).
     * @param categoryCode   Unique code for specific configuration scope, e.g. "dataSource" for data source settings
     * @param currentValue   Current (valid) value
     * @param dataType       Data type of current and default value
     */
    public DbConfigurationParam(String code, String description, String categoryCode, @Nullable String currentValue,
            DataTypeEnum dataType) {
        this(code, description, categoryCode, currentValue, null, dataType, false, null);
    }

    public String getCode() {
        return code;
    }

    @Override
    public void setId(@Nullable String s) {
        throw new UnsupportedOperationException("it's not possible to change identifier of this entity: "
                + DbConfigurationParam.class.getName());
    }

    @Nullable
    @Override
    public String getId() {
        return getCode();
    }

    /**
     * Gets parameter description.
     *
     * @return parameter description
     */
    public String getDescription() {
        return description;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets unique code for specific configuration scope, e.g. "dataSource" for data source settings.
     *
     * @param categoryCode The code for specific configuration scope
     */
    public void setCategoryCode(String categoryCode) {
        Constraints.hasText(categoryCode, "categoryCode must not be empty");

        this.categoryCode = categoryCode;
    }

    /**
     * Gets value (current or default or even nothing).
     *
     * @return value
     */
    @Nullable
    public String getValue() {
        if (getCurrentValue() != null) {
            return getCurrentValue();
        } else if (getDefaultValue() != null) {
            return getDefaultValue();
        }

        return null;
    }

    /**
     * Checks consistency:
     * <ul>
     *     <li>if mandatory=true then current or default value must be defined
     *     <li>if validationRegEx is defined then current value and default value are validated
     *     <li>if current and default values can be converted to the target data type
     * </ul>
     * @param conversionService Conversion service
     * @throws ConfigurationException when checking consistency failed
     */
    public void checkConsistency(ConversionService conversionService) {
        // if mandatory=true then current or default value must be defined
        if (isMandatory() && StringUtils.isEmpty(getCurrentValue()) && StringUtils.isEmpty(getDefaultValue())) {
            throw new ConfigurationException("Param is mandatory but there is neither current value nor default value: "
                    + this, getCode());
        }

        // if validationRegEx is defined then current value and default value are validated
        if (StringUtils.isNotEmpty(validationRegEx)) {
            if (StringUtils.isNotEmpty(currentValue) && !currentValue.matches(validationRegEx)) {
                throw new ConfigurationException("Current value '" + currentValue
                        + "' does not matches regular expression '" + validationRegEx, getCode());
            }
            if (StringUtils.isNotEmpty(defaultValue) && !defaultValue.matches(validationRegEx)) {
                throw new ConfigurationException("Default value '" + defaultValue
                        + "' does not matches regular expression '" + validationRegEx, getCode());
            }
        }

        // if current and default values can be converted to the target data type
        if (StringUtils.isNotEmpty(getCurrentValue())) {
            try {
                if (!conversionService.canConvert(String.class, getDataType().getTypeClass())) {
                    throw new ConfigurationException(
                            Tools.fm("Current value '{}' can't be converted to target type {}",
                                    getCurrentValue(), getDataType().name()), getCode());
                }

                conversionService.convert(getCurrentValue(), getDataType().getTypeClass());
            } catch (ConversionException ex) {
                throw new ConfigurationException(
                        Tools.fm("Current value '{}' can't be converted to target type {}",
                                getCurrentValue(), getDataType().name()), ex, getCode());
            }
        }
        if (StringUtils.isNotEmpty(getDefaultValue())) {
            try {
                if (!conversionService.canConvert(String.class, getDataType().getTypeClass())) {
                    throw new ConfigurationException(
                            Tools.fm("Default value '{}' can't be converted to target type {}",
                                    getDefaultValue(), getDataType().name()), getCode());
                }

                conversionService.convert(getDefaultValue(), getDataType().getTypeClass());
            } catch (ConversionException ex) {
                throw new ConfigurationException(
                        Tools.fm("Default value '{}' can't be converted to target type {}",
                                getDefaultValue(), getDataType().name()), ex, getCode());
            }
        }
    }

    @Nullable
    public String getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets current (valid) value.
     *
     * @param currentValue The current value
     */
    public void setCurrentValue(@Nullable String currentValue) {
        this.currentValue = currentValue;
    }

    @Nullable
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets default value if there is no value defined.
     *
     * @param defaultValue The default value
     */
    public void setDefaultValue(@Nullable String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public DataTypeEnum getDataType() {
        return dataType;
    }

    /**
     * Sets data type of current and default value.
     *
     * @param dataType The data type
     */
    public void setDataType(DataTypeEnum dataType) {
        Constraints.notNull(dataType, "dataType must not be null");
        this.dataType = dataType;
        this.dataTypeInternal = dataType.name();
    }

    // for Hibernate access
    private String getDataTypeInternal() {
        return dataTypeInternal;
    }

    // for Hibernate access
    private void setDataTypeInternal(String dataTypeInternal) {
        Constraints.notNull(dataTypeInternal, "dataTypeInternal must not be null");
        this.dataTypeInternal = dataTypeInternal;
        this.dataType = DataTypeEnum.valueOf(dataTypeInternal);
    }

    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Sets if this configuration parameter is mandatory.
     *
     * @param mandatory {@code true} if configuration parameter is mandatory otherwise {@code false}
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Nullable
    public String getValidationRegEx() {
        return validationRegEx;
    }

    /**
     * Sets regular expression for checking if current value is valid.
     *
     * @param validationRegEx The regular expression
     * @throws PatternSyntaxException If the expression's syntax is invalid
     */
    public void setValidationRegEx(@Nullable String validationRegEx) {
        this.validationRegEx = validationRegEx;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof DbConfigurationParam && super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("code", code)
            .append("description", StringUtils.abbreviate(description, 30))
            .append("categoryCode", categoryCode)
            .append("currentValue", currentValue)
            .append("defaultValue", defaultValue)
            .append("dataType", dataType)
            .append("mandatory", mandatory)
            .append("validation", validationRegEx)
            .toString();
    }

    @Override
    public String toHumanString() {
        return "(code = " + code + ", currentValue = " + currentValue + ", defaultValue = " + defaultValue + ")";
    }
}
