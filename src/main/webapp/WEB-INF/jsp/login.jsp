<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF‑8">
  <title>Login</title>
  <style>
    body {
      margin: 0;
      padding: 0;
      font-family: Arial, sans-serif;
      background: #A9BCA9;
      color: #333333;
    }
    .container {
      max-width: 400px;
      margin: 10% auto;
      background: var(--card-bg);
      color: var(--card-text);
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    h1, h2 {
      text-align: center;
      margin: 0 0 15px;
    }
    .error {
      color: #AA3333;
      text-align: center;
      margin-bottom: 10px;
    }
    form {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    input[type="text"],
    input[type="password"] {
      padding: 10px;
      font-size: 1rem;
      border: 1px solid #888;
      border-radius: 4px;
    }
    .btn {
      background: #55883B;
      color: #fff;
      border: none;
      padding: 12px;
      font-size: 1rem;
      border-radius: 4px;
      cursor: pointer;
      transition: background 0.3s;
    }
    .btn:hover {
      background: #446a30;
    }
  </style>
</head>
<body>
  <div class="container">
    <h1>Welcome</h1>
    <h2>Login to start</h2>

    <c:if test="${not empty errorMessage}">
      <div class="error">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <input type="text" name="name" placeholder="User Name" required />
      <input type="password" name="password" placeholder="Password" required />
      <button type="submit" class="btn">Log In</button>
    </form>

    <p style="margin-top: 1rem;">
      <a href="${pageContext.request.contextPath}/register">Create new account</a>
    </p>
  </div>


</body>
</html>
