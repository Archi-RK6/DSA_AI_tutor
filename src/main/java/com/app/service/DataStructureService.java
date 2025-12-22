package com.app.service;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DataStructureService {

    private static final String PREFIX = UUID.randomUUID().toString();
    public static final String SESSION_ADAPTER_KEY = "ACTIVE_DS_ADAPTER" + PREFIX;
    public static final String SESSION_TYPE_KEY    = "ACTIVE_DS_TYPE" + PREFIX;

    private final StructureRegistry registry;

    public DataStructureService(StructureRegistry registry) {
        this.registry = registry;
    }

    public StructureRegistry.Type selectedType(HttpSession session) {
        Object t = session.getAttribute(SESSION_TYPE_KEY);
        return t instanceof StructureRegistry.Type ? (StructureRegistry.Type) t : null;
    }

    public void select(HttpSession session, String type) {
        StructureRegistry.Type t = registry.parse(type);
        session.setAttribute(SESSION_TYPE_KEY, t);
        session.setAttribute(SESSION_ADAPTER_KEY, registry.newAdapter(t));
    }

    public List<Step> operate(HttpSession session, OperationRequest req) {
        DataStructureAdapter adapter = (DataStructureAdapter) session.getAttribute(SESSION_ADAPTER_KEY);
        if (adapter == null) throw new IllegalStateException("No structure selected");
        return adapter.operate(req);
    }

    public List<String> snapshot(HttpSession session) {
        DataStructureAdapter adapter = (DataStructureAdapter) session.getAttribute(SESSION_ADAPTER_KEY);
        if (adapter == null) throw new IllegalStateException("No structure selected");
        return adapter.snapshot();
    }
}
