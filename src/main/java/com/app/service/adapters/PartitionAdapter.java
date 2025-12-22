package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.Partition;
import com.app.nonlinear.interfaces.Position;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PartitionAdapter implements DataStructureAdapter {

    private final Partition<String> uf = new Partition<>();
    private final Map<String, Position<String>> loc = new HashMap<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            // accept both names so old/new UI won't break
            case "make", "makeCluster" -> {
                String x = mustNonEmpty(r.getValue(), "element");
                if (!loc.containsKey(x)) {
                    loc.put(x, uf.makeCluster(x));
                }
                steps.add(new Step("make", snapshot(), null, "make(" + x + ")"));
            }
            case "union" -> {
                String[] ab = parseTwo(mustNonEmpty(r.getValue(), "value 'A B'"));
                Position<String> pa = require(ab[0]);
                Position<String> pb = require(ab[1]);
                uf.union(pa, pb);
                steps.add(new Step("union", snapshot(), null, "union(" + ab[0] + ", " + ab[1] + ")"));
            }
            case "find" -> {
                String x = mustNonEmpty(r.getValue(), "element");
                Position<String> px = require(x);
                Position<String> root = uf.find(px);
                steps.add(new Step("find", snapshot(), null,
                        "find(" + x + ") = " + (root == null ? "null" : root.getElement())));
            }
            case "size" -> steps.add(new Step("size", List.of("size=" + loc.size()), null, "size = " + loc.size()));
            case "clusters" -> {
                int c = countClusters();
                steps.add(new Step("clusters", List.of("clusters=" + c), null, "clusters = " + c));
            }
            case "isEmpty" -> {
                boolean empty = loc.isEmpty();
                steps.add(new Step("isEmpty", List.of("isEmpty=" + empty), null, "isEmpty = " + empty));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for PARTITION: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (Map.Entry<String, Position<String>> e : loc.entrySet()) {
            String label = e.getKey();
            Position<String> root = uf.find(e.getValue());
            String rep = root.getElement();
            groups.computeIfAbsent(rep, k -> new ArrayList<>()).add(label);
        }
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, List<String>> g : groups.entrySet()) {
            List<String> members = g.getValue();
            Collections.sort(members);
            lines.add(g.getKey() + ": " + String.join(", ", members));
        }
        Collections.sort(lines);
        return lines;
    }

    private Position<String> require(String x) {
        Position<String> p = loc.get(x);
        if (p == null) throw new IllegalArgumentException("Element not found: " + x + ". Use make first.");
        return p;
    }

    private int countClusters() {
        Set<String> reps = new HashSet<>();
        for (Position<String> p : loc.values()) {
            reps.add(uf.find(p).getElement());
        }
        return reps.size();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String mustNonEmpty(String s, String what) {
        String v = safe(s);
        if (v.isEmpty()) throw new IllegalArgumentException("Required: " + what);
        return v;
    }

    private static String[] parseTwo(String s) {
        String[] parts = s.split("[,;\\s]+");
        if (parts.length < 2) throw new IllegalArgumentException("Need two ids, e.g. 'A,B'");
        return new String[]{parts[0], parts[1]};
    }
}
