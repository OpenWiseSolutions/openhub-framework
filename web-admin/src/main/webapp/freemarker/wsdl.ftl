<#import "lib/utils.ftl" as p>

<@p.page>

<div class="wsdl">
    <h4><@spring.message "wsdl.title"/></h4>

    <ul>
        <#list model["wsdls"] as wsdlName>
            <li><a href="${rootContext}/ws/${wsdlName}.wsdl">${wsdlName}.wsdl</a></li>
        </#list>
    </ul>

    <br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>