<#import "lib/utils.ftl" as p>

<@p.page>

<div class="view">
    <h4><@spring.message "log.searchByDate.title"/></h4>

    <form method="get" action="">
        <br>
        <table>
            <tr>
                <th>
                    <label for="fromDate"><@spring.message "log.searchByDate.dateFrom"/></label><br/>
                </th>
                <td>
                    <input id="fromDate" type="text" name="fromDate" size="50" value="${fromDate}"/><br/>
                    <span class="info"><span></span><@spring.message "log.searchByDate.dateFromInfo"/></span>
                </td>
            </tr>
            <tr>
                <th><@spring.message "log.searchByDate.groupBy"/></th>
                <td>
                    <#list config.propertyNames as propertyName>
                        <#assign selected = config.groupBy?seq_contains(propertyName)/>
                        <label>
                            <input type="checkbox" name="groupBy" value="${propertyName}" <#if selected>checked</#if>/>
                        ${propertyName}
                        </label><br/>
                    </#list>
                </td>
            </tr>
            <tr>
                <th><label for="groupSize"><@spring.message "log.searchByDate.groupSize"/></label></th>
                <td><input id="groupSize" name="groupSize" size="50" value="${config.groupLimit}"/></td>
            </tr>
            <tr>
                <th><@spring.message "log.searchByDate.view"/></th>
                <td><label><input id="viewTable" type="checkbox" name="view" value="table"/> <@spring.message "log.searchByDate.viewAsTable"/></label></td>
            </tr>
            <tr>
                <th><@spring.message "log.searchByDate.filter"/></th>
                <td><span class="info"><span></span><@spring.message "log.searchByDate.filter.info"/>:</td>
            </tr>
            <#list config.propertyNames as propertyName>
                <tr>
                    <td><label for="filter.${propertyName}">${propertyName}</label></td>
                    <td><input id="filter.${propertyName}" type="text" name="filter.${propertyName}" size="50"/></td>
                </tr>
            </#list>
            <tr>
                <td><label for="filter.msg"><@spring.message "log.searchByDate.msg"/></label></td>
                <td><input id="filter.msg" type="text" name="msg" size="50"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
                    <input type="submit" value="<@spring.message "search"/>"/>
                    <span class="info"><span></span><@spring.message "log.searchByDate.maxResults"/> ${config.limit}</span>
                </td>
            </tr>
        </table>
    </form>

    <button type="button" onclick="window.location.href='${rootContext}/web/admin/console';"><@spring.message "return"/></button>

</div>

</@p.page>