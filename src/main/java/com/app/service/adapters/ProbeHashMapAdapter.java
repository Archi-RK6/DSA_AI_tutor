package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.ProbeHashMap;
import com.app.service.DataStructureAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProbeHashMapAdapter implements DataStructureAdapter {

    private final ProbeHashMap<String, String> map = new ProbeHashMap<>();

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
            default -> throw new IllegalArgumentException("Unsupported operation for PROBE_HASH_MAP: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        try {
            Field fCap = map.getClass().getSuperclass().getDeclaredField("capacity");
            fCap.setAccessible(true);
            int capacity = (int) fCap.get(map);
            out.add("CAP=" + capacity);


            Field fTable = map.getClass().getDeclaredField("table");
            Field fDef  = map.getClass().getDeclaredField("DEFUNCT");
            fTable.setAccessible(true);
            fDef.setAccessible(true);

            Object table = fTable.get(map); // MapEntry[] (null/DEFUNCT/MapEntry)
            Object DEFUNCT = fDef.get(map);

            int len = Array.getLength(table);
            for (int i = 0; i < len; i++) {
                Object slot = Array.get(table, i);
                if (slot == null) {
                    out.add(i + "|EMPTY");
                } else if (slot == DEFUNCT) {
                    out.add(i + "|DEFUNCT");
                } else {
                    // slot имеет методы getKey()/getValue()
                    Method mGetKey = slot.getClass().getMethod("getKey");
                    Method mGetVal = slot.getClass().getMethod("getValue");
                    Object k = mGetKey.invoke(slot);
                    Object v = mGetVal.invoke(slot);
                    out.add(i + "|" + k + "=" + v);
                }
            }
            return out;
        } catch (Exception ex) {

            out.clear();
            out.add("CAP=?");
            out.add("TABLE=UNAVAILABLE");
            return out;
        }
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
