<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix='tags' tagdir='/WEB-INF/tags' %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/css/style.css"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/js/main.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
    <link rel="stylesheet" type="text/css"
          href="css/jquery-ui-1.10.3.custom.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
    <c:set var="url"
           value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}"/>
    <title>Registration</title>
</head>
<body>
<h2>Registration</h2>
<tags:message/>
<div id="loginAnswer"></div>
<div id="content">
    <form:form method="POST" action="registrate" commandName="UserForm"
               id="registrationForm" role="form">
    <div class="control-group">
        Login:
        <div class="controls">
            <form:input path="login" name="login" id="login"/>
            <form:errors path="login" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Password:
        <div class="controls">
            <form:input path="password" name="password" type="password"
                        id="password"/>
            <form:errors path="password" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Confirm:
        <div class="controls">
            <form:input path="confirm" name="confirm" type="password" id="confirm"/>
            <form:errors path="confirm" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Email:
        <div class="controls">
            <form:input path="email" name="email" id="email"/>
            <form:errors path="email" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        First name:
        <div class="controls">
            <form:input path="firstName" name="firstName" id="firstName"/>
            <form:errors path="firstName" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Last name:
        <div class="controls">
            <form:input path="lastName" name="lastName" id="lastName"/>
            <form:errors path="lastName" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Birthday:
        <div class="controls">
            <form:input path="birthday" name="birthday" id="datepicker"/>
            <form:errors path="birthday" cssClass="errors"/></div>
    </div>
    <div class="control-group">
        Role:
        <div class="controls">
            <form:select path="role" name="role" size="1" id="role">
                <form:option selected="selected" value="user">User</form:option>
            </form:select>
        </div>
    </div>    <br>
    <div class="control-group">
        <tags:captcha privateKey="6Lfb5ecSAAAAACnLK1mCj2WH0WN8psVBNIM8RSH8"
                      publicKey="6Lfb5ecSAAAAAG5KvFCMTG-WnwJUo2-Rdc4h61gY"/> </div>
        <input type="button" value="Cancel" onclick="window.location='${url}'"
               id="cancelBtn" class="btn"/>
        <input type="submit" name="ok" value="Ok" id="createSubmit" class="btn btn-primary" />
        </form:form>
</body>
</html>