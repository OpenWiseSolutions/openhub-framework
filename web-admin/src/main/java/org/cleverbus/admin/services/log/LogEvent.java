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

package org.cleverbus.admin.services.log;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.util.Assert;

public class LogEvent {

    private final LogParserConfig config;

    private DateTime date;
    private String message;
    private final Object[] properties;

    public LogEvent(LogParserConfig config) {
        Assert.notNull(config);
        this.config = config;
        this.properties = new Object[config.getPropertyCount()];
    }

    public LogParserConfig getConfig() {
        return config;
    }

    public List<String> getPropertyNames() {
        return config.getPropertyNames();
    }

    public int getPropertyCount() {
        return getPropertyNames().size();
    }

    public Object[] getProperties() {
        return properties;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void appendMessage(String message) {
        this.message += message;
    }

}
