/**
 * Requests/responses saving.
 * <p/>
 * Default implementation uses Camel events that has one possible disadvantage - it's necessary to join request
 * and response together (= two Camel events) and if exchange is changed from sending request until response receive
 * then it's not possible to join it.
 * <p/>
 * Requests/responses are saved into database, {@link org.openhubframework.openhub.core.reqres.RequestResponseService} defines contract
 * - {@link org.openhubframework.openhub.core.reqres.RequestResponseServiceDefaultImpl default implementation} save them directly to DB 
 * in synchronous manner.
 */
package org.openhubframework.openhub.core.reqres;