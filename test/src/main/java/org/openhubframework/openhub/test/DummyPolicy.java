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

import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.Policy;
import org.apache.camel.spi.RouteContext;


/**
 * Dummy policy implementation that nothing to do.
 * This class is handy for testing routes with reference to any policy, e.g. authorization policy.
 *
 * @author Petr Juza
 */
public class DummyPolicy implements Policy {

    @Override
    public void beforeWrap(RouteContext routeContext, ProcessorDefinition<?> processorDefinition) {
        // nothing to do
    }

    @Override
    public Processor wrap(RouteContext routeContext, Processor processor) {
        return processor;
    }
}
