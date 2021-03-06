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

package org.openhubframework.openhub.test.data;

import org.openhubframework.openhub.api.entity.ServiceExtEnum;


/**
 * Enumeration of possible services (aka modules) - for test purposes only.
 *
 * @author Petr Juza
 */
public enum ServiceTestEnum implements ServiceExtEnum {

    ACCOUNT,

    CUSTOMER;

    @Override
    public String getServiceName() {
        return name();
    }
}

