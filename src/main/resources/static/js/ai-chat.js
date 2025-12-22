(() => {
  window.addEventListener('DOMContentLoaded', () => {
    const d = document;

    const root     = d.getElementById('ai-chat-root');
    const toggle   = d.getElementById('ai-chat-toggle');
    const closeBtn = d.getElementById('ai-chat-close');
    const form     = d.getElementById('ai-chat-form');
    const inputEl  = d.getElementById('ai-chat-input');
    const messages = d.getElementById('ai-chat-messages');

    if (!root || !toggle) return;

    const apiBase =
      (typeof window !== 'undefined' && window.ctx) ? window.ctx : '';

    function openChat() {
      root.classList.remove('ai-chat-hidden');
      root.setAttribute('aria-hidden', 'false');
      toggle.setAttribute('aria-expanded', 'true');
      inputEl && inputEl.focus();
    }

    function closeChat() {
      root.classList.add('ai-chat-hidden');
      root.setAttribute('aria-hidden', 'true');
      toggle.setAttribute('aria-expanded', 'false');
    }

    function appendMessage(text, who) {
      if (!messages) return;
      const safe = String(text ?? '')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
      const div = d.createElement('div');
      div.className = 'ai-chat-msg ' + (who === 'user' ? 'user' : 'ai');
      div.innerHTML = safe;
      messages.appendChild(div);
      messages.scrollTop = messages.scrollHeight;
    }

    function buildContextSync() {
      const ctx = {};

      const dsTypeBadge = d.getElementById('dsType');
      if (dsTypeBadge && dsTypeBadge.textContent) {
        ctx.selectedType = dsTypeBadge.textContent.trim();
      }

      const opSel = d.getElementById('operation');
      if (opSel) {
        ctx.operation = opSel.value;
        const opt = opSel.options[opSel.selectedIndex];
        if (opt) ctx.operationLabel = opt.text;
      }

      const idxInput = d.getElementById('index');
      if (idxInput && idxInput.value.trim() !== '') ctx.index = idxInput.value.trim();

      const valInput = d.getElementById('value');
      if (valInput && valInput.value.trim() !== '') ctx.value = valInput.value.trim();

      const msgEl = d.getElementById('msg');
      if (msgEl && msgEl.textContent && msgEl.textContent.trim() !== '') {
        ctx.lastUiResult = msgEl.textContent.trim();
      }

      return ctx;
    }

    async function buildContext() {
      const ctx = buildContextSync();

      try {
        const snapRes = await fetch(`${apiBase}/api/snapshot`, { method: 'GET' });
        if (snapRes.ok) {
          const snap = await snapRes.json();
          ctx.currentSnapshot = snap;
        }
      } catch (e) {
      }

      return ctx;
    }

    toggle.addEventListener('click', (e) => {
      e.preventDefault();
      root.classList.contains('ai-chat-hidden') ? openChat() : closeChat();
    });

    if (closeBtn) {
      closeBtn.addEventListener('click', (e) => {
        e.preventDefault();
        closeChat();
      });
    }

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') closeChat();
    });

    if (form) {
      form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!inputEl) return;

        const msg = inputEl.value.trim();
        if (!msg) return;

        appendMessage(msg, 'user');
        inputEl.value = '';

        const payload = {
          message: msg,
          context: await buildContext()
        };

        try {
          const res = await fetch(`${apiBase}/ai/chat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
          });

          if (!res.ok) {
            appendMessage(`Error: ${res.status}`, 'ai');
            return;
          }

          const data = await res.json();
          appendMessage(
            data && typeof data.reply === 'string' ? data.reply : '...',
            'ai'
          );
        } catch (err) {
          appendMessage('Network error', 'ai');
        }
      });
    }
  });
})();
