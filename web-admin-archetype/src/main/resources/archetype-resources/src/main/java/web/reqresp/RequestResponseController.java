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

package ${package}.web.reqresp;

import java.util.List;

import ${package}.services.log.LogParserConstants;
import ${package}.web.common.editor.DateTimeEditor;
import org.cleverbus.api.entity.Request;
import org.cleverbus.core.reqres.RequestResponseService;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller that encapsulates actions around request/response tracking.
 *
 * @author <a href="mailto:tomas.hanus@cleverlance.com">Tomas Hanus</a>
 * @since 0.4
 */
@Controller
@RequestMapping("/reqResp")
public class RequestResponseController {

    @Autowired
    private RequestResponseService requestResponseService;

    @RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
    public String searchRequests(
                @RequestParam(value = "fromDate", required = false) DateTime fromDate,
                @RequestParam(value = "toDate", required = false) DateTime toDate,
                @RequestParam(value = "uri", required = false) String uri,
                @RequestParam(value = "content", required = false) String content,
                @ModelAttribute("model") ModelMap model) {

        if (fromDate != null && toDate != null) {
            List<Request> requestList =
                    requestResponseService.findByCriteria(fromDate.toDate(), toDate.toDate(), uri, content);

            model.addAttribute("fromDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            fromDate));
            model.addAttribute("toDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            toDate));

            if (!requestList.isEmpty()) {
                model.addAttribute("requestList", requestList);
            } else {
                model.addAttribute("emptyList", Boolean.TRUE);
            }
        } else {

            model.addAttribute("fromDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            DateTime.now()
                                    .minusHours(1)
                                    .withMinuteOfHour(0)
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)));
            model.addAttribute("toDate",
                    LogParserConstants.LOGBACK_ISO8601_FORMAT.print(
                            DateTime.now()
                                    .withSecondOfMinute(0)
                                    .withMillisOfSecond(0)));
        }

        model.addAttribute("uri", uri);
        model.addAttribute("content", content);

        return "reqRespSearch";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(DateTime.class, new DateTimeEditor());
        binder.registerCustomEditor(DateMidnight.class, new DateTimeEditor());
    }

}
