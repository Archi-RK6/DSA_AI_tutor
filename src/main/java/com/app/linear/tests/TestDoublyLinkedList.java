package com.app.linear.tests;

import com.app.linear.implementations.DoublyLinkedList;
import java.util.Iterator;

public class TestDoublyLinkedList {

    public static void test() {
        DoublyLinkedList<Integer> dll = new DoublyLinkedList<>();

        dll.addFirst(10);
        dll.addFirst(20);
        dll.addFirst(30);
        System.out.println("After add_first operations: " + dll); // [30 <-> 20 <-> 10]

        dll.addLast(40);
        dll.addLast(50);
        System.out.println("After add_last operations: " + dll);  // [30 <-> 20 <-> 10 <-> 40 <-> 50]

        System.out.println("Size of list: " + dll.size());         // 5
        System.out.println("Is list empty? " + dll.isEmpty());     // false

        System.out.println("First element: " + dll.first());       // 30
        System.out.println("Last element: " + dll.last());         // 50

        Integer removedFirst = dll.removeFirst();
        System.out.println("Removed first element: " + removedFirst);
        System.out.println("After remove_first: " + dll);          // [20 <-> 10 <-> 40 <-> 50]

        Integer removedLast = dll.removeLast();
        System.out.println("Removed last element: " + removedLast);
        System.out.println("After remove_last: " + dll);           // [20 <-> 10 <-> 40]

        System.out.println("Length using size(): " + dll.size());  // 3
        System.out.println("Boolean value of dll: " + !dll.isEmpty());

        DoublyLinkedList<Integer> dll2 = new DoublyLinkedList<>();
        dll2.addFirst(40);
        dll2.addFirst(10);
        dll2.addFirst(20);
        System.out.println("dll == dll2: " + dll.equals(dll2));    // true

        System.out.println("\nDoubly Linked List default iterator");
        for (Integer item : dll2) {
            System.out.println(item);
        }

        System.out.println("\nDoubly Linked List snapshot iterator");
        Iterator<Integer> snap = dll2.snapshotIterator();
        while (snap.hasNext()) {
            System.out.println(snap.next());
        }
    }
}
