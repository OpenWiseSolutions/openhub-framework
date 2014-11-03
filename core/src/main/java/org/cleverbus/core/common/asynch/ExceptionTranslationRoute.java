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

package org.cleverbus.core.common.asynch;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.route.AbstractBasicRoute;
import org.cleverbus.api.route.CamelConfiguration;
import org.cleverbus.core.common.exception.ExceptionTranslator;


/**
 * Route definition that defines route for exception translation.
 * See {@link ExceptionTranslator} for more details.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@CamelConfiguration(value = ExceptionTranslationRoute.ROUTE_BEAN)
public class ExceptionTranslationRoute extends AbstractBasicRoute {

    public static final String ROUTE_BEAN = "exTranslatorRouteBean";

    /**
     * Route for processing FATAL error.
     */
    public static final String ROUTE_ID_EX_TRANSLATION = "exceptionTranslation" + ROUTE_SUFFIX;

    @Override
    protected void doConfigure() throws Exception {

        from(AsynchConstants.URI_EX_TRANSLATION)
                .routeId(ROUTE_ID_EX_TRANSLATION)

                .errorHandler(noErrorHandler())

                .process(ExceptionTranslator.getInstance());
    }
}
