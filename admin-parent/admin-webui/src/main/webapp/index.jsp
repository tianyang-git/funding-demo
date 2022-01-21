<%--
  Created by IntelliJ IDEA.
  User: tianyang
  Date: 2021/11/15
  Time: 11:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<base href="http://${pageContext.request.serverName }:${pageContext.request.serverPort }${pageContext.request.contextPath }/"/>

    hello world<br/>

    <%--http://localhost:8080/funding/   +   test/ssm.html--%>
    <a href="admin/to/login/page.html">登录 Base标签</a><br>
    <a href="${pageContext.request.contextPath}/admin/to/login/page.html">登录</a><br>

    <a href="test/ssm.html">Spring SSM 环境 Base标签</a><br/>

    <a href="${pageContext.request.contextPath}/test/ssm.html">Spring SSM 环境</a>
</body>
</html>
