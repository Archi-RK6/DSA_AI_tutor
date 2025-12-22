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
      max-width:1000px; margin:40px auto;
      background: var(--card-bg);
      color: var(--card-text);
       padding:20px;
      border-radius:8px; display:flex; gap:20px;
    }
    .sidebar {
      flex:0 0 250px; background: var(--sidebar-bg);
      border-radius:8px; padding:10px;
      max-height:600px; overflow-y:auto;
    }
    .chapter-item {
      padding:8px; margin-bottom:4px;
      border-radius:4px; cursor:pointer;
    }
    .chapter-item.active {
      background:#55883B; color:#fff;
    }
    .content-area {
      flex:1; background:#fff;
      color: black;
      border-radius:8px; padding:20px;
      max-height:600px; overflow-y:auto;
    }
    .chapter-pane {
      display:none;
      min-height:100px; /* тестовая высота */
    }

  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">
<%@ include file="header.jsp" %>

  <div style="position:absolute; top:60px; right:20px;">
    <a href="/admin_home">Back</a> |
    <a href="/admin_home/manage_content?topicId=${Topic}">Manage</a>
  </div>
  <h1 style="text-align:center; color:#656d4f;">${topicName}</h1>

  <div class="container">
    <div class="sidebar">
      <c:forEach var="chap" items="${chapters}" varStatus="loop">
        <div class="chapter-item" data-idx="${loop.index}">
          <c:out value="${chap.contentChapter}" />
        </div>
      </c:forEach>
    </div>

    <div class="content-area">
      <c:forEach var="chap" items="${chapters}" varStatus="loop">
        <div class="chapter-pane" id="pane-${loop.index}">
          <c:out escapeXml="false" value="${chap.contentText}"/>
        </div>
      </c:forEach>
    </div>
    <script>
      document.addEventListener('DOMContentLoaded', () => {
        console.log('script start');
        const items = document.querySelectorAll('.chapter-item');
        const panes = document.querySelectorAll('.chapter-pane');
        console.log(items, panes);
        items.forEach(item => {
          item.addEventListener('click', () => {
            items.forEach(i=>i.classList.remove('active'));
            item.classList.add('active');
            panes.forEach(p=>p.style.setProperty('display','none','important'));
            const pane = document.getElementById('pane-'+item.dataset.idx);
            console.log('activating', pane);
            pane && pane.style.setProperty('display','block','important');
          });
        });
        items[0] && items[0].click();
      });
    </script>
</body>
</html>
