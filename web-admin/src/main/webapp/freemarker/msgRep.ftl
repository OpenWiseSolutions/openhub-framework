<#import "lib/utils.ftl" as p>

<@p.page>

<script>

    function sDateFunc() {
        var today = new Date();
        today.setDate(today.getDate() - 7); // a week ago
        var dd = today.getDate();
        var mm = today.getMonth() + 1; //January is 0!
        var yyyy = today.getFullYear();
        if (dd < 10) {
            dd = '0' + dd
        }
        if (mm < 10) {
            mm = '0' + mm
        }
        today = dd + '.' + mm + '.' + yyyy;
        return today;
    }

    function eDateFunc() {
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth() + 1; //January is 0!
        var yyyy = today.getFullYear();
        if (dd < 10) {
            dd = '0' + dd
        }
        if (mm < 10) {
            mm = '0' + mm
        }
        today = dd + '.' + mm + '.' + yyyy;
        return today;
    }

    /* jQuery-ui Datepicker localization configuration */
    $.datepicker.regional['cs'] = {
        closeText: "<@spring.message "dp.close"/>", // Display text for close link
        prevText: "<@spring.message "dp.prev"/>", // Display text for previous month link
        nextText: "<@spring.message "dp.next"/>", // Display text for next month link
        currentText: "<@spring.message "dp.today"/>", // Display text for current month link
        monthNames: ["<@spring.message "dp.m1"/>", "<@spring.message "dp.m2"/>", "<@spring.message "dp.m3"/>", "<@spring.message "dp.m4"/>",
                "<@spring.message "dp.m5"/>", "<@spring.message "dp.m6"/>", "<@spring.message "dp.m7"/>",
                "<@spring.message "dp.m8"/>", "<@spring.message "dp.m9"/>", "<@spring.message "dp.m10"/>",
                "<@spring.message "dp.m11"/>", "<@spring.message "dp.m12"/>"], // Names of months for drop-down and formatting
        monthNamesShort: ["<@spring.message "dp.sm1"/>", "<@spring.message "dp.sm2"/>", "<@spring.message "dp.sm3"/>", "<@spring.message "dp.sm4"/>",
                "<@spring.message "dp.sm5"/>", "<@spring.message "dp.sm6"/>", "<@spring.message "dp.sm7"/>",
                "<@spring.message "dp.sm8"/>", "<@spring.message "dp.sm9"/>", "<@spring.message "dp.sm10"/>",
                "<@spring.message "dp.sm11"/>", "<@spring.message "dp.sm12"/>"], // For formatting
        dayNames: ["<@spring.message "dp.d7"/>", "<@spring.message "dp.d1"/>", "<@spring.message "dp.d2"/>",
                "<@spring.message "dp.d3"/>", "<@spring.message "dp.d4"/>", "<@spring.message "dp.d5"/>", "<@spring.message "dp.d6"/>"], // For formatting
        dayNamesShort: ['<@spring.message "dp.sd7"/>', '<@spring.message "dp.sd1"/>', '<@spring.message "dp.sd2"/>',
            '<@spring.message "dp.sd3"/>', '<@spring.message "dp.sd4"/>', '<@spring.message "dp.sd5"/>', '<@spring.message "dp.sd6"/>'],  // For formatting
        dayNamesMin: ["<@spring.message "dp.sd7"/>", "<@spring.message "dp.sd1"/>", "<@spring.message "dp.sd2"/>",
                    "<@spring.message "dp.sd3"/>", "<@spring.message "dp.sd4"/>", "<@spring.message "dp.sd5"/>", "<@spring.message "dp.sd6"/>"], // Column headings for days starting at Sunday
        weekHeader: "Sm", // Column header for week of the year
        dateFormat: "mm/dd/yy", // See format options on parseDate
        firstDay: 1, // The first day of the week, Sun = 0, Mon = 1, ...
        showMonthAfterYear: false
    };
    $.datepicker.setDefaults($.datepicker.regional['cs']);
    $(function () {
        var existsStartD = "${model["reqStartDate"]!}";
        var existsEndD = "${model["reqEndDate"]!}";
        $("#datefrom").datepicker({ dateFormat: "dd.mm.yy" }).val(existsStartD ? existsStartD : sDateFunc());
        $("#dateto").datepicker({ dateFormat: "dd.mm.yy" }).val(existsEndD ? existsEndD : eDateFunc());
    });
