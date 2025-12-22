package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.HeapPriorityQueue;
import com.app.nonlinear.interfaces.Entry;

public class TestHeapPriorityQueue {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : "(" + e.getKey() + ", " + e.getValue() + ")";
    }

    public static void test() {
        System.out.println("Creating heap-based priority queue with ArrayList...");
        HeapPriorityQueue<Integer, String> pq = new HeapPriorityQueue<>();
        pq.insert(4, "A");
        pq.insert(2, "B");
        pq.insert(5, "C");
        pq.insert(1, "D");

        System.out.println("Minimum: " + fmt(pq.min()));                 // Expected: (1, D)
        System.out.println("Removing minimum: " + fmt(pq.removeMin()));   // Removes (1, D)
        System.out.println("Minimum now: " + fmt(pq.min()));              // Expected: (2, B)
        System.out.println("Size: " + pq.size());                         // Expected: 3

        while (!pq.isEmpty()) {
            System.out.println("Removing: " + fmt(pq.removeMin()));
        }
    }
}
