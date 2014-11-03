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

package org.cleverbus.core.throttling;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.cleverbus.spi.throttling.ThrottleScope;

import org.junit.Test;


/**
 * Test suite for {@link ThrottleCounterMemoryImpl}.
 *
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
public class ThrottleCountingMemoryImplTest {

    @Test
    public void testCounting() throws Exception {
        ThrottleCounterMemoryImpl counter = new ThrottleCounterMemoryImpl();

        ThrottleScope scope1 = new ThrottleScope("crm", "op1");
        int count = counter.count(scope1, 10);
        assertThat(count, is(1));

        ThrottleScope scope2 = new ThrottleScope("crm", "op2");
        count = counter.count(scope2, 10);
        assertThat(count, is(1));
        count = counter.count(scope2, 10);
        assertThat(count, is(2));

        ThrottleScope scope3 = new ThrottleScope("erp", "op1");
        count = counter.count(scope3, 10);
        assertThat(count, is(1));

        count = counter.count(scope1, 10);
        assertThat(count, is(2));

        ThrottleScope scope4 = new ThrottleScope("crm", "op4");
        count = counter.count(scope4, 1);
        assertThat(count, is(1));

        Thread.sleep(1500);

        count = counter.count(scope4, 1);
        assertThat(count, is(1));

        // test dump
        counter.dumpMemory();
    }

    @Test
    public void testMultiThreadCounting() throws Exception {
        final ThrottleCounterMemoryImpl counter = new ThrottleCounterMemoryImpl();

        // prepare threads
        int threads = 5;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    // new instance for each thread
                    ThrottleScope scope1 = new ThrottleScope("crm", "op1");
                    ThrottleScope scope2 = new ThrottleScope("crm", "op2");

                    counter.count(scope1, 10);
                    counter.count(scope2, 10);
                } finally {
                    latch.countDown();
                }
            }
        };

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
        }

        latch.await();

        // verify counters
        ThrottleScope scope1 = new ThrottleScope("crm", "op1");
        ThrottleScope scope2 = new ThrottleScope("crm", "op2");

        int count = counter.count(scope1, 10);
        assertThat(count, is(threads + 1));

        count = counter.count(scope2, 10);
        assertThat(count, is(threads + 1));
    }
}
