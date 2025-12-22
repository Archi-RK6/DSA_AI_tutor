package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.ChainHashMap;
import com.app.nonlinear.implementations.UnsortedTableMap;
import com.app.nonlinear.interfaces.AbstractHashMap;
import com.app.nonlinear.interfaces.Entry;

import java.lang.reflect.Field;

public class TestChainHashMap {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : e.getKey() + ": " + e.getValue();
    }

    public static void test() {
        System.out.println("Creating ChainHashMap...");
        ChainHashMap<String, Integer> cmap = new ChainHashMap<>(13);

        System.out.println("\nInserting key-value pairs...");
        cmap.put("apple", 1);
        cmap.put("banana", 2);
        cmap.put("orange", 3);
        cmap.put("lemon", 4);
        cmap.put("grape", 5);
        cmap.put("kiwi", 6);
        cmap.put("mango", 7);
        cmap.put("pear", 8);
        cmap.put("plum", 9);
        cmap.put("fig", 10);
        cmap.put("date", 11);

        System.out.println("\nCurrent entries (after insertions):");
        for (Entry<String, Integer> entry : cmap.entrySet()) {
            System.out.println(fmt(entry));
        }

        System.out.println("\nAccessing specific keys:");
        System.out.println("apple: " + cmap.get("apple"));
        System.out.println("grape: " + cmap.get("grape"));
        System.out.println("melon: " + cmap.get("melon"));

        System.out.println("\nRemoving 'banana' and 'lemon'...");
        cmap.remove("banana");
        cmap.remove("lemon");

        System.out.println("\nEntries after removals:");
        for (Entry<String, Integer> entry : cmap.entrySet()) {
            System.out.println(fmt(entry));
        }

        System.out.println("\n--- Buckets and their chains (separate chaining) ---");
        printBuckets(cmap);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void printBuckets(ChainHashMap<K, V> cmap) {
        try {
            Field capF = AbstractHashMap.class.getDeclaredField("capacity");
            capF.setAccessible(true);
            int capacity = (int) capF.get(cmap);

            Field tableF = ChainHashMap.class.getDeclaredField("table");
            tableF.setAccessible(true);
            UnsortedTableMap<K, V>[] table = (UnsortedTableMap<K, V>[]) tableF.get(cmap);

            for (int i = 0; i < capacity; i++) {
                UnsortedTableMap<K, V> bucket = table[i];
                if (bucket == null) {
                    System.out.println("Bucket " + i + ": Empty");
                } else {
                    System.out.print("Bucket " + i + ": ");
                    for (Entry<K, V> e : bucket.entrySet()) {
                        System.out.print("(" + e.getKey() + ": " + e.getValue() + ") -> ");
                    }
                    System.out.println("None");
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to reflect buckets: " + e.getMessage());
        }
    }
}

