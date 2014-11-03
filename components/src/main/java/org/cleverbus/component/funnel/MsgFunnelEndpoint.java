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

package org.cleverbus.component.funnel;

import org.cleverbus.api.entity.MsgStateEnum;
import org.cleverbus.spi.AsyncEventNotifier;
import org.cleverbus.spi.msg.MessageService;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;


/**
 * Endpoint for {@link MsgFunnelComponent msg-funnel} component.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class MsgFunnelEndpoint extends DefaultEndpoint {

    public static final int DEFAULT_IDLE_INTERVAL = 600;

    /**
     * Interval (in seconds) that determines how long can be message processing.
     */
    private int idleInterval = DEFAULT_IDLE_INTERVAL;

    /**
     * {@code true} if funnel component should guaranteed order of processing messages.
     * By default funnel works with running messages (PROCESSING, WAITING, WAITING_FOR_RES) only
     * and if it's necessary to guarantee processing order then also PARTLY_FAILED, POSTPONED and FAILED
     * messages should be involved.
     * <p/>
     * Use {@link #isExcludeFailedState()} to exclude FAILED state from searching messages.
     */
    private boolean guaranteedOrder;

    /**
     * {@link MsgStateEnum#FAILED FAILED} state is used for guaranteed order by default;
     * {@code true} if you want to exclude FAILED state.
     * <p/>
     * This option has influence only if {@link #isGuaranteedOrder() guaranteed processing order} is enabled.
     */
    private boolean excludeFailedState;

    /**
     * Funnel component identifier.
     */
    private String id;

    /**
     * Creates new endpoint.
     *
     * @param endpointUri the URI
     * @param component the "msg-funnel" component
     */
    public MsgFunnelEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new MsgFunnelProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("you cannot send messages to this endpoint:" + getEndpointUri());
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    AsyncEventNotifier getAsyncEventNotifier() {
        return ((MsgFunnelComponent)getComponent()).getAsyncEventNotifier();
    }

    MessageService getMessageService() {
        return ((MsgFunnelComponent)getComponent()).getMessageService();
    }

    /**
     * Gets interval (in seconds) that determines how long can be message processing.
     *
     * @return interval
     */
    public int getIdleInterval() {
        return idleInterval;
    }

    public void setIdleInterval(int idleInterval) {
        this.idleInterval = idleInterval;
    }

    /**
     * Gets {@code true} if funnel component should guaranteed order of processing messages.
     *
     * @return {@code true} if funnel component should guaranteed order of processing messages otherwise {@code false}
     */
    public boolean isGuaranteedOrder() {
        return guaranteedOrder;
    }

    public void setGuaranteedOrder(boolean guaranteedOrder) {
        this.guaranteedOrder = guaranteedOrder;
    }

    public boolean isExcludeFailedState() {
        return excludeFailedState;
    }

    /**
     * Sets flag whether you want to exclude FAILED state from searching messages for guaranteed order.
     * <p/>
     * This option has influence only if {@link #isGuaranteedOrder() guaranteed processing order} is enabled.
     *
     * @param excludeFailedState {@code true} if you want to exclude FAILED state
     */
    public void setExcludeFailedState(boolean excludeFailedState) {
        this.excludeFailedState = excludeFailedState;
    }

    /**
     * Gets funnel component identifier.
     *
     * @return funnel component identifier
     */
    public String getId() {
        return id;
    }

    //TODO (juza) kontrola unikatnosti ID

    public void setId(String id) {
        this.id = id;
    }
}
