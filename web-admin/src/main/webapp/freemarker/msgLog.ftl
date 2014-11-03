<#import "/spring.ftl" as spring />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><@spring.message "msg.log.title"/></title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link rel="stylesheet" href="${springMacroRequestContext.contextPath}/css/log.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${springMacroRequestContext.contextPath}/css/highlight.vs.css">
    <script src="${springMacroRequestContext.contextPath}/js/jquery-1.9.1.min.js"></script>
    <script src="${springMacroRequestContext.contextPath}/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${springMacroRequestContext.contextPath}/js/highlight.min.js"></script>
</head>
<body>
<h2><@spring.message "msg.log.title"/></h2>

<#if correlationId??>
    <#if logErr??>
        <@spring.message "msg.log.readError"/>: ${logErr}
    <#else>
    <h4><@spring.message "msg.log.correlationID"/>: ${correlationId}</h4>
        <@spring.message "msg.log.info"/><br/><br/>

    <div style="font-size: small; font-family: monospace; white-space: pre;">${log}</div>
    </#if>
<#else>
    <@spring.message "msg.log.noMsg"/> = ${requestMsgId}
</#if>