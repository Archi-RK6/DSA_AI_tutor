<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>Admin: Manage Chapters</title>
  <style>
    body {
      margin:0; padding:0;
      font-family: Arial, sans-serif;
      background: #A9BCA9;
      color: #333;
      text-align: center;
    }
    .container {
      display: inline-block;
      background: var(--card-bg);
      color: var(--card-text);
      padding: 20px;
      border-radius: 8px;
      text-align: left;
    }

    .chapter-form input, .chapter-form button {
      margin: 5px;
      padding: 6px;
      border: 1px solid #656d4f;
      border-radius:4px;
      font-size: 1rem;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    th, td {
      border: 1px solid #aaa;
      padding: 8px;
    }
    th {
      background: var(--sidebar-bg)
    }
    button.delete {
      background: #e57373;
      color: white;
      border: none;
      padding: 4px 8px;
      border-radius: 4px;
      cursor: pointer;
    }
    button.delete:hover {
      background: #d32f2f;
    }
  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>

  <div style="position:absolute; top:60px; right:20px;">
      <a href="/admin_home">Back</a>
    </div>

  <div class="container">
    <h1>Manage Chapters</h1>
    <form class="chapter-form" action="/admin_home/topic" method="post">
      <input type="hidden" name="topicId" value="${topicId}"/>
      <input type="text" name="contentChapter" placeholder="Chapter title" required/>
      <input type="text" name="contentText" placeholder="Chapter text" required/>
      <button type="submit">Add Chapter</button>
    </form>

    <table>
      <tr>
        <th>ID</th>
        <th>Topic ID</th>
        <th>Chapter</th>
        <th>Text</th>
        <th>Action</th>
      </tr>
      <c:forEach var="chap" items="${chapters}">
        <tr>
          <td>${chap.id}</td>
          <td>${chap.topicId}</td>
          <td><c:out value="${chap.contentChapter}"/></td>
          <td><c:out value="${chap.contentText}"/></td>
          <td>
            <form action="/admin_home/topic/delete" method="post" style="display:inline;">
              <input type="hidden" name="id" value="${chap.id}"/>
              <button type="submit" class="delete">Delete</button>
            </form>
          </td>
        </tr>
      </c:forEach>
    </table>
  </div>
</body>
</html>
