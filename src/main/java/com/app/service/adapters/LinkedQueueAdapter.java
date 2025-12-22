package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.LinkedQueue;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class LinkedQueueAdapter implements DataStructureAdapter {
    private final LinkedQueue<String> q = new LinkedQueue<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        if ("enqueue".equals(op)) {
            q.enqueue(req.getValue());
        } else if ("dequeue".equals(op)) {
            q.dequeue();
        } else if ("first".equals(op)) {
            String x = q.first();
        } else if ("isEmpty".equals(op)) {
            boolean b = q.isEmpty();
        } else if ("size".equals(op)) {
            int s = q.size();
        } else {
            throw new IllegalArgumentException("Unsupported op for LinkedQueue: " + op);
        }
        Step s = new Step();
        s.setAction("snapshot");
        List<String> snap = new ArrayList<>();
        LinkedQueue<String> tmp = new LinkedQueue<>();
        while (!q.isEmpty()) {
            String v = q.dequeue();
            tmp.enqueue(v);
            snap.add(v);
        }
        while (!tmp.isEmpty()) {
            q.enqueue(tmp.dequeue());
        }
        s.setSnapshot(snap);
        steps.add(s);
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        LinkedQueue<String> tmp = new LinkedQueue<>();
        while (!q.isEmpty()) {
            String v = q.dequeue();
            tmp.enqueue(v);
            out.add(v);
        }
        while (!tmp.isEmpty()) {
            q.enqueue(tmp.dequeue());
        }
        return out;
    }
}
