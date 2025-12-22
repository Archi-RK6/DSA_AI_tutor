<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Data Structure Selection</title>
  <style>
    .container { max-width: 1100px; margin: 40px auto; }
    .ds-category { margin-bottom: 1.5rem; }
    .ds-category-title { font-weight: 700; margin: .5rem 0; font-size: 1.1rem; }
    .ds-grid { display:flex; flex-wrap:wrap; gap:.6rem; }
    .ds-button {
      display:inline-block; min-width: 220px; padding: 14px 16px;
      text-decoration:none; text-align:center; cursor:pointer; border-radius:10px;
      border:1px solid #2b3760;
    }
    body.light-mode .ds-button { background:#E6F0DC; color:#111; }
    body.dark-mode  .ds-button { background:#0f1530; color:#e8eaf6; }
    .ds-button:hover { filter:brightness(1.05); }
  </style>
</head>
<body class="<%= (session.getAttribute("theme") == null ? "light" : session.getAttribute("theme")) + "-mode" %>">

<%@ include file="header.jsp" %>

<div class="container">
  <h1>Select a Data Structure</h1>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <div class="ds-category">
    <div class="ds-category-title">Linear Data Structures</div>
    <div class="ds-grid">
      <a class="ds-button" href="${ctx}/visualize?type=ARRAY_LIST">Array List</a>
      <a class="ds-button" href="${ctx}/visualize?type=SINGLY_LINKED_LIST">Singly Linked List</a>
      <a class="ds-button" href="${ctx}/visualize?type=DOUBLY_LINKED_LIST">Doubly Linked List</a>
      <a class="ds-button" href="${ctx}/visualize?type=CIRCULARLY_LINKED_LIST">Circularly Linked List</a>
      <a class="ds-button" href="${ctx}/visualize?type=LINKED_POSITIONAL_LIST">Linked Positional List</a>
    </div>
  </div>

  <div class="ds-category">
    <div class="ds-category-title">Stack Structures</div>
    <div class="ds-grid">
      <a class="ds-button" href="${ctx}/visualize?type=ARRAY_STACK">Array Stack</a>
      <a class="ds-button" href="${ctx}/visualize?type=LINKED_STACK">Linked Stack</a>
    </div>
  </div>

  <div class="ds-category">
    <div class="ds-category-title">Queue Structures</div>
    <div class="ds-grid">
      <a class="ds-button" href="${ctx}/visualize?type=ARRAY_QUEUE">Array Queue</a>
      <a class="ds-button" href="${ctx}/visualize?type=LINKED_QUEUE">Linked Queue</a>
    </div>
  </div>

  <div class="ds-category">
    <div class="ds-category-title">Non-linear Data Structures</div>
    <div class="ds-grid">
      <a class="ds-button" href="${ctx}/visualize?type=GRAPH">Graph (Adjacency List)</a>
      <a class="ds-button" href="${ctx}/visualize?type=BINARY_SEARCH_TREE">Binary Search Tree</a>
      <a class="ds-button" href="${ctx}/visualize?type=AVL_TREE">AVL Tree</a>
      <a class="ds-button" href="${ctx}/visualize?type=RED_BLACK_TREE">Red-Black Tree</a>
      <a class="ds-button" href="${ctx}/visualize?type=SPLAY_TREE">Splay Tree</a>
      <a class="ds-button" href="${ctx}/visualize?type=UNSORTED_TABLE_MAP">Unsorted Table Map</a>
      <a class="ds-button" href="${ctx}/visualize?type=SORTED_TABLE_MAP">Sorted Table Map</a>
      <a class="ds-button" href="${ctx}/visualize?type=CHAIN_HASH_MAP">Chaining Hash Map</a>
      <a class="ds-button" href="${ctx}/visualize?type=PROBE_HASH_MAP">Open-Address Hash Map</a>
      <a class="ds-button" href="${ctx}/visualize?type=UNSORTED_PRIORITY_QUEUE">Unsorted Priority Queue</a>
      <a class="ds-button" href="${ctx}/visualize?type=SORTED_PRIORITY_QUEUE">Sorted Priority Queue</a>
      <a class="ds-button" href="${ctx}/visualize?type=HEAP_PRIORITY_QUEUE">Heap Priority Queue</a>
      <a class="ds-button" href="${ctx}/visualize?type=PARTITION">Partition</a>
    </div>
  </div>

  <div class="ds-category">
    <div class="ds-category-title">Sorting Algorithms</div>
    <p><em>No algorithms available yet</em></p>
  </div>
</div>
</body>
</html>