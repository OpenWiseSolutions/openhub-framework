package org.openhubframework.openhub.common.synchronization;

/**
 * Method {@link #syncBlockNoResult()} in this class can be synchronized by one one value in
 * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
 * <p>
 * If you need to method return object (not {@code void}), use interace {@link SynchronizationBlock}.
 * </p>
 *
 * @author Roman Havlicek
 * @see SynchronizationBlock
 * @see SynchronizationExecutor#execute(SynchronizationBlock, String, Object)
 * @since 2.0
 */
public abstract class SynchronizationNoResultBlock implements SynchronizationBlock {

    @Override
    public final <T> T syncBlock() {
        syncBlockNoResult();
        return null;
    }

    /**
     * Method will be synchronized by one value calling
     * {@link SynchronizationExecutor#execute(SynchronizationBlock, String, Object)}.
     */
    protected abstract void syncBlockNoResult();
}
