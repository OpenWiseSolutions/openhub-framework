<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "directCall.title"/></h4>

    <form method="post" action="">
        <table border="0">
            <tr>
                <td width="120px" style="font-weight: bold"><@spring.message "directCall.soapHeader"/>:</td>
                <td><textarea rows="15" cols="130" name="header">${model["header"]!}</textarea></td>
            </tr>

            <tr>
                <td width="120px" style="font-weight: bold"><@spring.message "directCall.body"/>:</td>
                <td><textarea rows="30" cols="130" name="body">${model["body"]!}</textarea></td>
            </tr>

            <tr>
                <td style="font-weight: bold"><@spring.message "directCall.uri"/>:</td>
                <td><input type="text" name="uri" size="70" value="${model["uri"]!}"/></td>
            </tr>

            <tr>
                <td style="font-weight: bold"><@spring.message "directCall.senderRef"/>:</td>
                <td><input type="text" name="senderRef" size="50" value="${model["senderRef"]!}"/></td>
            </tr>

            <tr>
                <td><@spring.message "directCall.soapAction"/>:</td>
                <td><input type="text" name="soapAction" size="50" value="${model["soapAction"]!}"/></td>
            </tr>

            <tr>
                <td colspan="2" align="left"><input type="submit" value="<@spring.message "submit"/>"/></td>
            </tr>
        </table>

        <div class="clearBoth">
            <#if model["callResult"]??>
                <strong><@spring.message "directCall.response"/></strong>:<br/>
                <pre>${model["callResult"]?html}</pre>
            </#if>

            <#if model["callResultError"]??>
                <strong style="color: red;">&gt;&gt;&gt;&gt; <@spring.message "directCall.error"/> &lt;&lt;&lt;&lt;</strong><br/>
                <pre>${model["callResultError"]?html}</pre>
            </#if>
        </div>
    </form>

    <br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>
