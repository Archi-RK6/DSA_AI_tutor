package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.SortedTableMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SortedTableMapAdapter implements DataStructureAdapter {

    private final SortedTableMap<String, String> map = new SortedTableMap<>();

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
            case "firstEntry" -> {
                Entry<String,String> e = map.firstEntry();
                steps.add(new Step("firstEntry", snapshot(), null,
                        e == null ? "firstEntry = null" : "firstEntry = " + e.getKey() + "=" + e.getValue()));
            }
            case "lastEntry" -> {
                Entry<String,String> e = map.lastEntry();
                steps.add(new Step("lastEntry", snapshot(), null,
                        e == null ? "lastEntry = null" : "lastEntry = " + e.getKey() + "=" + e.getValue()));
            }
            case "floorEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = map.floorEntry(key);
                steps.add(new Step("floorEntry", snapshot(), null,
                        e == null ? "floorEntry(" + key + ") = null" : "floorEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            case "ceilingEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = map.ceilingEntry(key);
                steps.add(new Step("ceilingEntry", snapshot(), null,
                        e == null ? "ceilingEntry(" + key + ") = null" : "ceilingEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            case "lowerEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = map.lowerEntry(key);
                steps.add(new Step("lowerEntry", snapshot(), null,
                        e == null ? "lowerEntry(" + key + ") = null" : "lowerEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            case "higherEntry" -> {
                String key = mustNonEmpty(r.getValue(), "key");
                Entry<String,String> e = map.higherEntry(key);
                steps.add(new Step("higherEntry", snapshot(), null,
                        e == null ? "higherEntry(" + key + ") = null" : "higherEntry(" + key + ") = " + e.getKey() + "=" + e.getValue()));
            }
            case "subMap" -> {
                String[] range = parseTwo(r.getValue(), "value 'from,to'");
                List<String> list = new ArrayList<>();
                for (Entry<String,String> e : map.subMap(range[0], range[1])) {
                    list.add(e.getKey() + "=" + e.getValue());
                }
                String msg = "subMap(" + range[0] + "," + range[1] + ") = [" +
                        list.stream().collect(Collectors.joining(", ")) + "]";
                steps.add(new Step("subMap", snapshot(), null, msg));
            }
            default -> throw new IllegalArgumentException("Unsupported operation for SORTED_TABLE_MAP: " + op);
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

    private static String[] parseTwo(String s, String what) {
        String v = mustNonEmpty(s, what);
        String[] parts = v.split("[,;\\s]+");
        if (parts.length < 2) throw new IllegalArgumentException("Need two tokens in " + what);
        return new String[]{ parts[0], parts[1] };
    }
}
