/**
 * Requests/responses saving.
 * <p/>
 * Default implementation uses Camel events that has one possible disadvantage - it's necessary to join request
 * and response together (= two Camel events) and if exchange is changed from sending request until response receive
 * then it's not possible to join it.
 * <p/>
 * Requests/responses are saved into database, {@link RequestResponseService} defines contract
 * - {@link RequestResponseServiceDefaultImpl default implementation} save them directly to DB in synchronous manner.
 */
package org.cleverbus.core.reqres;