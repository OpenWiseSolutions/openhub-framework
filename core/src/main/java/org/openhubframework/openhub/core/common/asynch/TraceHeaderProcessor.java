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

package org.openhubframework.openhub.core.common.asynch;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.api.asynch.model.TraceHeader;
import org.openhubframework.openhub.api.asynch.model.TraceIdentifier;
import org.openhubframework.openhub.api.common.ExchangeConstants;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.exception.ValidationIntegrationException;
import org.openhubframework.openhub.common.log.Log;
import org.openhubframework.openhub.core.common.validator.TraceIdentifierValidator;
import org.springframework.util.Assert;
import org.springframework.ws.soap.SoapHeaderElement;


/**
 * Processor that gain trace header information and save it as message header {@link #TRACE_HEADER} in exchange.
 * <p/>
 * Processor works with our input asynchronous messages (trace header is in SOAP header)
 * and also with input messages (trace header is in SOAP body).
 * <p/>
 * Trace header is mandatory by default but you can change it.
 *
 * @author Petr Juza
 */
public class TraceHeaderProcessor implements Processor {

    /**
     * Header value that holds {@link TraceHeader}.
     */
    public static final String TRACE_HEADER = ExchangeConstants.TRACE_HEADER;

    public static final String TRACE_HEADER_ELM = "traceHeader";

    private final JAXBContext jaxb2;
    private final ValidationEventHandler validationEventHandler = getValidationEventHandler();

    /**
     * {@code true} when trace header is mandatory, or {@code false} when nothing happens when there is no trace header.
     */
    private final boolean mandatoryHeader;

    /**
     * collection of trace identifier validators
     */
    private List<TraceIdentifierValidator> validatorList;

    /**
     * Creates immutable Trace Header processor.
     *
     * @param mandatoryHeader if trace header is mandatory
     * @param validatorList   the collection of trace identifier validators
     * @throws JAXBException
     */
    public TraceHeaderProcessor(boolean mandatoryHeader, @Nullable List<TraceIdentifierValidator> validatorList) throws
            JAXBException {
        jaxb2 = JAXBContext.newInstance(TraceHeader.class);
        this.mandatoryHeader = mandatoryHeader;
        this.validatorList = validatorList;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // check existence of trace header in the exchange
        if (exchange.getIn().getHeader(TRACE_HEADER) != null) {
            return;
        }

        SoapHeaderElement traceHeaderElm = exchange.getIn().getHeader(TRACE_HEADER_ELM, SoapHeaderElement.class);
        if (traceHeaderElm != null) {
            setFromTraceHeader(exchange, traceHeaderElm.getSource(), false);
            return;
        }

        try {
            // try unmarshalling body as TraceHeader (it could be TraceHeader child)
            Source traceHeaderElmSource = exchange.getIn().getBody(Source.class);
            if (traceHeaderElmSource != null) {
                setFromTraceHeader(exchange, traceHeaderElmSource, true);
                return;
            }
        } catch (JAXBException exc) {
            Log.debug("Failed to unmarshal body as TraceHeader", exc);
        }

        if (isMandatoryHeader()) {
            throw new ValidationIntegrationException(InternalErrorEnum.E104);
        }
    }

    private void setFromTraceHeader(Exchange exchange, Source traceHeaderElmSource, boolean headerInBody) throws JAXBException {
        // unmarshal
        Unmarshaller unmarshaller = jaxb2.createUnmarshaller();
        if (!headerInBody) {
            // if there is trace header in the body then error events are thrown because there are other elements
            //  in the body
            unmarshaller.setEventHandler(validationEventHandler);
        }

        TraceHeader traceHeader = unmarshaller.unmarshal(traceHeaderElmSource, TraceHeader.class).getValue();
        if (traceHeader == null) {
            if (isMandatoryHeader()) {
                throw new ValidationIntegrationException(InternalErrorEnum.E105, "there is no trace header");
            }
        } else {
            // validate header content
            TraceIdentifier traceId = traceHeader.getTraceIdentifier();
            if (traceId == null) {
                if (isMandatoryHeader()) {
                    throw new ValidationIntegrationException(InternalErrorEnum.E105, "there is no trace identifier");
                }
            } else {
                if (traceId.getApplicationID() == null) {
                    throw new ValidationIntegrationException(InternalErrorEnum.E105, "there is no application ID");

                } else if (traceId.getCorrelationID() == null) {
                    throw new ValidationIntegrationException(InternalErrorEnum.E105, "there is no correlation ID");

                } else if (traceId.getTimestamp() == null) {
                    throw new ValidationIntegrationException(InternalErrorEnum.E105, "there is no timestamp ID");
                }

                validateTraceIdentifier(traceId);
                exchange.getIn().setHeader(TRACE_HEADER, traceHeader);
                Log.debug("traceHeader saved to exchange: " + ToStringBuilder.reflectionToString(traceId));
            }
        }
    }

    private ValidationEventHandler getValidationEventHandler() {
        return new ValidationEventHandler() {
            public boolean handleEvent(ValidationEvent event) {
                if (event.getSeverity() == ValidationEvent.WARNING) {
                    Log.warn("Ignored {}", event, event.getLinkedException());
                    return true; // handled - ignore as WARNING does not prevent unmarshalling
                } else {
                    return false; // not handled - ERROR and FATAL_ERROR prevent successful unmarshalling
                }
            }
        };
    }

    /**
     * Checks that {@link TraceIdentifier} contains values which are valid.
     *
     * @param traceId the {@link TraceIdentifier}
     * @throws ValidationIntegrationException
     */
    private void validateTraceIdentifier(TraceIdentifier traceId) {
        Assert.notNull(traceId, "the traceId must not be null");

        // if not defined some implementation, the validation is skipped
        if (validatorList == null || validatorList.isEmpty()) {
            Log.debug("no traceIdentifier validator found");
            return;
        }

        for (TraceIdentifierValidator validator : validatorList) {
            if (validator.isValid(traceId)) {
                Log.debug("the trace identifier '{0}' is allowed", ToStringBuilder.reflectionToString(traceId));
                return;
            }
        }

        // trace identifier values was not found in any list of possible values
        throw new ValidationIntegrationException(InternalErrorEnum.E120,
                "the trace identifier '" + ToStringBuilder.reflectionToString(traceId,
                        ToStringStyle.SHORT_PREFIX_STYLE) + "' is not allowed");
    }

    /**
     * Returns {@code true} when trace header is mandatory,
     * or {@code false} when nothing happens when there is no trace header.
     *
     * @return if trace header is mandatory
     */
    public boolean isMandatoryHeader() {
        return mandatoryHeader;
    }

    /**
     * Returns list of {@link TraceIdentifierValidator}.
     *
     * @return the list of {@link TraceIdentifierValidator}
     */
    public List<TraceIdentifierValidator> getValidatorList() {
        return Collections.unmodifiableList(validatorList);
    }
}
