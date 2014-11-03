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

package org.cleverbus.api.event;

import java.lang.reflect.ParameterizedType;
import java.util.EventObject;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.util.Assert;



/**
 * Base class for implementing {@link EventNotifier Camel event notifiers}.
 * <p/>
 * Implements only one direct inherited child, no more inheritance levels.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public abstract class EventNotifierBase<T extends EventObject> extends EventNotifierSupport implements CamelContextAware {

    private Class<T> eventClass;

    private CamelContext camelContext;

    @SuppressWarnings("unchecked")
    public EventNotifierBase() {
        // valid only if there is only one inherited child of this class,
        //  see http://stackoverflow.com/questions/3403909/get-generic-type-of-class-at-runtime
        this.eventClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
	public final void notify(EventObject event) throws Exception {
		doNotify((T)event);
	}

    /**
     * Calls notification implementation.
     *
     * @param event the event
     */
    protected abstract void doNotify(T event) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @param event the event
     * @return {@code true} if {@link EventObject} is instance of generic T, otherwise {@code false}
     */
    @Override
	public boolean isEnabled(EventObject event) {
        return eventClass.isAssignableFrom(event.getClass());
	}

    @Override
    public final void setCamelContext(CamelContext camelContext) {
        Assert.notNull(camelContext, "camelContext must not be null");

        this.camelContext = camelContext;
    }

    @Override
    public final CamelContext getCamelContext() {
        return camelContext;
    }

    /**
     * Gets {@link ProducerTemplate} default instance from Camel Context.
     *
     * @return ProducerTemplate
     * @throws IllegalStateException when there is no ProducerTemplate
     */
    public ProducerTemplate getProducerTemplate() {
        if (!isStarted() && !isStarting()) {
            throw new IllegalStateException(getClass().getName() + " is not started so far!");
        }

        Set<ProducerTemplate> templates = camelContext.getRegistry().findByType(ProducerTemplate.class);
        Assert.state(templates.size() >= 1, "ProducerTemplate must be at least one.");

        return templates.iterator().next();
    }
}
