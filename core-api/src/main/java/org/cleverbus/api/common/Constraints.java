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

package org.cleverbus.api.common;


import org.cleverbus.api.exception.ErrorExtEnum;
import org.cleverbus.api.exception.IllegalDataException;
import org.cleverbus.api.exception.InternalErrorEnum;
import org.cleverbus.api.exception.ValidationIntegrationException;


/**
 * Assertion utility class that assists in validating arguments by throwing specific
 * {@link ValidationIntegrationException} exceptions.
 *
 * <p>Useful for identifying data integration errors early and clearly at runtime.
 *
 * <p>For example, if the contract of a public method states it does not
 * allow {@code null} arguments, Constraints can be used to validate that
 * contract. Doing this clearly indicates a contract violation when it
 * occurs and protects the class's invariants.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 */
public final class Constraints {

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalDataException occurs if {@code text} is null or does not contain nor non-whitespace
     *                              character.
     */
    public static void hasText(String text, String message) throws IllegalDataException {
        notNull(text, message);
        if (!org.springframework.util.StringUtils.hasText(text)) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     *
     * @param text      the String to check
     * @param message   the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalDataException occurs if {@code text} is null or does not contain nor non-whitespace
     *                              character.
     */
    public static void hasText(String text, String message, ErrorExtEnum errorCode) throws IllegalDataException {
        notNull(text, message);
        if (!org.springframework.util.StringUtils.hasText(text)) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull(Object object, String message, ErrorExtEnum errorCode) {
        if (object == null) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert that an object <strong>is</strong> {@code null}.
     * <pre class="code">Assert.isNull(clazz, "The class must be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert that an object <strong>is</strong> {@code null}.
     * <pre class="code">Assert.isNull(clazz, "The class must be null");</pre>
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String message, ErrorExtEnum errorCode) {
        if (object != null) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    /**
     * Assert a boolean expression, throwing {@code IllegalArgumentException}
     * if the test result is {@code false}.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(Boolean expression, String message) {
        notNull(expression, message);
        if (expression.booleanValue() != Boolean.TRUE) {
            throw new IllegalDataException(InternalErrorEnum.E109, message);
        }
    }

    /**
     * Assert a boolean expression, throwing {@code IllegalArgumentException}
     * if the test result is {@code false}.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @param errorCode the internal error code
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(Boolean expression, String message, ErrorExtEnum errorCode) {
        notNull(expression, message);
        if (expression.booleanValue() != Boolean.TRUE) {
            throw new IllegalDataException(errorCode, message);
        }
    }

    private Constraints() {
    }
}
