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

package org.openhubframework.openhub.common.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;


/**
 * OpenHub {@link DataSource} Configuration.
 * <p>
 * Abstract class that provides configuration capability of {@link DataSource} by {@link #dataSource()}.
 * </p>
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public abstract class AbstractDatabaseConfig {

    @Autowired(required = false)
    private MBeanExporter mbeanExporter;

    /**
     * Return instance of {@link DataSource} used in the application.
     *
     * @return the initialized DataSource.
     */
    public abstract DataSource dataSource();

    /**
     * Exclude candidate bean from {@link MBeanExporter} to auto-scan the instance and export it via JMX.
     *
     * @param candidate candidate object
     * @param beanName bean name
     */
    protected final void excludeMBeanIfNecessary(Object candidate, String beanName) {
        if (this.mbeanExporter != null && JmxUtils.isMBean(candidate.getClass())) {
            this.mbeanExporter.addExcludedBean(beanName);
        }
    }
}
