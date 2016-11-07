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

package org.openhubframework.openhub.core.common.ws;

import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.core.common.exception.ExceptionTranslator;
import org.openhubframework.openhub.core.common.ws.component.ErrorAwareWebServiceMessageReceiverHandlerAdapter;

import org.springframework.ws.InvalidXmlException;


/**
 * Extension error handler that adds {@link InternalErrorEnum} into fault message.
 *
 * @author Petr Juza
 */
public class ErrorCodeAwareWebServiceMessageReceiverHandlerAdapter
        extends ErrorAwareWebServiceMessageReceiverHandlerAdapter {

    @Override
    protected String getFaultString(InvalidXmlException ex) {
        return ExceptionTranslator.composeErrorMessage(InternalErrorEnum.E111, ex);
    }
}
