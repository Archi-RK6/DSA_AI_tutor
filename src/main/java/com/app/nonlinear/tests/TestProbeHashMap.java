package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.ProbeHashMap;
import com.app.nonlinear.interfaces.AbstractHashMap;
import com.app.nonlinear.interfaces.AbstractMap;
import com.app.nonlinear.interfaces.Entry;

import java.lang.reflect.Field;

public class TestProbeHashMap {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : e.getKey() + ": " + e.getValue();
    }

    public static void test() {
        System.out.println("Creating ProbeHashMap...");
        ProbeHashMap<String, Integer> pmap = new ProbeHashMap<>(7); // small capacity to trigger probing

        System.out.println("\nInserting key-value pairs...");
        pmap.put("apple", 1);
        pmap.put("banana", 2);
        pmap.put("orange", 3);
        pmap.put("lemon", 4);
        pmap.put("grape", 5);

        System.out.println("\nCurrent entries (after insertions):");
        for (Entry<String, Integer> entry : pmap.entrySet()) {
            System.out.println(fmt(entry));
        }

        System.out.println("\nAccessing specific keys:");
        System.out.println("apple: " + pmap.get("apple"));
        System.out.println("grape: " + pmap.get("grape"));
        System.out.println("melon: " + pmap.get("melon"));

        System.out.println("\nRemoving 'banana' and 'lemon'...");
        pmap.remove("banana");
        pmap.remove("lemon");

        System.out.println("\nEntries after removals:");
        for (Entry<String, Integer> entry : pmap.entrySet()) {
            System.out.println(fmt(entry));
        }

        System.out.println("\n--- Internal Table (probing layout) ---");
        printInternalTable(pmap);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void printInternalTable(ProbeHashMap<K, V> pmap) {
        try {
            Field capF = AbstractHashMap.class.getDeclaredField("capacity");
            capF.setAccessible(true);
            int capacity = (int) capF.get(pmap);

            Field tableF = ProbeHashMap.class.getDeclaredField("table");
            tableF.setAccessible(true);
            AbstractMap.MapEntry<K, V>[] table = (AbstractMap.MapEntry<K, V>[]) tableF.get(pmap);

            Field defunctF = ProbeHashMap.class.getDeclaredField("DEFUNCT");
            defunctF.setAccessible(true);
            Object DEFUNCT = defunctF.get(pmap);

            for (int i = 0; i < capacity; i++) {
                AbstractMap.MapEntry<K, V> entry = table[i];
                if (entry == null) {
                    System.out.println("Slot " + i + ": Empty");
                } else if (entry == DEFUNCT) {
                    System.out.println("Slot " + i + ": DEFUNCT");
                } else {
                    System.out.println("Slot " + i + ": (" + entry.getKey() + ": " + entry.getValue() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to reflect internal table: " + e.getMessage());
        }
    }
}

