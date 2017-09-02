/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin.web.common;

import java.beans.PropertyEditorSupport;
import java.text.Format;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Customization of {@link WebDataBinder}.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
@ControllerAdvice
public class ControllerAdviceInitBinder {

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        // due to JSR-303/JSR-349 Bean Validation we have to register custom editors for Java8 date/time types
        webDataBinder.registerCustomEditor(
                Instant.class,
                new Editor<>(
                        Instant::parse,
                        DateTimeFormatter.ISO_INSTANT.toFormat()));

        webDataBinder.registerCustomEditor(
                LocalDate.class,
                new Editor<>(
                        text -> LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE),
                        DateTimeFormatter.ISO_LOCAL_DATE.toFormat()));

        webDataBinder.registerCustomEditor(
                LocalDateTime.class,
                new Editor<>(
                        text -> LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                LocalTime.class,
                new Editor<>(
                        text -> LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME),
                        DateTimeFormatter.ISO_LOCAL_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                OffsetDateTime.class,
                new Editor<>(
                        text -> OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                OffsetTime.class,
                new Editor<>(
                        text -> OffsetTime.parse(text, DateTimeFormatter.ISO_OFFSET_TIME),
                        DateTimeFormatter.ISO_OFFSET_TIME.toFormat()));

        webDataBinder.registerCustomEditor(
                ZonedDateTime.class,
                new Editor<>(
                        text -> ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME),
                        DateTimeFormatter.ISO_ZONED_DATE_TIME.toFormat()));
    }

    private static class Editor<T> extends PropertyEditorSupport {

        private final Function<String, T> parser;
        private final Format format;

        Editor(Function<String, T> parser, Format format) {

            this.parser = parser;
            this.format = format;
        }

        @Override
        public void setAsText(String text) {
            setValue(this.parser.apply(text));
        }

        @Override
        public String getAsText() {
            return format.format(getValue());
        }
    }
}
