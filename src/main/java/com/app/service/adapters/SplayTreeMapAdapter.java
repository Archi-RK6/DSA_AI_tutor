package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.SplayTreeMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class SplayTreeMapAdapter implements DataStructureAdapter {

    private final SplayTreeMap<String, String> map = new SplayTreeMap<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            case "put" -> {
                String[] kv = parseKV(r.getValue());
                map.put(kv[0], kv[1]);
                steps.add(new Step("put", snapshot(), null, "put(" + kv[0] + ", " + kv[1] + ")"));
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
            case "firstEntry" -> {
                Entry<String,String> e = firstEntryManual();
                steps.add(new Step("firstEntry", snapshot(), null,
                        e == null ? "firstEntry = null" : "firstEntry = " + e.getKey() + "=" + e.getValue()));
            }
            case "lastEntry" -> {
                Entry<String,String> e = lastEntryManual();
                steps.add(new Step("lastEntry", snapshot(), null,
                        e == null ? "lastEntry = null" : "lastEntry = " + e.getKey() + "=" + e.getValue()));
            }
            case "floorEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = floorEntryManual(key);
                steps.add(new Step("floorEntry", snapshot(), null,
                        e == null ? "floorEntry(" + key + ") = null" : "floorEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            case "ceilingEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = ceilingEntryManual(key);
                steps.add(new Step("ceilingEntry", snapshot(), null,
                        e == null ? "ceilingEntry(" + key + ") = null" : "ceilingEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for SPLAY_TREE: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        return map.exportStructure();
    }


    private Entry<String,String> firstEntryManual() {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) {
            if (best == null || cmp(e.getKey(), best.getKey()) < 0) best = e;
        }
        return best;
    }

    private Entry<String,String> lastEntryManual() {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) {
            if (best == null || cmp(e.getKey(), best.getKey()) > 0) best = e;
        }
        return best;
    }

    private Entry<String,String> floorEntryManual(String key) {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) {
            if (cmp(e.getKey(), key) <= 0) {
                if (best == null || cmp(e.getKey(), best.getKey()) > 0) best = e;
            }
        }
        return best;
    }

    private Entry<String,String> ceilingEntryManual(String key) {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) {
            if (cmp(e.getKey(), key) >= 0) {
                if (best == null || cmp(e.getKey(), best.getKey()) < 0) best = e;
            }
        }
        return best;
    }

    private int cmp(String a, String b) { return a.compareTo(b); }


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
