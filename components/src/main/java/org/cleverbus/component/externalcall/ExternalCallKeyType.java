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

package org.cleverbus.component.externalcall;

import org.cleverbus.api.asynch.AsynchConstants;
import org.cleverbus.api.entity.EntityTypeExtEnum;
import org.cleverbus.api.entity.Message;
import org.cleverbus.api.extcall.ExtCallComponentParams;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.support.ExpressionAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * A type of the key to use. See {@link ExternalCallComponent} for usage.
 */
public enum ExternalCallKeyType {
    /**
     * A key will be generated based on the message source system and correlation ID,
     * protecting external call from duplication, but not from obsolete calls.
     * <p/>
     * {@link ExtCallComponentParams#EXTERNAL_CALL_KEY} property will be appended, if specified.
     */
    MESSAGE(new ExpressionAdapter() {
        @Override
        public Object evaluate(Exchange exchange) {
            Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
            Assert.notNull(msg, "Message must be provided in header " + AsynchConstants.MSG_HEADER);
            Assert.notNull(msg.getSourceSystem(), "Message must have Source System");
            Assert.notNull(msg.getCorrelationId(), "Message must have Correlation ID");

            String key = msg.getSourceSystem().getSystemName() + KEY_SEPARATOR + msg.getCorrelationId();
            Object keySuffix = exchange.getProperty(ExtCallComponentParams.EXTERNAL_CALL_KEY, Object.class);
            if (keySuffix != null && StringUtils.hasText(keySuffix.toString())) {
                key += KEY_SEPARATOR + keySuffix.toString();
            }

            return key;
        }
    }),

    /**
     * A key will be generated based on the message object ID (and possibly entity type, if specified),
     * protecting external call from both duplication and obsolete calls,
     * but also possibly unnecessarily skipping calls, if object ID is specified incorrectly.
     * <p/>
     * {@link ExtCallComponentParams#EXTERNAL_CALL_KEY} property will be appended, if specified.
     */
    ENTITY(new ExpressionAdapter() {
        @Override
        public Object evaluate(Exchange exchange) {
            Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
            Assert.notNull(msg, "Message must be provided in header " + AsynchConstants.MSG_HEADER);
            Assert.notNull(msg.getObjectId(), "Message must have Object ID");

            String key = msg.getObjectId();
            EntityTypeExtEnum keyPrefix = msg.getEntityType();
            if (keyPrefix != null) {
                key = keyPrefix.getEntityType() + KEY_SEPARATOR + key;
            }
            Object keySuffix = exchange.getProperty(ExtCallComponentParams.EXTERNAL_CALL_KEY, Object.class);
            if (keySuffix != null && StringUtils.hasText(keySuffix.toString())) {
                key += KEY_SEPARATOR + keySuffix.toString();
            }

            return key;
        }
    }),

    /**
     * A key will not be generated.
     * Instead it will be taken from {@link ExtCallComponentParams#EXTERNAL_CALL_KEY} property as is.
     */
    CUSTOM(new ExpressionAdapter() {
        @Override
        public Object evaluate(Exchange exchange) {
            Object key = exchange.getProperty(ExtCallComponentParams.EXTERNAL_CALL_KEY, Object.class);
            Assert.notNull(key, "External Call Key must be provided in property " + ExtCallComponentParams.EXTERNAL_CALL_KEY);
            return key.toString();
        }
    });

    private static final String KEY_SEPARATOR = "_";

    private final Expression keyExpression;

    ExternalCallKeyType(Expression keyExpression) {
        this.keyExpression = keyExpression;
    }

    /**
     * @return the expression that will generate this type of the key from an exchange
     */
    public Expression getExpression() {
        return keyExpression;
    }
}
