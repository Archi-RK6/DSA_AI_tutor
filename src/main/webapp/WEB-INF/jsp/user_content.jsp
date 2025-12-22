<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>${topicName}</title>
  <style>
    body {
      margin:0; padding:0; font-family:Arial,sans-serif;
      background:#A9BCA9;
    }
    .container {
      max-width: 1000px;
      margin: 80px auto 40px auto;
      background: var(--card-bg);
      color: var(--card-text);
      padding: 20px;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .pdf-wrap {
      width: 100%;
      height: calc(100vh - 220px);
      min-height: 520px;
      background: #fff;
      border-radius: 8px;
      overflow: hidden;
    }
    .pdf-frame {
      width: 100%;
      height: 100%;
      border: 0;
      display: block;
    }
  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>

  <div class="container">
    <h1 style="text-align:center; color:#656d4f; margin:0;">${topicName}</h1>

    <c:set var="pdfSrc" value="${pageContext.request.contextPath}${pdfPath}" />
    <div class="pdf-wrap">
      <object data="${pdfSrc}" type="application/pdf" class="pdf-frame">
        <iframe src="${pdfSrc}" class="pdf-frame"></iframe>
      </object>
    </div>
  </div>

</body>
</html>
