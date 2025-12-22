<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String theme = (String) session.getAttribute("theme");
    if (theme == null) theme = "light";
    if ("toggle".equals(request.getParameter("theme"))) {
        theme = "dark".equals(theme) ? "light" : "dark";
        session.setAttribute("theme", theme);
    }
%>

<style>
  body.light-mode {
      background:#f5f5f5;
      color:#222;
  }
  body.dark-mode {
      background:#0b1020;
      color:#e8eaf6;
  }

  .top-nav {
      display:flex;
      justify-content:space-between;
      align-items:center;
      padding:10px 20px;
  }
  body.light-mode .top-nav {
      background:#A9BCA9;
  }
  body.dark-mode .top-nav {
      background:#141a33;
  }

  .menu-list {
      list-style:none;
      margin:0;
      padding:0;
      display:flex;
      gap:20px;
  }
  .menu-list li a {
      text-decoration:none;
      font-weight:bold;
      color:inherit;
  }

  .theme-toggle {
      cursor:pointer;
      font-weight:bold;
      text-decoration:none;
      color:inherit;
  }

  /* Buttons for DS selection */
  .ds-category { margin-bottom: 2rem; }
  .ds-category-title { font-weight:bold; margin:0.5rem 0; font-size:1.2em; }
  .ds-button {
      display:inline-block;
      min-width:180px;
      margin:10px;
      padding:15px 20px;
      font-size:1.1em;
      font-weight:bold;
      border:none;
      border-radius:8px;
      cursor:pointer;
      text-align:center;
  }
  body.light-mode .ds-button {
      background:#55883B;
      color:#fff;
  }
  body.dark-mode .ds-button {
      background:#446a30;
      color:#e8eaf6;
  }
  body.light-mode {
      --card-bg: #E6F0DC;
      --card-text: #222;
      --accent: #55883B;
      --sidebar-bg: #C5D5B2;
  }
  body.dark-mode {
      --card-bg: #141a33;
      --card-text: #e8eaf6;
      --accent: #A9BCA9;
      --sidebar-bg: #21294a;
  }
  .ds-button:hover { opacity:0.9; }
  .ds-button.active { outline:3px solid #4c6fff; }

  .heap-wrap { position: relative; padding: 16px 8px 8px; min-height: 220px; }
  .heap-svg { position: absolute; left: 0; top: 0; width: 100%; height: 100%; pointer-events: none; }
  .heap-level {
    display: flex; justify-content: center; gap: 60px;
    margin: 18px 0;
  }
  .heap-node {
    width: 56px; height: 56px; border-radius: 999px;
    display: flex; align-items: center; justify-content: center;
    font-weight: 700; box-shadow: 0 1px 0 rgba(0,0,0,.08);
    border: 3px solid #e0663b; /* теплый кант как на скрине */
  }

  /* нижний массив с индексами */
  .heap-array {
    margin-top: 14px; padding: 10px; border-radius: 10px;
    display: flex; flex-wrap: wrap; justify-content: center; gap: 8px;
    border: 2px solid var(--array-border, #2b3760);
    background: var(--array-bg, #e8f6ee);
  }
  .heap-cell {
    position: relative; min-width: 58px; padding: 8px 10px;
    text-align: center; border: 1px solid var(--array-border, #2b3760);
    border-radius: 8px; font-weight: 600;
  }
  .heap-cell-value { line-height: 1; }
  .heap-cell-index {
    position: absolute; bottom: -16px; left: 0; right: 0;
    font-size: 12px; opacity: .7;
  }

  /* темизация */
  body.light-mode .heap-node { background: #ffe96a; color: #111; }
  body.dark-mode  .heap-node { background: #f1cf4b; color: #10121a; }

  body.light-mode { --array-bg:#E6F4EF; --array-border:#2b3760; --edge-color:#9aa4b2; }
  body.dark-mode  { --array-bg:#10172a; --array-border:#44507a; --edge-color:#6270a3; }


.ai-chat-toggle{
  position:fixed; right:20px; bottom:20px; z-index:9999;
  width:56px; height:56px; border-radius:50%;
  border:none; cursor:pointer; box-shadow:0 6px 18px rgba(0,0,0,.25);
  background:#446a30; color:#fff; font-size:22px;
}
body.dark-mode .ai-chat-toggle{ background:#55883B; }

.ai-chat-root{ position:fixed; right:20px; bottom:90px; z-index:9999; }
.ai-chat-hidden{ display:none; }

.ai-chat-window{
  width:360px; max-height:60vh; display:flex; flex-direction:column;
  border-radius:12px; overflow:hidden; box-shadow:0 10px 30px rgba(0,0,0,.35);
  background:var(--chat-bg,#fff); border:1px solid rgba(0,0,0,.1);
}
body.light-mode .ai-chat-window{ --chat-bg:#ffffff; color:#1b1b1b; }
body.dark-mode  .ai-chat-window{ --chat-bg:#0f1530; color:#e8eaf6; border-color:#2b3760; }

.ai-chat-header{
  padding:10px 12px; font-weight:700; display:flex; align-items:center; justify-content:space-between;
  background:rgba(85,136,59,.1);
}
body.dark-mode .ai-chat-header{ background:rgba(85,136,59,.2); }

.ai-chat-header button{
  border:none; background:transparent; color:inherit; font-size:20px; cursor:pointer;
}

.ai-chat-messages{
  padding:10px 12px; overflow:auto; flex:1; display:flex; flex-direction:column; gap:10px;
}
.ai-chat-msg{ padding:10px 12px; border-radius:10px; line-height:1.35; white-space:pre-wrap; }
.ai-chat-msg.user{ align-self:flex-end; background:rgba(85,136,59,.15); }
.ai-chat-msg.ai{   align-self:flex-start; background:rgba(43,55,96,.12); }

.ai-chat-form{ display:flex; gap:8px; padding:10px; border-top:1px solid rgba(0,0,0,.08); }
.ai-chat-form input{
  flex:1; padding:10px 12px; border-radius:8px; border:1px solid rgba(0,0,0,.15);
  background:inherit; color:inherit;
}
.ai-chat-form button{ padding:10px 14px; border-radius:8px; border:none; cursor:pointer;
  background:#446a30; color:#fff; }
body.dark-mode .ai-chat-form button{ background:#55883B; }

.ai-chat-fab {
    position: fixed; right: 20px; bottom: 20px;
    width: 52px; height: 52px; border-radius: 50%;
    background: #2e7d32; color: #fff; display:flex; align-items:center; justify-content:center;
    box-shadow: 0 8px 24px rgba(0,0,0,.25); cursor: pointer; z-index: 2147483646;
    border: 2px solid rgba(255,255,255,.6);
  }
  .ai-chat-root {
    position: fixed; right: 20px; bottom: 90px; width: 380px; max-width: calc(100vw - 40px);
    background: var(--panel, #fff); color: var(--text, #111); border: 1px solid var(--node-br, #ccc);
    border-radius: 12px; box-shadow: 0 18px 40px rgba(0,0,0,.35); z-index: 2147483646;
  }
  .ai-chat-hidden { display: none; }
  .ai-chat-header { display:flex; align-items:center; justify-content:space-between; padding:10px 12px; border-bottom:1px solid var(--node-br, #ccc); font-weight:700; }
  .ai-chat-messages { max-height: 300px; overflow:auto; padding: 10px 12px; }
  .ai-chat-msg { padding:6px 10px; border-radius:10px; margin:6px 0; }
  .ai-chat-msg.user { background: var(--chip-bg, #eef2ff); border:1px solid var(--chip-br, #c7d2fe); }
  .ai-chat-msg.ai   { background: var(--badge-bg, #e8f3e5); border:1px solid var(--node-br, #a7c0a7); }
  .ai-chat-form { display:flex; gap:8px; padding:10px 12px; border-top:1px solid var(--node-br, #ccc); }
  .ai-chat-form input { flex:1; padding:8px 10px; border:1px solid var(--node-br,#ccc); border-radius:8px; background:var(--panel,#fff); color:var(--text,#111); }
  .ai-chat-form button { padding:8px 12px; border-radius:8px; background:var(--accent,#55883B); color:#fff; border:none; cursor:pointer; }

#ai-toggle,
  #ai-drawer {
    position: fixed !important;
    z-index: 2147483647 !important;
  }
</style>

<nav class="top-nav">
<c:choose>
    <c:when test="${sessionScope.AUTH_NAME == 'admin'}">
        <c:url var="homeUrl" value="/admin_home"/>
    </c:when>
    <c:when test="${not empty sessionScope.AUTH_NAME}">
        <c:url var="homeUrl" value="/user_home"/>
    </c:when>
    <c:otherwise>
        <c:url var="homeUrl" value="/login"/>
    </c:otherwise>
</c:choose>
<c:url var="logoutUrl" value="/logout"/>

  <ul class="menu-list">
    <li><a href="${homeUrl}">Home</a></li>
    <li><a href="https://docs.google.com/forms/d/e/1FAIpQLSf3dwF2Z2wpi3MywE5GwiChzvcuftcw5O3d4lQscwuRCyb2vg/viewform?usp=header">Feedback</a></li>
    <li><a href="#">About</a></li>
    <li><a href="${logoutUrl}">Logout</a></li>
  </ul>
  <a class="theme-toggle" href="?theme=toggle">
    <c:choose>
      <c:when test="${sessionScope.theme == 'dark'}">☀ Light Mode</c:when>
      <c:otherwise>🌙 Dark Mode</c:otherwise>
    </c:choose>
  </a>
</nav>

<!--AI -->
<div id="ai-chat-toggle" class="ai-chat-fab" title="Ask AI">🤖</div>

<div id="ai-chat-root" class="ai-chat-root ai-chat-hidden" role="dialog" aria-modal="true">
  <div class="ai-chat-header">
    <span>AI Tutor</span>
    <button id="ai-chat-close" style="background:transparent;border:none;font-size:18px;cursor:pointer" aria-label="Close">✕</button>
  </div>
  <div id="ai-chat-messages" class="ai-chat-messages" aria-live="polite"></div>
  <form id="ai-chat-form" class="ai-chat-form">
    <input id="ai-chat-input" type="text" placeholder="Ask about this screen, the algorithm, errors…" autocomplete="off"/>
    <button type="submit">Send</button>
  </form>
</div>

<button id="ai-chat-toggle" class="ai-chat-toggle" title="Ask AI">🤖</button>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/ai-chat.css"/>

<script>
  window.ctx = '<%= request.getContextPath() %>';
</script>
<script defer src="${pageContext.request.contextPath}/js/ai-chat.js"></script>

<script>
  document.addEventListener("DOMContentLoaded", function() {
    document.body.classList.remove("light-mode","dark-mode");
    document.body.classList.add("<%= theme %>-mode");
  });
</script>

