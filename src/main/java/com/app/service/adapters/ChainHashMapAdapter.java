package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.ChainHashMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.service.DataStructureAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ChainHashMapAdapter implements DataStructureAdapter {

    private final ChainHashMap<String, String> map = new ChainHashMap<>();

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
            default -> throw new IllegalArgumentException("Unsupported operation for CHAIN_HASH_MAP: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        try {
            // capacity из AbstractHashMap
            Field fCap = map.getClass().getSuperclass().getDeclaredField("capacity");
            fCap.setAccessible(true);
            int capacity = (int) fCap.get(map);
            out.add("CAP=" + capacity);

            // table из ChainHashMap
            Field fTable = map.getClass().getDeclaredField("table");
            fTable.setAccessible(true);
            Object table = fTable.get(map); // UnsortedTableMap[] (может быть null в ячейках)
            int len = Array.getLength(table);

            for (int i = 0; i < len; i++) {
                Object bucket = Array.get(table, i);
                if (bucket == null) {
                    out.add(i + "|");
                    continue;
                }
                // bucket.entrySet() -> Iterable<Entry<K,V>>
                Method mEntrySet = bucket.getClass().getMethod("entrySet");
                @SuppressWarnings("unchecked")
                Iterable<?> entries = (Iterable<?>) mEntrySet.invoke(bucket);

                List<String> kvs = new ArrayList<>();
                for (Object e : entries) {
                    @SuppressWarnings("unchecked")
                    Entry<String,String> en = (Entry<String,String>) e;
                    kvs.add(en.getKey() + "=" + en.getValue());
                }
                out.add(i + "|" + String.join(";", kvs));
            }
            return out;
        } catch (Exception ex) {
            // Fallback: плоский список
            out.clear();
            List<String> flat = new ArrayList<>();
            for (Entry<String,String> e : map.entrySet()) {
                flat.add(e.getKey() + "=" + e.getValue());
            }
            flat.sort(String::compareTo);
            return flat;
        }
    }

    // ---- helpers ----
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
