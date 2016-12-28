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

package org.openhubframework.openhub.component.externalcall;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;

import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.openhubframework.openhub.api.asynch.AsynchConstants;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.api.extcall.ExtCallComponentParams;
import org.openhubframework.openhub.component.AbstractComponentsDbTest;
import org.openhubframework.openhub.core.common.asynch.AsynchMessageRoute;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;
import org.openhubframework.openhub.test.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.ServiceTestEnum;
import org.openhubframework.openhub.test.route.ActiveRoutes;

/**
 * Test suite for {@link ExternalCallComponent}.
 */
@ActiveRoutes(classes = {AsynchMessageRoute.class})
public class ExternalCallComponentTest extends AbstractComponentsDbTest {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalCallComponentTest.class);

    private static final String REQUEST_XML =
            "  <cus:setCustomerRequest xmlns=\"http://openhubframework.org/ws/Customer-v1\""
                    + "         xmlns:cus=\"http://openhubframework.org/ws/CustomerService-v1\">"
                    + "         <cus:customer>"
                    + "            <externalCustomerID>12</externalCustomerID>"
                    + "            <customerNo>23</customerNo>"
                    + "            <customerTypeID>2</customerTypeID>"
                    + "            <lastName>Juza</lastName>"
                    + "            <firstName>Petr</firstName>"
                    + "         </cus:customer>"
                    + "  </cus:setCustomerRequest>";

    @Autowired
    private ExternalCallDao externalCallDao;

    @Produce
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mockEndpoint;

    private Long extCallId;

    @Value("${asynch.externalCall.skipUriPattern}")
    private String skipUriPattern;

    @Before
    public void resetExtCallId() {
        extCallId = null;
    }

    @Test
    public void testExternalCallOK() throws Exception {
        final Message msg = messages(1)[0];

        // mock response and in-process asserts
        mockEndpoint.whenAnyExchangeReceived(
                recordCallIdAndAnswer("external call reply body", "mock:test", "ok123456"));

        // send message
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg, "mock:test", "ok123456", "external call original body");
        mockEndpoint.assertIsSatisfied();

        // verify result
        assertEquals("external call reply body", reply);

        // check the call is now in DB as OK and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg);
    }

    @Test
    public void testExternalCallObjectEntityIdOK() throws Exception {
        final Message msg = messages(1)[0];

        // mock response and in-process asserts
        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndAnswer("external call reply body", "mock:test",
                        "CRM_" + msg.getCorrelationId() + "_[CustomEntityId]:123654"));

        // send message
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg, ExternalCallKeyType.MESSAGE, "mock:test",
                new CustomEntityId("123654"), "external call original body");
        mockEndpoint.assertIsSatisfied();

        // verify result
        assertEquals("external call reply body", reply);

        // check the call is now in DB as OK and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg);
    }

    @Test
    public void testExternalCallFailedIOException() throws Exception {
        Message msg = messages(1)[0];

        // simulate failure
        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndThrow(
                new IOException("test exception to simulate failure"), "mock:test", "fail123456"));

        // send message
        mockEndpoint.expectedMessageCount(1);
        try {
            requestViaExternalCall(msg, "mock:test", "fail123456", "external call original body");
            fail("Should've gotten an exception by now");
        } catch (Exception exc) {
            // verify failure
            assertThat(exc, is(instanceOf(IOException.class)));
        }
        mockEndpoint.assertIsSatisfied();

        // check the call is now in DB as FAILED and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg);
    }

    @Test
    public void testExternalCallFailedIntegrationException() throws Exception {
        Message msg = messages(1)[0];

        // simulate failure
        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndThrow(
                new IntegrationException(InternalErrorEnum.E100, "test exception to simulate failure"), "mock:test", "fail123456"));

        // send message
        mockEndpoint.expectedMessageCount(1);
        try {
            requestViaExternalCall(msg, "mock:test", "fail123456", "external call original body");
            fail("Should've gotten an exception by now");
        } catch (Exception exc) {
            // verify failure
            assertThat(exc, is(instanceOf(IntegrationException.class)));
        }
        mockEndpoint.assertIsSatisfied();

        // check the call is now in DB as FAILED and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg);
    }

    @Test
    public void testExternalCallDuplicate() throws Exception {
        Message msg = messages(1)[0];

        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndAnswer("external call reply body", "mock:test", "dupe123456"));

        // send first message
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg, "mock:test", "dupe123456", "external call original body");
        mockEndpoint.assertIsSatisfied();

        // verify result
        assertEquals("external call reply body", reply); // 1st reply is as expected

        // check the call is now in DB as OK and with the correct timestamp
        ExternalCallStateEnum state = ExternalCallStateEnum.OK;
        Long callId = extCallId;
        assertExtCallStateInDB(callId, state, msg);

        // send a duplicate message
        reply = requestViaExternalCall(msg, "mock:test", "dupe123456", "external call original body");
        mockEndpoint.assertIsSatisfied();

        assertEquals("external call original body", reply); // 2nd returned original body unchanged -- no call, no reply

        // check the call is in DB completely unchanged
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg);
    }

    @Test
    public void testExternalCallDuplicateFail() throws Exception {
        Message msg = messages(1)[0];

        mockEndpoint.whenExchangeReceived(1, recordCallIdAndThrow(
                new IOException("Simulated Failure for Testing Duplicate of Failure"), "mock:test", "dupeFail123456"));


        mockEndpoint.whenExchangeReceived(2, recordCallIdAndAnswer("external call reply body", "mock:test", "dupeFail123456"));

        // send first message
        mockEndpoint.expectedMessageCount(1);
        try {
            requestViaExternalCall(msg, "mock:test", "dupeFail123456", "external call original body");
            // verify result
            fail("Should've failed due to exception by now");
        } catch (Exception exc) {
            // verify failure
            assertThat(exc, is(instanceOf(IOException.class)));
        }
        mockEndpoint.assertIsSatisfied();

        // check the call is now in DB as OK and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg);

        // send a duplicate message
        mockEndpoint.expectedMessageCount(2);
        String reply = requestViaExternalCall(msg, "mock:test", "dupeFail123456", "external call original body");
        mockEndpoint.assertIsSatisfied();

        assertEquals("external call reply body", reply); // 2nd returned reply, since it was called again after 1st fail

        // check the call is in DB now as OK
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg);
    }

    @Test
    public void testExternalCallNewer() throws Exception {
        Message[] msg = messages(2);
        assumeTrue("For this test the 2nd message must be older",
                msg[0].getMsgTimestamp().compareTo(msg[1].getMsgTimestamp()) < 0);

        mockEndpoint.whenAnyExchangeReceived(
                recordCallIdAndAnswer("external call reply body", "mock:test", "twiceKey"));

        // send 1st message
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg[0], "mock:test", "twiceKey", "external call original body");
        mockEndpoint.assertIsSatisfied();

        // verify result
        assertEquals("external call reply body", reply); // 1st reply is as expected

        // check the call is now in DB as OK and with the correct timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg[0]);

        // send 2nd message
        mockEndpoint.expectedMessageCount(2);
        reply = requestViaExternalCall(msg[1], "mock:test", "twiceKey", "external call original body 2");
        mockEndpoint.assertIsSatisfied();

        // verify the call worked, since the 2nd message is newer
        assertEquals("external call reply body", reply); // 2nd reply is as expected too

        // check the call is in DB with the new timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg[1]);
    }

    @Test
    public void testExternalCallOlder() throws Exception {
        Message[] msg = messages(2);
        assumeTrue("For this test the 2nd message must be older",
                msg[0].getMsgTimestamp().compareTo(msg[1].getMsgTimestamp()) <= 0);

        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndAnswer("external call reply body", "mock:test", "older123456"));

        // send 2nd message - reverse order
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg[1], "mock:test", "older123456", "external call original body");
        mockEndpoint.assertIsSatisfied();

        // verify result
        assertEquals("external call reply body", reply); // 1st reply is as expected

        // check the call is now in DB as OK and with the correct msg1 NEWER timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg[1]);

        // send 1st message - reverse order
        reply = requestViaExternalCall(msg[0], "mock:test", "older123456", "external call original body 2");
        mockEndpoint.assertIsSatisfied();


        // verify the 2nd call was skipped, since the 2nd message is older
        assertEquals("external call original body 2", reply);

        // check the call is now in DB as OK, still with msg1 NEWER timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg[1]);
    }

    @Test
    public void testExternalCallOlderFail() throws Exception {
        Message[] msg = messages(2);
        assumeTrue("For this test the 2nd message must be newer",
                msg[0].getMsgTimestamp().compareTo(msg[1].getMsgTimestamp()) <= 0);

        mockEndpoint.whenExchangeReceived(1, recordCallIdAndThrow(new IOException("Simulated External Call Failure"), "mock:test", "olderFail123456"));
        mockEndpoint.whenExchangeReceived(2, recordCallIdAndAnswer("external call reply body", "mock:test", "olderFail123456"));

        // send 2nd message - reverse order
        mockEndpoint.expectedMessageCount(1);
        try {
            requestViaExternalCall(msg[1], "mock:test", "olderFail123456", "external call original body");
            fail("Should've failed due to an exception by now");
        } catch (Exception exc) {
            // verify failure
            assertThat(exc, is(instanceOf(IOException.class)));
        }
        mockEndpoint.assertIsSatisfied();

        // check the call is now in DB as FAILED and with the correct msg1 NEWER timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg[1]);

        // send 1st message - reverse order
        String reply = requestViaExternalCall(msg[0], "mock:test", "olderFail123456", "external call original body 2");
        mockEndpoint.assertIsSatisfied();

        // verify the 2nd call was NOT made, since the 1st failed call is newer
        assertEquals("external call original body 2", reply);

        // check the call remains unchanged
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg[1]);
    }

    @Test
    public void testExternalCallOlderFailBetween() throws Exception {
        Message[] msg = messages(3);
        assumeTrue("For this test the 2nd message must be newer than 1st",
                msg[0].getMsgTimestamp().compareTo(msg[1].getMsgTimestamp()) <= 0);
        assumeTrue("For this test the 3nd message must be newest - newer than 1st and 2nd",
                msg[1].getMsgTimestamp().compareTo(msg[2].getMsgTimestamp()) <= 0);

        mockEndpoint.whenExchangeReceived(1, recordCallIdAndThrow(new IOException("Simulated External Call Failure"), "mock:test", "olderFailBetween123456"));
        mockEndpoint.whenExchangeReceived(2, recordCallIdAndAnswer("external call reply body", "mock:test", "olderFailBetween123456"));

        // send 2nd message to fail, but trigger external call update
        mockEndpoint.expectedMessageCount(1);
        try {
            requestViaExternalCall(msg[1], "mock:test", "olderFailBetween123456", "external call original body");
            fail("Should've failed due to an exception by now");
        } catch (Exception exc) {
            // verify failure
            assertThat(exc, is(instanceOf(IOException.class)));
        }
        mockEndpoint.assertIsSatisfied();
        // check the call is now in DB as FAILED and with the correct msg1 NEWER timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg[1]);

        // send 1st (oldest) message - should be ignored
        mockEndpoint.expectedMessageCount(1);
        String reply = requestViaExternalCall(msg[0], "mock:test", "olderFailBetween123456", "external call original body 2");
        mockEndpoint.assertIsSatisfied();

        // verify the 2nd call was NOT made, since the 1st failed call is newer
        assertEquals("external call original body 2", reply);

        // check the call remains unchanged
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.FAILED, msg[1]);

        // send 3rd (latest) message - should be allowed
        mockEndpoint.expectedMessageCount(2);
        reply = requestViaExternalCall(msg[2], "mock:test", "olderFailBetween123456", "external call original body 3");
        mockEndpoint.assertIsSatisfied();

        // verify the 3rd call was made, since it's the newest call
        assertEquals("external call reply body", reply);

        // check the call remains unchanged
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, msg[2]);
    }

    @Test
    public void testExternalCallSkipUriPattern() throws Exception {
        // for this test the following must be set in application0.cfg:
        // asynch.externalCall.skipUriPattern = mock:(//)?ignoreTestEndpointUri.*
        assumeThat(skipUriPattern, equalTo("mock:(//)?ignoreTestEndpointUri.*"));

        final Message msg = messages(1)[0];

        MockEndpoint mockIgnore = getCamelContext().getEndpoint("mock://ignoreTestEndpointUri_something", MockEndpoint.class);
        mockIgnore.expectedMessageCount(0);

        // send message
        String reply = requestViaExternalCall(msg, "mock:ignoreTestEndpointUri_something", "skip123456", "external call original body");

        mockIgnore.assertIsSatisfied();

        // verify result
        assertEquals("external call original body", reply);
    }

    @Test(timeout = 60000)
    public void testExternalCallLockFailure() throws Exception {
        final int messageCount = 21; // how many messages total should be sent
        final int batchSize = 3; // how many messages should be sent together with verifications after each batch
        final int responseDelay = 10; // delay before mock responds, in millis
        boolean lockFailureEncountered = false; // test is pointless if it was never encountered

        Message[] messages = messages(messageCount);

        // set up mock to reply and to verify that the external call is reused
        mockEndpoint.whenAnyExchangeReceived(recordCallIdAndAnswer("external call reply body", responseDelay, "mock:test", "concurrentKey"));

        for (int batchStart = 0; batchStart < messages.length; batchStart += batchSize) {
            Message[] batch = Arrays.copyOfRange(messages, batchStart, Math.min(messages.length, batchStart + batchSize));
            lockFailureEncountered |= sendAndVerifyBatch(batch);
        }

        assumeTrue("This test is pointless if lock failure is never encountered", lockFailureEncountered);
    }

    private boolean sendAndVerifyBatch(Message[] messages) throws Exception {
        boolean lockFailureEncountered = false;
        HashMap<Message, Future<String>> replies = new HashMap<Message, Future<String>>();
        // send messages that have no reply, resend messages that have LockFailureException instead of a reply
        // verify results and re-send failures - test has timeout set because this is potentially endless
        Queue<Message> unverifiedMessages = new LinkedList<Message>(Arrays.asList(messages));
        while (!unverifiedMessages.isEmpty()) {
            Message message = unverifiedMessages.poll();
            boolean replyAvailable = replies.containsKey(message);
            if (replyAvailable) {
                Future<String> reply = replies.get(message);
                try {
                    reply.get(); // this will throw an exception if it occurred during processing
                } catch (Exception exc) {
                    if (ExceptionUtils.indexOfType(exc, LockFailureException.class) != -1) {
                        // expected cause - this test verifies that this scenario happens and is handled properly
                        lockFailureEncountered = true;
                        replyAvailable = false; // mark reply unavailable to resend the original message
                    } else {
                        // fail by rethrowing
                        LOG.error("Unexpected failure for message {} --", message, exc);
                        throw exc;
                    }
                }
            }
            if (!replyAvailable) {
                unverifiedMessages.add(message); // mark message as still unverified
                replies.put(message,
                        requestViaExternalCallAsync(message, "mock:test", "concurrentKey", "external call original body"));
            }
        }
        // check the call is now in DB as OK and with the correct LAST msg timestamp
        assertExtCallStateInDB(extCallId, ExternalCallStateEnum.OK, messages[messages.length - 1]);
        return lockFailureEncountered;
    }

    private Future<String> requestViaExternalCallAsync(final Message msg, final String targetURI, final Object key, final String body) throws Exception {
        return getStringBodyFuture(producer.asyncSend("extcall:custom:" + targetURI,
                prepareExternalCallProcessor(msg, key, body)));

    }

    private String requestViaExternalCall(final Message msg, final String targetURI, final Object key, final String body) throws Exception {
        return requestViaExternalCall(msg, ExternalCallKeyType.CUSTOM, targetURI, key, body);
    }

    private String requestViaExternalCall(
                    final Message msg,
                    @Nullable final ExternalCallKeyType externalCallKeyType,
                    final String targetURI,
                    final Object key,
                    final String body) throws Exception {
        ExternalCallKeyType keyType = (externalCallKeyType == null) ? ExternalCallKeyType.CUSTOM : externalCallKeyType;
        Exchange reply = producer.request("extcall:" + keyType.toString() + ":" + targetURI,
                prepareExternalCallProcessor(msg, key, body));
        Exception exc = reply.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        if (exc != null) {
            throw exc;
        }
        exc = reply.getException();
        if (exc != null) {
            throw exc;
        }
        return reply.hasOut() ? reply.getOut().getBody(String.class) : reply.getIn().getBody(String.class);
    }

    private Processor prepareExternalCallProcessor(final Message msg, final Object key, final String body) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeaders(headers(msg));
                exchange.getIn().setBody(body);
                exchange.setProperty(ExtCallComponentParams.EXTERNAL_CALL_KEY, key);
            }
        };
    }

    /**
     * Returns a processor that answers with the specified answer body.
     * The received exchange is verified to have {@link Message} in {@link AsynchConstants#MSG_HEADER}.
     * <p/>
     * It also records the external call ID into the instance field {@link #extCallId}
     * or verifies the new one matches the old one, if one is already recorded.
     *
     * @param answerBody    the body to set for the exchange IN message
     * @param operationName
     * @param entityId
     * @return processor, e.g. for use with mocks
     */
    private Processor recordCallIdAndAnswer(final Object answerBody, String operationName, String entityId) {
        return recordCallIdAndAnswer(answerBody, 0, operationName, entityId);
    }

    /**
     * Same as {@link #recordCallIdAndAnswer(Object, String, String)}, but throws an exception instead.
     *
     * @param answerException the exception to throw
     * @param operationName
     * @param entityId
     * @return processor, e.g. for use with mocks
     */
    private Processor recordCallIdAndThrow(final Exception answerException, final String operationName, final String entityId) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                verifyAndRecordCallId(exchange, operationName, entityId);
                throw answerException;
            }
        };
    }

    /**
     * Same as {@link #recordCallIdAndAnswer(Object, String, String)}, but waits for the specified delay before answering.
     *
     * @param answerBody    the body to set for the exchange IN message
     * @param responseDelay delay in milliseconds to wait before answering
     * @param operationName
     * @param entityId
     * @return processor, e.g. for use with mocks
     */
    private Processor recordCallIdAndAnswer(final Object answerBody, final int responseDelay, final String operationName, final String entityId) {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                verifyAndRecordCallId(exchange, operationName, entityId);
                //simulate delay, then respond
                Thread.sleep(responseDelay);
                exchange.getIn().setBody(answerBody);
            }
        };
    }

    private void verifyAndRecordCallId(Exchange exchange, String operationName, String entityId) {
        Message msg = exchange.getIn().getHeader(AsynchConstants.MSG_HEADER, Message.class);
        ExternalCall extCall = externalCallDao.getExternalCall(operationName, entityId);
        LOG.info("Processing ExternalCall={} for Message={}", extCall, msg);

        assertNotNull(extCall);
        assertEquals(extCall.getState(), ExternalCallStateEnum.PROCESSING);
        // check it's also in the DB in the correct state
        assertExtCallStateInDB(extCall.getId(), ExternalCallStateEnum.PROCESSING, msg);

        Long newExtCallId = extCall.getId();
        if (extCallId == null) {
            extCallId = newExtCallId;
        } else {
            assertEquals(extCallId, newExtCallId);
        }
    }

    /** Verifies there's an ExternalCall instance in DB with the specified ID, state and msgId + msgTimestamp. */
    private void assertExtCallStateInDB(Long callId, ExternalCallStateEnum state, Message message) {
        ExternalCall extCall = em.find(ExternalCall.class, callId);
        assertNotNull(String.format("ExternalCall with ID [%s] doesn't exist in the DB", callId),
                extCall);
        assertEquals(String.format("ExternalCall [%s]%ndoesn't have the expected state [%s]", extCall, state),
                state, extCall.getState());
        assertEquals(String.format("ExternalCall [%s]%ndoesn't reference the expected message [%s]", extCall, message),
                message.getMsgId(), extCall.getMsgId());
        assertEquals(
                String.format(
                        "ExternalCall msgTimestamp [%s] doesn't match expected msgTimestamp [%s] of message [%s]",
                        extCall.getMsgTimestamp(), message.getMsgTimestamp(), message),
                message.getMsgTimestamp().getTime(), extCall.getMsgTimestamp().getTime());
    }

    private Message[] messages(final int messageCount) throws Exception {
        return createAndSaveMessages(messageCount, ExternalSystemTestEnum.CRM, ServiceTestEnum.CUSTOMER, "setCustomer",
                REQUEST_XML);
    }

    private Map<String, Object> headers(Message msg) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(AsynchConstants.MSG_HEADER, msg);
        headers.put(AsynchConstants.ASYNCH_MSG_HEADER, true);
        return headers;
    }

    private Future<String> getStringBodyFuture(final Future<Exchange> reply) {
        return new Future<String>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return reply.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return reply.isCancelled();
            }

            @Override
            public boolean isDone() {
                return reply.isDone();
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                return getReplyString(reply.get());
            }

            @Override
            public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return getReplyString(reply.get(timeout, unit));
            }

            private String getReplyString(Exchange exchange) throws InterruptedException, ExecutionException {
                throwExceptionOptionally(exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class));
                throwExceptionOptionally(exchange.getException());
                return exchange.getOut().getBody(String.class);
            }

            private void throwExceptionOptionally(Exception exc) throws InterruptedException, ExecutionException {
                if (exc != null) {
                    if (exc instanceof InterruptedException) {
                        throw (InterruptedException) exc;
                    } else if (exc instanceof ExecutionException) {
                        throw (ExecutionException) exc;
                    } else {
                        throw new ExecutionException(exc);
                    }
                }
            }
        };
    }

    private class CustomEntityId {

        private String entityId;

        public CustomEntityId(String entityId) {
            this.entityId = entityId;
        }

        @Override
        public String toString() {
            return "[CustomEntityId]:" + entityId;
        }
    }
}
