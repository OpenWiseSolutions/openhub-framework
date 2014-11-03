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

package org.cleverbus.core.common.directcall;

import java.io.IOException;


/**
 * Contract for calling external system with assistance of internal route.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 * @see DirectCallRegistry
 */
public interface DirectCall {

    /**
     * Makes call to external system with assistance of internal route.
     *
     * @param callId the unique call identifier
     * @return response
     * @throws IOException when error occurred during communication with external system
     */
    String makeCall(String callId) throws IOException;

}
