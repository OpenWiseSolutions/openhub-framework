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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 * Configuration holder for {@link LogParser}
 */
public class LogParserConfig {

    private DateTime fromDate;
    private Integer limit;
    private Map<String, String> filter;
    private List<String> groupBy;
    private Integer groupLimit;
    private String msg;

    public LogParserConfig() {
        setFromDate(DateTime.now());
        setLimit(LogParserConstants.MAX_RESULT_LIMIT);
        setFilter(Collections.<String, String>emptyMap());
        setGroupBy(Collections.<String>emptySet());
        setGroupLimit(null);
        setMsg(null);
    }

    public LogEvent createLogEvent() {
        return new LogEvent(this);
    }

    /** the date to find log lines after */
    public DateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(DateTime fromDate) {
        this.fromDate = fromDate;
    }

    /** the limit of how many lines should be returned */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * The Property=Value sets that the specified log event should have in order to not be ignored.
     */
    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        if (filter == null) {
            this.filter = Collections.emptyMap();
        } else {
            this.filter = new LinkedHashMap<String, String>(filter);
            this.filter.keySet().retainAll(getPropertyNames());
        }
    }

    /**
     * The property names that results will be grouped by.
     */
    public List<String> getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Collection<String> groupBy) {
        if (groupBy == null) {
            this.groupBy = Collections.emptyList();
        } else {
            this.groupBy = new ArrayList<String>(getPropertyNames());
            this.groupBy.retainAll(groupBy);
        }
    }

    /** max number of lines to return for each group */
    public Integer getGroupLimit() {
        return groupLimit;
    }

    public void setGroupLimit(Integer groupLimit) {
        this.groupLimit = groupLimit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fromDate", fromDate)
                .append("limit", limit)
                .append("filter", filter)
                .append("groupBy", groupBy)
                .append("groupLimit", groupLimit)
                .toString();
    }

    public String describe() {
        return String.format("first %s log lines with up to %s per group, after %s, grouped by %s, with properties %s",
                limit, groupLimit, fromDate, groupBy, filter);
    }

    public Pattern getDatePattern() {
        return LogParserConstants.LOG_LINE_DATE_PATTERN;
    }

    public DateTimeFormatter getDateFormat() {
        return LogParserConstants.LOGBACK_ISO8601_OPTIONAL_TIME_FORMAT;
    }

    public Pattern getPropertiesPattern() {
        return LogParserConstants.LOG_LINE_PROPERTIES_PATTERN;
    }

    public int getPropertyCount() {
        return getPropertyNames().size();
    }

    public List<String> getPropertyNames() {
        return LogParserConstants.LOG_LINE_PROPERTIES;
    }

    public boolean isMatchesFilter(String propertyValue, int propertyIndex) {
        String expectedValue = filter.get(getPropertyNames().get(propertyIndex));
        return expectedValue == null || StringUtils.containsIgnoreCase(propertyValue, expectedValue);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
