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

package org.cleverbus.modules;

import org.cleverbus.core.common.asynch.ExceptionTranslationRoute;
import org.cleverbus.test.AbstractTest;
import org.cleverbus.test.ActiveRoutes;

import org.springframework.test.context.ContextConfiguration;


/**
 * Parent class for all tests in this modules with Apache Camel.
 * This parent class automatically registers all routes in "modules" package in Spring context.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@ActiveRoutes(classes = ExceptionTranslationRoute.class)
@ContextConfiguration(locations = {"classpath:/META-INF/test_modules_conf.xml"})
public abstract class AbstractModulesTest extends AbstractTest {

}
