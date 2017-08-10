/*
 * Copyright 2012-2017 the original author or authors.
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

package org.openhubframework.openhub.admin.web.message.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createGetUrl;
import static org.openhubframework.openhub.test.rest.TestRestUtils.createJson;
import static org.openhubframework.openhub.test.rest.TestRestUtils.toUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import javax.json.JsonObject;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ReflectionUtils;

import org.openhubframework.openhub.admin.AbstractAdminModuleRestTest;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.entity.MessageFilter;
import org.openhubframework.openhub.api.entity.MsgStateEnum;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.core.common.asynch.msg.MessageOperationService;
import org.openhubframework.openhub.spi.msg.MessageService;
import org.openhubframework.openhub.test.data.EntityTypeTestEnum;
import org.openhubframework.openhub.test.data.ErrorTestEnum;
import org.openhubframework.openhub.test.data.ExternalSystemTestEnum;
import org.openhubframework.openhub.test.data.ServiceTestEnum;


/**
 * Simple test suite for {@link MessageController}.
 *
 * @author Karel Kovarik
 */
@TestPropertySource(properties = {
        "ohf.admin.console.messages.limit = 42"
})
public class MessageControllerTest extends AbstractAdminModuleRestTest {

    private static final String ROOT_URI = MessageController.REST_URI;

    // mocked messageService
    @MockBean
    private MessageService messageService;

    @MockBean
    private MessageOperationService messageOperationService;

