/**
 * Repair processing messages and external calls.
 * <p/>
 * When processing of the message doesn't finish for long time (probably some error occurred) then it's necessary
 * to restart processing of this message.
 */
package org.cleverbus.core.common.asynch.repair;