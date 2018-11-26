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

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Executor for {@link SynchronizationBlock} that synchronized more then one threads by one value.
 * <p>
 * For synchronization by one value call {@link #execute(SynchronizationBlock, String, Object)}, where in method
 * {@link SynchronizationBlock#syncBlock()} are code that will be synchronized by value. If synchronization block part
 * has no result than use {@link SynchronizationNoResultBlock#syncBlockNoResult()}.
 * </p>
 * <p>
 * Attribute syncValueType must be unique for domain which will be by value (syncValue) synchronized
 * (like THROTTLING, or ALERT_COUNT).
 * </p>
 * <p>
 * <b>Example:</b><br/>
 * In this example increment for property count is synchronized by value SYNCHRONIZATION_VALUE in domain ALERT.
 * Method has no result.
 * <pre>
 * int count = 0;
 * String synchronizationValue = "SYNCHRONIZATION_VALUE";
 * SynchronizationExecutor.getInstance().execute(new SynchronizationNoResultBlock() {
 *
 *      {@code @Override}
 *      protected void syncBlockNoResult() {
 *          count++;
 *      }
 * }, "ALERT", synchronizationValue);
 * </pre>
 * </p>
 * <p>
 * Instance of this class gets by {@link #getInstance()}.
 * </p>
 *
 * @author Roman Havlicek
 * @see SynchronizationBlock
 * @see SynchronizationNoResultBlock
 * @since 2.0
 */
public final class SynchronizationExecutor {

    /**
     * Instance of this class.
     */
    private static SynchronizationExecutor synchronizationExecutor;

    /**
     * Contains all scopes in progress ({@link SynchronizationBlock}) in map by type.
     */
    private final Map<String, Set<Object>> scopesInProgress = new HashMap<>();

    /**
     * Contains all locks for sync value type.
     */
    private final Map<String, Object> locksForSyncValueType = new ConcurrentHashMap<>();

    /**
     * Gets instance by {@link #getInstance()}.
     */
    private SynchronizationExecutor() {
    }

    /**
     * Execute method {@link SynchronizationBlock#syncBlock()} in synchronization by one value
     * (attribute syncValue).
     * <p>
     * Attribute syncValueType must be unique for domain which will be by value (syncValue) synchronized
     * (like THROTTLING, or ALLERT_COUNT).
     * </p>
     *
     * @param syncBlock     interface with method {@link SynchronizationBlock#syncBlock()} that
     *                      will be synchronized by value
     * @param syncValueType type of value (like THROTTLING is synchronized every throttling scopes)
     * @param syncValue     value by which will be method synchronized (like concrete throttling scope)
     * @param <T>           type of return object
     * @return return from method {@link SynchronizationBlock#syncBlock()}
     */
    public <T> T execute(SynchronizationBlock syncBlock, String syncValueType, Object syncValue) {
        Assert.notNull(syncBlock, "syncBlock must not be null");
        Assert.hasText(syncValueType, "syncValueType must not be empty");
        Assert.notNull(syncValue, "syncValue must not be null");

        Object lockSyncValueType = getLockForSyncValueType(syncValueType);

        while (isLock(syncValueType, syncValue)) {
            synchronized (lockSyncValueType) {
                try {
                    lockSyncValueType.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Error in wait method in '"
                            + SynchronizationExecutor.class.getSimpleName() + "'. Error: " + e.getMessage(), e);
                }
            }
        }

        try {
            return syncBlock.syncBlock();
        } finally {
            removeSyncValue(syncValueType, syncValue);

            synchronized (lockSyncValueType) {
                lockSyncValueType.notifyAll();
            }
        }
    }

    /**
     * Checks if thread which call this method will be locked.
     *
     * @param syncValueType synchronization type
     * @param syncValue     synchrozniation value
     * @return {@code true} - thread will be locked, {@code false} - otherwise
     */
    private boolean isLock(String syncValueType, Object syncValue) {
        Assert.hasText(syncValueType, "syncValueType must not be empty");
        Assert.notNull(syncValue, "syncValue must not be null");

        synchronized (scopesInProgress) {
            Set<Object> valuesInProgress = getValuesInProgressByClass(syncValueType);
            if (valuesInProgress.contains(syncValue)) {
                return true;
            } else {
                valuesInProgress.add(syncValue);
                return false;
            }
        }
    }

    /**
     * Remove synchronization value from scopes in progress.
     *
     * @param syncValueType synchronization type
     * @param syncValue     synchronization value
     */
    private void removeSyncValue(String syncValueType, Object syncValue) {
        Assert.hasText(syncValueType, "syncValueType must not be empty");
        Assert.notNull(syncValue, "syncValue must not be null");

        synchronized (scopesInProgress) {
            Set<Object> valuesInProgress = getValuesInProgressByClass(syncValueType);
            valuesInProgress.remove(syncValue);
            if (valuesInProgress.isEmpty()) {
                scopesInProgress.remove(syncValueType);
            }
        }
    }

    /**
     * Gets scopes in progress for synchronization type.
     *
     * @param syncValueType synchronization type
     * @return scopes in progress
     */
    private Set<Object> getValuesInProgressByClass(String syncValueType) {
        Assert.hasText(syncValueType, "syncValueType must not be empty");

        Set<Object> result = scopesInProgress.get(syncValueType);
        if (result == null) {
            result = new HashSet<>();
            scopesInProgress.put(syncValueType, result);
        }
        return result;
    }

    /**
     * Get lock for sync value type.
     *
     * @param syncValueType synchronization value type
     * @return lock
     */
    private Object getLockForSyncValueType(String syncValueType) {
        Assert.hasText(syncValueType, "syncValueType must not be empty");

        synchronized (locksForSyncValueType) {
            Object result = locksForSyncValueType.get(syncValueType);
            if (result == null) {
                result = new Object();
                locksForSyncValueType.put(syncValueType, result);
            }
            return result;
        }
    }

    //--------------------------------------------- STATIC -------------------------------------------------------------

    /**
     * Gets instance of this class.
     *
     * @return instance of this class
     */
    public synchronized static SynchronizationExecutor getInstance() {
        if (synchronizationExecutor == null) {
            synchronizationExecutor = new SynchronizationExecutor();
        }
        return synchronizationExecutor;
    }
}
