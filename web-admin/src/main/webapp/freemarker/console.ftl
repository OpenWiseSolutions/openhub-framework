<#import "lib/utils.ftl" as p>

<@p.page>

<div class="links">
    <h4><@spring.message "console.title"/></h4>
    <ul>
        <li><@spring.message "console.overview.title"/></li>
        <ul>
            <li><a href="${rootContext}/web/admin/changes"><@spring.message "console.overview.changes"/></a></li>
            <li><a href="${rootContext}/http/version"><@spring.message "console.overview.version"/></a></li>
            <li><a href="${rootContext}/web/admin/errorCatalog"><@spring.message "console.overview.errors"/></a></li>
            <li><a href="${rootContext}/web/admin/endpoints"><@spring.message "console.overview.endpoints"/></a></li>
            <li><a href="${rootContext}/web/admin/wsdl"><@spring.message "console.overview.wsdl"/></a></li>
            <#if !model["javamelody"]>
                <@sec.authorize access="hasRole('ROLE_MONITORING')">
                    <li><a href="${rootContext}/monitoring/javamelody"><@spring.message "console.overview.monitoring"/></a></li>
                </@sec.authorize>
            </#if>
        </ul>

        <li><@spring.message "console.msg.title"/></li>
        <ul>
            <li><a href="${rootContext}/web/admin/log/"><@spring.message "console.msg.searchByDate"/></a></li>
            <li><a href="${rootContext}/web/admin/reqResp/search"><@spring.message "console.reqResp.search"/></a></li>
            <li><a href="${rootContext}/web/admin/messages/searchForm"><@spring.message "console.msg.searchByCorrelation"/></a></li>
            <li><a href="${rootContext}/web/admin/messages/messagesByContent"><@spring.message "console.msg.searchByString"/></a></li>
            <li><a href="${rootContext}/web/admin/messages/messageReport"><@spring.message "console.msg.states"/></a></li>
        </ul>

        <li><@spring.message "console.operations.title"/></li>
        <ul>
            <li><a href="${rootContext}/web/admin/operations/messageOperations"><@spring.message "console.operations.restart"/></a></li>
            <li><a href="${rootContext}/web/admin/directCall"><@spring.message "console.operations.externalCall"/></a></li>
            <li><a href="${rootContext}/web/admin/stop"><@spring.message "console.operations.stop"/></a></li>
        </ul>
    </ul>
</div>

<div class="clearBoth"><@spring.message "console.info"/></div>

</@p.page>