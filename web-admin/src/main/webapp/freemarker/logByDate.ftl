<#import "/spring.ftl" as spring />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><@spring.message "log.searchByDate2.title"/></title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link rel="stylesheet" href="${springMacroRequestContext.contextPath}/css/log.css"/>
    <link rel="stylesheet" type="text/css" media="screen" href="${springMacroRequestContext.contextPath}/css/highlight.vs.css">
    <script src="${springMacroRequestContext.contextPath}/js/jquery-1.9.1.min.js"></script>
    <script src="${springMacroRequestContext.contextPath}/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="${springMacroRequestContext.contextPath}/js/highlight.min.js"></script>
    <script src="${springMacroRequestContext.contextPath}/js/logHooks.js"></script>
</head>
<body onload="initLogHooks()">
<#if logErr??>
    <@spring.message "log.searchByDate2.readErr"/>: ${logErr}
<#else>

    <#assign query><#if springMacroRequestContext.queryString??>
    ?${springMacroRequestContext.queryString}</#if></#assign>

<script>hljs.initHighlightingOnLoad();</script>

<p class="navigation top">
    <#list -1..6 as daysAgo >
        <#assign fromDay = fromDate.minusDays(daysAgo).withMillisOfDay(0) >
        <a class="date" href="./${fromDay}${query}"><span></span>${fromDay.toString('yyyy-MM-dd')}</a>
    </#list>
</p>

<p class="navigation top">
    <#list -1..11 as hoursAgo >
        <#assign fromHour = fromDate.minusHours(hoursAgo).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0) >
        <a class="time" href="./${fromHour}${query}"><span></span>${fromHour.toString('HH:mm')}</a>
    </#list>
</p>

<h1><@spring.message "log.searchByDate2.logFrom"/> ${fromDate.toString('yyyy-MM-dd HH:mm:ss.SSS')}</h1>

    <#assign grouped = (config.groupBy!?size &gt; 0)>
    <#assign startedTable = false>
    <#assign newGroup = false />
    <#assign eventCount = 0>
    <#list logEvents as logEvent>
        <#assign eventCount = eventCount + 1>
        <#assign lastEvent = logEvent>
        <#if grouped>
            <#assign eventGroup>
                <#list config.groupBy as propertyName>
                ${logEvent.properties[config.propertyNames?seq_index_of(propertyName)]}
                </#list>
            </#assign>
            <#assign newGroup = (eventGroup != lastGroup!'') />
            <#if newGroup>
                <#assign lastGroup = eventGroup/>
                <#assign groupName>
                    <#list config.groupBy as propertyName>
                    ${propertyName} ${logEvent.properties[config.propertyNames?seq_index_of(propertyName)]}
                    </#list>
                </#assign>
                <#assign groupQuery>
                    <#list config.groupBy as propertyName><#t>
                        <#if propertyName_index == 0>?<#else>&amp;</#if><#t>
                    filter.${propertyName}=<#t>
                    ${logEvent.properties[config.propertyNames?seq_index_of(propertyName)]?url('UTF-8')}<#t>
                    </#list><#t>
                </#assign>

                <#if startedTable>
                </table>
                    <#assign startedTable = false>
                </#if>
            <h5 class="groupHeader"><a href="./${fromDate}${groupQuery}"><span></span>${groupName}</a></h5>
            </#if>
        </#if>

        <#if view?? && view = "table">
            <#if !startedTable >
                <#assign startedTable = true>
            <table>
                <tr class="header">
                    <th>date</th>
                    <#list logEvent.propertyNames as propertyName>
                        <th>${propertyName?xhtml}</th>
                    </#list>
                    <th width="100%">&nbsp;</th>
                </tr>
            </#if>
            <tr class="line ${logEvent.properties?join(' ')}">
                <td class="property date"><a
                        id="${logEvent_index}">${logEvent.date.toString('yyyy-MM-dd HH:mm:ss.SSS')}</a></td>
                <#list logEvent.properties as property>
                    <td class="property ${logEvent.propertyNames[property_index]}">${property?xhtml}</td>
                </#list>
                <td width="100%">&nbsp;</td>
            </tr>
            <tr class="line ${logEvent.properties?join(' ')}">
                <td>&nbsp;</td>
                <td colspan="${logEvent.propertyCount - 1}" class="property msg">${logEvent.message?xhtml}</td>
            </tr>
        <#else>
            <a id="${logEvent_index}" class="line ${logEvent.properties?join(' ')}"><#t>
                <span class="property date">${logEvent.date.toString('yyyy-MM-dd HH:mm:ss.SSS')}</span>&nbsp;<#t>
                <#list logEvent.properties as property><#t>
                    <span class="property ${logEvent.propertyNames[property_index]}">${property?xhtml}</span>&nbsp;<#t>
                </#list><#t>
                <span class="property msg">${logEvent.message?xhtml}</span><#t>
            </a><#lt>
        </#if>
    </#list>

    <#if startedTable>
    </table>
    </#if>

    <#if eventCount = 0>
        <p class="warn"><@spring.message "log.searchByDate2.noMsg"/></p>
    <#else>
        <p class="info"><@spring.messageArgs "log.searchByDate2.resultInfo", [eventCount, config.limit] /></p>
        <#if eventCount &gt;= config.limit>
            <p class="navigation bottom"><@spring.message "log.searchByDate2.continueFrom"/>
            <span class="time">
            <a href="./${lastEvent.date}${query}"><span></span>${lastEvent.date.toString('yyyy-MM-dd HH:mm:ss.SSS')}</a>
        </#if>
        </span>
        </p>
    </#if>
</#if>

</body>
</html>