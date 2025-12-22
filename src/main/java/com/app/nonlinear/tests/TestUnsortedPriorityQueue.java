package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.UnsortedPriorityQueue;
import com.app.nonlinear.interfaces.Entry;

public class TestUnsortedPriorityQueue {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : "(" + e.getKey() + ", " + e.getValue() + ")";
    }

    public static void test() {
        System.out.println("=== Testing UnsortedPriorityQueue ===");
        UnsortedPriorityQueue<Integer, String> pq = new UnsortedPriorityQueue<>();

        System.out.println("Is empty? " + pq.isEmpty());
        System.out.println("Size: " + pq.size());
        System.out.println("Min (should be null): " + fmt(pq.min()));
        System.out.println("Remove min (should be null): " + fmt(pq.removeMin()));

        System.out.println("\nInserting entries...");
        System.out.println("Insert (4, 'apple')");
        pq.insert(4, "apple");
        System.out.println("Insert (1, 'banana')");
        pq.insert(1, "banana");
        System.out.println("Insert (3, 'cherry')");
        pq.insert(3, "cherry");
        System.out.println("Insert (2, 'date')");
        pq.insert(2, "date");

        System.out.println("\nIs empty? " + pq.isEmpty());
        System.out.println("Size: " + pq.size());

        System.out.println("\nCurrent min entry (should be (1, 'banana')): " + fmt(pq.min()));

        System.out.println("\nRemoving min...");
        Entry<Integer, String> removed = pq.removeMin();
        System.out.println("Removed entry: " + fmt(removed));

        System.out.println("Size after removal: " + pq.size());
        System.out.println("New min entry (should be (2, 'date')): " + fmt(pq.min()));

        System.out.println("\nRemoving all remaining entries:");
        while (!pq.isEmpty()) {
            Entry<Integer, String> entry = pq.removeMin();
            System.out.println("Removed: " + fmt(entry));
        }

        System.out.println("\nFinal size: " + pq.size());
        System.out.println("Is empty? " + pq.isEmpty());
        System.out.println("Test complete.");
    }
}

