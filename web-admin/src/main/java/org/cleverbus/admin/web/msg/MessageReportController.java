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

package org.cleverbus.admin.web.msg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cleverbus.admin.dao.dto.MessageReportDto;
import org.cleverbus.admin.services.MessageReportService;
import org.cleverbus.common.log.Log;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for displaying the message report list and updated report based on the startDate; endDate parameters.
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 */
@Controller
@RequestMapping("/messages")
public class MessageReportController {

    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private MessageReportService msgReportService;

    /**
     * Initial controller method, when the user lands on the page /admin/messageReport.
     * By default is loaded the messageReportList based on the current date - 7 days> and the current date
     *
     * @param model controller object for binding variables into the view template
     * @return renders the msgRep view -> /freemarker/msgRep.ftl
     */
    @RequestMapping(value = "/messageReport", method = RequestMethod.GET)
    public String showMessageReport(@ModelAttribute("model") ModelMap model) {

        List<TransformedMessageDto> messageReportList = transformToView(
                msgReportService.getMessageStateSummary(getDateForQuery(7), getDateForQuery(0)));

        model.addAttribute("msgreplist", messageReportList);
        return "msgRep";
    }

    /**
     * Renders the view with the object List {@link TransformedMessageDto} based on a time span data from the user.
     *
     * @param startDate user-input from the web form
     * @param endDate   user-input from the web form
     * @param model     controller object for binding variables into the view template
     * @return renders the updated msgRep view -> /freemarker/msgRep.ftl
     */
    @RequestMapping(value = "/updatereport", method = RequestMethod.POST)
    public String updateMessageReport(@RequestParam("formStartDate") String startDate,
                                      @RequestParam("formEndDate") String endDate,
                                      @ModelAttribute("model") ModelMap model) {

        Assert.notNull(startDate, "startDate on updateReport must be defined");
        Assert.notNull(endDate, "endDate on updateReport must be defined");

        List<TransformedMessageDto> messageReportList = transformToView(
                msgReportService.getMessageStateSummary(formatDateForQuery(startDate), formatDateForQuery(endDate)));

        model.addAttribute("reqStartDate", startDate);
        model.addAttribute("reqEndDate", endDate);
        model.addAttribute("msgreplist", messageReportList);
        return "msgRep";
    }

    /**
     * Method generates a date object base on the daysShift param.
     *
     * @param daysShift 0 means now, 7 means (now, -7 days)
     * @return date object
     */
    private Date getDateForQuery(int daysShift) {
        Date queryDate = new Date();
        if (daysShift > 0) {
            queryDate = DateUtils.addDays(queryDate, -daysShift);
        }
        return queryDate;
    }

    /**
     * Date formatting method, JS function and GET method transfer a String object, that needs to be parsed.
     *
     * @param paramFromWeb user-friendly web-form String object
     * @return date object for the DB query method of the DAO impl
     */
    private Date formatDateForQuery(String paramFromWeb) {
        Date myDate = null;
        try {
            myDate = inputDateFormat.parse(paramFromWeb);
        } catch (ParseException e) {
            Log.error("Parse exception occurred in MessageDaoImpl.dateHandler", e);
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
