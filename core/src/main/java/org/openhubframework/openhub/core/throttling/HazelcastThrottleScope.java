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

package org.openhubframework.openhub.core.throttling;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import org.springframework.util.Assert;

import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Serializable version of {@link ThrottleScope} for {@link ThrottleCounterHazelcastImpl Hazelcast implementation}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public final class HazelcastThrottleScope implements DataSerializable {

    private String sourceSystem;

    private String serviceName;

    // empty for serialization/deserialization
    public HazelcastThrottleScope() {
    }

    HazelcastThrottleScope(ThrottleScope throttleScope) {
        Assert.notNull(throttleScope, "the throttleScope must not be null");

        this.sourceSystem = throttleScope.getSourceSystem();
        this.serviceName = throttleScope.getServiceName();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(sourceSystem);
        out.writeUTF(serviceName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sourceSystem = in.readUTF();
        this.serviceName = in.readUTF();
    }
}
