<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">

    <h4><@spring.message "msg.searchByContent.title"/></h4>

    <div id="wrapper">

        <div id="dateops" class="ui-corner-all">
            <fieldset>
                <legend><@spring.message "msg.searchByContent.legend"/></legend>
                <form name="dateRange" action="messagesByContent" method="post">
                    <ul>
                        <li>
                            <label for="content" style="width: 200px;"><@spring.message "msg.searchByContent.string"/>:</label>
                            <input type="text" id="substring" name="substring" value="${model["substring"]!}"
                                   style="width: 200px;"/>
                        </li>
                        <li>
                            <label for="btns"><@spring.message "msg.searchByContent.action"/>:</label><input type="submit" value="<@spring.message "search"/>"/>

                            <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>
                        </li>
                    </ul>
                </form>
            </fieldset>
        </div>
        <br>

        <#if model["messageList"]??>
            <div id="results">
                <table class="bottomBorder">
                    <thead>
                    <tr>
                        <th><@spring.message "msg.msgId"/></th>
                        <th><@spring.message "msg.correlationId"/></th>
                        <th><@spring.message "msg.startTimestamp"/></th>
                        <th><@spring.message "msg.sourceSystem"/></th>
                        <th><@spring.message "msg.receiveTimestamp"/></th>
                        <th><@spring.message "msg.state"/></th>
                        <th><@spring.message "msg.errorCode"/></th>
                        <th><@spring.message "msg.serviceName"/></th>
                        <th><@spring.message "msg.operationName"/></th>
                        <th><@spring.message "msg.funnelValue"/></th>
                        <th class="last"><@spring.message "msg.objectId"/></th>
                    </tr>
                    </thead>
                    <#list model["messageList"] as messageList>
                        <tr class="${["odd", "even"][messageList_index % 2]}">
                            <td>
                                <a href="${rootContext}/web/admin/messages/${messageList.msgId?c}">${messageList.msgId?c}</a>
                            </td>
                            <td>${messageList.correlationId}&nbsp;</td>
                            <td>${messageList.startProcessTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
                            <td>${messageList.sourceSystem.systemName}&nbsp;</td>
                            <td>${messageList.receiveTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
                            <td class="${((messageList.state == "FAILED")?string("nok", "ok"))}"><strong>${messageList.state}<strong>&nbsp;</td>
                            <td><strong><#if messageList.failedErrorCode??>${messageList.failedErrorCode.getErrorCode()}</#if><strong>&nbsp;</td>
                            <td>${messageList.service.serviceName}&nbsp;</td>
                            <td>${messageList.operationName}</td>
                            <td><#if messageList.funnelValue??>${messageList.funnelValue}</#if>&nbsp;</td>
                            <td class="last"><#if messageList.objectId??>${messageList.objectId}</#if>&nbsp;</td>
                        </tr>
                    </#list>
                </table>
            </div>
            <br>
            <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>
        <#else>
            <#if model["substring"]??>
                <div class="clearBoth">
                    <@spring.message "msg.searchByContent.noMsg"/> = ${model["substring"]}.
                </div>
            </#if>
        </#if>
        <br>
    </div>
</div>

</@p.page>