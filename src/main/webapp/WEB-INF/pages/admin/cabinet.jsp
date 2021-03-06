<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
    <link href="http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-glyphicons.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin cabinet</title>
</head>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/jquery-2.0.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/underscore.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/backbone.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/jquery.validate.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libraries/bootbox.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css"
      href="css/jquery-ui-1.10.3.custom.min.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/backbone_main.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/backbone_model.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/backbone_collection.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/backbone_view.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/backbone_route.js"></script>
<body>
<script>
    $(document).ready(function () {

        App.users = new App.Collections.Users();
        App.users.fetch().then(function () {
            new App.Views.App({ collection: App.users });
        });

    });
</script>

<div id="adminInfo">Admin ${user.login} (<a href="logout">logout</a>)</div>
<div class="message"></div>
<div id="content">
    <a href="#add" id="addLink">Add new User</a>
    <table id="usersTable" border="1px" class="table table-striped">
        <tr>
            <td>#</td>
            <td>Login</td>
            <td>Email</td>
            <td>First Name</td>
            <td>Last Name</td>
            <td>Age</td>
            <td>Role</td>
            <td>Actions</td>
        </tr>
    </table>

</div>

<div id="modal" class="modal hide">
    <div class="modal-header">
        <h2>Error</h2>
    </div>
    <div class="modal-body">
        <p>Internal Server Error. Try again later.</p>
    </div>
</div>

<script type="text/template" id="userRowTemplate">
    <td><@=id@></td>
    <td><@=login@></td>
    <td><@=email@></td>
    <td><@=firstName@></td>
    <td><@=lastName@></td>
    <td><@=age@></td>
    <td><@=role.name@></td>
    <td>
        <button type="button" class="btn btn-default btn-lg" id="edit">
            <span class="glyphicon glyphicon-wrench"></span>
        </button>
        <button type="button" class="btn btn-default btn-lg" id="delete">
            <span class="glyphicon glyphicon-trash"></span>
        </button>
    </td>
</script>

<script type="text/template" id="userFormTemplate">
    <form role="form" id="userForm">
        <div class="control-group">
            <label for="login">Login</label>

            <div class="controls">
                <input type="text" class="form-control" id="login" placeholder="Login" name="login"></div>
        </div>
        <div class="control-group">
            <label for="password">Password</label>

            <div class="controls">
                <input type="password" class="form-control" id="password" placeholder="Password" name="password"></div>
        </div>
        <div class="control-group">
            <label for="confirm">Confirm</label>

            <div class="controls">
                <input type="password" class="form-control" id="confirm" placeholder="Password" name="confirm"></div>
        </div>
        <div class="control-group">
            <label for="email">Email</label>

            <div class="controls">
                <input type="email" class="form-control" id="email" placeholder="Email" name="email"></div>
        </div>
        <div class="control-group">
            <label for="firstName">First Name</label>

            <div class="controls">
                <input type="text" class="form-control" id="firstName" placeholder="First Name" name="firstName"></div>
        </div>
        <div class="control-group">
            <label for="lastName">Last Name</label>

            <div class="controls">
                <input type="text" class="form-control" id="lastName" placeholder="Last Name" name="lastName"></div>
        </div>
        <div class="control-group">
            <label for="birthday">Birthday</label>

            <div class="controls">
                <input type="text" class="form-control" id="birthday" placeholder="Birthday" name="birthday"></div>
        </div>
        <div class="control-group">
            <label for="role">Role</label>

            <div class="controls">
                <select id="role">
                    <option value="admin">Admin</option>
                    <option value="user">User</option>
                </select>
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
        <input type="button" value="Cancel" id="cancelBtn">
    </form>
</script>

</body>
</html>