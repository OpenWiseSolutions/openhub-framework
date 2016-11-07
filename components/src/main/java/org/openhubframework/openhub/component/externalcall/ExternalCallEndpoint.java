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

package org.openhubframework.openhub.component.externalcall;

import org.openhubframework.openhub.spi.extcall.ExternalCallService;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultEndpoint;


/**
 * See {@link ExternalCallComponent}
 */
public class ExternalCallEndpoint extends DefaultEndpoint {

    private ExternalCallKeyType keyType;
    private String targetURI;

    public ExternalCallEndpoint(String uri, ExternalCallComponent externalCallComponent, ExternalCallKeyType keyType, String targetURI) {
        super(uri, externalCallComponent);
        this.keyType = keyType;
        this.targetURI = targetURI;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ExternalCallProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException(
                ExternalCallEndpoint.class.getSimpleName() + " doesn't support consuming from it");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    ProducerTemplate getProducerTemplate() {
        return ((ExternalCallComponent)getComponent()).getProducerTemplate();
    }

    ExternalCallService getService() {
        return ((ExternalCallComponent)getComponent()).getService();
    }

    public ExternalCallKeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(ExternalCallKeyType keyType) {
        this.keyType = keyType;
    }

    public String getTargetURI() {
        return targetURI;
    }

    public void setTargetURI(String targetURI) {
        this.targetURI = targetURI;
    }
}
