<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="message">
	<c:if test="${not empty message}">${message}</c:if>
	<c:forEach items="${errors}" var="error">
		<c:out value="${error}" />
		<br>
	</c:forEach>
</div>