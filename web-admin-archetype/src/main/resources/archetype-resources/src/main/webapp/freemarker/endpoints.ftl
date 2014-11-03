<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "endpoints.title"/></h4>

    <ul>
        <#list model["endpoints"] as endpoint>
            <li>${endpoint}</li>
        </#list>
    </ul>

    <br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>