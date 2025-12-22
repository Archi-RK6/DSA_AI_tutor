package com.app.linear.tests;

import com.app.linear.implementations.SinglyLinkedList;

import java.util.Iterator;

public class TestSinglyLinkedList {

    public static void test() {
        SinglyLinkedList<Integer> lst = new SinglyLinkedList<>();

        // Adds 10 at the end: list becomes [10]
        lst.addLast(10);

        // Adds 5 at the front: list becomes [5 -> 10]
        lst.addFirst(5);

        // Adds 20 at the end: list becomes [5 -> 10 -> 20]
        lst.addLast(20);

        // [5 -> 10 -> 20]
        System.out.println(lst);

        // Returns the first element (not removed): 5
        System.out.println(lst.first());

        // Returns the last element (not removed): 20
        System.out.println(lst.last());

        // Returns the number of elements: 3
        System.out.println(lst.size());

        // Same as above via "len" concept (size in Java): 3
        System.out.println(lst.size());

        // Checks if the list is empty: false
        System.out.println(lst.isEmpty());

        // Boolean context equivalent: true if non-empty
        System.out.println(!lst.isEmpty());

        // Removes 5 from front; prints 5
        Integer removed = lst.removeFirst();
        System.out.println(removed);

        // [10 -> 20]
        System.out.println(lst);

        // Test equality
        SinglyLinkedList<Integer> lst2 = new SinglyLinkedList<>();
        lst2.addFirst(20);
        lst2.addFirst(10);

        // True  (same content and order)
        System.out.println(lst.equals(lst2));

        lst2.addLast(30);

        // False (lst2 has extra element)
        System.out.println(lst.equals(lst2));

        // Default iterator (lazy)
        System.out.println("\nSingly Linked List default iterator");
        for (Integer item : lst2) {
            System.out.println(item);
        }

        // Snapshot iterator
        System.out.println("\nSingly Linked List snapshot iterator");
        Iterator<Integer> snap = lst2.snapshotIterator();
        while (snap.hasNext()) {
            System.out.println(snap.next());
        }
    }
}

