package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.HeapAdaptablePriorityQueue;
import com.app.nonlinear.interfaces.Entry;

public class TestHeapAdaptablePriorityQueue {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : "(" + e.getKey() + ", " + e.getValue() + ")";
    }

    public static void test() {
        System.out.println("=== Testing HeapAdaptablePriorityQueue ===");
        HeapAdaptablePriorityQueue<Integer, String> pq = new HeapAdaptablePriorityQueue<>();

        System.out.println("Inserting (4,'A'), (2,'B'), (5,'C'), (1,'D') ...");
        Entry<Integer, String> e4 = pq.insert(4, "A");
        Entry<Integer, String> e2 = pq.insert(2, "B");
        Entry<Integer, String> e5 = pq.insert(5, "C");
        Entry<Integer, String> e1 = pq.insert(1, "D");

        System.out.println("Min: " + fmt(pq.min()));                 // (1,'D')
        System.out.println("RemoveMin: " + fmt(pq.removeMin()));      // removes (1,'D')
        System.out.println("Min now: " + fmt(pq.min()));              // (2,'B')
        System.out.println("Size: " + pq.size());                     // 3

        System.out.println("\nReplaceValue on key=2 to 'B*' and ReplaceKey on (5,'C') -> key=0");
        pq.replaceValue(e2, "B*");
        pq.replaceKey(e5, 0); // should bubble up to min

        System.out.println("Min after updates: " + fmt(pq.min()));    // (0,'C')
        System.out.println("\nRemoving arbitrary entry (4,'A') using remove(entry) ...");
        pq.remove(e4);

        System.out.println("Draining queue:");
        while (!pq.isEmpty()) {
            System.out.println("  " + fmt(pq.removeMin()));
        }
        System.out.println("=== End ===");
    }
}
