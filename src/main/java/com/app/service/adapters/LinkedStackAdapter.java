package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.LinkedStack;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class LinkedStackAdapter implements DataStructureAdapter {
    private final LinkedStack<String> stack = new LinkedStack<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        if ("push".equals(op)) {
            stack.push(req.getValue());
        } else if ("pop".equals(op)) {
            stack.pop();
        } else if ("top".equals(op)) {
            String x = stack.top();
        } else if ("isEmpty".equals(op)) {
            boolean b = stack.isEmpty();
        } else if ("size".equals(op)) {
            int s = stack.size();
        } else {
            throw new IllegalArgumentException("Unsupported op for LinkedStack: " + op);
        }
        Step s = new Step();
        s.setAction("snapshot");
        List<String> snap = new ArrayList<>();
        for (String v : stack) snap.add(v);
        s.setSnapshot(snap);
        steps.add(s);
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        for (String v : stack) out.add(v);
        return out;
    }
}
