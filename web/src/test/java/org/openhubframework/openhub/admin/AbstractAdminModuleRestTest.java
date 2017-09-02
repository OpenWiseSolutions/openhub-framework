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

package org.openhubframework.openhub.admin;

import org.springframework.test.context.ContextConfiguration;

import org.openhubframework.openhub.admin.web.common.AbstractOhfController;
import org.openhubframework.openhub.test.rest.AbstractRestTest;


/**
 * Parent class for all tests in this module based on REST and DB.
 *
 * @author Petr Juza
 * @since 2.0
 */
@ContextConfiguration(classes = AdminTestConfig.class)
public abstract class AbstractAdminModuleRestTest extends AbstractRestTest {

    protected static final String BASE_URI = AbstractOhfController.BASE_PATH;

}
