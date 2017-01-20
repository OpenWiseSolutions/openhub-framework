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

import static org.openhubframework.openhub.api.configuration.CoreProps.ASYNCH_EXTERNAL_CALL_SKIP_URI_PATTERN;

import java.util.regex.Pattern;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.openhubframework.openhub.api.configuration.ConfigurableValue;
import org.openhubframework.openhub.api.configuration.ConfigurationItem;
import org.openhubframework.openhub.api.entity.ExternalCall;
import org.openhubframework.openhub.api.entity.ExternalCallStateEnum;
import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.api.exception.LockFailureException;
import org.openhubframework.openhub.core.common.dao.ExternalCallDao;
import org.openhubframework.openhub.spi.extcall.ExternalCallService;


/**
 * Implementation of {@link ExternalCallService} interface.
 * Supports specifying a RegEx pattern to skip matched operation URIs.
 *
 * @author Petr Juza
 */
@Service
public class ExternalCallServiceImpl implements ExternalCallService {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalCallServiceImpl.class);

    // note: can't be used ConfigurationItem<Pattern> because property can be null/empty and then
    //  it's problem to create pattern, see PropertySourcesPropertyResolver.getProperty:
    //  java.lang.IllegalArgumentException: Cannot convert value [] from source type [String] to target type [Pattern]
    @ConfigurableValue(key = ASYNCH_EXTERNAL_CALL_SKIP_URI_PATTERN)
    private ConfigurationItem<String> skipOperationUriList;

    private Pattern uriPattern;

    @Autowired
    private ExternalCallDao extCallDao;

    @PostConstruct
    public void initPattern() {
        if (StringUtils.isNotEmpty(skipOperationUriList.getValue(null))) {
            uriPattern = Pattern.compile(skipOperationUriList.getValue());
        }
    }

    @Override
    @Transactional
    public ExternalCall prepare(String operationUri, String operationKey, Message message) {
        if (uriPattern != null && uriPattern.matcher(operationUri).matches()) {
            LOG.warn("Not allowing an external call for ignored operation URI: [{}] matches pattern [{}]",
                    operationUri, uriPattern);
            return null;
        }

        ExternalCall extCall = extCallDao.getExternalCall(operationUri, operationKey);
        LOG.debug("Locking msgId={}, extCall={}", message.getMsgId(), extCall);

        if (extCall == null) {
            // create a new call in the Processing state, if none registered so far
            extCall = ExternalCall.createProcessingCall(operationUri, operationKey, message);
            extCallDao.insert(extCall);
            LOG.debug("Locked msgId={}, extCall={}", message.getMsgId(), extCall);
            return extCall;
        }

        long extCallAge = message.getMsgTimestamp().getTime() - extCall.getMsgTimestamp().getTime();

        switch (extCall.getState()) {
            case PROCESSING:
                // don't allow two concurrent external calls to be made, even if this one is newer
                throw new LockFailureException(String.format(
                        "Another external call is currently being processed for uri=[%s] key=[%s] msgId=[%s]",
                        operationUri, operationKey, extCall.getMsgId()));
            case OK:
                if (extCallAge < 0) {
                    // the existing external call is younger/newer and is OK, skip this new call
                    LOG.warn("Not allowing an external call, since it's older than a successful call: {}", extCall);
                    return null;
                } else if (extCallAge == 0) {
                    // this external call already happened with OK result, skip it
                    LOG.info("Not allowing an external call, since it's a duplicate of a successful call: {}", extCall);
                    return null;
                }
                break;
            default:
                LOG.error("Unexpected ExternalCall State: " + extCall);
                // log error, but otherwise behave as if it was FAILED:
            case FAILED:
            case FAILED_END:
                if (extCallAge < 0) {
                    // the existing external call is younger/newer, skip this new call
                    LOG.warn("Not allowing an external call, since it's older than a failed call: {}", extCall);
                    return null;
                }
                break;
        }
        // mark the found external call as taken for processing by this message:
        extCallDao.lockExternalCall(extCall);
        extCall.setMessage(message);
        extCall.setMsgId(message.getMsgId());
        extCall.setMsgTimestamp(message.getMsgTimestamp());
        LOG.debug("Locked msgId={}, extCall={}", message.getMsgId(), extCall);
        return extCall;
    }

    @Override
    @Transactional
    public void complete(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the external call must be in PROCESSING state, but state is " + extCall.getState());
        extCall.setState(ExternalCallStateEnum.OK);
        extCallDao.update(extCall);
        LOG.debug("External call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.OK);
    }

    @Override
    @Transactional
    public void failed(ExternalCall extCall) {
        Assert.notNull(extCall, "the extCall must not be null");
        Assert.isTrue(extCall.getState() == ExternalCallStateEnum.PROCESSING,
                "the external call must be in PROCESSING state, but state is " + extCall.getState());
        extCall.setState(ExternalCallStateEnum.FAILED);
        extCallDao.update(extCall);
        LOG.debug("External call " + extCall.toHumanString() + " changed state to " + ExternalCallStateEnum.FAILED);
    }
}
