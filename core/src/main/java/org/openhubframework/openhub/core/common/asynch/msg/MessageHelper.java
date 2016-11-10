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

package org.openhubframework.openhub.core.common.asynch.msg;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.common.log.Log;

import org.apache.commons.lang.StringUtils;

/**
 * Helper class for message manipulation.
 */
//TODO (juza) applicant for moving to API
public final class MessageHelper {

    private MessageHelper() {
    }

    /**
     * Checks the properties for those that end with {@link AsynchConstants#BUSINESS_ERROR_PROP_SUFFIX}
     * and moves the found exceptions to the Message's business error list, subsequently returning it.
     * <p/>
     * After this operation succeeds, there will be no more properties with the specified suffix,
     * and the Message will contain all the exception messages that were previously in these properties.
     *
     * @param msg the message to collect and set business errors for
     * @param properties the exchange properties, possibly with some new business errors
     */
    public static void updateBusinessErrors(Message msg, Map<String, Object> properties) {
        List<String> errorList = new ArrayList<String>();
        errorList.addAll(msg.getBusinessErrorList());

        List<String> errorProps = new LinkedList<String>();
        for (String property : properties.keySet()) {
            if (property.endsWith(AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX)) {
                errorProps.add(property);
            }
        }

        for (String errorProp : errorProps) {
            Object businessError = properties.remove(errorProp);
            if (businessError instanceof Exception) {
                Exception ex = (Exception) businessError;
                errorList.add(ex.getMessage());
            } else {
                Log.error("Property with suffix " + AsynchConstants.BUSINESS_ERROR_PROP_SUFFIX
                        + " isn't of type Exception");
            }
        }

        String businessErrors = StringUtils.join(errorList, Message.ERR_DESC_SEPARATOR);
        msg.setBusinessError(businessErrors);
    }
}
