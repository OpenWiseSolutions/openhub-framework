package org.openhubframework.openhub.common.synchronization;

/**
 * Method {@link #syncBlock()} in this interface can be synchronized by one value in
 * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
 * <p>
 * If you need {@code void} method, use {@link SynchronizationNoResultBlock}.
 * </p>
 *
 * @author Roman Havlicek
 * @see SynchronizationExecutor#execute(SynchronizationBlock, String, Object)
 * @see SynchronizationNoResultBlock
 * @since 2.0
 */
public interface SynchronizationBlock {

    /**
     * Method will be synchronized by one value calling
     * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
     *
     * @param <T> type of return object
     * @return returned object of calling this method
     */
    <T> T syncBlock();
}
