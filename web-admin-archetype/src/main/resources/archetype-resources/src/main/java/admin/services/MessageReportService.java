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

package ${package}.admin.services;

import java.util.Date;
import java.util.List;

import ${package}.admin.dao.dto.MessageReportDto;


/**
 * Service for retrieving data for reports about messages.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
public interface MessageReportService {

    /**
     * Returns list of aggregated values about messages in specified time interval.
     * Messages are aggregated and also ordered by service, operation name, source system and state.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return list of {@link MessageReportDto aggregated DTOs}
     */
    public List<MessageReportDto> getMessageStateSummary(Date startDate, Date endDate);
}
