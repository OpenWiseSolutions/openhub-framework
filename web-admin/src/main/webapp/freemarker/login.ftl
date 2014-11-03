<#import "lib/utils.ftl" as p>

<@p.page>

<div id="login">
    <h1><@spring.message "login.title"/></h1>
    <form id="loginForm" method="post" action="${rootContext}/j_spring_security_check">
        <input id="user" name="username" type="text" placeholder="<@spring.message "login.input.username"/>" tabindex="1" />
        <input id="pass" name="password" type="password" placeholder="<@spring.message "login.input.password"/>" tabindex="2" />
        <input type="submit" name="submit" value="<@spring.message "login.btn.login"/>" />
    </form>
    <script type="text/javascript" language="JavaScript">
        document.getElementById("user").focus();
    </script>
    <#if badCredentials>
        <div class="alert-box error"><@spring.message "login.msg.bad.credentials"/></div>
    </#if>

    <#if successLogout>
        <div class="alert-box success"><@spring.message "login.msg.success.logout"/></div>
    </#if>
</div>

</@p.page>