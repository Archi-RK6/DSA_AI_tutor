package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.UnsortedTableMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class UnsortedTableMapAdapter implements DataStructureAdapter {

    private final UnsortedTableMap<String, String> map = new UnsortedTableMap<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            case "put" -> {
                String[] kv = parseKV(r.getValue());
                String prev = map.put(kv[0], kv[1]);
                String msg = "put(" + kv[0] + ", " + kv[1] + ")" +
                        (prev != null ? " replaced=" + prev : "");
                steps.add(new Step("put", snapshot(), null, msg));
            }
            case "get" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                String val = map.get(key);
                steps.add(new Step("get", snapshot(), null, "get(" + key + ") = " + val));
            }
            case "remove" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                String prev = map.remove(key);
                steps.add(new Step("remove", snapshot(), null, "remove(" + key + ") -> " + prev));
            }
            case "size" -> {
                steps.add(new Step("size", List.of("size=" + map.size()), null, "size = " + map.size()));
            }
            case "isEmpty" -> {
                steps.add(new Step("isEmpty", List.of("isEmpty=" + map.isEmpty()), null, "isEmpty = " + map.isEmpty()));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for UNSORTED_TABLE_MAP: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> rows = new ArrayList<>();
        for (Entry<String, String> e : map.entrySet()) {
            rows.add(e.getKey() + "=" + e.getValue());
        }
        return rows;
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
