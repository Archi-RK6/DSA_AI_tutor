package com.app.linear.tests;

import java.util.Iterator;
import com.app.linear.implementations.ArrayQueue;

public class TestArrayQueue {

    public static void test() {
        ArrayQueue<Integer> aq = new ArrayQueue<>(16);  // small capacity for demo

        aq.enqueue(5);
        aq.enqueue(15);
        aq.enqueue(1);
        aq.enqueue(0);
        aq.enqueue(52);
        aq.enqueue(-9);

        System.out.println("Initial Queue (via iterator):");
        for (Integer x : aq) {
            System.out.print(x + " ");
        }
        System.out.println();

        System.out.println("First element: " + aq.first());  // 5
        System.out.println("Size: " + aq.size());            // 6
        System.out.println("Is empty? " + aq.isEmpty());     // false

        System.out.println("Dequeue operations:");
        System.out.println(aq.dequeue()); // 5
        System.out.println(aq.dequeue()); // 15

        System.out.println("Queue after 2 dequeues:");
        for (Integer x : aq) {
            System.out.print(x + " ");   // 1 0 52 -9
        }
        System.out.println();

        aq.enqueue(99);
        aq.enqueue(100);

        System.out.println("Queue after enqueuing 99 and 100:");
        for (Integer x : aq) {
            System.out.print(x + " ");   // 1 0 52 -9 99 100
        }
        System.out.println();

        System.out.println("Final size: " + aq.size());      // 6

        System.out.println("\nSnapshot iteration:");
        Iterator<Integer> snap = aq.snapshotIterator();
        while (snap.hasNext()) {
            System.out.print(snap.next() + " ");
        }
        System.out.println();
    }
}

