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

import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import org.openhubframework.openhub.api.entity.Message;
import org.openhubframework.openhub.modules.ExternalSystemEnum;
import org.openhubframework.openhub.spi.msg.MessageService;


/**
 * Controller that encapsulates actions around messages.
 *
 * @author Petr Juza
 * @author Tomas Hanus
 */
@Controller("LegacyMessageControler")
@RequestMapping("/messages")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageLogParser messageLogParser;

    @RequestMapping(value = "/{msgId}", method = RequestMethod.GET)
    public String getMsgDetailByMsgId(@PathVariable("msgId") Long msgId, Model model)  {
        Message msg = messageService.findEagerMessageById(msgId);

        model.addAttribute("requestMsgId", msgId);

        if (msg != null) {
            // pretty-print envelope
            if (StringUtils.isNotEmpty(msg.getEnvelope())) {
                msg.setEnvelope(prettyPrintXML(msg.getEnvelope()));
            }
            // pretty-print payload
            if (StringUtils.isNotEmpty(msg.getPayload())) {
                msg.setPayload(prettyPrintXML(msg.getPayload()));
            }

            model.addAttribute("msg", msg);
        }

        return "msg";
    }

    @RequestMapping(value = "/{msgId}/log", method = RequestMethod.GET)
    public String getLogOfMsgByMsgId(@PathVariable("msgId") Long msgId, Model model)  {
        Message msg = messageService.findMessageById(msgId);

        model.addAttribute("requestMsgId", msgId);

        if (msg != null) {
            String correlationId = msg.getCorrelationId();

            model.addAttribute("correlationId", correlationId);

            List<String> logLines = new ArrayList<String>();
            try {
                long start = System.currentTimeMillis();
                SortedSet<Instant> logDates = getMsgDates(msg);
                logDates.add(Instant.now()); // adding today just in case

                LOG.debug("Starts searching log for correlationId = " + correlationId);

                for (Instant logDate : logDates) {
                    logLines.addAll(messageLogParser.getLogLines(correlationId, logDate));
                }

                LOG.debug("Finished searching log in " + (System.currentTimeMillis() - start) + " ms.");

                model.addAttribute("log", StringUtils.join(logLines, "\n"));
            } catch (IOException ex) {
                model.addAttribute("logErr", "Error occurred during reading log file: " + ex.getMessage());
            }
        }

        return "msgLog";
    }

    /**
     * Creates a set of message dates, to search logs for these dates.
     * This set contains just the dates of the following timestamps:
     * <ul>
     * <li>source system timestamp</li>
     * <li>received timestamp</li>
     * <li>start processing timestamp</li>
     * <li>last update timestamp</li>
     * </ul>
     * It's a Set, so that only distinct dates will be included.
     * It's a SortedSet, so that the dates are in their natural order.
     * The elements are LocalDate (without time),
     * so that the dates will be in the correct order
     * and the set will correctly not include duplicates.
     *
     * @param msg the message to find dates for
     * @return a set of dates
     */
    private SortedSet<Instant> getMsgDates(Message msg) {
        TreeSet<Instant> logDates = new TreeSet<Instant>();
        Instant[] msgDates = new Instant[]{
                msg.getMsgTimestamp(),
                msg.getReceiveTimestamp(),
                msg.getStartProcessTimestamp(),
                msg.getLastUpdateTimestamp()
        };
        for (Instant msgDate : msgDates) {
            if (msgDate != null) {
                logDates.add(msgDate);
            }
        }
        return logDates;
    }

    @RequestMapping(value = "/searchForm", method = RequestMethod.GET)
    public String getSearchForm(@RequestParam(value = "source", required = false) ExternalSystemEnum sourceSystem,
            @RequestParam(value = "correlation", required = false) String correlationId,
            @ModelAttribute("model") ModelMap model)  {

        addExternalSystemsIntoModel(model);

        if (hasText(correlationId)) {
            Message msg = messageService.findMessageByCorrelationId(correlationId,
                    sourceSystem == ExternalSystemEnum.UNDEFINED ? null : sourceSystem);
            if (msg != null) {
                return "redirect:/web/admin/messages/" + msg.getMsgId();
            } else {
                model.addAttribute("resultFailed", Boolean.TRUE);

                return "msg_search";
            }
        }

        model.put("source", sourceSystem);
        model.put("correlation", correlationId);

        return "msg_search";
    }

    private void addExternalSystemsIntoModel(ModelMap model) {
        Map<String, String> systemsMap = new LinkedHashMap<String, String>();
        for (ExternalSystemEnum systemEnum : ExternalSystemEnum.values()) {
            systemsMap.put(systemEnum.name(), systemEnum.name());
        }

        model.addAttribute("systemsMap", systemsMap);
    }

    @RequestMapping(value = "/searchForm", method = RequestMethod.POST)
    public String searchMessage(@RequestParam("sourceSystem") ExternalSystemEnum sourceSystem,
            @RequestParam("correlationId") String correlationId,
            @ModelAttribute("model") ModelMap model)  {

        Message msg = messageService.findMessageByCorrelationId(correlationId,
                sourceSystem == ExternalSystemEnum.UNDEFINED ? null : sourceSystem);

        if (msg != null) {
            return "redirect:/web/admin/messages/" + msg.getMsgId();
        } else {
            addExternalSystemsIntoModel(model);
            model.addAttribute("resultFailed", Boolean.TRUE);

            return "msg_search";
        }
    }

    @RequestMapping(value = "/messagesByContent", method = { RequestMethod.GET, RequestMethod.POST })
    public String showMessagesByContent(@RequestParam(value = "substring", required = false) String substring,
            @ModelAttribute("model") ModelMap model) {

        if (StringUtils.isNotBlank(substring)) {
            List<Message> messageList = messageService.findMessagesByContent(substring);

            if (!messageList.isEmpty()) {
                model.addAttribute("messageList", messageList);
            }
            model.put("substring", substring);
        }

        return "msgByContent";
    }

    /**
     * Converts input XML to "nice" XML.
     *
     * @param original the original XML
     * @return pretty XML
     */
    static String prettyPrintXML(String original) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            Document document = DocumentHelper.parseText(original);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            return sw.toString();
        } catch (Exception exc) {
            LOG.debug("Error pretty-printing XML: ", exc);
            return original;
        }
    }
}
