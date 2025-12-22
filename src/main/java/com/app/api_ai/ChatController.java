package com.app.api_ai;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.service.ChatService;
import com.app.service.DataStructureService;
import com.app.service.StructureRegistry;
import com.app.web.VisualizationApiController;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private static final String SESSION_CHAT_HISTORY_KEY = "AI_CHAT_HISTORY";
    private static final int MAX_CHAT_HISTORY_LINES = 16; // ~8 обменов (user+ai)

    private static final int MAX_OP_HISTORY_TO_SEND = 40;

    private final ChatService chatService;
    private final DataStructureService dataStructureService;

    public ChatController(ChatService chatService, DataStructureService dataStructureService) {
        this.chatService = chatService;
        this.dataStructureService = dataStructureService;
    }

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chatJson(HttpSession session, @RequestBody(required = false) ChatRequest req) {
        String msg = req != null ? req.getMessage() : null;

        Map<String, Object> ctx = new LinkedHashMap<>();
        if (req != null && req.getContext() != null) {
            ctx.putAll(req.getContext());
        }

        StructureRegistry.Type selected = dataStructureService.selectedType(session);
        if (selected != null) {
            ctx.put("selectedType", selected.name());
        }

        // Current snapshot
        try {
            List<String> snap = dataStructureService.snapshot(session);
            ctx.put("currentSnapshot", trimLines(snap, 120, 7000));
        } catch (Exception ignored) {
        }

        Object lastReqObj = session.getAttribute(VisualizationApiController.SESSION_LAST_OP_REQUEST_KEY);
        if (lastReqObj instanceof OperationRequest lastReq) {
            ctx.put("lastOperation", opToMap(lastReq));
        }

        Object lastStepsObj = session.getAttribute(VisualizationApiController.SESSION_LAST_OP_STEPS_KEY);
        if (lastStepsObj instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Step) {
            @SuppressWarnings("unchecked")
            List<Step> steps = (List<Step>) lastStepsObj;

            ctx.put("lastSteps", summarizeStepsTail(steps, 14));

            Step last = steps.get(steps.size() - 1);
            if (last != null && last.getSnapshot() != null) {
                ctx.put("lastStepSnapshot", trimLines(last.getSnapshot(), 120, 7000));
            }

            if (steps.size() >= 2) {
                Step prev = steps.get(steps.size() - 2);
                if (prev != null && prev.getSnapshot() != null) {
                    ctx.put("prevStepSnapshot", trimLines(prev.getSnapshot(), 120, 7000));
                }
            }
        }

        Object opHistObj = session.getAttribute(VisualizationApiController.SESSION_OP_HISTORY_KEY);
        List<String> opHistory = readStringList(opHistObj);
        if (!opHistory.isEmpty()) {
            ctx.put("operationHistory", tail(opHistory, MAX_OP_HISTORY_TO_SEND));
        } else {
            ctx.put("operationHistory", List.of());
        }

        List<String> chatHistory = getOrCreateChatHistory(session);
        String answer = chatService.chatWithContext(msg, ctx, chatHistory);

        chatHistory.add("User: " + (msg == null ? "" : msg));
        chatHistory.add("Assistant: " + answer);
        trimChatHistory(chatHistory);
        session.setAttribute(SESSION_CHAT_HISTORY_KEY, chatHistory);

        return new ChatResponse(answer);
    }

    private static List<String> getOrCreateChatHistory(HttpSession session) {
        Object obj = session.getAttribute(SESSION_CHAT_HISTORY_KEY);
        if (obj instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<String> h = (List<String>) list;
            return h;
        }
        List<String> h = new ArrayList<>();
        session.setAttribute(SESSION_CHAT_HISTORY_KEY, h);
        return h;
    }

    private static void trimChatHistory(List<String> history) {
        while (history.size() > MAX_CHAT_HISTORY_LINES) {
            history.remove(0);
        }
    }

    private static Map<String, Object> opToMap(OperationRequest r) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (r.getOperation() != null) m.put("operation", r.getOperation());
        if (r.getIndex() != null) m.put("index", r.getIndex());
        if (r.getValue() != null) m.put("value", r.getValue());
        return m;
    }

    private static List<Map<String, Object>> summarizeStepsTail(List<Step> steps, int maxTail) {
        int from = Math.max(0, steps.size() - maxTail);
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = from; i < steps.size(); i++) {
            Step s = steps.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("i", i);
            if (s.getAction() != null) row.put("action", s.getAction());
            if (s.getMessage() != null && !s.getMessage().isBlank()) row.put("message", s.getMessage());
            if (s.getHighlights() != null && !s.getHighlights().isEmpty()) row.put("highlights", s.getHighlights());
            out.add(row);
        }
        return out;
    }

    private static List<String> trimLines(List<String> lines, int maxLines, int maxCharsTotal) {
        if (lines == null) return List.of();
        List<String> limited = lines.size() <= maxLines ? lines : lines.subList(0, maxLines);

        int chars = 0;
        List<String> out = new ArrayList<>();
        for (String s : limited) {
            if (s == null) continue;
            String line = s.length() > 180 ? s.substring(0, 180) + "..." : s;
            if (chars + line.length() > maxCharsTotal) break;
            out.add(line);
            chars += line.length();
        }
        return out;
    }

    private static List<String> tail(List<String> list, int n) {
        if (list == null) return List.of();
        if (list.size() <= n) return list;
        return list.subList(list.size() - n, list.size());
    }

    private static List<String> readStringList(Object obj) {
        if (!(obj instanceof List<?> list)) return List.of();
        List<String> out = new ArrayList<>();
        for (Object it : list) {
            if (it instanceof String s) out.add(s);
        }
        return out;
    }
}
