<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<title>Manage Topics</title>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>

  <div style="position: absolute; top: 60px; right: 10px;">
        <a href="/admin_home">Back</a>
  </div>

  <h2>Admin: Manage Topics</h2>

  <form action="/admin_home/topics" method="post">
    Name: <input type="text" name="name" required/>
    Path: <input type="text" name="path" required/>
    <button type="submit">Add Topic</button>
  </form>

  <hr/>

  <table border="1" cellpadding="5">
    <tr><th>ID</th><th>Name</th><th>Path</th><th>Action</th></tr>
    <c:forEach var="t" items="${topics}">
      <tr>
        <td>${t.id}</td>
        <td>${t.name}</td>
        <td>${t.path}</td>
        <td>
          <form action="/admin_home/topics/delete" method="post" style="display:inline;">
            <input type="hidden" name="id" value="${t.id}"/>
            <button type="submit">Delete</button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</body>
</html>