</script>

<div class="view">

    <h4><@spring.message "msg.rep.title"/></h4>

    <div id="wrapper">

        <div id="dateops" class="ui-corner-all">
            <fieldset>
                <form name="dateRange" action="updatereport" method="post">
                    <ul>
                        <li>
                            <label for="datefrom"><@spring.message "msg.rep.dateFrom"/>:</label><input type="text" id="datefrom"
                                                                          name="formStartDate"/>
                        <span class="ui-icon ui-icon-circle-triangle-w"
                              onclick="$('#datefrom').datepicker('setDate','c-7d');">- 7</span>
                        <span class="ui-icon ui-icon-circle-triangle-e"
                              onclick="$('#datefrom').datepicker('setDate','c+7d');">+ 7</span>
                        </li>
                        <li>
                            <label for="dateto"><@spring.message "msg.rep.dateTo"/>:</label><input type="text" id="dateto" name="formEndDate"/>
                        <span class="ui-icon ui-icon-circle-triangle-w"
                              onclick="$('#dateto').datepicker('setDate','c-7d');">- 7</span>
                        <span class="ui-icon ui-icon-circle-triangle-e"
                              onclick="$('#dateto').datepicker('setDate','c+7d');">+ 7</span>
                        </li>
                        <li>
                            <label for="btns"><@spring.message "msg.rep.action"/>:</label><input type="submit" value="<@spring.message "show"/>"/>
                        </li>
                    </ul>
                </form>
            </fieldset>
        </div>
        <br>

        <div id="results">
            <!-- IF the reqStartDate variable exists in POST request, the <p> element is displayed, by default onPageLoad it's not present -->
            <#if model["reqStartDate"]??><span class="notifier"><@spring.message "msg.rep.resultsFrom"/>: ${model["reqStartDate"]!}
                <@spring.message "msg.rep.resultsTo"/>: ${model["reqEndDate"]!}</span></#if>
            <table class="bottomBorder">
                <thead>
                <tr>
                    <th><@spring.message "msg.rep.nr"/></th>
                    <th><@spring.message "msg.rep.serviceName"/></th>
                    <th><@spring.message "msg.rep.opName"/></th>
                    <th><@spring.message "msg.rep.sourceSystem"/></th>
                    <th>OK</th>
                    <th>Processing</th>
                    <th>Partly failed</th>
                    <th>Failed</th>
                    <th>Waiting</th>
                    <th>Waiting for RES</th>
                    <th class="last">Cancel</th>

                </tr>
                </thead>
                <#list model["msgreplist"] as msgreplist>
                    <tr class="${["odd", "even"][msgreplist_index % 2]}">
                        <td>${msgreplist_index + 1}</td>
                        <td>${msgreplist.serviceName}</td>
                        <td>${msgreplist.operationName}</td>
                        <td>${msgreplist.sourceSystem}</td>
                        <td>${msgreplist.stateOK}</td>
                        <td>${msgreplist.stateProcessing}</td>
                        <td>${msgreplist.statePartlyFailed}</td>
                        <!-- Failed state colorization, if the field is empty == 0 "ok", else "nok" text-color:red -->
                        <td class="${((msgreplist.stateFailed == 0)?string("ok", "nok"))}">${msgreplist.stateFailed}</td>

                        <td>${msgreplist.stateWaiting}</td>
                        <td>${msgreplist.stateWaitingForRes}</td>
                        <td class="last">${msgreplist.stateCancel}</td>
                    </tr>
                </#list>
            </table>
        </div>
        <br>
    </div>

    <br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>