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

package org.openhubframework.openhub.admin.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.openhubframework.openhub.admin.dao.MessageReportDao;
import org.openhubframework.openhub.admin.dao.dto.MessageReportDto;


/**
 * Implementation of {@link MessageReportService}.
 *
 * @author Viliam Elischer
 */
@Service
public class MessageReportServiceImpl implements MessageReportService {

    @Autowired
    private MessageReportDao dao;

    @Override
    public List<MessageReportDto> getMessageStateSummary(Instant startDate, Instant endDate) {
        Assert.notNull(startDate, "startDate mustn't be null");
        Assert.notNull(endDate, "startDate mustn't be null");

        // adjust dates to start from 0.00 and end 23.59
        Instant from = startDate.truncatedTo(ChronoUnit.DAYS);
        Instant to = from.plus(1, ChronoUnit.DAYS);

        return dao.getMessageStateSummary(from, to);
    }
}
