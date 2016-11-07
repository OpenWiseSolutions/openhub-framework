<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Doh! - OpenHub - integration framework</title>
    <meta http-equiv="X-UA-Compatible" content="IE=9">
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/error_tmpl.css"/>
</head>
<body>

<div class="header">
    <div class="headerWrapper">
        <span class="logo"><a href="${pageContext.request.contextPath}">
            <img class="logo" src="${pageContext.request.contextPath}/css/images/logo-cbs.png" alt="OpenHub"/></a></span>
        <span class="headline">Integration framework</span>
    </div>
</div>

<div class="page">
    <div id="error-out"><h1>An error has occurred</h1>

        <div id="error-out-desc">
            <h3>Error description</h3>

            <p id="error-code"><b>Error code:</b> ${pageContext.errorData.statusCode}</p>

            <p id="error-request-uri"><b>Request
                URI:</b> ${pageContext.request.scheme}://${header.host}${pageContext.errorData.requestURI}</p>
            <% if (exception != null) { %>
            <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
            <% } else { %>
            <p>Please check your server log files for more details or just return
                <a href="javascript:history.back()">back to the previous page</a>
            </p>
            <% } %>
        </div>
    </div>

</div>
<div class="footer">
    <div class="footerWrapper">
        <span class="companyLogo"><a href="http://www.openhubframework.org" target="_blank">OpenHub</a></span>
    </div>
</div>
</body>
</html>