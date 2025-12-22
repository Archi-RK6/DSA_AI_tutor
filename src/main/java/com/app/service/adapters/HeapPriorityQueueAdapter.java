package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.HeapPriorityQueue;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class HeapPriorityQueueAdapter implements DataStructureAdapter {

    private final HeapPriorityQueue<String,String> pq = new HeapPriorityQueue<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            case "insert" -> {
                String[] kv = parseKV(r.getValue());
                pq.insert(kv[0], kv[1]);
                steps.add(new Step("insert", snapshot(), null, "insert(" + kv[0] + ", " + kv[1] + ")"));
            }
            case "min" -> {
                Entry<String,String> e = pq.min();
                steps.add(new Step("min", snapshot(), null,
                        "min = " + (e == null ? "null" : (e.getKey() + "=" + e.getValue()))));
            }
            case "removeMin" -> {
                Entry<String,String> e = pq.removeMin();
                steps.add(new Step("removeMin", snapshot(), null,
                        "removeMin -> " + (e == null ? "null" : (e.getKey() + "=" + e.getValue()))));
            }
            case "size" -> steps.add(new Step("size", List.of("size=" + pq.size()), null, "size=" + pq.size()));
            case "isEmpty" -> steps.add(new Step("isEmpty", List.of("isEmpty=" + (pq.size()==0)), null, "isEmpty=" + (pq.size()==0)));
            default -> throw new IllegalArgumentException("Unsupported operation for HEAP_PRIORITY_QUEUE: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        return pq.exportArray();
    }


    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String mustNonEmpty(String s, String what) {
        String v = safe(s);
        if (v.isEmpty()) throw new IllegalArgumentException("Required: " + what);
        return v;
    }
    private static String[] parseKV(String s) {
        String v = mustNonEmpty(s, "value 'key,value'");
        String[] parts = v.split("[,;\\s]+", 2);
        if (parts.length < 2) throw new IllegalArgumentException("Provide both key and value, e.g. 'A,1'");
        return new String[]{ parts[0], parts[1] };
    }
}
