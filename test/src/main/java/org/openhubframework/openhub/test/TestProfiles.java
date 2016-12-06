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

package org.openhubframework.openhub.test;

import org.springframework.test.annotation.IfProfileValue;


/**
 * Defines test profiles which are useful when specific test runs with specific target environment
 * (=specific target system).
 * <p/>
 * When you want to run some test for specific environment, then you have to run VM with system parameter
 * for target environment, e.g. {@code -Dintegration=ALL}
 *
 * @author Petr Juza
 * @see IfProfileValue
 */
public abstract class TestProfiles {

    // -- profile names

    /**
     * Integration tests (= tests which depends on external systems).
     */
    public static final String INTEGRATION = "integration";


    // -- profile values

    /**
     * All types of integration tests.
     */
    public static final String INTEGRATION_ALL = "ALL";


    private TestProfiles() {
    }
}
