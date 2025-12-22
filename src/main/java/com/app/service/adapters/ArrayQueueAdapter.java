package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.ArrayQueue;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArrayQueueAdapter implements DataStructureAdapter {
    private final ArrayQueue<String> q = new ArrayQueue<>();

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
            throw new IllegalArgumentException("Unsupported op for ArrayQueue: " + op);
        }
        Step s = new Step();
        s.setAction("snapshot");
        List<String> snap = new ArrayList<>();
        ArrayQueue<String> tmp = new ArrayQueue<>();
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
        ArrayQueue<String> tmp = new ArrayQueue<>();
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
