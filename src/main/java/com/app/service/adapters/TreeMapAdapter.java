package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.TreeMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class TreeMapAdapter implements DataStructureAdapter {

    private final TreeMap<String,String> map = new TreeMap<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        String op = safe(r.getOperation());
        List<Step> steps = new ArrayList<>();

        switch (op) {
            case "put" -> {
                String[] kv = parseKV(r.getValue());
                map.put(kv[0], kv[1]);
                steps.add(new Step("put", snapshot(), null, "Inserted " + kv[0] + "=" + kv[1]));
            }
            case "get" -> {
                String k = must(r.getValue(), "key");
                steps.add(new Step("get", snapshot(), null, "get(" + k + ")=" + map.get(k)));
            }
            case "remove" -> {
                String k = must(r.getValue(), "key");
                steps.add(new Step("remove", snapshot(), null, "remove(" + k + ") -> " + map.remove(k)));
            }
            case "size"    -> steps.add(new Step("size",    List.of("size=" + map.size()), null, "size=" + map.size()));
            case "isEmpty" -> steps.add(new Step("isEmpty", List.of("isEmpty=" + map.isEmpty()), null, "isEmpty=" + map.isEmpty()));
            case "firstEntry" -> {
                Entry<String,String> e = firstEntryManual();
                steps.add(new Step("firstEntry", snapshot(), null, e == null ? "first=null" : "first=" + e.getKey() + "=" + e.getValue()));
            }
            case "lastEntry" -> {
                Entry<String,String> e = lastEntryManual();
                steps.add(new Step("lastEntry", snapshot(), null, e == null ? "last=null" : "last=" + e.getKey() + "=" + e.getValue()));
            }
            case "floorEntry" -> {
                String k = must(r.getValue(), "key");
                Entry<String,String> e = floorEntryManual(k);
                steps.add(new Step("floorEntry", snapshot(), null, e == null ? "floor("+k+")=null" : "floor("+k+")="+e.getKey()+"="+e.getValue()));
            }
            case "ceilingEntry" -> {
                String k = must(r.getValue(), "key");
                Entry<String,String> e = ceilingEntryManual(k);
                steps.add(new Step("ceilingEntry", snapshot(), null, e == null ? "ceil("+k+")=null" : "ceil("+k+")="+e.getKey()+"="+e.getValue()));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for BINARY_SEARCH_TREE: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        return map.exportStructure();
    }

    // ---- helpers ----
    private Entry<String,String> firstEntryManual() {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) if (best == null || e.getKey().compareTo(best.getKey()) < 0) best = e;
        return best;
    }
    private Entry<String,String> lastEntryManual() {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet()) if (best == null || e.getKey().compareTo(best.getKey()) > 0) best = e;
        return best;
    }
    private Entry<String,String> floorEntryManual(String k) {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet())
            if (e.getKey().compareTo(k) <= 0 && (best == null || e.getKey().compareTo(best.getKey()) > 0)) best = e;
        return best;
    }
    private Entry<String,String> ceilingEntryManual(String k) {
        Entry<String,String> best = null;
        for (Entry<String,String> e : map.entrySet())
            if (e.getKey().compareTo(k) >= 0 && (best == null || e.getKey().compareTo(best.getKey()) < 0)) best = e;
        return best;
    }

    private static String safe(String s){ return s==null?"":s.trim(); }
    private static String must(String s,String what){ String v=safe(s); if(v.isEmpty()) throw new IllegalArgumentException("Required: "+what); return v; }
    private static String[] parseKV(String s){
        String v = must(s, "value 'key,value'");
        String[] p = v.split("[,;\\s]+", 2);
        if (p.length < 2) throw new IllegalArgumentException("Provide both key and value, e.g. 'A,1'");
        return new String[]{p[0], p[1]};
    }
}
