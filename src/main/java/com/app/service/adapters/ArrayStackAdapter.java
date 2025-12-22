package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.ArrayStack;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArrayStackAdapter implements DataStructureAdapter {
    private final ArrayStack<String> stack = new ArrayStack<>();

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
            throw new IllegalArgumentException("Unsupported op for ArrayStack: " + op);
        }
        Step s = new Step();
        s.setAction("snapshot");
        List<String> snap = new ArrayList<>();
        ArrayStack<String> tmp = new ArrayStack<>();
        while (!stack.isEmpty()) {
            String v = stack.pop();
            tmp.push(v);
            snap.add(0, v);
        }
        while (!tmp.isEmpty()) {
            stack.push(tmp.pop());
        }
        s.setSnapshot(snap);
        steps.add(s);
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        ArrayStack<String> tmp = new ArrayStack<>();
        while (!stack.isEmpty()) {
            String v = stack.pop();
            tmp.push(v);
            out.add(0, v);
        }
        while (!tmp.isEmpty()) {
            stack.push(tmp.pop());
        }
        return out;
    }
}
