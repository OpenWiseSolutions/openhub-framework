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

package org.openhubframework.openhub.spi.alerts;

/**
 * Listener notifies when any alert is activated.
 *
 * @author Petr Juza
 * @since 0.4
 */
public interface AlertListener {

    /**
     * Does this listener support the specified alert?
     *
     * @return {@code true} if specified alert is supported by this listener otherwise {@code false}
     * @see CoreAlertsEnum
     */
    boolean supports(AlertInfo alert);

    /**
     * Notifies that specified alert is activated.
     *
     * @param alert the alert
     * @param actualCount the actual count of items
     */
    void onAlert(AlertInfo alert, long actualCount);
}
