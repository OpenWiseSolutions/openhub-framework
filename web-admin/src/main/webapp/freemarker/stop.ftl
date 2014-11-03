<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "stop.title"/></h4>

    <br/>

    <#if isStopping>
        <h5><@spring.message "stop.msg.overview"/></h5>
        <table border="1" cellpadding="4" cellspacing="0">
            <thead>
                <tr>
                    <th><@spring.message "stop.msg.processing"/></th>
                    <th><@spring.message "stop.msg.waitingForRes"/></th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>${processingCount?c}&nbsp;</td>
                    <td>${waitingForResCount?c}&nbsp;</td>
                </tr>
            </tbody>
        </table>
        <a href="javascript:history.go(0)"><@spring.message "refresh"/></A>

        <br/><br/><br/>

        <form method="post" action="${rootContext}/web/admin/cancelStop">
            <input type="submit" value="<@spring.message "stop.btn.cancel"/>" style="font-weight: bold;" />
        </form>

        <@spring.message "stop.cancel.info"/>

    <#else>
        <form method="post" action="${rootContext}/web/admin/stop">
            <input type="submit" value="<@spring.message "stop.btn.stop"/>" style="font-weight: bold;" />
        </form>

        <@spring.message "stop.info"/>
    </#if>

    <br/><br/>
    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>
</div>

</@p.page>