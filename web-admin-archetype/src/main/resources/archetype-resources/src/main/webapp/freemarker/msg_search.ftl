<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view" xmlns="http://www.w3.org/1999/html">

    <h4><@spring.message "msg.search.title"/></h4>

    <form method="post" action="">
        <@spring.message "msg.search.sourceSystem"/>:
        <select name="sourceSystem">
            <#list model["systemsMap"]?keys as key>
                <option value="${key}"<#if key = model["source"]!> selected</#if>>${key}</option>
            </#list>
        </select>

        <br/>

        <@spring.message "msg.search.correlationId"/>:
        <input type="text" name="correlationId" size="50" value="${model["correlation"]!}"/>

        <br/><br/>
        <input type="submit" value="<@spring.message "search"/>"/>

        <#if model["resultFailed"]??>
            <strong><@spring.message "msg.search.noMsg"/></strong>
        </#if>
    </form>

    <br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>