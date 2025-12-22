const stage = document.getElementById('stage');
const msg = document.getElementById('msg');
const runBtn = document.getElementById('run');
const opSel = document.getElementById('operation');
const idxInput = document.getElementById('index');
const valInput = document.getElementById('value');
const valInput2 = document.getElementById('value2');
const dsTypeBadge = document.getElementById('dsType');

const apiBase = (typeof ctx !== 'undefined' ? ctx : '');
let currentType = null;

const lastOpByType = Object.create(null);

const LS_KEY_TYPE = 'viz:lastSelectedType';
const LS_KEY_SNAP_PREFIX = 'viz:snapshot:';
const LS_KEY_MSG_PREFIX = 'viz:msg:';

function showMsg(text) {
  msg.textContent = text || '';
  try { localStorage.setItem(LS_KEY_MSG_PREFIX + (currentType || 'UNKNOWN'), msg.textContent); } catch (_) {}
}

function safe(s) { return (s == null ? '' : String(s)).trim(); }
function toInt(s) { const n = parseInt(s, 10); return Number.isNaN(n) ? null : n; }
function uniqEdgeKey(a, b) { return a < b ? `${a}|${b}` : `${b}|${a}`; }

function normalizeType(t) {
  if (!t) return 'ARRAY_LIST';
  const map = {
    'array list':'ARRAY_LIST',
    'singly linked list':'SINGLY_LINKED_LIST',
    'doubly linked list':'DOUBLY_LINKED_LIST',
    'circularly linked list':'CIRCULARLY_LINKED_LIST',
    'array stack':'ARRAY_STACK',
    'linked stack':'LINKED_STACK',
    'array queue':'ARRAY_QUEUE',
    'linked queue':'LINKED_QUEUE',
    'linked positional list':'LINKED_POSITIONAL_LIST',
    'graph':'GRAPH',
    'adjacency graph':'GRAPH',
    'binary search tree':'BINARY_SEARCH_TREE',
    'bst':'BINARY_SEARCH_TREE',
    'avl tree':'AVL_TREE',
    'red-black tree':'RED_BLACK_TREE',
    'red black tree':'RED_BLACK_TREE',
    'splay tree':'SPLAY_TREE',
    'unsorted table map':'UNSORTED_TABLE_MAP',
    'sorted table map':'SORTED_TABLE_MAP',
    'chaining hash map':'CHAIN_HASH_MAP',
    'chain hash map':'CHAIN_HASH_MAP',
    'open-address hash map':'PROBE_HASH_MAP',
    'probe hash map':'PROBE_HASH_MAP',
    'unsorted priority queue':'UNSORTED_PRIORITY_QUEUE',
    'sorted priority queue':'SORTED_PRIORITY_QUEUE',
    'heap priority queue':'HEAP_PRIORITY_QUEUE',
    'disjoint set':'PARTITION',
    'union find':'PARTITION',
    'partition':'PARTITION'
  };
  const lc = String(t).trim().toLowerCase();
  if (map[lc]) return map[lc];
  return String(t).trim().replace(/[\s-]+/g,'_').toUpperCase();
}

function lsSnapKey(type) { return LS_KEY_SNAP_PREFIX + type; }
function lsMsgKey(type) { return LS_KEY_MSG_PREFIX + type; }

function saveSelectedType(type) {
  try { localStorage.setItem(LS_KEY_TYPE, type); } catch (_) {}
}

function loadSelectedTypeFallback() {
  try { return localStorage.getItem(LS_KEY_TYPE) || null; } catch (_) { return null; }
}

function saveSnapshot(type, snap) {
  try {
    if (Array.isArray(snap)) localStorage.setItem(lsSnapKey(type), JSON.stringify(snap));
  } catch (_) {}
}

function loadSnapshot(type) {
  try {
    const raw = localStorage.getItem(lsSnapKey(type));
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : null;
  } catch (_) { return null; }
}

function loadSavedMsg(type) {
  try { return localStorage.getItem(lsMsgKey(type)) || ''; } catch (_) { return ''; }
}

