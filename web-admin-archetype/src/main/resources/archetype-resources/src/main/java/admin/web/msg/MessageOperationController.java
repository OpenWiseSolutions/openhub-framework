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

package ${package}.admin.web.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.cleverbus.core.common.asynch.msg.MessageOperationService;


/**
 * Controller that maps request from the Admin GUI to the message operations:
 * <ul>
 *   <li>message restart based on the msgID and type.
 *   Restart of the message = update the state from FAILED/CANCEL -> PARTLY FAILED in table [message] and [external_call]</li>
 *   <li>message cancel - only NEW and PARTLY_FAILED messages can be canceled</li>
 * </ul>
 *
 * @author <a href="mailto:viliam.elischer@cleverlance.com">Viliam Elischer</a>
 * @author <a href="mailto:petr.juza@cleverlance.com">Petr Juza</a>
 */
@Controller
@RequestMapping("/operations")
public class MessageOperationController {

    private static final String VIEW_NAME = "msgOps";

    private static final String RESTART_RESULT_ATTR = "resultRestartString";

    private static final String CANCEL_RESULT_ATTR = "resultCancelString";

    /** Message operation service result states */

    private static final String OP_NF = "MESSAGE NOT FOUND";

    private static final String RESTART_OK = "RESTART PERFORMED";

    private static final String RESTART_NOK = "RESTART FAILED";

    private static final String CANCEL_OK = "CANCEL PERFORMED";

    private static final String CANCEL_NOK = "CANCEL FAILED";

    @Autowired
    private MessageOperationService service;

    @RequestMapping(value = "/messageOperations", method = RequestMethod.GET)
    public String showMessageOps(@ModelAttribute("model") ModelMap modelMap) {
        return VIEW_NAME;
    }

    /**
     * The restart method - gets input from web-user and calls a service method
     * for updating the DB record based on the MSG ID.
     * <p/>
     * By default, when the checkbox is on web send as empty = the default value is not annotation driven, but manually
     * setup to false.
     *
     * @param modelMap     default view object for adding data to the view resolver (FTL)
     * @param messageID    user-input form parameter, used in the restart operation
     * @param totalRestart user-input, if the message should be restarted from scratch
     * @return name of the view
     */
    @RequestMapping(value = "/restartMessage", method = RequestMethod.POST)
    public String restartMessage(@ModelAttribute("model") ModelMap modelMap,
                                 @RequestParam("messageID") Long messageID,
                                 @RequestParam(value = "totalRestart", required = false) Boolean totalRestart) {

        if (totalRestart == null) {
            totalRestart = false;
        }

        if (messageID == null) {
            modelMap.addAttribute(RESTART_RESULT_ATTR, OP_NF);
        } else {
            try {
                service.restartMessage(messageID, totalRestart);
                modelMap.addAttribute(RESTART_RESULT_ATTR, RESTART_OK);
            } catch (RuntimeException rex) {
                modelMap.addAttribute(RESTART_RESULT_ATTR, RESTART_NOK + ": " + rex.getMessage());
            }
        }
        return VIEW_NAME;
    }

    /**
     * Cancel next message processing.
     * Only NEW and PARTLY_FAILED messages can be canceled.
     *
     * @param modelMap     default view object for adding data to the view resolver (FTL)
     * @param messageID    the message ID
     * @return name of the view
     */
    @RequestMapping(value = "/cancelMessage", method = RequestMethod.POST)
    public String cancelMessage(@ModelAttribute("model") ModelMap modelMap, @RequestParam("messageID") Long messageID) {

        if (messageID == null) {
            modelMap.addAttribute(CANCEL_RESULT_ATTR, OP_NF);
        } else {
            try {
                service.cancelMessage(messageID);
                modelMap.addAttribute(CANCEL_RESULT_ATTR, CANCEL_OK);
            } catch (RuntimeException rex) {
                modelMap.addAttribute(CANCEL_RESULT_ATTR, CANCEL_NOK + ": " + rex.getMessage());
            }
        }

        return VIEW_NAME;
    }
}
