<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Admin — Select Data Structure</title>
  <style>
    body { font-family: ui-sans-serif, system-ui; margin: 2rem; }
    .card { border: 1px solid #e5e7eb; padding: 1rem; border-radius: 10px; max-width: 560px; }
    button, select { padding: .5rem .8rem; border-radius: 8px; border: 1px solid #d1d5db; cursor: pointer; }
    button { background: #111827; color: #fff; }
        .top-nav {
        background-color: #f0f0f0; /* светлый фон навигации */
        padding: 10px;
    }
  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>
<div class="card">
  <h2>Choose Active Data Structure</h2>
  <form method="post" action="${pageContext.request.contextPath}/admin/select">
    <label for="type">Type</label>
    <select name="type" id="type">
      <option value="ARRAY_LIST">ArrayList</option>
      <option value="SINGLY_LINKED_LIST">Singly Linked List</option>
      <option value="DOUBLY_LINKED_LIST">Doubly Linked List</option>
    </select>
    <button type="submit">Activate</button>
  </form>
  <p style="margin-top:1rem;">Current: <b>${selectedType}</b></p>
  <p style="margin-top:1rem;"><a href="${pageContext.request.contextPath}/visualize">Open Visualizer</a></p>
</div>
</body>
</html>
