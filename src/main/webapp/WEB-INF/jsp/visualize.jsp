<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Visualizer</title>
  <style>
    body.light-mode {
      --bg: #f7faf7;
      --text: #0b1220;
      --panel: #ffffff;
      --muted: #A9BCA9;
      --edge: #65708a;
      --node-bg: #E6F0DC;
      --node-br: #446a30;
      --chip-bg: #eef2ff;
      --chip-br: #c7d2fe;
      --accent: #55883B;
      --badge-bg: #e8f3e5;
      --badge-fg: #1f2a1f;
    }
    body.dark-mode {
      --bg: #0b1024;
      --text: #e7ecf6;
      --panel: #0f1530;
      --muted: #7c8a7c;
      --edge: #96a1c4;
      --node-bg: #1a2147;
      --node-br: #2e3a74;
      --chip-bg: #0f1530;
      --chip-br: #2b3760;
      --accent: #7ac06a;
      --badge-bg: #1d2b1d;
      --badge-fg: #d9f2d9;
    }

    body {
      background: var(--bg);
      color: var(--text);
      font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
    }

    .bar {
      display:flex; flex-wrap:wrap; gap:.6rem; align-items:center;
      margin: 16px 0; padding: 12px 14px;
      background: var(--panel); border:1px solid var(--node-br); border-radius:12px;
    }
    .badge {
      background: var(--badge-bg); color: var(--badge-fg);
      padding: 6px 10px; border-radius: 999px; font-weight: 700;
      border: 1px solid var(--node-br);
    }
    .bar input, .bar select, .bar button {
      background: var(--panel); color: var(--text);
      border:1px solid var(--node-br); border-radius:8px; padding: 8px 10px;
    }
    .bar button {
      background: var(--accent); color: #fff; border: none; cursor:pointer;
    }
    .bar button:disabled {
      opacity:.6; cursor:not-allowed;
    }

    #stage {
      position: relative;
      min-height: 650px;
      height: 72vh;
      padding: 16px;
      background: var(--panel);
      border:1px solid var(--node-br);
      border-radius: 14px;
      overflow: auto;
    }

    .msg { margin-top: 8px; color: var(--muted); }

    /* Existing generic */
    .horiz-list { display:flex; align-items:center; flex-wrap:wrap; gap:.4rem; }
    .node {
      padding: 8px 12px; border-radius: 10px;
      background: var(--node-bg); color: var(--text);
      border:1px solid var(--node-br);
      font-weight: 700;
    }
    .arrow { opacity:.7; }

    /* ---------- Trees / Graph / Heap ---------- */
    .tree-wrap { position: relative; min-height: 300px; }
    .tree-svg { position:absolute; left:0; top:0; width:100%; height:100%; pointer-events:none; }
    .tree-edge { stroke: var(--edge); stroke-width: 2; opacity: .9; }

    .tree-node {
      position:absolute; transform: translate(-50%,-50%);
      min-width: 38px; text-align:center;
      padding: 8px 10px; border-radius: 999px;
      background: var(--node-bg); border:1px solid var(--node-br);
      box-shadow: 0 6px 14px rgba(0,0,0,.12);
      font-weight: 700;
      user-select: none;
    }

    .tree-node[data-color="R"] { background:#991b1b; color:#fff; border-color:#7f1d1d; }
    .tree-node[data-color="B"] { background:#0f172a; color:#e2e8f0; border-color:#1f2937; }

    .graph-wrap { position: relative; min-height: 360px; }
    .graph-svg  { position:absolute; left:0; top:0; width:100%; height:100%; pointer-events:none; }
    .graph-edge { stroke: var(--edge); stroke-width: 2; opacity: .8; }

    .graph-node {
      position:absolute; transform: translate(-50%,-50%);
      padding: 10px 12px; border-radius: 999px;
      background: var(--node-bg); border:1px solid var(--node-br); font-weight:700;
      box-shadow: 0 6px 14px rgba(0,0,0,.12);
      user-select: none;
    }

    .heap-array { display:flex; gap:.4rem; flex-wrap:wrap; margin-top: 14px; }

    /* FIX: heap nodes were using .heap-node in JS, but CSS did not define it,
       so left/top were ignored and nodes stacked vertically. */
    .heap-wrap { position: relative; min-height: 360px; }
    .heap-svg  { position:absolute; left:0; top:0; width:100%; height:100%; pointer-events:none; }
    .heap-node {
      position:absolute; transform: translate(-50%,-50%);
      min-width: 38px; text-align:center;
      padding: 8px 10px; border-radius: 999px;
      background: var(--node-bg); border:1px solid var(--node-br);
      box-shadow: 0 6px 14px rgba(0,0,0,.12);
      font-weight: 700;
      user-select: none;
    }

    /* ---------- KV chips / buckets ---------- */
    .kv-row { display:flex; flex-wrap:wrap; gap:.4rem; }
    .kv-chip {
      padding: 6px 10px; border-radius: 10px;
      background: var(--chip-bg); border:1px solid var(--chip-br);
    }

    .bucket-grid {
      display:grid; gap: 12px;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      margin-top: 6px;
    }
    .bucket {
      background: var(--panel); border:1px solid var(--node-br); border-radius:12px;
      padding: 10px;
    }
    .bucket h5 { margin: 0 0 6px 0; color: var(--text); }

    .array-wrap { display: inline-block; }
    .array-cells { display:flex; gap:.35rem; align-items:stretch; }
    .array-cell {
      min-width: 56px;
      text-align:center;
      padding: 10px 12px;
      border-radius: 10px;
      background: var(--node-bg);
      border: 1px solid var(--node-br);
      font-weight: 800;
    }
    .array-indexes {
      display:flex; gap:.35rem;
      margin-top: 6px;
      justify-content: center;
    }
    .array-index {
      min-width: 56px;
      text-align:center;
      font-size: .75rem;
      color: var(--muted);
      font-weight: 700;
    }

    .ll-row { display:flex; align-items:center; flex-wrap:wrap; gap:.35rem; }
    .ll-node {
      display:flex; align-items:center; gap:.55rem;
      background: var(--node-bg);
      border: 1px solid var(--node-br);
      border-radius: 10px;
      padding: 8px 10px;
      font-weight: 700;
    }
    .ll-data { font-weight: 800; }
    .ll-next { font-size: .75rem; opacity: .7; font-weight: 700; }
    .ll-arrow { opacity:.75; font-weight: 900; padding: 0 4px; }
    .ll-loop { margin-left: .4rem; opacity: .85; font-weight: 800; }
    .ll-null {
      opacity:.65;
      padding: 6px 10px;
      border-radius: 10px;
      border: 1px dashed var(--edge);
      font-weight: 700;
    }

    .dll-row { display:flex; align-items:center; flex-wrap:wrap; gap:.35rem; }
    .dll-node {
      display:grid;
      grid-template-columns: auto auto auto;
      align-items:center;
      border-radius: 10px;
      overflow:hidden;
      border: 1px solid var(--node-br);
      background: var(--node-bg);
      font-weight: 700;
    }
    .dll-prev, .dll-data, .dll-next { padding: 8px 10px; }
    .dll-data {
      font-weight: 900;
      border-left: 1px solid var(--node-br);
      border-right: 1px solid var(--node-br);
    }
    .dll-prev, .dll-next { font-size: .75rem; opacity: .7; }
    .dll-arrow { opacity:.75; font-weight: 900; padding: 0 4px; }

    .pos-wrap { display:flex; flex-direction:column; align-items:center; gap:.25rem; }
    .pos-label { font-size: .75rem; color: var(--muted); font-weight: 700; }

    .stack-wrap { display:inline-flex; flex-direction:column; align-items:center; gap:.35rem; }
    .stack-label { font-size:.75rem; color: var(--muted); font-weight: 800; }
    .stack-col { display:flex; flex-direction:column; gap:.25rem; min-width: 140px; }
    .stack-node {
      text-align:center;
      padding: 10px 12px;
      border-radius: 10px;
      background: var(--node-bg);
      border: 1px solid var(--node-br);
      font-weight: 900;
    }
    .stack-link { text-align:center; opacity:.75; font-weight: 900; }

    .queue-wrap { display:flex; justify-content:center; }
    .queue-row { display:flex; align-items:center; flex-wrap:wrap; gap:.35rem; }
    .queue-node {
      padding: 10px 12px;
      border-radius: 10px;
      background: var(--node-bg);
      border: 1px solid var(--node-br);
      font-weight: 900;
    }
    .queue-link { opacity:.75; font-weight: 900; padding: 0 4px; }
    .queue-end {
      padding: 6px 10px;
      border-radius: 999px;
      background: var(--badge-bg);
      color: var(--badge-fg);
      border: 1px solid var(--node-br);
      font-size: .75rem;
      font-weight: 900;
      user-select: none;
    }

    .pq-wrap { display:flex; flex-direction:column; gap:.6rem; }
    .pq-title { color: var(--muted); font-weight: 900; font-size: .85rem; }
    .pq-row { display:flex; flex-wrap:wrap; gap:.4rem; align-items:center; }
    .pq-item {
      display:flex; gap:.45rem; align-items:center;
      padding: 8px 10px;
      border-radius: 10px;
      background: var(--chip-bg);
      border: 1px solid var(--chip-br);
      font-weight: 700;
    }
    .pq-key { font-weight: 900; }
    .pq-val { opacity: .85; }

    .ds-table {
      width:100%;
      border-collapse: separate;
      border-spacing: 0;
      border-radius: 12px;
      overflow:hidden;
      border: 1px solid var(--node-br);
      margin-bottom: 8px;
    }
    .ds-table thead th {
      background: var(--badge-bg);
      color: var(--badge-fg);
      text-align:left;
      font-weight: 900;
      padding: 10px 12px;
      border-bottom: 1px solid var(--node-br);
    }
    .ds-table tbody td {
      padding: 10px 12px;
      border-bottom: 1px solid var(--node-br);
    }
    .ds-table tbody tr:last-child td { border-bottom: none; }

    .last-op {
      margin-top: 6px;
      color: var(--muted);
      font-weight: 900;
      font-size: .85rem;
    }

    /* Keep AI overlay always above */
    #ai-toggle,
    #ai-drawer { z-index: 2147483647 !important; }

    .tree-svg,
    .graph-svg,
    .heap-svg,
    #stage svg { pointer-events: none; }

    #stage,
    .tree-wrap,
    .graph-wrap,
    .heap-wrap {
      position: relative;
      z-index: 1;
    }

    #stage::before,
    #stage::after { pointer-events: none; }

    #msg {
      margin-top: 10px;
      padding: 10px 12px;
      border: 2px solid var(--node-br);
      border-radius: 12px;

      font-weight: 700;
      color: #1f2937;
      background: rgba(255, 255, 255, 0.6);

      min-height: 18px;
    }

    body.dark #msg {
      color: #f3f4f6;
      background: rgba(0,0,0,0.25);
    }

  </style>
</head>

<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">

<%@ include file="header.jsp" %>

<div class="bar">
  <span class="badge" id="dsType"><c:out value="${selectedType}"/></span>
  <select id="operation"></select>
  <input id="index" type="number" placeholder="index"/>

  <input id="value" type="text" placeholder="value"/>
  <input id="value2" type="text" placeholder="value 2" style="display:none;"/>

  <button id="run">Run</button>
</div>

<div id="stage"></div>
<div class="msg" id="msg"></div>

<script>
  window.ctx = '<%= request.getContextPath() %>';
  const ctx = window.ctx;
</script>
<script src="${pageContext.request.contextPath}/js/visualizer.js"></script>
</body>
</html>
