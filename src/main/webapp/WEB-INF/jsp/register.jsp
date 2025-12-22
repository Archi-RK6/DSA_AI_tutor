<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register</title>
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
            background: #ffffff;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            text-align: center;
        }
        .container h1 {
            margin-bottom: 10px;
        }
        .container h2 {
            margin-bottom: 20px;
            font-weight: normal;
            color: #666666;
        }
        .container input {
            width: 100%;
            padding: 10px;
            margin: 8px 0;
            border-radius: 5px;
            border: 1px solid #cccccc;
            font-size: 14px;
        }
        .container .btn {
            width: 100%;
            padding: 10px;
            background-color: #446a30;
            color: #ffffff;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }
        .container .btn:hover {
            background-color: #55883B;
        }
        .error {
            color: #b00020;
            margin-bottom: 10px;
        }
        .link {
            margin-top: 15px;
            font-size: 14px;
        }
        .link a {
            color: #2b4a80;
            text-decoration: none;
        }
        .link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Create account</h1>
    <h2>Register to start</h2>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
    <div class="error"><%= errorMessage %></div>
    <%
        }
    %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <input type="text" name="name" placeholder="User Name" required />
        <input type="password" name="password" placeholder="Password" required />
        <button type="submit" class="btn">Register</button>
    </form>

    <div class="link">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login">Log in</a>
    </div>
</div>
</body>
</html>
