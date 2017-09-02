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

package org.openhubframework.openhub.admin.web.common.rpc.paging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * Offset-based paging properties.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.paging.offset-based")
public class OffsetBasedPagingProperties {

    private Boolean oneBased;

    private Integer maxPageSize;

    private String pageParamName;

    private String sizeParamName;

    private String sortParamName;

    public Boolean getOneBased() {
        return oneBased;
    }

    public void setOneBased(Boolean oneBased) {
        this.oneBased = oneBased;
    }

    public Integer getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(Integer maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public String getPageParamName() {
        return pageParamName;
    }

    public void setPageParamName(String pageParamName) {
        this.pageParamName = pageParamName;
    }

    public String getSizeParamName() {
        return sizeParamName;
    }

    public void setSizeParamName(String sizeParamName) {
        this.sizeParamName = sizeParamName;
    }

    public String getSortParamName() {
        return sortParamName;
    }

    public void setSortParamName(String sortParamName) {
        this.sortParamName = sortParamName;
    }
}
