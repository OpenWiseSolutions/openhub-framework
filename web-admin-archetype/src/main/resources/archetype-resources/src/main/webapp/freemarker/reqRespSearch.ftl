<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">

    <h4><@spring.message "reqResp.title"/></h4>

    <div id="wrapper">

        <div id="dateops" class="ui-corner-all">
            <fieldset>
                <legend><@spring.message "reqResp.legend"/></legend>
                <form action="" method="post">
                    <ul>
                        <li>
                            <label for="fromDate" style="width: 250px;"><@spring.message "reqResp.filter.fromDate"/>:*</label>
                            <input type="datetime" id="fromDate" name="fromDate" size="50" value="${model["fromDate"]!}" style="width: 400px;"/>
                        </li>
                        <li>
                            <label for="toDate" style="width: 250px;"><@spring.message "reqResp.filter.toDate"/>:*</label>
                            <input type="datetime" id="toDate" name="toDate" size="50" value="${model["toDate"]!}" style="width: 400px;"/>
                        </li>
                        <li>
                            <label for="uri" style="width: 250px;"><@spring.message "reqResp.filter.uri"/>:</label>
                            <input type="text" id="uri" name="uri" value="${model["uri"]!}" style="width: 400px;"/>
                        </li>
                        <li>
                            <label for="content" style="width: 250px;"><@spring.message "reqResp.filter.content"/>:</label>
                            <input type="text" id="content" name="content" value="${model["content"]!}" style="width: 400px;"/>
                        </li>
                        <li>
                            <label for="btn"><@spring.message "reqResp.action"/>:</label><input type="submit" value="<@spring.message "search"/>"/>
                            <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>
                        </li>
                    </ul>
                </form>
            </fieldset>
        </div>
        <br>

        <#if model["requestList"]??>
            <div id="results">
                <table class="bottomBorder">

                    <table border="1" cellpadding="1" cellspacing="0" width="1200px">
                        <thead>
                        <tr>
                            <th rowspan="2"><@spring.message "reqResp.id"/></th>
                            <th rowspan="2"><@spring.message "reqResp.uri"/></th>
                            <th rowspan="2"><@spring.message "reqResp.message"/></th>
                            <th><@spring.message "reqResp.req.timestamp"/></th>
                            <th><@spring.message "reqResp.req.payload"/></th>
                            <th rowspan="2" class="last"><@spring.message "reqResp.state"/></th>
                        </tr>
                        <tr>
                            <th><@spring.message "reqResp.resp.timestamp"/></th>
                            <th><@spring.message "reqResp.resp.payload"/></th>
                        </tr>
                        </thead>
                        <tbody>
                            <#list model["requestList"] as request>
                            <tr>
                                <td rowspan="2">${request.id?c}&nbsp;</td>
                                <td rowspan="2" style="max-width: 150px; word-wrap:break-word;">${request.getNormalizedUri()}
                                    &nbsp;</td>
                                <td rowspan="2" >
                                    <#if request.msgId??>
                                        <a href="${rootContext}/web/admin/messages/${request.msgId?c}">${request.msgId?c}</a>
                                    </#if>&nbsp;
                                </td>
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
            </div>
            <br>

            <script type="text/javascript">
                // format all xml blocks
                $('.xml').each(function(i, block) {
                    $(this).text(formatXml($(this).text()));
                });
                // highlight all xml codes
                $('.xml').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
            </script>

            <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>
        <#else>
            <#if model["emptyList"]?? && model["emptyList"] == true>
                <div class="clearBoth">
                    <@spring.message "reqResp.noMsg"/>
                </div>
            </#if>
        </#if>
        <br>
    </div>
</div>

</@p.page>