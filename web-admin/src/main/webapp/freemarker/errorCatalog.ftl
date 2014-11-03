<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "errorCodes.title"/></h4>

    <#list model["errorCodesCatalog"]?keys as catalogName>
        <h5>${catalogName}:</h5>

        <table border="1" cellpadding="1" cellspacing="0" width="1200px">
            <thead>
            <tr style="background-color: #d3d3d3; font-weight: bold;">
                <td style="width: 100px;"><@spring.message "errorCodes.code"/></td>
                <td><@spring.message "errorCodes.desc"/></td>
            </tr>
            </thead>
            <tbody>
                <#list model["errorCodesCatalog"][catalogName] as errorCode>
                <tr>
                    <td id=${errorCode.getErrorCode()}>${errorCode.getErrorCode()}</td>
                    <td>${errorCode.getErrDesc()}</td>
                </tr>
                </#list>
            </tbody>
        </table>

        <br/><br/>
    </#list>

    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>