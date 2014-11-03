<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
<h4><@spring.message "msg.detail.title"/></h4>

    <#if msg??>

    <a href="${rootContext}/web/admin/messages/${msg.msgId?c}/log">
        <button type="button"
                onclick="window.location='${rootContext}/web/admin/messages/${msg.msgId?c}/log';"><@spring.message "msg.detail.logRef"/></button>
    </a>
    <br/><br/>

    <table border="1" cellpadding="1" cellspacing="0" width="1200px">
        <thead>
        <tr style="background-color: #d3d3d3; font-weight: bold;">
            <td style="width: 200px;"><@spring.message "msg.detail.attributeName"/></td>
            <td><@spring.message "msg.detail.attributeValue"/></td>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><@spring.message "msg.detail.msgId"/></td>
            <td>${msg.msgId?c}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.correlationId"/></td>
            <td>${msg.correlationId}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.processId"/></td>
            <td><#if msg.processId??>${msg.processId}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.state"/></td>
            <td class="${((msg.state == "FAILED")?string("nok", "ok"))}"><strong>${msg.state}<strong>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.startTimestamp"/></td>
            <td>${msg.startProcessTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.lastTimestamp"/></td>
            <td>${msg.lastUpdateTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.errorCode"/></td>
            <td class="${((msg.state == "FAILED")?string("nok", "ok"))}"><strong>
                <#if msg.failedErrorCode??><a
                        href="${rootContext}/web/admin/errorCatalog/#${msg.failedErrorCode.getErrorCode()}">${msg.failedErrorCode.getErrorCode()}</a>
                </#if><strong>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.failedCount"/></td>
            <td>${msg.failedCount}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.systemName"/></td>
            <td>${msg.sourceSystem.systemName}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.receiveTimestamp"/></td>
            <td>${msg.receiveTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.msgTimestamp"/></td>
            <td>${msg.msgTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.serviceName"/></td>
            <td>${msg.service.serviceName}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.operationName"/></td>
            <td>${msg.operationName}&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.objectId"/></td>
            <td><#if msg.objectId??>${msg.objectId}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.entityType"/></td>
            <td><#if msg.entityType??>${msg.entityType.getEntityType()}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.funnelValue"/></td>
            <td><#if msg.funnelValue??>${msg.funnelValue}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.businessError"/></td>
            <td><#if msg.businessError??>${msg.businessError}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.parentMsgId"/></td>
            <td><#if msg.parentMsgId??>${msg.parentMsgId}</#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.payload"/></td>
            <td style="font-size: small; font-family: monospace; word-wrap:break-word;">
                <#if msg.payload??>
                    <pre><code class="xml">${msg.payload?html}</code></pre></#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.envelope"/></td>
            <td style="font-size: small; font-family: monospace; word-wrap:break-word;">
                <#if msg.envelope??>
                    <pre><code class="xml">${msg.envelope?html}</code></pre></#if>&nbsp;</td>
        </tr>
        <tr>
            <td><@spring.message "msg.detail.failedDesc"/></td>
            <td style="font-size: small; font-family: monospace; word-wrap:break-word;"><#if msg.failedDesc??>${msg.failedDesc}</#if>
                &nbsp;</td>
        </tr>
        </tbody>
    </table>
    <br/>

        <#if msg.requests?has_content>
        <h5><@spring.message "msg.detail.reqResp"/></h5>
        <#-- req/resp table -->
        <table border="1" cellpadding="1" cellspacing="0" width="1200px">
            <thead>
            <tr>
                <th rowspan="2"><@spring.message "msg.detail.reqResp.id"/></th>
                <th rowspan="2"><@spring.message "msg.detail.reqResp.uri"/></th>
                <th><@spring.message "msg.detail.reqResp.req.timestamp"/></th>
                <th><@spring.message "msg.detail.reqResp.req.payload"/></th>
                <th rowspan="2" class="last"><@spring.message "msg.detail.reqResp.state"/></th>
            </tr>
            <tr>
                <th><@spring.message "msg.detail.reqResp.resp.timestamp"/></th>
                <th><@spring.message "msg.detail.reqResp.resp.payload"/></th>
            </tr>
            </thead>
            <tbody>
                <#list msg.requests as request>
                <tr>
                    <td rowspan="2">${request.id?c}&nbsp;</td>
                    <td rowspan="2" style="max-width: 150px; word-wrap:break-word;">${request.getNormalizedUri()}
                        &nbsp;</td>
                    <td>${request.reqTimestamp}&nbsp;</td>
                    <td style="max-width: 650px; font-size: small; font-family: monospace; word-wrap:break-word;">
                        <div class="truncateContainer">
                            <#if request.request??>
                                <pre><code class="xml">${request.request?html}</code></pre></#if>&nbsp;
                        </div>
                    </td>
                    <#if request.response??>
                        <td rowspan="2" class="last ${((request.response.failed)?string("nok", "ok"))}">
                            <strong>
                                <#if request.response.failed>
                                    <@spring.message "msg.detail.reqResp.state.fail"/>
                                <#else>
                                    <@spring.message "msg.detail.reqResp.state.ok"/>
                                </#if>&nbsp;
                            </strong></td>
                    <#else>
                        <td rowspan="2" class="last"></td>
                    </#if>
                </tr>
                <tr>
                    <td><#if request.response??>${request.response.resTimestamp}</#if>&nbsp;</td>
                    <td style="max-width: 650px; font-size: small; font-family: monospace; word-wrap:break-word;">
                        <div class="truncateContainer">
                            <#if request.response??>
                                <#if request.response.failed>
                                ${request.response.failedReason}
                                <#else>
                                    <pre><code class="xml">${request.response.response?html}</code></pre>
                                </#if>&nbsp;
                            </#if>
                        </div>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>

        <#else>
        <div class="clearBoth"><@spring.message "msg.detail.reqResp.noCalls"/></div>
        </#if>

        <#-- externall call table -->
        <#if msg.externalCalls?has_content>
        <h5><@spring.message "msg.detail.externalCalls"/></h5>

        <table border="1" cellpadding="1" cellspacing="0" width="1200px">
            <thead>
            <tr>
                <th><@spring.message "msg.detail.extCall.id"/></th>
                <th><@spring.message "msg.detail.extCall.state"/></th>
                <th><@spring.message "msg.detail.extCall.name"/></th>
                <th><@spring.message "msg.detail.extCall.callId"/></th>
                <th class="last"><@spring.message "msg.detail.extCall.lastTimestamp"/></th>
            </tr>
            </thead>
            <#list msg.externalCalls as externalCall>
                <tr>
                    <td>${externalCall.id?c}&nbsp;</td>
                    <td class="${((externalCall.state == "FAILED" || externalCall.state == "FAILED_END")?string("nok", "ok"))}">
                        <strong>${externalCall.state}<strong>&nbsp;</td>
                    <td>${externalCall.operationName}&nbsp;</td>
                    <td>${externalCall.entityId}&nbsp;</td>
                    <td class="last">${externalCall.lastUpdateTimestamp?string("dd.MM.yyyy HH:mm:ss")}&nbsp;</td>
                </tr>
            </#list>
        </table>
        <br/>
        <#else>
        <div class="clearBoth"><@spring.message "msg.detail.extCall.noCalls"/></div>
        </#if>

    <script type="text/javascript">
        // format all xml blocks
        $('.xml').each(function (i, block) {
            $(this).text(formatXml($(this).text()));
        });
        // highlight all xml codes
        $('.xml').each(function (i, block) {
            hljs.highlightBlock(block);
        });
    </script>

    <#else>
    <div class="clearBoth"><@spring.message "msg.detail.noMsg"/> = ${requestMsgId?c}</div>
    </#if>

<button type="button"
        onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>