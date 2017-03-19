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

package org.openhubframework.openhub.admin.web.reqresp;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.admin.services.log.LogParserConstants;
import org.openhubframework.openhub.admin.web.common.editor.LogbackIso8601DateTimeEditor;
import org.openhubframework.openhub.api.entity.Request;
import org.openhubframework.openhub.core.reqres.RequestResponseService;

/**
 * Controller that encapsulates actions around request/response tracking.
 *
 * @author Tomas Hanus
 * @since 0.4
 */
@Controller
@RequestMapping("/reqResp")
public class RequestResponseController {

    private static final String ATTR_FROM_DATE = "fromDate";

    private static final String ATTR_TO_DATE = "toDate";

    private final RequestResponseService requestResponseService;

    @Autowired
    public RequestResponseController(RequestResponseService requestResponseService) {
        this.requestResponseService = requestResponseService;
    }

    @RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
    public String searchRequests(
                @RequestParam(value = ATTR_FROM_DATE, required = false) OffsetDateTime fromDate,
                @RequestParam(value = ATTR_TO_DATE, required = false) OffsetDateTime toDate,
                @RequestParam(value = "uri", required = false) String uri,
                @RequestParam(value = "content", required = false) String content,
                @ModelAttribute("model") ModelMap model) {

        if (fromDate != null && toDate != null) {
            List<Request> requestList =
                    requestResponseService.findByCriteria(fromDate.toInstant(), toDate.toInstant(), uri, content);

            model.addAttribute(ATTR_FROM_DATE, fromDate.format(LogParserConstants.LOGBACK_ISO8601_FORMATTER));
            model.addAttribute(ATTR_TO_DATE, toDate.format(LogParserConstants.LOGBACK_ISO8601_FORMATTER));

            if (!requestList.isEmpty()) {
                model.addAttribute("requestList", requestList);
            } else {
                model.addAttribute("emptyList", Boolean.TRUE);
            }
        } else {
            // set current date/time - from is minus 1 hour
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime from = now.minusHours(1).truncatedTo(ChronoUnit.HOURS);
            OffsetDateTime to = now.truncatedTo(ChronoUnit.SECONDS);

            model.addAttribute(ATTR_FROM_DATE, from.format(LogParserConstants.LOGBACK_ISO8601_FORMATTER));
            model.addAttribute(ATTR_TO_DATE, to.format(LogParserConstants.LOGBACK_ISO8601_FORMATTER));
        }

        model.addAttribute("uri", uri);
        model.addAttribute("content", content);

        return "reqRespSearch";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(OffsetDateTime.class, new LogbackIso8601DateTimeEditor());
    }
}
