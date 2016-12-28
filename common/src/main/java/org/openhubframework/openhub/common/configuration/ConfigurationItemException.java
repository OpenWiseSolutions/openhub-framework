/*
 * Copyright 2016 the original author or authors.
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

package org.openhubframework.openhub.common.configuration;

/**
 * Configuration item exception that reflects error state of configuration item.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
//TODO (thanus, 18/12/2016, TASK: OHFJIRA-9) maybe it could extends ConfigurationException
public class ConfigurationItemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String key;

    /**
     * Constructs a new configuration exception for the key with the specified detail message.
     *
     * @param key the key value - parameter name.
     */
    public ConfigurationItemException(String key) {
        this.key = key;
    }

    /**
     * Constructs a new configuration exception for the key with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     * @param key     the key value - parameter name.
     */
    public ConfigurationItemException(String message, String key) {
        super(message);
        this.key = key;
    }

    /**
     * Constructs a new configuration exception for the key with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated
     * in this configuration exception's detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param key     the key value - parameter name.
     */
    public ConfigurationItemException(String message, Throwable cause, String key) {
        super(message, cause);
        this.key = key;
    }

    /**
     * Constructs a new configuration exception for the key with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated
     * in this configuration exception's detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param key   the key value - parameter name.
     */
    public ConfigurationItemException(Throwable cause, String key) {
        super(cause);
        this.key = key;
    }

    /**
     * Gets the {@code key} that represented unique ID for configuration value.
     *
     * @return key which is misconfigured
     */
    public String getKey() {
        return key;
    }
}
