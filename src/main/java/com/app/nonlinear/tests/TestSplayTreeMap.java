package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.SplayTreeMap;
import com.app.nonlinear.interfaces.Entry;

public class TestSplayTreeMap {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : e.getKey() + " -> " + e.getValue();
    }

    public static void test() {
        System.out.println("=== Testing SplayTreeMap ===");
        SplayTreeMap<String, Integer> map = new SplayTreeMap<>();

        System.out.println("\nInserting entries...");
        map.put("M", 13);
        map.put("F", 6);
        map.put("T", 20);
        map.put("C", 3);
        map.put("J", 10);
        map.put("P", 17);
        map.put("W", 25);

        System.out.println("\nGetting entries...");
        System.out.println("M: " + map.get("M"));
        System.out.println("F: " + map.get("F"));
        System.out.println("X (non-existent): " + map.get("X"));

        System.out.println("\nUpdating entry for key 'F'...");
        map.put("F", 60);
        System.out.println("F (updated): " + map.get("F"));

        System.out.println("\nAll entries (in order):");
        for (Entry<String, Integer> e : map.entrySet()) {
            System.out.println(fmt(e));
        }

        System.out.println("\nFloor entry for 'G':");
        Entry<String, Integer> floor = map.floorEntry("G");
        System.out.println(fmt(floor));

        System.out.println("\nLower entry for 'J':");
        Entry<String, Integer> lower = map.lowerEntry("J");
        System.out.println(fmt(lower));

        System.out.println("\nLast entry:");
        Entry<String, Integer> last = map.lastEntry();
        System.out.println(fmt(last));

        System.out.println("\nSubmap from 'D' to 'T':");
        for (Entry<String, Integer> e : map.subMap("D", "T")) {
            System.out.println(fmt(e));
        }

        System.out.println("\nRemoving 'P' and 'C'...");
        map.remove("P");
        map.remove("C");

        System.out.println("\nEntries after removals:");
        for (Entry<String, Integer> e : map.entrySet()) {
            System.out.println(fmt(e));
        }
        System.out.println("=== End ===");
    }
}

