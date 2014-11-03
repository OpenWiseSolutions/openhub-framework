<#import "lib/utils.ftl" as p>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><@spring.message "index.title"/></title>
    <meta http-equiv="X-UA-Compatible" content="IE=9" >
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link rel="stylesheet" type="text/css" media="screen" href="${springMacroRequestContext.contextPath}/css/main.css">
</head>
<body>
<div class="header">
    <div class="headerWrapper">
        <span class="logo"><a href="web/admin/homepage"><img class="logo" src="${springMacroRequestContext.contextPath}/css/images/logo-cbs.png" alt="CleverBus"/></a></span>
        <span class="headline"><@spring.message "index.integrationFramework"/></span>
    </div>
</div>
<div class="page">

    <div class="links menu">
        <h4><@spring.message "index.signpost"/></h4>
        <ul>
            <li><@spring.message "index.overview"/></li>
            <ul>
                <li><a href="${springMacroRequestContext.contextPath}/http/version"><@spring.message "index.currentVersion"/></a></li>
                <li><a href="${springMacroRequestContext.contextPath}/web/admin/console"><@spring.message "index.admin"/></a></li>
            </ul>
        </ul>
    </div>
</div>

<div class="footer">
    <div class="footerWrapper">
        <div class="companyLogo"><a href="http://www.cleverlance.com"><img class="logo" src="${springMacroRequestContext.contextPath}/css/images/cleverlance-logo.png" alt="CleverBus"/></a></div>
    </div>
</div>
</body>
</html>