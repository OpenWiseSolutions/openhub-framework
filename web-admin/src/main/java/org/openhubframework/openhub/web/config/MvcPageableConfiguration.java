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

package org.openhubframework.openhub.web.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;

import org.openhubframework.openhub.admin.web.common.rpc.paging.OffsetBasedPagingProperties;
import org.openhubframework.openhub.common.AutoConfiguration;


/**
 * Spring configuration to pageable support.
 *
 * @author <a href="mailto:tomas.hanus@openwise.cz">Tomas Hanus</a>
 * @since 2.0
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
@AutoConfiguration
public class MvcPageableConfiguration {

    @Autowired
    private OffsetBasedPagingProperties pagingProperties;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableResolver;

    @Autowired
    private SortHandlerMethodArgumentResolver sortResolver;

    @PostConstruct
    public void configureResolvers() {
        // correct behaviour is: fallback page number must be 0 at all
        pageableResolver.setFallbackPageable(new PageRequest(0, pagingProperties.getMaxPageSize()));
        pageableResolver.setOneIndexedParameters(pagingProperties.getOneBased());
        pageableResolver.setMaxPageSize(pagingProperties.getMaxPageSize());
        pageableResolver.setPageParameterName(pagingProperties.getPageParamName());
        pageableResolver.setSizeParameterName(pagingProperties.getSizeParamName());

        sortResolver.setSortParameter(pagingProperties.getSortParamName());
    }
}