const OPS = {
  ARRAY_LIST: [
    {v:'add',label:'Add (index, value)',needsIndex:true,needsValue:true},
    {v:'set',label:'Set (index, value)',needsIndex:true,needsValue:true},
    {v:'remove',label:'Remove (index)',needsIndex:true,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  SINGLY_LINKED_LIST: [
    {v:'addFirst',label:'Add First (value)',needsIndex:false,needsValue:true},
    {v:'addLast',label:'Add Last (value)',needsIndex:false,needsValue:true},
    {v:'removeFirst',label:'Remove First',needsIndex:false,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'last',label:'Last',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  DOUBLY_LINKED_LIST: [
    {v:'addFirst',label:'Add First (value)',needsIndex:false,needsValue:true},
    {v:'addLast',label:'Add Last (value)',needsIndex:false,needsValue:true},
    {v:'removeFirst',label:'Remove First',needsIndex:false,needsValue:false},
    {v:'removeLast',label:'Remove Last',needsIndex:false,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'last',label:'Last',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  CIRCULARLY_LINKED_LIST: [
    {v:'addFirst',label:'Add First (value)',needsIndex:false,needsValue:true},
    {v:'addLast',label:'Add Last (value)',needsIndex:false,needsValue:true},
    {v:'removeFirst',label:'Remove First',needsIndex:false,needsValue:false},
    {v:'rotate',label:'Rotate',needsIndex:false,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'last',label:'Last',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  ARRAY_STACK: [
    {v:'push',label:'Push (value)',needsIndex:false,needsValue:true},
    {v:'pop',label:'Pop',needsIndex:false,needsValue:false},
    {v:'top',label:'Top',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  LINKED_STACK: [
    {v:'push',label:'Push (value)',needsIndex:false,needsValue:true},
    {v:'pop',label:'Pop',needsIndex:false,needsValue:false},
    {v:'top',label:'Top',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  ARRAY_QUEUE: [
    {v:'enqueue',label:'Enqueue (value)',needsIndex:false,needsValue:true},
    {v:'dequeue',label:'Dequeue',needsIndex:false,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  LINKED_QUEUE: [
    {v:'enqueue',label:'Enqueue (value)',needsIndex:false,needsValue:true},
    {v:'dequeue',label:'Dequeue',needsIndex:false,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  LINKED_POSITIONAL_LIST: [
    {v:'addFirst',label:'Add First (value)',needsIndex:false,needsValue:true},
    {v:'addLast',label:'Add Last (value)',needsIndex:false,needsValue:true},
    {v:'addBefore',label:'Add Before (index, value)',needsIndex:true,needsValue:true},
    {v:'addAfter',label:'Add After (index, value)',needsIndex:true,needsValue:true},
    {v:'set',label:'Set (index, value)',needsIndex:true,needsValue:true},
    {v:'remove',label:'Remove (index)',needsIndex:true,needsValue:false},
    {v:'first',label:'First',needsIndex:false,needsValue:false},
    {v:'last',label:'Last',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  GRAPH: [
    {v:'addVertex',label:'Add Vertex (id)',needsIndex:false,needsValue:true},
    {v:'removeVertex',label:'Remove Vertex (id)',needsIndex:false,needsValue:true},
    {v:'addEdge',label:'Add Edge (u,v[,w])',needsIndex:false,needsValue:true, pair:true},
    {v:'removeEdge',label:'Remove Edge (u,v)',needsIndex:false,needsValue:true, pair:true},
    {v:'outDegree',label:'Out Degree (vertex)',needsIndex:false,needsValue:true},
    {v:'inDegree',label:'In Degree (vertex)',needsIndex:false,needsValue:true},
    {v:'numVertices',label:'Num Vertices',needsIndex:false,needsValue:false},
    {v:'numEdges',label:'Num Edges',needsIndex:false,needsValue:false},
  ],
  BINARY_SEARCH_TREE: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
    {v:'firstEntry',label:'First Entry',needsIndex:false,needsValue:false},
    {v:'lastEntry',label:'Last Entry',needsIndex:false,needsValue:false},
    {v:'floorEntry',label:'Floor Entry (key)',needsIndex:false,needsValue:true},
    {v:'ceilingEntry',label:'Ceiling Entry (key)',needsIndex:false,needsValue:true},
  ],
  AVL_TREE: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
    {v:'firstEntry',label:'First Entry',needsIndex:false,needsValue:false},
    {v:'lastEntry',label:'Last Entry',needsIndex:false,needsValue:false},
    {v:'floorEntry',label:'Floor Entry (key)',needsIndex:false,needsValue:true},
    {v:'ceilingEntry',label:'Ceiling Entry (key)',needsIndex:false,needsValue:true},
  ],
  RED_BLACK_TREE: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
    {v:'firstEntry',label:'First Entry',needsIndex:false,needsValue:false},
    {v:'lastEntry',label:'Last Entry',needsIndex:false,needsValue:false},
    {v:'floorEntry',label:'Floor Entry (key)',needsIndex:false,needsValue:true},
    {v:'ceilingEntry',label:'Ceiling Entry (key)',needsIndex:false,needsValue:true},
  ],
  SPLAY_TREE: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
    {v:'firstEntry',label:'First Entry',needsIndex:false,needsValue:false},
    {v:'lastEntry',label:'Last Entry',needsIndex:false,needsValue:false},
    {v:'floorEntry',label:'Floor Entry (key)',needsIndex:false,needsValue:true},
    {v:'ceilingEntry',label:'Ceiling Entry (key)',needsIndex:false,needsValue:true},
  ],
  UNSORTED_TABLE_MAP: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  SORTED_TABLE_MAP: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
    {v:'firstEntry',label:'First Entry',needsIndex:false,needsValue:false},
    {v:'lastEntry',label:'Last Entry',needsIndex:false,needsValue:false},
    {v:'floorEntry',label:'Floor Entry (key)',needsIndex:false,needsValue:true},
    {v:'ceilingEntry',label:'Ceiling Entry (key)',needsIndex:false,needsValue:true},
  ],
  CHAIN_HASH_MAP: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  PROBE_HASH_MAP: [
    {v:'put',label:'Put (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'get',label:'Get (key)',needsIndex:false,needsValue:true},
    {v:'remove',label:'Remove (key)',needsIndex:false,needsValue:true},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  UNSORTED_PRIORITY_QUEUE: [
    {v:'insert',label:'Insert (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'min',label:'Min',needsIndex:false,needsValue:false},
    {v:'removeMin',label:'Remove Min',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  SORTED_PRIORITY_QUEUE: [
    {v:'insert',label:'Insert (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'min',label:'Min',needsIndex:false,needsValue:false},
    {v:'removeMin',label:'Remove Min',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  HEAP_PRIORITY_QUEUE: [
    {v:'insert',label:'Insert (key,value)',needsIndex:false,needsValue:true, pair:true},
    {v:'min',label:'Min',needsIndex:false,needsValue:false},
    {v:'removeMin',label:'Remove Min',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
  PARTITION: [
    {v:'make',label:'Make (id)',needsIndex:false,needsValue:true},
    {v:'find',label:'Find (id)',needsIndex:false,needsValue:true},
    {v:'union',label:'Union (a,b)',needsIndex:false,needsValue:true, pair:true},
    {v:'clusters',label:'Clusters',needsIndex:false,needsValue:false},
    {v:'size',label:'Size',needsIndex:false,needsValue:false},
    {v:'isEmpty',label:'Is Empty',needsIndex:false,needsValue:false},
  ],
};

function placeholderFor(type, op) {
  if (op === 'put' || op === 'insert') return 'key,value';
  if (op === 'union') return 'a,b';
  if (type === 'GRAPH' && (op === 'addEdge' || op === 'removeEdge')) return 'u,v';
  if (type === 'GRAPH' && (op === 'outDegree' || op === 'inDegree')) return 'vertex';
  if (op === 'get' || op === 'remove' || op === 'floorEntry' || op === 'ceilingEntry') return 'key';
  if (op === 'addVertex' || op === 'removeVertex' || op === 'make' || op === 'find') return 'id';
  return 'value';
}

async function apiSelect(type) {
  const form = new URLSearchParams();
  form.append('type', type);
  const res = await fetch(`${apiBase}/api/select`, {
    method:'POST',
    headers:{'Content-Type':'application/x-www-form-urlencoded'},
    body: form
  });
  if (!res.ok) {
    const txt = await res.text().catch(()=> '');
    throw new Error(`Select failed (${res.status}) ${txt}`);
  }
}

async function apiOperate(payload) {
  const res = await fetch(`${apiBase}/api/operate`, {
    method:'POST',
    headers:{'Content-Type':'application/json'},
    body: JSON.stringify(payload)
  });
  if (!res.ok) {
    const txt = await res.text().catch(()=> '');
    throw new Error(`Operate failed (${res.status}) ${txt}`);
  }
  return await res.json();
}

async function apiSnapshot() {
  const res = await fetch(`${apiBase}/api/snapshot`);
  if (!res.ok) {
    const txt = await res.text().catch(()=> '');
    throw new Error(`Snapshot failed (${res.status}) ${txt}`);
  }
  return await res.json();
}

function setCurrentType(typeRaw) {
  const t = normalizeType(typeRaw);
  currentType = OPS[t] ? t : 'ARRAY_LIST';
  if (dsTypeBadge) dsTypeBadge.textContent = currentType;
  saveSelectedType(currentType);
  setOpsFor(currentType);
}

function setOpsFor(type) {
  opSel.innerHTML = '';
  const ops = OPS[type] || [];
  for (const o of ops) {
    const opt = document.createElement('option');
    opt.value = o.v;
    opt.textContent = o.label;
    opSel.appendChild(opt);
  }
  if (opSel.options.length > 0) opSel.selectedIndex = 0;
  updateInputs();
  validateForm();
}

function getCurrentMeta() {
  const ops = OPS[currentType] || [];
  return ops.find(o => o.v === opSel.value) || {needsIndex:false, needsValue:false};
}

function updateInputs() {
  const meta = getCurrentMeta();
  const ph = placeholderFor(currentType, opSel.value);
  const needsPair = !!meta.pair || (meta.needsValue && ph.includes(','));

  idxInput.disabled = !meta.needsIndex;
  idxInput.style.display = meta.needsIndex ? '' : 'none';
  if (!meta.needsIndex) idxInput.value = '';

  valInput.disabled = !meta.needsValue;
  valInput.style.display = meta.needsValue ? '' : 'none';
  if (!meta.needsValue) valInput.value = '';

  if (valInput2) {
    if (meta.needsValue && needsPair) {
      valInput2.disabled = false;
      valInput2.style.display = '';
      const parts = ph.split(',');
      valInput.placeholder = (parts[0] || 'value 1').trim();
      valInput2.placeholder = (parts[1] || 'value 2').trim();
    } else {
      valInput2.disabled = true;
      valInput2.style.display = 'none';
      valInput2.value = '';
      valInput.placeholder = ph;
    }
  } else {
    valInput.placeholder = ph;
  }

  validateForm();
}

function validateForm() {
  const meta = getCurrentMeta();
  let ok = true;
  showMsg(loadSavedMsg(currentType));

  if (meta.needsIndex) {
    const v = safe(idxInput.value);
    if (v === '' || toInt(v) === null) {
      ok = false;
      showMsg('Please provide a valid index.');
    }
  }

  if (ok && meta.needsValue) {
    const ph = placeholderFor(currentType, opSel.value);
    const needsPair = !!meta.pair || ph.includes(',');
    const v1 = safe(valInput.value);

    if (v1 === '') {
      ok = false;
      showMsg('Value cannot be empty.');
    }
    if (ok && needsPair && valInput2 && valInput2.style.display !== 'none') {
      const v2 = safe(valInput2.value);
      if (v2 === '') {
        ok = false;
        showMsg('Please fill both values.');
      }
    }
  }

  runBtn.disabled = !ok;
  return ok;
}

function clearStage() { stage.innerHTML = ''; }

function mkLabel(text) {
  const d = document.createElement('div');
  d.textContent = text;
  d.style.fontWeight = '800';
  d.style.margin = '2px 0 8px 0';
  d.style.opacity = '0.9';
  return d;
}

function mkHint(text) {
  const d = document.createElement('div');
  d.textContent = text;
  d.style.marginTop = '10px';
  d.style.opacity = '0.8';
  d.style.fontSize = '0.95rem';
  return d;
}

function parseKeyValue(line) {
  const s0 = safe(line);
  if (s0 === '') return { k: '', v: '' };

  let s = s0;

  if (s.startsWith('(') && s.endsWith(')')) s = s.slice(1, -1).trim();
  if (s.startsWith('[') && s.endsWith(']')) s = s.slice(1, -1).trim();
  if (s.startsWith('{') && s.endsWith('}')) s = s.slice(1, -1).trim();

  s = s.replace(/^Entry\s*\(/i, '').replace(/\)\s*$/,'').trim();
  s = s.replace(/^Item\s*\(/i, '').replace(/\)\s*$/,'').trim();

  const mKeyVal =
      s.match(/^\s*key\s*[:=]\s*(.+?)\s*,\s*value\s*[:=]\s*(.+?)\s*$/i) ||
      s.match(/^\s*value\s*[:=]\s*(.+?)\s*,\s*key\s*[:=]\s*(.+?)\s*$/i);

  if (mKeyVal) {
    if (/^\s*key\s*[:=]/i.test(s)) return { k: safe(mKeyVal[1]), v: safe(mKeyVal[2]) };
    return { k: safe(mKeyVal[2]), v: safe(mKeyVal[1]) };
  }

  const idxEq = s.indexOf('=');
  if (idxEq >= 0) return { k: safe(s.slice(0, idxEq)), v: safe(s.slice(idxEq + 1)) };

  const idxCol = s.indexOf(':');
  if (idxCol >= 0) return { k: safe(s.slice(0, idxCol)), v: safe(s.slice(idxCol + 1)) };

  const parts = s.split(',').map(x => safe(x)).filter(Boolean);
  if (parts.length >= 2) return { k: parts[0], v: parts.slice(1).join(',') };

  const mArrow = s.match(/^\s*(.+?)\s*->\s*(.+?)\s*$/);
  if (mArrow) return { k: safe(mArrow[1]), v: safe(mArrow[2]) };

  return { k: s0, v: '' };
}

function compareKeys(a, b) {
  const na = Number(a);
  const nb = Number(b);
  const aNum = Number.isFinite(na) && safe(a) !== '';
  const bNum = Number.isFinite(nb) && safe(b) !== '';
  if (aNum && bNum) return na - nb;
  return String(a).localeCompare(String(b));
}

function drawArrayList(items) {
  clearStage();
  if (!items || !items.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const cellW = '46px';
  const tbl = document.createElement('table');
  tbl.style.borderCollapse = 'separate';
  tbl.style.borderSpacing = '10px 6px';
  tbl.style.marginTop = '6px';

  const tr1 = document.createElement('tr');
  const tr2 = document.createElement('tr');

  items.forEach((v, i) => {
    const td = document.createElement('td');
    td.className = 'node';
    td.style.textAlign = 'center';
    td.style.minWidth = cellW;
    td.textContent = String(v);
    tr1.appendChild(td);

    const ti = document.createElement('td');
    ti.style.textAlign = 'center';
    ti.style.opacity = '0.75';
    ti.style.fontWeight = '700';
    ti.style.minWidth = cellW;
    ti.textContent = String(i);
    tr2.appendChild(ti);
  });

  tbl.appendChild(tr1);
  tbl.appendChild(tr2);
  stage.appendChild(tbl);
}

function drawSinglyLinkedList(items, circular=false) {
  clearStage();
  if (!items || !items.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const wrap = document.createElement('div');
  wrap.style.position = 'relative';

  const row = document.createElement('div');
  row.className = 'horiz-list';
  row.style.alignItems = 'stretch';
  wrap.appendChild(row);

  items.forEach((v, i) => {
    const node = document.createElement('div');
    node.style.display = 'flex';
    node.style.flexDirection = 'column';
    node.style.gap = '6px';

    const data = document.createElement('div');
    data.className = 'node';
    data.style.fontWeight = '800';
    data.style.textAlign = 'center';
    data.textContent = String(v);

    const next = document.createElement('div');
    next.style.opacity = '0.75';
    next.style.fontWeight = '700';
    next.style.textAlign = 'center';
    next.textContent = 'next';

    node.appendChild(data);
    node.appendChild(next);

    row.appendChild(node);

    if (i < items.length - 1) {
      const a = document.createElement('span');
      a.className = 'arrow';
      a.textContent = '→';
      row.appendChild(a);
    }
  });

  if (!circular) {
    const nullNode = document.createElement('div');
    nullNode.className = 'node';
    nullNode.style.opacity = '0.65';
    nullNode.style.textAlign = 'center';
    nullNode.style.fontWeight = '800';
    nullNode.textContent = 'null';
    row.appendChild(document.createElement('span')).className = 'arrow';
    row.lastChild.textContent = '→';
    row.appendChild(nullNode);
  } else {
    row.appendChild(document.createElement('span')).className = 'arrow';
    row.lastChild.textContent = '↩';
  }

  stage.appendChild(wrap);
}

function drawDoublyLinkedList(items) {
  clearStage();
  if (!items || !items.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const wrap = document.createElement('div');
  const row = document.createElement('div');
  row.className = 'horiz-list';
  wrap.appendChild(row);

  items.forEach((v, i) => {
    const node = document.createElement('div');
    node.style.display = 'grid';
    node.style.gridTemplateColumns = 'auto auto auto';
    node.style.border = '1px solid var(--node-br)';
    node.style.borderRadius = '10px';
    node.style.overflow = 'hidden';
    node.style.background = 'var(--node-bg)';

    const prev = document.createElement('div');
    prev.style.padding = '8px 10px';
    prev.style.opacity = '0.75';
    prev.style.fontWeight = '700';
    prev.textContent = 'prev';

    const data = document.createElement('div');
    data.style.padding = '8px 10px';
    data.style.fontWeight = '900';
    data.style.borderLeft = '1px solid var(--node-br)';
    data.style.borderRight = '1px solid var(--node-br)';
    data.textContent = String(v);

    const next = document.createElement('div');
    next.style.padding = '8px 10px';
    next.style.opacity = '0.75';
    next.style.fontWeight = '700';
    next.textContent = 'next';

    node.appendChild(prev);
    node.appendChild(data);
    node.appendChild(next);

    row.appendChild(node);

    if (i < items.length - 1) {
      const a = document.createElement('span');
      a.className = 'arrow';
      a.textContent = '↔';
      row.appendChild(a);
    }
  });

  stage.appendChild(wrap);
}

function drawStack(items, reverseVisual=false, showTopLabel=true) {
  clearStage();
  if (!items || !items.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const list = reverseVisual ? [...items] : [...items].reverse();

  const wrap = document.createElement('div');
  wrap.style.display = 'inline-flex';
  wrap.style.flexDirection = 'column';
  wrap.style.alignItems = 'center';
  wrap.style.gap = '6px';

  if (showTopLabel) {
    const top = document.createElement('div');
    top.textContent = 'TOP';
    top.style.fontWeight = '900';
    top.style.opacity = '0.9';
    wrap.appendChild(top);
  }

  list.forEach((v, i) => {
    const node = document.createElement('div');
    node.className = 'node';
    node.style.minWidth = '120px';
    node.style.textAlign = 'center';
    node.style.fontWeight = '900';
    node.textContent = String(v);
    wrap.appendChild(node);

    if (i < list.length - 1) {
      const link = document.createElement('div');
      link.textContent = '↓';
      link.style.opacity = '0.8';
      link.style.fontWeight = '900';
      wrap.appendChild(link);
    }
  });

  stage.appendChild(wrap);
}

function drawQueue(items, linked=false) {
  clearStage();
  if (!items || !items.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const wrap = document.createElement('div');
  const row = document.createElement('div');
  row.className = 'horiz-list';
  row.style.alignItems = 'center';

  const front = document.createElement('div');
  front.textContent = 'FRONT';
  front.style.fontWeight = '900';
  front.style.opacity = '0.9';
  row.appendChild(front);

  row.appendChild(Object.assign(document.createElement('span'), {className:'arrow', textContent:'→'}));

  items.forEach((v, i) => {
    const node = document.createElement('div');
    node.className = 'node';
    node.style.minWidth = '90px';
    node.style.textAlign = 'center';
    node.style.fontWeight = '800';
    node.textContent = String(v);
    row.appendChild(node);

    if (linked && i < items.length - 1) {
      const a = document.createElement('span');
      a.className = 'arrow';
      a.textContent = '→';
      row.appendChild(a);
    }
  });

  row.appendChild(Object.assign(document.createElement('span'), {className:'arrow', textContent:'→'}));

  const rear = document.createElement('div');
  rear.textContent = 'REAR';
  rear.style.fontWeight = '900';
  rear.style.opacity = '0.9';
  row.appendChild(rear);

  wrap.appendChild(row);
  stage.appendChild(wrap);
}

function drawKeyValueTable(lines, title) {
  clearStage();
  showMsg(loadSavedMsg(currentType));

  const head = mkLabel(title || 'Table');
  stage.appendChild(head);

  const table = document.createElement('table');
  table.style.width = '100%';
  table.style.borderCollapse = 'collapse';
  table.style.marginTop = '8px';

  const thead = document.createElement('thead');
  const hr = document.createElement('tr');
  ['Key','Value'].forEach(h => {
    const th = document.createElement('th');
    th.textContent = h;
    th.style.textAlign = 'left';
    th.style.padding = '8px 10px';
    th.style.borderBottom = '2px solid var(--node-br)';
    th.style.opacity = '0.95';
    hr.appendChild(th);
  });
  thead.appendChild(hr);
  table.appendChild(thead);

  const tbody = document.createElement('tbody');
  const items = (lines || []).map(parseKeyValue).filter(x => x.k !== '');

  if (currentType === 'SORTED_TABLE_MAP' || title === 'SORTED_TABLE_MAP') {
    items.sort((a,b) => compareKeys(a.k, b.k));
  }

  if (!items.length) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 2;
    td.textContent = '(empty)';
    td.style.padding = '10px';
    td.style.opacity = '0.8';
    tr.appendChild(td);
    tbody.appendChild(tr);
  } else {
    items.forEach(({k,v}) => {
      const tr = document.createElement('tr');

      const tdK = document.createElement('td');
      tdK.textContent = k;
      tdK.style.padding = '8px 10px';
      tdK.style.borderBottom = '1px solid rgba(0,0,0,0.08)';

      const tdV = document.createElement('td');
      tdV.textContent = v;
      tdV.style.padding = '8px 10px';
      tdV.style.borderBottom = '1px solid rgba(0,0,0,0.08)';

      tr.appendChild(tdK);
      tr.appendChild(tdV);
      tbody.appendChild(tr);
    });
  }

  table.appendChild(tbody);
  stage.appendChild(table);

  const last = lastOpByType[currentType];
  if (last) stage.appendChild(mkHint(`Last operation: ${last}`));
}

function parseHashSnapshot(lines) {
  let cap = null;
  const rows = [];
  for (const raw of lines || []) {
    const line = safe(raw);
    if (!line) continue;
    if (line.startsWith('CAP=')) { cap = line; continue; }
    const m = line.match(/^(\d+)\|(.*)$/);
    if (!m) continue;
    const idx = parseInt(m[1], 10);
    const rest = safe(m[2]);
    rows.push({ idx, rest });
  }
  rows.sort((a,b) => a.idx - b.idx);
  return { cap, rows };
}

function drawChainHashMapTable(lines) {
  clearStage();
  if (!lines || !lines.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const { cap, rows } = parseHashSnapshot(lines);

  const head = mkLabel('CHAIN_HASH_MAP');
  stage.appendChild(head);

  const table = document.createElement('table');
  table.className = 'ds-table';

  const thead = document.createElement('thead');
  const hr = document.createElement('tr');
  ['Index', 'Bucket (linked list)'].forEach(h => {
    const th = document.createElement('th');
    th.textContent = h;
    hr.appendChild(th);
  });
  thead.appendChild(hr);
  table.appendChild(thead);

  const tbody = document.createElement('tbody');

  rows.forEach(({ idx, rest }) => {
    const tr = document.createElement('tr');

    const tdI = document.createElement('td');
    tdI.textContent = String(idx);
    tr.appendChild(tdI);

    const tdB = document.createElement('td');
    const wrap = document.createElement('div');
    wrap.className = 'll-row';

    const items = rest ? rest.split(';').map(s => safe(s)).filter(Boolean) : [];

    if (!items.length) {
      const nullChip = document.createElement('div');
      nullChip.className = 'll-null';
      nullChip.textContent = '(empty)';
      wrap.appendChild(nullChip);
    } else {
      items.forEach((it, i) => {
        const node = document.createElement('div');
        node.className = 'll-node';
        const kv = document.createElement('div');
        kv.className = 'll-data';
        kv.textContent = it;
        node.appendChild(kv);
        wrap.appendChild(node);

        if (i < items.length - 1) {
          const a = document.createElement('span');
          a.className = 'll-arrow';
          a.textContent = '→';
          wrap.appendChild(a);
        }
      });
    }

    tdB.appendChild(wrap);
    tr.appendChild(tdB);

    tbody.appendChild(tr);
  });

  table.appendChild(tbody);
  stage.appendChild(table);

  if (cap) stage.appendChild(mkHint(cap));
  const last = lastOpByType[currentType];
  if (last) stage.appendChild(mkHint(`Last operation: ${last}`));
}

function drawProbeHashMapTable(lines) {
  clearStage();
  if (!lines || !lines.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const { cap, rows } = parseHashSnapshot(lines);

  const head = mkLabel('PROBE_HASH_MAP');
  stage.appendChild(head);

  const table = document.createElement('table');
  table.className = 'ds-table';

  const thead = document.createElement('thead');
  const hr = document.createElement('tr');
  ['Index', 'Slot'].forEach(h => {
    const th = document.createElement('th');
    th.textContent = h;
    hr.appendChild(th);
  });
  thead.appendChild(hr);
  table.appendChild(thead);

  const tbody = document.createElement('tbody');

  rows.forEach(({ idx, rest }) => {
    const tr = document.createElement('tr');

    const tdI = document.createElement('td');
    tdI.textContent = String(idx);
    tr.appendChild(tdI);

    const tdS = document.createElement('td');

    if (rest === 'EMPTY') {
      const empty = document.createElement('div');
      empty.className = 'll-null';
      empty.textContent = 'EMPTY';
      tdS.appendChild(empty);
    } else if (rest === 'DEFUNCT') {
      const def = document.createElement('div');
      def.className = 'll-null';
      def.textContent = 'DEFUNCT';
      def.style.borderStyle = 'solid';
      def.style.opacity = '0.8';
      tdS.appendChild(def);
    } else {
      const node = document.createElement('div');
      node.className = 'll-node';
      const kv = document.createElement('div');
      kv.className = 'll-data';
      kv.textContent = rest;
      node.appendChild(kv);
      tdS.appendChild(node);
    }

    tr.appendChild(tdS);
    tbody.appendChild(tr);
  });

  table.appendChild(tbody);
  stage.appendChild(table);

  if (cap) stage.appendChild(mkHint(cap));
  const last = lastOpByType[currentType];
  if (last) stage.appendChild(mkHint(`Last operation: ${last}`));
}

function parseTreeLines(lines) {
  const nodes = {};
  const children = new Set();

  for (const raw of lines || []) {
    const line = safe(raw);
    if (!line || !line.includes('|')) continue;

    const parts = line
        .split('|')
        .map(s => safe(s))
        .map(s => (s === 'null' || s === 'NULL' || s === '-' ? '' : s));

    if (parts.length === 5) {
      const [id, key, color, L, R] = parts;
      if (!id) continue;
      nodes[id] = { id, label:key, left: L || null, right: R || null, color: (color||'').toUpperCase() };
      if (L) children.add(L);
      if (R) children.add(R);
      continue;
    }

    if (parts.length === 4) {
      const [a, b, c, d] = parts;
      const isColor = (b === 'R' || b === 'B');
      if (isColor) {
        const key = a, color = b, L = c, R = d;
        if (!key) continue;
        nodes[key] = { id:key, label:key, left: L || null, right: R || null, color };
        if (L) children.add(L);
        if (R) children.add(R);
      } else {
        const id = a, key = b, L = c, R = d;
        if (!id) continue;
        nodes[id] = { id, label:key, left: L || null, right: R || null, color: null };
        if (L) children.add(L);
        if (R) children.add(R);
      }
      continue;
    }

    if (parts.length === 3) {
      const [key, L, R] = parts;
      if (!key) continue;
      nodes[key] = { id:key, label:key, left: L || null, right: R || null, color: null };
      if (L) children.add(L);
      if (R) children.add(R);
    }
  }

  let root = null;
  for (const k of Object.keys(nodes)) {
    if (!children.has(k)) { root = k; break; }
  }

  return { nodes, root };
}

function layoutTree(nodes, rootId, width) {
  const coords = {};
  let xCounter = 0;
  const ids = Object.keys(nodes);
  const total = Math.max(ids.length, 1);

  function dfs(id, depth) {
    if (!id || !nodes[id]) return;
    dfs(nodes[id].left, depth+1);
    xCounter += 1;
    const x = Math.round((xCounter/(total+1)) * width);
    const y = 60 + depth*92;
    coords[id] = { x, y };
    dfs(nodes[id].right, depth+1);
  }

  dfs(rootId, 0);
  return coords;
}

function drawTree(lines, colored=false) {
  clearStage();
  const { nodes, root } = parseTreeLines(lines);
  if (!root) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const wrap = document.createElement('div');
  wrap.className = 'tree-wrap';

  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.classList.add('tree-svg');
  wrap.appendChild(svg);

  stage.appendChild(wrap);

  const width = Math.max(stage.clientWidth, 900);
  const coords = layoutTree(nodes, root, width);
  const svgH = Math.max(...Object.values(coords).map(p => p.y)) + 140;

  svg.setAttribute('width', width);
  svg.setAttribute('height', svgH);
  wrap.style.height = svgH + 'px';

  for (const id of Object.keys(nodes)) {
    const n = nodes[id];
    const p = coords[id];
    if (!p) continue;
    for (const side of ['left','right']) {
      const child = n[side];
      if (child && coords[child]) {
        const c = coords[child];
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', p.x); line.setAttribute('y1', p.y);
        line.setAttribute('x2', c.x); line.setAttribute('y2', c.y);
        line.setAttribute('stroke', 'var(--edge)');
        line.setAttribute('stroke-width', '2');
        svg.appendChild(line);
      }
    }
  }

  for (const id of Object.keys(nodes)) {
    const n = nodes[id];
    const p = coords[id];
    if (!p) continue;
    const div = document.createElement('div');
    div.className = 'tree-node';
    if (colored && (n.color === 'R' || n.color === 'B')) div.dataset.color = n.color;
    div.style.left = `${p.x}px`;
    div.style.top = `${p.y}px`;
    div.textContent = n.label;
    wrap.appendChild(div);
  }
}

function drawUnsortedPriorityQueue(lines) {
  clearStage();
  showMsg(loadSavedMsg(currentType));

  const head = mkLabel('UNSORTED_PRIORITY_QUEUE');
  stage.appendChild(head);

  const items = (lines || []).map(parseKeyValue).filter(x => x.k !== '' || x.v !== '');

  if (!items.length) {
    stage.appendChild(mkHint('(empty)'));
    return;
  }

  let minIdx = 0;
  for (let i = 1; i < items.length; i++) {
    if (compareKeys(items[i].k, items[minIdx].k) < 0) minIdx = i;
  }

  const wrap = document.createElement('div');
  wrap.className = 'pq-wrap';

  const title = document.createElement('div');
  title.className = 'pq-title';
  title.textContent = 'Bag of entries (unsorted) — scan for min by key';
  wrap.appendChild(title);

  const bag = document.createElement('div');
  bag.style.display = 'flex';
  bag.style.flexWrap = 'wrap';
  bag.style.gap = '10px';
  bag.style.marginTop = '10px';

  items.forEach((it, i) => {
    const chip = document.createElement('div');
    chip.className = 'pq-item';

    const k = document.createElement('span');
    k.className = 'pq-key';
    k.textContent = it.k;

    const v = document.createElement('span');
    v.className = 'pq-val';
    v.textContent = it.v !== '' ? `=${it.v}` : '';

    chip.appendChild(k);
    chip.appendChild(v);

    if (i === minIdx) {
      chip.style.borderColor = 'var(--accent)';
      chip.style.boxShadow = '0 6px 14px rgba(0,0,0,.12)';
    }

    bag.appendChild(chip);
  });

  wrap.appendChild(bag);

  const scan = document.createElement('div');
  scan.className = 'pq-title';
  scan.style.marginTop = '12px';
  scan.textContent = `min (by key) = ${items[minIdx].k}${items[minIdx].v !== '' ? '=' + items[minIdx].v : ''}`;
  wrap.appendChild(scan);

  stage.appendChild(wrap);

  const last = lastOpByType[currentType];
  if (last) stage.appendChild(mkHint(`Last operation: ${last}`));
}

function drawHeapArray(lines) {
  clearStage();
  if (!lines || !lines.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const arr = lines.map(s => {
    const {k,v} = parseKeyValue(s);
    return v === '' ? k : `${k}=${v}`;
  });

  const wrap = document.createElement('div');
  wrap.className = 'heap-wrap';

  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.classList.add('heap-svg');
  wrap.appendChild(svg);

  stage.appendChild(wrap);

  const n = arr.length;
  const width = Math.max(stage.clientWidth, 900);

  function level(i) { return Math.floor(Math.log2(i+1)); }
  function levelStart(l) { return (1<<l) - 1; }
  function nodesInLevel(l) { return (1<<l); }

  const coords = {};
  for (let i=0;i<n;i++) {
    const l = level(i);
    const pos = i - levelStart(l);
    const count = nodesInLevel(l);
    const x = Math.round(((pos+1)/(count+1)) * width);
    const y = 60 + l*95;
    coords[i] = {x,y};
  }

  const maxY = Math.max(...Object.values(coords).map(p => p.y));
  const svgH = maxY + 120;
  svg.setAttribute('width', width);
  svg.setAttribute('height', svgH);
  wrap.style.height = (svgH + 110) + 'px';

  for (let i=0;i<n;i++) {
    const p = coords[i];
    const li = 2*i+1;
    const ri = 2*i+2;
    [li,ri].forEach(ci => {
      if (ci < n) {
        const c = coords[ci];
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', p.x); line.setAttribute('y1', p.y);
        line.setAttribute('x2', c.x); line.setAttribute('y2', c.y);
        line.setAttribute('stroke', 'var(--edge)');
        line.setAttribute('stroke-width', '2');
        svg.appendChild(line);
      }
    });
  }

  for (let i=0;i<n;i++) {
    const p = coords[i];
    const node = document.createElement('div');
    node.className = 'heap-node';
    node.style.left = `${p.x}px`;
    node.style.top = `${p.y}px`;
    node.textContent = arr[i];
    wrap.appendChild(node);
  }

  const tbl = document.createElement('table');
  tbl.style.borderCollapse = 'separate';
  tbl.style.borderSpacing = '10px 6px';
  tbl.style.marginTop = (svgH + 10) + 'px';

  const tr1 = document.createElement('tr');
  const tr2 = document.createElement('tr');

  const cellW = '60px';

  for (let i=0;i<n;i++) {
    const td = document.createElement('td');
    td.className = 'node';
    td.style.textAlign = 'center';
    td.style.minWidth = cellW;
    td.style.fontWeight = '800';
    td.textContent = arr[i];
    tr1.appendChild(td);

    const ti = document.createElement('td');
    ti.style.textAlign = 'center';
    ti.style.opacity = '0.75';
    ti.style.fontWeight = '700';
    ti.style.minWidth = cellW;
    ti.textContent = String(i);
    tr2.appendChild(ti);
  }

  tbl.appendChild(tr1);
  tbl.appendChild(tr2);
  wrap.appendChild(tbl);
}

function parseGraph(lines) {
  const nodes = new Set();
  const edges = new Set();

  for (const raw of lines || []) {
    const line = safe(raw);
    if (!line) continue;

    const parts = line.split(':');
    const v = safe(parts[0]);
    if (!v) continue;
    nodes.add(v);

    if (parts.length >= 2) {
      const neigh = safe(parts.slice(1).join(':'));
      neigh.split(',').map(s => safe(s)).filter(Boolean).forEach(w => {
        nodes.add(w);
        edges.add(uniqEdgeKey(v, w));
      });
    }
  }

  const nodeList = Array.from(nodes).sort();
  const edgeList = Array.from(edges).map(k => k.split('|'));
  return { nodeList, edgeList };
}

function drawGraph(lines) {
  clearStage();
  if (!lines || !lines.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const { nodeList, edgeList } = parseGraph(lines);
  if (!nodeList.length) { showMsg('Empty'); return; }

  const wrap = document.createElement('div');
  wrap.className = 'graph-wrap';

  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.classList.add('graph-svg');
  wrap.appendChild(svg);

  stage.appendChild(wrap);

  const width = Math.max(stage.clientWidth, 900);
  const height = Math.max(460, Math.min(740, 140 + nodeList.length*24));
  svg.setAttribute('width', width);
  svg.setAttribute('height', height);
  wrap.style.height = height + 'px';

  const cx = width / 2;
  const cy = height / 2;
  const r = Math.min(width, height) * 0.35;

  const pos = {};
  nodeList.forEach((name, i) => {
    const angle = (2*Math.PI*i)/nodeList.length - Math.PI/2;
    pos[name] = { x: cx + r*Math.cos(angle), y: cy + r*Math.sin(angle) };
  });

  edgeList.forEach(([a,b]) => {
    if (!pos[a] || !pos[b]) return;
    const line = document.createElementNS('http://www.w3.org/2000/svg','line');
    line.setAttribute('x1', pos[a].x);
    line.setAttribute('y1', pos[a].y);
    line.setAttribute('x2', pos[b].x);
    line.setAttribute('y2', pos[b].y);
    line.setAttribute('stroke', 'var(--edge)');
    line.setAttribute('stroke-width', '2');
    svg.appendChild(line);
  });

  nodeList.forEach(name => {
    const p = pos[name];
    const div = document.createElement('div');
    div.className = 'graph-node';
    div.style.left = `${p.x}px`;
    div.style.top = `${p.y}px`;
    div.textContent = name;
    wrap.appendChild(div);
  });
}

function drawPartition(lines) {
  clearStage();
  if (!lines || !lines.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  const grid = document.createElement('div');
  grid.className = 'bucket-grid';

  lines.forEach(raw => {
    const line = safe(raw);
    if (!line) return;

    const card = document.createElement('div');
    card.className = 'bucket';

    const parts = line.split(':');
    const rep = safe(parts[0]) || 'cluster';
    const members = parts.length >= 2
        ? safe(parts.slice(1).join(':')).split(',').map(s => safe(s)).filter(Boolean)
        : [];

    const h = document.createElement('h5');
    h.textContent = `Representative: ${rep}`;
    card.appendChild(h);

    const row = document.createElement('div');
    row.className = 'kv-row';

    if (!members.length) {
      const chip = document.createElement('div');
      chip.className = 'kv-chip';
      chip.textContent = '(empty)';
      row.appendChild(chip);
    } else {
      members.forEach(m => {
        const chip = document.createElement('div');
        chip.className = 'kv-chip';
        chip.textContent = m;
        row.appendChild(chip);
      });
    }

    card.appendChild(row);
    grid.appendChild(card);
  });

  stage.appendChild(grid);
}

function renderSnapshot(list) {
  clearStage();
  if (!list || !list.length) { showMsg('Empty'); return; }
  showMsg(loadSavedMsg(currentType));

  switch (currentType) {
    case 'ARRAY_LIST': drawArrayList(list); return;
    case 'SINGLY_LINKED_LIST': drawSinglyLinkedList(list, false); return;
    case 'CIRCULARLY_LINKED_LIST': drawSinglyLinkedList(list, true); return;
    case 'DOUBLY_LINKED_LIST': drawDoublyLinkedList(list); return;
    case 'ARRAY_STACK': drawStack(list, false, false); return;
    case 'LINKED_STACK': drawStack(list, true, true); return;
    case 'ARRAY_QUEUE': drawQueue(list, false); return;
    case 'LINKED_QUEUE': drawQueue(list, true); return;
    case 'LINKED_POSITIONAL_LIST': drawSinglyLinkedList(list, false); return;
    case 'GRAPH': drawGraph(list); return;
    case 'HEAP_PRIORITY_QUEUE': drawHeapArray(list); return;
    case 'UNSORTED_PRIORITY_QUEUE': drawUnsortedPriorityQueue(list); return;
    case 'SORTED_PRIORITY_QUEUE': drawKeyValueTable(list, currentType); return;
    case 'UNSORTED_TABLE_MAP':
    case 'SORTED_TABLE_MAP':
      drawKeyValueTable(list, currentType);
      return;
    case 'CHAIN_HASH_MAP': drawChainHashMapTable(list); return;
    case 'PROBE_HASH_MAP': drawProbeHashMapTable(list); return;
    case 'BINARY_SEARCH_TREE': {
      const hasPipes = (list || []).some(s => String(s).includes('|'));
      if (hasPipes) drawTree(list, false);
      else drawKeyValueTable(list, 'BINARY_SEARCH_TREE');
      return;
    }
    case 'AVL_TREE': {
      const hasPipes = (list || []).some(s => String(s).includes('|'));
      if (hasPipes) drawTree(list, false);
      else drawKeyValueTable(list, 'AVL_TREE');
      return;
    }
    case 'RED_BLACK_TREE':
      drawTree(list, true);
      return;
    case 'SPLAY_TREE': {
      const hasPipes = (list || []).some(s => String(s).includes('|'));
      if (hasPipes) drawTree(list, false);
      else drawKeyValueTable(list, 'SPLAY_TREE');
      return;
    }
    case 'PARTITION': drawPartition(list); return;
    default: {
      const line = document.createElement('div');
      line.className = 'horiz-list';
      list.forEach((e, i) => {
        const node = document.createElement('div');
        node.className = 'node';
        node.textContent = e;
        line.appendChild(node);
        if (i < list.length - 1) {
          const arrow = document.createElement('span');
          arrow.className = 'arrow';
          arrow.textContent = '→';
          line.appendChild(arrow);
        }
      });
      stage.appendChild(line);
      return;
    }
  }
}

function extractServerMessage(steps) {
  if (!Array.isArray(steps) || steps.length === 0) return '';
  for (let i = steps.length - 1; i >= 0; i--) {
    const m = safe(steps[i]?.message);
    if (m) return m;
  }
  return '';
}

function extractStepsPayload(res) {
  if (Array.isArray(res)) return res;
  if (res && Array.isArray(res.steps)) return res.steps;
  return null;
}


function computeResultText(type, op, snapshot) {
  const items = Array.isArray(snapshot) ? snapshot : [];
  const n = items.length;

  if (op === 'size') return `Size: ${n}`;
  if (op === 'isEmpty') return `Is empty: ${n === 0 ? 'true' : 'false'}`;

  if (type === 'UNSORTED_PRIORITY_QUEUE' && op === 'min') {
    if (!items.length) return 'min = null';
    const parsed = items.map(parseKeyValue).filter(x => x.k !== '' || x.v !== '');
    if (!parsed.length) return 'min = null';
    let minIdx = 0;
    for (let i = 1; i < parsed.length; i++) {
      if (compareKeys(parsed[i].k, parsed[minIdx].k) < 0) minIdx = i;
    }
    return `min = ${parsed[minIdx].k}${parsed[minIdx].v !== '' ? '=' + parsed[minIdx].v : ''}`;
  }

  if (op === 'top') {
    if (n === 0) return 'Top: (structure is empty)';
    const v = (type === 'ARRAY_STACK') ? items[n - 1] : items[0];
    return `Top: ${v}`;
  }
  if (op === 'first') {
    if (n === 0) return 'First: (structure is empty)';
    return `First: ${items[0]}`;
  }
  if (op === 'last') {
    if (n === 0) return 'Last: (structure is empty)';
    return `Last: ${items[n - 1]}`;
  }

  if (type === 'GRAPH' && (op === 'numVertices' || op === 'numEdges')) {
    const { nodeList, edgeList } = parseGraph(items);
    if (op === 'numVertices') return `numVertices = ${nodeList.length}`;
    if (op === 'numEdges') return `numEdges = ${edgeList.length}`;
  }

  if (type === 'HEAP_PRIORITY_QUEUE' && op === 'min') {
    if (!items.length) return 'min = null';
    const {k,v} = parseKeyValue(items[0]);
    return v ? `min = ${k}=${v}` : `min = ${k}`;
  }

  return '';
}

function persistCurrentView() {
  if (!currentType) return;
  try {
    const cached = loadSnapshot(currentType);
    if (cached && cached.length) saveSnapshot(currentType, cached);
    localStorage.setItem(LS_KEY_TYPE, currentType);
    localStorage.setItem(lsMsgKey(currentType), msg.textContent || '');
  } catch (_) {}
}

function restoreFromCacheIfNeeded(force) {
  if (!currentType) return;
  const cached = loadSnapshot(currentType);
  if (!cached || !cached.length) return;
  const emptyNow = !stage || stage.childNodes.length === 0 || safe(stage.textContent) === '';
  if (force || emptyNow) {
    renderSnapshot(cached);
    const m = loadSavedMsg(currentType);
    if (m) msg.textContent = m;
  }
}

opSel.addEventListener('change', updateInputs);

[valInput, idxInput, valInput2].forEach(inp => {
  if (!inp) return;
  inp.addEventListener('input', validateForm);
  inp.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (validateForm()) runBtn.click();
    }
  });
});

runBtn.addEventListener('click', async (e) => {
  e.preventDefault();
  if (!validateForm()) return;

  const meta = getCurrentMeta();
  const ph = placeholderFor(currentType, opSel.value);
  const needsPair = !!meta.pair || (meta.needsValue && ph.includes(','));

  const payload = { operation: opSel.value };

  if (meta.needsIndex) payload.index = toInt(idxInput.value);

  if (meta.needsValue) {
    const v1 = safe(valInput.value);
    if (needsPair && valInput2 && valInput2.style.display !== 'none') {
      const v2 = safe(valInput2.value);
      payload.value = `${v1},${v2}`;
    } else {
      payload.value = v1;
    }
  }

  runBtn.disabled = true;
  showMsg('Working...');

  try {
    const res = await apiOperate(payload);
    const steps = extractStepsPayload(res);

    if (steps && steps.length) {
      const serverMsg = extractServerMessage(steps);
      if (serverMsg) showMsg(serverMsg);

      lastOpByType[currentType] =
        payload.operation + (payload.value != null ? `(${payload.value})` : '');

      const lastStep = steps[steps.length - 1];

      let snap = lastStep?.snapshot;
      if (!Array.isArray(snap)) {
        try { snap = await apiSnapshot(); } catch (_) { snap = loadSnapshot(currentType) || []; }
      }

      if (Array.isArray(snap)) {
        renderSnapshot(snap);
        saveSnapshot(currentType, snap);
      }

      if (!serverMsg) {
        const fallback = computeResultText(currentType, payload.operation, snap);
        if (fallback) showMsg(fallback);
      }

    } else {
      let snap = [];
      try { snap = await apiSnapshot(); } catch (_) { snap = loadSnapshot(currentType) || []; }

      if (Array.isArray(snap) && snap.length) {
        renderSnapshot(snap);
        saveSnapshot(currentType, snap);
      } else {
        restoreFromCacheIfNeeded(true);
      }

      const fallback = computeResultText(currentType, payload.operation, snap);
      if (fallback) showMsg(fallback);
    }
  } catch (err) {
    showMsg(err?.message || String(err));
    restoreFromCacheIfNeeded(false);
  } finally {
    runBtn.disabled = false;
    validateForm();
  }
});

window.addEventListener('beforeunload', persistCurrentView);

const bodyObserver = new MutationObserver(() => {
  restoreFromCacheIfNeeded(false);
});
if (document.body) bodyObserver.observe(document.body, { attributes: true, attributeFilter: ['class'] });

const stageObserver = new MutationObserver(() => {
  restoreFromCacheIfNeeded(false);
});
if (stage) stageObserver.observe(stage, { childList: true, subtree: true });

(async function boot() {
  try {
    const badgeRaw = dsTypeBadge ? safe(dsTypeBadge.textContent) : '';
    const fallbackType = loadSelectedTypeFallback();
    const chosen = normalizeType(badgeRaw || fallbackType || 'ARRAY_LIST');

    setCurrentType(chosen);

    const cached = loadSnapshot(currentType);
    if (cached && cached.length) {
      renderSnapshot(cached);
      const m = loadSavedMsg(currentType);
      if (m) msg.textContent = m;
    }

    try {
      await apiSelect(currentType);
      const snap = await apiSnapshot();

      if (Array.isArray(snap) && snap.length) {
        renderSnapshot(snap);
        saveSnapshot(currentType, snap);
      } else {
        restoreFromCacheIfNeeded(true);
      }
    } catch (_) {
      restoreFromCacheIfNeeded(true);
    }

    validateForm();
  } catch (e) {
    showMsg(e?.message || String(e));
    restoreFromCacheIfNeeded(true);
  }
})();
