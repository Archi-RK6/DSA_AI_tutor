package com.app.web;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.service.DataStructureService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VisualizationApiController {

    public static final String SESSION_LAST_OP_REQUEST_KEY = "LAST_DS_OP_REQUEST";
    public static final String SESSION_LAST_OP_STEPS_KEY   = "LAST_DS_OP_STEPS";
    public static final String SESSION_LAST_SNAPSHOT_KEY   = "LAST_DS_SNAPSHOT";

    public static final String SESSION_OP_HISTORY_KEY      = "DS_OP_HISTORY";
    private static final int MAX_OP_HISTORY = 80;

    private final DataStructureService ds;

    public VisualizationApiController(DataStructureService ds) {
        this.ds = ds;
    }

    @PostMapping("/select")
    public void select(@RequestParam("type") String type, HttpSession session) {
        ds.select(session, type);

        session.removeAttribute(SESSION_LAST_OP_REQUEST_KEY);
        session.removeAttribute(SESSION_LAST_OP_STEPS_KEY);
        session.removeAttribute(SESSION_LAST_SNAPSHOT_KEY);
        session.setAttribute(SESSION_OP_HISTORY_KEY, new ArrayList<String>());
    }

    @GetMapping("/snapshot")
    public List<String> snapshot(HttpSession session) {
        try {
            List<String> snap = ds.snapshot(session);
            session.setAttribute(SESSION_LAST_SNAPSHOT_KEY, snap);
            return snap;
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PostMapping("/operate")
    public List<Step> operate(@RequestBody OperationRequest request, HttpSession session) {
        try {
            List<Step> steps = ds.operate(session, request);

            session.setAttribute(SESSION_LAST_OP_REQUEST_KEY, request);
            session.setAttribute(SESSION_LAST_OP_STEPS_KEY, steps);

            if (steps != null && !steps.isEmpty()) {
                Step last = steps.get(steps.size() - 1);
                if (last != null && last.getSnapshot() != null) {
                    session.setAttribute(SESSION_LAST_SNAPSHOT_KEY, last.getSnapshot());
                }
            }

            appendOpHistory(session, request, steps);

            return steps;
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void appendOpHistory(HttpSession session, OperationRequest req, List<Step> steps) {
        Object obj = session.getAttribute(SESSION_OP_HISTORY_KEY);

        List<String> history;
        if (obj instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof String)) {
            history = (List<String>) obj;
        } else {
            history = new ArrayList<>();
            session.setAttribute(SESSION_OP_HISTORY_KEY, history);
        }

        String op = formatOp(req);

        String lastMsg = null;
        if (steps != null && !steps.isEmpty()) {
            Step last = steps.get(steps.size() - 1);
            if (last != null && last.getMessage() != null && !last.getMessage().isBlank()) {
                lastMsg = trim(last.getMessage(), 180);
            }
        }

        String entry = op + (lastMsg != null ? (" -> " + lastMsg) : "");
        history.add(entry);

        while (history.size() > MAX_OP_HISTORY) {
            history.remove(0);
        }
    }

    private String formatOp(OperationRequest req) {
        String op = req != null && req.getOperation() != null ? req.getOperation() : "op";
        List<String> params = new ArrayList<>();

        if (req != null && req.getIndex() != null) params.add("index=" + req.getIndex());
        if (req != null && req.getValue() != null && !req.getValue().isBlank()) params.add("value=" + req.getValue());

        return op + "(" + String.join(", ", params) + ")";
    }

    private String trim(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
