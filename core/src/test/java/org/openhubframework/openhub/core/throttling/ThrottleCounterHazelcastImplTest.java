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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.openhubframework.openhub.spi.throttling.ThrottleScope;


/**
 * Test suite for {@link ThrottleCounterMemoryImpl}.
 *
 * @author Petr Juza
 * @since 2.0
 */
public class ThrottleCounterHazelcastImplTest extends AbstractThrottleCounterTest {

    private Config config;

    @Before
    public void prepareConfig() throws IOException {
        Resource conf = new ClassPathResource("config/ohf_hazelcast.xml");
        config = new Config();
        config.setConfigurationFile(conf.getFile());
    }

    @After
    public void shutdownHazelcast() {
        // gracefully shutdowns HazelcastInstance => necessary for running another tests
        Hazelcast.shutdownAll();
    }

    @Test
    public void testSingleNodeCounting() throws Exception {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
        assertCounting(new ThrottleCounterHazelcastImpl(hazelcast));
    }

    @Test
    public void testTwoNodesCounting() throws Exception {
        HazelcastInstance hazelcast1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcast2 = Hazelcast.newHazelcastInstance(config);
        ThrottleCounterHazelcastImpl counter1 = new ThrottleCounterHazelcastImpl(hazelcast1);
        ThrottleCounterHazelcastImpl counter2 = new ThrottleCounterHazelcastImpl(hazelcast2);

        assertCounting(counter1);

        // see assertCounting()
        ThrottleScope scope1 = new ThrottleScope("crm", "op1");
        int count = counter2.count(scope1, 10);
        assertThat(count, is(3));
    }

    @Test
    public void testMultiThreadCountingWithSingleNode() throws Exception {
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(config);
        assertMultiThreadCounting(new ThrottleCounterHazelcastImpl(hazelcast));
    }

    @Test
    public void testMultiThreadCountingWithTwoNodes() throws Exception {
        HazelcastInstance hazelcast1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcast2 = Hazelcast.newHazelcastInstance(config);
        ThrottleCounterHazelcastImpl counter1 = new ThrottleCounterHazelcastImpl(hazelcast1);
        new ThrottleCounterHazelcastImpl(hazelcast2);

        new ThrottleCounterHazelcastImpl(Hazelcast.newHazelcastInstance(config));

        assertMultiThreadCounting(counter1);
    }
}
