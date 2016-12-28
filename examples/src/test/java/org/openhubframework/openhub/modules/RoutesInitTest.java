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

package org.openhubframework.openhub.modules;

import org.junit.BeforeClass;
import org.junit.Test;

import org.openhubframework.openhub.test.route.ActiveRoutesCollector;


/**
 * Test that verifies if all Camel routes are correctly initialized - if there are unique route IDs and unique URIs.
 *
 * @author Petr Juza
 */
public class RoutesInitTest extends AbstractExampleModulesDbTest {

    @BeforeClass
    public static void setProperty() {
        System.setProperty(ActiveRoutesCollector.TEST_CAMEL_INIT_ALL_ROUTES, "true");
    }

    @Test
    public void testInit() {
        // nothing to do - if all routes are successfully initialized then test is OK
        System.out.println("All routes were successfully initialized");
    }
}
