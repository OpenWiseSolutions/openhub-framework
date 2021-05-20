/*
 * Copyright 2002-2021 the original author or authors.
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

package org.openhubframework.openhub.admin.web.common;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;


/**
 * Parent class for all controllers. This class contains basic functionality for all controllers.
 *
 * @author Petr Juza
 * @since 2.0
 */
public abstract class AbstractOhfController {

    public static final String BASE_PATH = "/api";

    @Autowired
    private MessageSource messageSource;

    /**
     * Gets {@link MessageSource}.
     *
     * @return MessageSource
     */
    protected final MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Gets localization message from {@link LocaleContextHolder#getLocale() selected language}.
     *
     * @param code The code message
     * @param args Message arguments
     * @return message
     */
    protected String getMessage(String code, Object... args) {
        Assert.hasText(code, "code must not be empty");

        return getMessageSource().getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Gets localization message in {@link Locale#ENGLISH}.
     *
     * @param code The code message
     * @param args Message arguments
     * @return message in {@link Locale#ENGLISH}
     */
    protected String getMessageEN(String code, Object... args) {
        Assert.hasText(code, "code must not be empty");

        return getMessageSource().getMessage(code, args, Locale.ENGLISH);
    }

    /**
     * Gets sublist of input list for specified page.
     *
     * @param list      The list
     * @param pageable  The page information
     * @return sublist for the page
     */
    protected static <T> List<T> pageContentList(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        return list.subList(start, end);
    }
}
