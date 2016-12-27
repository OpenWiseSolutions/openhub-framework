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

package org.openhubframework.openhub.modules;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


/**
 * Module "example" properties.
 *
 * @author <a href="mailto:petr.juza@openwise.cz">Petr Juza</a>
 * @since 2.0
 */
@Component
@ConfigurationProperties(prefix = "module.example")
@Profile(ExampleProperties.EXAMPLE_PROFILE)
public class ExampleProperties {

    /**
     * Spring profile for module "example".
     */
    public static final String EXAMPLE_PROFILE = "example-module";

}
