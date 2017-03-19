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

package org.openhubframework.openhub.admin.web.msg;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.openhubframework.openhub.admin.dao.dto.MessageReportDto;
import org.openhubframework.openhub.admin.services.MessageReportService;


/**
 * Controller for displaying the message report list and updated report based on the startDate; endDate parameters.
 *
 * @author Viliam Elischer
 */
@Controller
@RequestMapping("/messages")
public class MessageReportController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReportController.class);

    private static final int DEFAULT_INTERVAL_DAYS = 7;

    @Autowired
    private MessageReportService msgReportService;

    /**
     * Initial controller method, when the user lands on the page /admin/messageReport.
     * By default is loaded the messageReportList based on the current date - 7 days&gt; and the current date
     *
     * @param model controller object for binding variables into the view template
     * @return renders the msgRep view -&gt; /freemarker/msgRep.ftl
     */
    @RequestMapping(value = "/messageReport", method = RequestMethod.GET)
    public String showMessageReport(@ModelAttribute("model") ModelMap model) {

        Instant to = Instant.now();
        Instant from = Instant.now().minus(DEFAULT_INTERVAL_DAYS, ChronoUnit.DAYS);

        List<TransformedMessageDto> messageReportList = transformToView(msgReportService.getMessageStateSummary(from, to));

        model.addAttribute("msgreplist", messageReportList);
        return "msgRep";
    }

    /**
     * Renders the view with the object List {@link TransformedMessageDto} based on a time span data from the user.
     *
     * @param startDate user-input from the web form
     * @param endDate   user-input from the web form
     * @param model     controller object for binding variables into the view template
     * @return renders the updated msgRep view -&gt; /freemarker/msgRep.ftl
     */
    @RequestMapping(value = "/updatereport", method = RequestMethod.POST)
    public String updateMessageReport(@RequestParam("formStartDate") String startDate,
                                      @RequestParam("formEndDate") String endDate,
                                      @ModelAttribute("model") ModelMap model) {

        Assert.notNull(startDate, "startDate on updateReport must be defined");
        Assert.notNull(endDate, "endDate on updateReport must be defined");

        Instant from = Instant.from(formatDateForQuery(startDate));
        Instant to = Instant.from(formatDateForQuery(endDate));

        List<TransformedMessageDto> messageReportList = transformToView(msgReportService.getMessageStateSummary(from, to));

        model.addAttribute("reqStartDate", startDate);
        model.addAttribute("reqEndDate", endDate);
        model.addAttribute("msgreplist", messageReportList);
        return "msgRep";
    }

    /**
     * Date formatting method, JS function and GET method transfer a String object, that needs to be parsed.
     *
     * @param paramFromWeb user-friendly web-form String object
     * @return date object for the DB query method of the DAO impl
     */
    private LocalDate formatDateForQuery(String paramFromWeb) {
        LocalDate myDate = null;
        try {
            myDate = LocalDate.parse(paramFromWeb, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            LOG.error("Parse exception occurred during parsing '{}'", e, paramFromWeb);
        }
        return myDate;
    }

    /**
     * This method is used for the processing of the raw SQL DB output into an new appropriate format for the view.
     *
     * @param raw the raw list of DTOs
     * @return list of {@link TransformedMessageDto} objects aggregated by the State + corresponding stateCount
     */
    private List<TransformedMessageDto> transformToView(List<MessageReportDto> raw) {
        List<TransformedMessageDto> result = new ArrayList<TransformedMessageDto>();
        TransformedMessageDto last = null;

        for (MessageReportDto item : raw) {
            if (null == last) {
                last = new TransformedMessageDto();
                last.fill(item);
                result.add(last);
            } else if(last.differs(item)) {
                last = new TransformedMessageDto();
                last.fill(item);
                result.add(last);
            } else {
                last.add(item);
            }
        }

        return result;
    }
}
