<#macro page>

    <#global rootContext = "${springMacroRequestContext.contextPath}">

    <#global sec = JspTaglibs["http://www.springframework.org/security/tags"] />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><#if title?has_content>${title} | </#if><@spring.message "index.title"/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=9">
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link rel="shortcut icon" href="${rootContext}/css/images/favicon.ico" type="image/x-icon">
    <link rel="stylesheet" type="text/css" media="screen" href="${rootContext}/css/main.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${rootContext}/css/login.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${rootContext}/css/log.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${rootContext}/css/highlight.vs.css">
    <link rel="stylesheet" href="${rootContext}/css/jquery-ui-1.10.3.custom.css"/>
    <link rel="stylesheet" href="${rootContext}/css/msgrep_tmpl.css"/>
    <script src="${rootContext}/js/jquery-1.9.1.min.js"></script>
    <script src="${rootContext}/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${rootContext}/js/highlight.min.js"></script>
    <script src="${rootContext}/js/logHooks.js"></script>
</head>
<body onload="initLogHooks()">

<div class="header">
    <div class="headerWrapper">
        <span class="logo"><a href="${rootContext}/web/admin/console"><img class="logo"
                                                                           src="${rootContext}/css/images/logo-cbs.png"
                                                                           alt="OpenHub"/></a></span>
        <span class="headline"><@spring.message "index.integrationFramework"/></span>
        <@sec.authorize access="isAuthenticated()">
            <span class="logout"><a href="${rootContext}/j_spring_security_logout"
                    ><@spring.message "login.text.logout"/> | <@sec.authentication property="principal.username"/></a>
            </span>
        </@sec.authorize>
    </div>
</div>
<div class="page">

<#-- This processes the enclosed content:  -->
<#nested>

</div>
<div class="footer">
    <div class="footerWrapper">
        <span class="companyLogo"><a href="http://www.openhubframework.org" target="_blank"><@spring.message "index.name"/></a></span>
    </div>
</div>
</body>
</html>
</#macro>