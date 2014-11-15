#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.services;

import java.util.Date;
import java.util.List;

import ${package}.dao.MessageReportDao;
import ${package}.dao.dto.MessageReportDto;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


/**
 * Implementation of {@link MessageReportService}.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
@Service
public class MessageReportServiceImpl implements MessageReportService {

    @Autowired
    private MessageReportDao dao;

    @Override
    public List<MessageReportDto> getMessageStateSummary(Date startDate, Date endDate) {
        Assert.notNull(startDate, "startDate mustn't be null");
        Assert.notNull(endDate, "startDate mustn't be null");

        // adjust dates to start from 0.00 and end 23.59
        DateTime from = new DateTime(startDate).withTimeAtStartOfDay();
        DateTime to = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay();

        return dao.getMessageStateSummary(from.toDate(), to.toDate());
    }
}
