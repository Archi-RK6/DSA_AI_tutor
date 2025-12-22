package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.SortedPriorityQueue;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class SortedPriorityQueueAdapter implements DataStructureAdapter {

    private final SortedPriorityQueue<String, String> pq = new SortedPriorityQueue<>();

    private final List<Pair> mirror = new ArrayList<>();

    private static class Pair {
        final String k, v;
        Pair(String k, String v) { this.k = k; this.v = v; }
        String asLine() { return k + "=" + v; }
    }

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            case "insert" -> {
                String[] kv = parseKV(r.getValue());
                pq.insert(kv[0], kv[1]);


                int pos = -1;
                for (int i = mirror.size() - 1; i >= 0; i--) {
                    if (mirror.get(i).k.compareTo(kv[0]) <= 0) { pos = i; break; }
                }
                if (pos == -1) mirror.add(0, new Pair(kv[0], kv[1]));
                else mirror.add(pos + 1, new Pair(kv[0], kv[1]));

                steps.add(new Step("insert", snapshot(), null, "insert(" + kv[0] + ", " + kv[1] + ")"));
            }
            case "min" -> {
                Entry<String,String> e = pq.min();
                steps.add(new Step(
                        "min",
                        snapshot(),
                        null,
                        e == null ? "min = null" : "min = " + e.getKey() + "=" + e.getValue()
                ));
            }
            case "removeMin" -> {
                Entry<String,String> e = pq.removeMin();
                if (e != null) {
                    String k = e.getKey(), v = e.getValue();
                    for (int i = 0; i < mirror.size(); i++) {
                        Pair p = mirror.get(i);
                        if (p.k.equals(k) && ((p.v == null && v == null) || (p.v != null && p.v.equals(v)))) {
                            mirror.remove(i);
                            break;
                        }
                    }
                }
                steps.add(new Step(
                        "removeMin",
                        snapshot(),
                        null,
                        e == null ? "removeMin = null" : "removeMin -> " + e.getKey() + "=" + e.getValue()
                ));
            }
            case "size" -> {
                steps.add(new Step("size", List.of("size=" + pq.size()), null, "size = " + pq.size()));
            }
            case "isEmpty" -> {
                boolean empty = pq.size() == 0;
                steps.add(new Step("isEmpty", List.of("isEmpty=" + empty), null, "isEmpty = " + empty));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for SORTED_PRIORITY_QUEUE: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>(mirror.size());
        for (Pair p : mirror) out.add(p.asLine());
        return out;
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