    @Test
    public void list_minimalOk() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI)
                .addParameter("receivedFrom", "2017-05-28T11:47:28+02:00")
                ;

        final ArgumentCaptor<MessageFilter> argumentCaptor = ArgumentCaptor.forClass(MessageFilter.class);
        Mockito.when(messageService.findMessagesByFilter(argumentCaptor.capture(), eq(42L)))
                .thenReturn(Collections.emptyList());

        // GET /api/messages
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.limit", is(42)))
                .andExpect(jsonPath("$.totalElements", is(0)))
        ;

        final MessageFilter filter = argumentCaptor.getValue();
        assertThat(filter.getReceivedFrom(), is(Instant.parse("2017-05-28T09:47:28Z")));
    }

    @Test
    public void list_Ok() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI)
                .addParameter("receivedFrom", "2017-05-28T11:47:28+02:00")
                .addParameter("receivedTo", "2017-05-30T11:47:28+02:00")
                .addParameter("lastChangeFrom", "2017-04-30T11:47:28+02:00")
                .addParameter("lastChangeTo", "2017-06-30T11:47:28+02:00")
                .addParameter("sourceSystem", "CRM")
                .addParameter("correlationId", "20301-2332-1321")
                .addParameter("processId", "10231-2311-1144")
                .addParameter("state", "OK")
                .addParameter("errorCode", "E114")
                .addParameter("serviceName", "HELLO")
                .addParameter("operationName", "check")
                .addParameter("fulltext", "fulltext-message")
                ;

        final ZonedDateTime dateTime =
                LocalDateTime
                        .of(2017,5,27,19,5,10)
                        .atZone(ZoneId.systemDefault());

        final Message msg = new Message();
        msg.setId(84L);
        msg.setCorrelationId("20301-2332-1321");
        msg.setProcessId("10231-2311-1144");
        msg.setState(MsgStateEnum.OK);
        msg.setStartProcessTimestamp(dateTime.plusMinutes(1).toInstant());
        msg.setLastUpdateTimestamp(dateTime.plusHours(2).toInstant());
        msg.setFailedErrorCode(new ErrorExtEnum() {
            @Override
            public String getErrorCode() {
                return ErrorTestEnum.E300.getErrorCode();
            }

            @Override
            public String getErrDesc() {
                return ErrorTestEnum.E300.getErrDesc();
            }
        });
        msg.setFailedCount(3);
        msg.setSourceSystem(() -> "CRM");
        msg.setReceiveTimestamp(dateTime.plusHours(1).toInstant());
        msg.setMsgTimestamp(dateTime.toInstant());
        msg.setService(() -> "CUSTOMER");
        msg.setOperationName("setCustomer");
        msg.setObjectId("customer42");
        msg.setEntityType(() -> "ACCOUNT");
        msg.setFunnelValue("MSISDN");
        msg.setFunnelComponentId("componentId");
        msg.setGuaranteedOrder(Boolean.TRUE);
        msg.setExcludeFailedState(Boolean.TRUE);
        msg.setBusinessError("Not enough balance in the account");
        msg.setParentMsgId(333L);
        msg.setPayload("<hel:hello>Hello</hel:hello>");
        msg.setEnvelope("<soap:envelope><hel:hello>Hello</hel:hello></soap:envelope>");
        msg.setFailedDesc("Something went terribly wrong");

        final ArgumentCaptor<MessageFilter> argumentCaptor = ArgumentCaptor.forClass(MessageFilter.class);
        Mockito.when(messageService.findMessagesByFilter(argumentCaptor.capture(), eq(42L)))
                .thenReturn(Collections.singletonList(msg));

        // GET /api/messages
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(84)))
                .andExpect(jsonPath("$.data[0].correlationId", is("20301-2332-1321")))
                .andExpect(jsonPath("$.data[0].sourceSystem", is("CRM")))
                .andExpect(jsonPath("$.data[0].received", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 20, 5, 10)))))
                .andExpect(jsonPath("$.data[0].processingStarted", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 19, 6, 10)))))
                .andExpect(jsonPath("$.data[0].state", is("OK")))
                .andExpect(jsonPath("$.data[0].errorCode", is("E300")))
                .andExpect(jsonPath("$.data[0].serviceName", is("CUSTOMER")))
                .andExpect(jsonPath("$.data[0].operationName", is("setCustomer")))
                .andExpect(jsonPath("$.limit", is(42)))
                .andExpect(jsonPath("$.totalElements", is(1)))
        ;

        final MessageFilter filter = argumentCaptor.getValue();
        assertThat(filter.getReceivedFrom(), is(Instant.parse("2017-05-28T09:47:28Z")));
        assertThat(filter.getReceivedTo(), is(Instant.parse("2017-05-30T09:47:28Z")));
        assertThat(filter.getLastChangeFrom(), is(Instant.parse("2017-04-30T09:47:28Z")));
        assertThat(filter.getLastChangeTo(), is(Instant.parse("2017-06-30T09:47:28Z")));
        assertThat(filter.getSourceSystem(), is("CRM"));
        assertThat(filter.getCorrelationId(), is("20301-2332-1321"));
        assertThat(filter.getProcessId(), is("10231-2311-1144"));
        assertThat(filter.getState(), is(MsgStateEnum.OK));
        assertThat(filter.getErrorCode(), is("E114"));
        assertThat(filter.getServiceName(), is("HELLO"));
        assertThat(filter.getOperationName(), is("check"));
        assertThat(filter.getFulltext(), is("fulltext-message"));
    }

    @Test
    public void list_badRequest_receivedFrom() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI)
                // without mandatory receivedFrom field
                ;

        // GET /api/messages
        mockMvc.perform(get(toUrl(uriBuilder))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void detail_ok() throws Exception {
        final ZonedDateTime zdt =
                LocalDateTime
                        .of(2017,5,27,19,5,10)
                        .atZone(ZoneId.systemDefault());

        final Message msg = new Message();
        msg.setId(42L);
        msg.setCorrelationId("123-456");
        msg.setProcessId("789-654");
        msg.setState(MsgStateEnum.PROCESSING);
        msg.setStartProcessTimestamp(zdt.plusMinutes(1).toInstant());
        msg.setLastUpdateTimestamp(zdt.plusHours(2).toInstant());
        msg.setFailedErrorCode(ErrorTestEnum.E300);
        msg.setFailedCount(3);
        msg.setSourceSystem(ExternalSystemTestEnum.CRM);
        msg.setReceiveTimestamp(zdt.plusHours(1).toInstant());
        msg.setMsgTimestamp(zdt.toInstant());
        msg.setService(ServiceTestEnum.CUSTOMER);
        msg.setOperationName("setCustomer");
        msg.setObjectId("customer42");
        msg.setEntityType(EntityTypeTestEnum.ACCOUNT);
        msg.setFunnelValue("MSISDN");
        msg.setFunnelComponentId("componentId");
        msg.setGuaranteedOrder(Boolean.TRUE);
        msg.setExcludeFailedState(Boolean.TRUE);
        msg.setBusinessError("Not enough balance in the account");
        msg.setParentMsgId(333L);
        msg.setPayload("<hel:hello>Hello</hel:hello>");
        msg.setEnvelope("<soap:envelope><hel:hello>Hello</hel:hello></soap:envelope>");
        msg.setFailedDesc("Something went terribly wrong");

        final Request request = new Request();
        request.setId(421L);
        request.setUri("spring-ws:http://helloservice.com");
        request.setReqTimestamp(zdt.minusSeconds(1).toInstant());
        request.setRequest("request-body");

        final ExternalCall externalCall = new ExternalCall();
        externalCall.setId(327L);
        externalCall.setState(ExternalCallStateEnum.OK);
        externalCall.setOperationName("setCustomerExtCall");
        externalCall.setLastUpdateTimestamp(zdt.minusSeconds(2).toInstant());
        externalCall.setEntityId("CRM_4yEW32321");

        final Field field = ReflectionUtils.findField(Message.class, "requests");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, msg, Collections.singleton(request));
        final Field externalCallField = ReflectionUtils.findField(Message.class, "externalCalls");
        ReflectionUtils.makeAccessible(externalCallField);
        ReflectionUtils.setField(externalCallField, msg, Collections.singleton(externalCall));

        Mockito.when(messageService.findEagerMessageById(42L))
                .thenReturn(msg);
        // GET /api/messages/{id}
        mockMvc.perform(get(toUrl(createGetUrl(ROOT_URI + "/42")))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(42)))
                .andExpect(jsonPath("correlationId", is("123-456")))
                .andExpect(jsonPath("processId", is("789-654")))
                .andExpect(jsonPath("state", is("PROCESSING")))
                .andExpect(jsonPath("processingStarted", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 19, 6, 10)))))
                .andExpect(jsonPath("lastChange", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 21, 5, 10)))))
                .andExpect(jsonPath("errorCode", is("E300")))
                .andExpect(jsonPath("failedCount", is(3)))
                .andExpect(jsonPath("sourceSystem", is("CRM")))
                .andExpect(jsonPath("received", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 20, 5, 10)))))
                .andExpect(jsonPath("msgTimestamp", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 19, 5, 10)))))
                .andExpect(jsonPath("serviceName", is("CUSTOMER")))
                .andExpect(jsonPath("operationName", is("setCustomer")))
                .andExpect(jsonPath("objectId", is("customer42")))
                .andExpect(jsonPath("entityType", is("ACCOUNT")))
                .andExpect(jsonPath("funnelValue", is("MSISDN")))
                .andExpect(jsonPath("funnelComponentId", is("componentId")))
                .andExpect(jsonPath("guaranteedOrder", is(true)))
                .andExpect(jsonPath("excludeFailedState", is(true)))
                .andExpect(jsonPath("businessError", is("Not enough balance in the account")))
                .andExpect(jsonPath("parentMsgId", is(333)))
                .andExpect(jsonPath("body", is("<hel:hello>Hello</hel:hello>")))
                .andExpect(jsonPath("envelope", is("<soap:envelope><hel:hello>Hello</hel:hello></soap:envelope>")))
                .andExpect(jsonPath("failedDescription", is("Something went terribly wrong")))
                .andExpect(jsonPath("requests", hasSize(1)))
                .andExpect(jsonPath("requests[0].id", is(421)))
                .andExpect(jsonPath("requests[0].uri", is("spring-ws:http://helloservice.com")))
                .andExpect(jsonPath("requests[0].timestamp", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 19, 5, 9)))))
                .andExpect(jsonPath("requests[0].payload", is("request-body")))
                .andExpect(jsonPath("externalCalls", hasSize(1)))
                .andExpect(jsonPath("externalCalls[0].id", is(327)))
                .andExpect(jsonPath("externalCalls[0].state", is("OK")))
                .andExpect(jsonPath("externalCalls[0].operationName", is("setCustomerExtCall")))
                .andExpect(jsonPath("externalCalls[0].callId", is("CRM_4yEW32321")))
                .andExpect(jsonPath("externalCalls[0].lastChange", is(systemDefaultIsoDateTime(LocalDateTime.of(2017, 5, 27, 19, 5, 8)))))
                .andExpect(jsonPath("allowedActions", hasSize(1)))
                .andExpect(jsonPath("allowedActions[0]", is("CANCEL")))
        ;
    }

    @Test
    public void detail_notFound() throws Exception {
        Mockito.when(messageService.findEagerMessageById(42L))
                .thenReturn(null); // not found

        // GET /api/messages/{id}
        mockMvc.perform(get(toUrl(createGetUrl(ROOT_URI + "/42")))
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isNotFound())
        ;

        Mockito.verify(messageService, times(1)).findEagerMessageById(eq(42L));
    }

    @Test
    public void action_restart_ok() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "RESTART")
                .add("data", createJson()
                        .add("totalRestart", "false"))
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result", is("OK")))
        ;

        Mockito.verify(messageOperationService, times(1)).restartMessage(eq(42L), eq(false));
    }

    @Test
    public void action_restart_totalRestart() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "RESTART")
                .add("data", createJson()
                        .add("totalRestart", "true"))
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result", is("OK")))
        ;

        Mockito.verify(messageOperationService, times(1)).restartMessage(eq(42L), eq(true));
    }


    @Test
    public void action_cancel_ok() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "CANCEL")
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result", is("OK")))
                ;

        Mockito.verify(messageOperationService, times(1)).cancelMessage(eq(42L));
    }

    @Test
    public void action_restart_badRequest() throws Exception {
        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "RESTART")
                .add("data", createJson()
                        .add("nothing", "true"))
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(isEmptyString()));
    }

    @Test
    public void action_cancel_exception() throws Exception {
        Mockito.doThrow(new IllegalStateException("Cancel failed"))
                .when(messageOperationService).cancelMessage(eq(42L));

        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "CANCEL")
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("result", is("ERROR")))
        ;

        Mockito.verify(messageOperationService, times(1)).cancelMessage(eq(42L));
    }

    @Test
    public void action_restart_exception() throws Exception {
        Mockito.doThrow(new IllegalStateException("Restart failed"))
                .when(messageOperationService).restartMessage(eq(42L), eq(true));

        final URIBuilder uriBuilder = createGetUrl(ROOT_URI + "/42/action");

        JsonObject request = createJson()
                .add("type", "RESTART")
                .add("data", createJson()
                        .add("totalRestart", "true"))
                .build();

        // performs POST: /api/messages/42/action
        mockMvc.perform(post(toUrl(uriBuilder))
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication("ADMIN"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("result", is("ERROR")))
                ;

        Mockito.verify(messageOperationService, times(1)).restartMessage(eq(42L), eq(true));
    }

    private static String systemDefaultIsoDateTime(final LocalDateTime localDateTime) {
        return "" + localDateTime.atOffset(OffsetDateTime.now().getOffset());
    }
}