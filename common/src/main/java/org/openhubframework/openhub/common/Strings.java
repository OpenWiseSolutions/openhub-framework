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

package org.openhubframework.openhub.common;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;


/**
 * The common {@link String} utilities.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
public final class Strings {

    private Strings() {
    }

    /**
     * Substitutes all places of the message using a values. See {@link MessageFormatter} for more detail.
     *
     * @param message the message with places defined as {}
     * @param values an array of values that are used to substitute formatting places of the message
     * @return the final string
     */
    public static String fm(String message, Object... values) {
        if (StringUtils.isBlank(message) || ArrayUtils.isEmpty(values)) {
            return message;
        }

        return MessageFormatter.arrayFormat(message, values).getMessage();
    }
}
