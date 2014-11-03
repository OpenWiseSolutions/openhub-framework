<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "msg.ops.title"/></h4>

    <p>
    <fieldset>
        <legend><@spring.message "msg.ops.restart"/></legend>
        <form action="restartMessage" method="post">
            <p>
                <label for="msgID"><@spring.message "msg.ops.restart.msgId"/>: </label>
                <input type="text" name="messageID">
                <label style="text-align: center; width: 120px" for="restartCheck"><@spring.message "msg.ops.restart.fromScratch"/></label>
                <input type="checkbox" id="restartCheck" name="totalRestart">
                <input type="submit" value="<@spring.message "msg.ops.restart.btn"/>">
            </p>
        </form>
        <p class="note">"<@spring.message "msg.ops.restart.info"/>"</p>
        <span class="result">${model["resultRestartString"]!}</span>
    </fieldset>
    </p>

    <p>
    <fieldset>
        <legend><@spring.message "msg.ops.cancel"/></legend>
        <form action="cancelMessage" method="post">
            <p>
                <label for="msgID"><@spring.message "msg.ops.cancel.msgId"/>: </label>
                <input type="text" name="messageID"><br>
                <input type="submit" value="<@spring.message "msg.ops.cancel.btn"/>">
            </p>
        </form>
        <p class="note">"<@spring.message "msg.ops.cancel.info"/>"</p>
        <span class="result">${model["resultCancelString"]!}</span>
    </fieldset>
    </p>
</div>

<button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</@p.page>

