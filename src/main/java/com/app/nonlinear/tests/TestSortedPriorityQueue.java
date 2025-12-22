package nonlinear.tests;

import com.app.nonlinear.implementations.SortedPriorityQueue;
import com.app.nonlinear.interfaces.Entry;

public class TestSortedPriorityQueue {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : "(" + e.getKey() + ", " + e.getValue() + ")";
    }

    public static void test() {
        System.out.println("=== Testing SortedPriorityQueue ===");
        SortedPriorityQueue<Integer, String> pq = new SortedPriorityQueue<>();

        System.out.println("Is empty? " + pq.isEmpty());
        System.out.println("Size: " + pq.size());
        System.out.println("Min (should be null): " + fmt(pq.min()));
        System.out.println("Remove min (should be null): " + fmt(pq.removeMin()));

        System.out.println("\nInserting entries...");
        pq.insert(5, "apple");
        System.out.println("Inserted (5, 'apple')");
        pq.insert(2, "banana");
        System.out.println("Inserted (2, 'banana')");
        pq.insert(7, "cherry");
        System.out.println("Inserted (7, 'cherry')");
        pq.insert(1, "date");
        System.out.println("Inserted (1, 'date')");

        System.out.println("\nIs empty? " + pq.isEmpty());
        System.out.println("Size: " + pq.size());

        System.out.println("\nCurrent min entry (should be (1, 'date')): " + fmt(pq.min()));

        System.out.println("\nRemoving min...");
        Entry<Integer, String> removed = pq.removeMin();
        System.out.println("Removed entry: " + fmt(removed));

        System.out.println("Size after removal: " + pq.size());
        System.out.println("New min entry (should be (2, 'banana')): " + fmt(pq.min()));

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

