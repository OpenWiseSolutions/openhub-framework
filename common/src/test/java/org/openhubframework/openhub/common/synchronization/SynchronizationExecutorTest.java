/*
 * Copyright 2017 the original author or authors.
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

package org.openhubframework.openhub.common.synchronization;

import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link SynchronizationExecutor}.
 *
 * @author Roman Havlicek
 * @see SynchronizationExecutor
 * @see SynchronizationBlock
 * @see SynchronizationNoResultBlock
 * @since 2.0
 */
public class SynchronizationExecutorTest {

    /**
     * Constant type for synchronization block.
     */
    private static final String SYNC_TEST_TYPE_ONE = "syncTestTypeOne";

    /**
     * Constant type for synchronization block.
     */
    private static final String SYNC_TEST_TYPE_TWO = "syncTestTypeTwo";

    /**
     * Test for one synchronization value.
     *
     * @throws Exception all errors
     * @see SynchronizationTestBlock
     */
    @Test
    public void testSynchronizationPart() throws Exception {
        final SynchronizationTestBlock syncTestBlock = new SynchronizationTestBlock(SYNC_TEST_TYPE_ONE);

        int threads = 15;

        final int count = 1000000;
        final CountDownLatch latch = new CountDownLatch(threads);
        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        syncTestBlock.incrementCount("ONE_SYNC_VALUE");
                    }
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

        assertThat(syncTestBlock.getCount("ONE_SYNC_VALUE"), is(count * threads * 10));
    }

    /**
     * Test for three synchronization value.
     *
     * @throws Exception all errors
     * @see SynchronizationTestBlock
     */
    @Test
    public void testSynchronizationPartMultiValue() throws Exception {
        final SynchronizationTestBlock syncTestBlockOne = new SynchronizationTestBlock(SYNC_TEST_TYPE_ONE);
        final SynchronizationTestBlock syncTestBlockTwo = new SynchronizationTestBlock(SYNC_TEST_TYPE_TWO);

        int threads = 5;

        final int count = 1000000;
        final CountDownLatch latch = new CountDownLatch(4 * threads);
        Runnable taskOne = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        syncTestBlockOne.incrementCount("ONE_SYNC_VALUE");
                    }
                } finally {
                    latch.countDown();
                }
            }
        };

        Runnable taskTwo = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        syncTestBlockOne.incrementCount("TWO_SYNC_VALUE");
                    }
                } finally {
                    latch.countDown();
                }
            }
        };

        Runnable taskThree = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        syncTestBlockTwo.incrementCount("THREE_SYNC_VALUE");
                    }
                } finally {
                    latch.countDown();
                }
            }
        };

        Runnable taskFour = new Runnable() {

            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        syncTestBlockTwo.incrementCount("FOUR_SYNC_VALUE");
                    }
                } finally {
                    latch.countDown();
                }
            }
        };

        // start processing and waits for result
        for (int i = 0; i < threads; i++) {
            new Thread(taskOne).start();
            new Thread(taskTwo).start();
            new Thread(taskThree).start();
            new Thread(taskFour).start();
        }

        latch.await();

        assertThat(syncTestBlockOne.getCount("ONE_SYNC_VALUE"), is(count * threads * 10));
        assertThat(syncTestBlockOne.getCount("TWO_SYNC_VALUE"), is(count * threads * 10));
        assertThat(syncTestBlockTwo.getCount("THREE_SYNC_VALUE"), is(count * threads * 10));
        assertThat(syncTestBlockTwo.getCount("FOUR_SYNC_VALUE"), is(count * threads * 10));
    }

    //----------------------------------------------- PRIVATE CLASS ----------------------------------------------------

    /**
     * Synchronization test block.
     */
    private static class SynchronizationTestBlock {

        private final String syncValueType;

        private final Map<String, Integer> syncValueToCount = new ConcurrentHashMap<>();

        private SynchronizationTestBlock(String syncValueType) {
            Assert.hasText(syncValueType, "syncValueType must not be empty");

            this.syncValueType = syncValueType;
        }

        public void incrementCount(final String syncValue) {
            Assert.hasText(syncValue, "syncValue must not be empty");

            SynchronizationExecutor.getInstance().execute(new SynchronizationNoResultBlock() {

                @Override
                protected void syncBlockNoResult() {
                    Integer count = syncValueToCount.get(syncValue);
                    if (count == null) {
                        count = 0;
                    }
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    count = count + 1;
                    syncValueToCount.put(syncValue, count);
                }
            }, this.syncValueType, syncValue);
        }

        public int getCount(String syncValue) {
            Assert.hasText(syncValue, "syncValue must not be empty");

            Integer result = syncValueToCount.get(syncValue);
            return result == null ? 0 : result;
        }
    }
}
