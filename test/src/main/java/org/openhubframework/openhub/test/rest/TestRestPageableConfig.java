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

package org.openhubframework.openhub.test.rest;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * <strong>Test</strong> Spring configuration to pageable support.
 *
 * @author Petr Juza
 * @since 2.0
 * @see org.springframework.data.web.config.SpringDataWebConfiguration#addArgumentResolvers(List)
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
@EnableSpringDataWebSupport
public class TestRestPageableConfig extends WebMvcConfigurationSupport {

    final static Boolean ONE_BASED_PAGINATION = Boolean.TRUE;

    final static int MAX_PAGE_SIZE = 100;

    final static String PAGE_PARAMETER_NAME = "p";

    final static String SIZE_PARAMETER_NAME = "s";

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableResolver;

    @PostConstruct
    public void configurePageableResolver() {
        // correct behaviour is: fallback page number must be 0 at all
        pageableResolver.setFallbackPageable(new PageRequest(0, MAX_PAGE_SIZE));
        pageableResolver.setOneIndexedParameters(ONE_BASED_PAGINATION);
        pageableResolver.setMaxPageSize(MAX_PAGE_SIZE);
        pageableResolver.setPageParameterName(PAGE_PARAMETER_NAME);
        pageableResolver.setSizeParameterName(SIZE_PARAMETER_NAME);
    }

    /**
     * Formats the {@code GET} query based upon configuration.
     * <p/>
     * <pre>{@code
     *  createPageQuery(1,20) &rarr; p=1&s=20
     * }</pre>
     *
     * @param page the page number
     * @param size the size of page
     * @return the query
     */
    public static String createPageQuery(int page, int size) {
        return PAGE_PARAMETER_NAME + "=" + page + "&" + SIZE_PARAMETER_NAME + "=" + size;
    }

    /**
     * Creates {@link Pageable} object based upon configuration.
     *
     * @param page the number of page
     * @param size the size of page
     * @return the {@link Pageable}
     */
    public static Pageable createPage(int page, int size) {
        return new PageRequest(ONE_BASED_PAGINATION ? page - 1 : page, size);
    }
}
