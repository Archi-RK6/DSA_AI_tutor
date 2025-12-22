<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>User Dashboard</title>
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
    }

    .slider-container {
      display: flex;
      gap: 20px;
      justify-content: center;
      margin-bottom: 20px;
      flex-wrap: wrap;
    }
    .slide {
      position: relative;
      width: 260px;
      height: 160px;
      overflow: hidden;
      border-radius: 8px;
      cursor: pointer;
      transition: transform 0.3s;
    }
    .slide img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0.5;
    }
    .slide .title {
      position: absolute;
      bottom: 10px;
      left: 15px;
      font-size: 1.2rem;
      color: #fff;
      text-shadow: 0 1px 4px rgba(0,0,0,0.6);
    }
    .slide:hover { transform: scale(1.03); }
    .manage-link { text-align: center; margin-top: 10px; }
    .manage-link a { color: #656d4f; font-weight: bold; }
    .top-nav {
        background-color: #f0f0f0;
        padding: 10px;
    }
  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>

<div class="container">
  <h1>User Dashboard</h1>

  <div class="slider-container">
        <c:forEach var="t" items="${topics}">
          <form action="/user_home/topic/${t.path}" method="get" style="margin:0;">
            <div class="slide">
              <img src="${t.imageUrl}" alt="${t.name} image"/>
              <div class="title">${t.name}</div>
              <button type="submit" style="position:absolute; inset:0; opacity:0; border:none; background:none; cursor:pointer;"></button>
            </div>
          </form>
        </c:forEach>
      </div>
  </div>
</div>
</body>
</html>
