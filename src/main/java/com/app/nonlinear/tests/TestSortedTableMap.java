package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.SortedTableMap;
import com.app.nonlinear.interfaces.Entry;

public class TestSortedTableMap {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : e.getKey() + " " + e.getValue();
    }

    public static void test() {
        System.out.println("Creating an empty SortedTableMap...");
        SortedTableMap<String, Integer> map = new SortedTableMap<>();

        System.out.println("\nAdding key-value pairs...");
        map.put("banana", 3);
        map.put("apple", 5);
        map.put("cherry", 7);
        map.put("date", 2);
        map.put("fig", 9);

        System.out.println("\nSize of map: " + map.size());
        System.out.println("Is map empty?: " + map.isEmpty());

        System.out.println("\nGetting values by key...");
        System.out.println("Value for 'banana': " + map.get("banana"));
        System.out.println("Value for 'apple': " + map.get("apple"));
        System.out.println("Value for 'grape' (not in map): " + map.get("grape"));

        System.out.println("\nUpdating a value...");
        System.out.println("Previous value for 'banana': " + map.put("banana", 6));
        System.out.println("New value for 'banana': " + map.get("banana"));

        System.out.println("\nRemoving a key...");
        System.out.println("Removed value for 'date': " + map.remove("date"));
        System.out.println("Value for 'date' after removal: " + map.get("date"));
        System.out.println("Size after removal: " + map.size());

        System.out.println("\nFirst entry: " + fmt(map.firstEntry()));
        System.out.println("Last entry: " + fmt(map.lastEntry()));

        System.out.print("\nCeiling entry of 'blueberry': ");
        Entry<String, Integer> e = map.ceilingEntry("blueberry");
        System.out.println(fmt(e));

        System.out.print("Floor entry of 'blueberry': ");
        e = map.floorEntry("blueberry");
        System.out.println(fmt(e));

        System.out.print("Lower entry of 'cherry': ");
        e = map.lowerEntry("cherry");
        System.out.println(fmt(e));

        System.out.print("Higher entry of 'cherry': ");
        e = map.higherEntry("cherry");
        System.out.println(fmt(e));

        System.out.println("\nIterating over all entries:");
        for (Entry<String, Integer> en : map.entrySet()) {
            System.out.println(en.getKey() + " -> " + en.getValue());
        }

        System.out.println("\nSubmap from 'banana' to 'fig':");
        for (Entry<String, Integer> en : map.subMap("banana", "fig")) {
            System.out.println(en.getKey() + " -> " + en.getValue());
        }
    }
}

