package com.app.linear.tests;

import com.app.linear.implementations.CircularlyLinkedList;

public class TestCircularlyLinkedList {
    public static void test() {
        CircularlyLinkedList<Integer> cll = new CircularlyLinkedList<>();

        System.out.println("Is list empty? " + cll.isEmpty());  // True
        System.out.println("Size of list: " + cll.size());      // 0
        System.out.println("Initial list: " + cll);

        cll.addFirst(10);
        cll.addFirst(20);
        cll.addFirst(30);
        System.out.println("After add_first operations: " + cll); // [30 -> 20 -> 10]

        cll.addLast(40);
        cll.addLast(50);
        System.out.println("After add_last operations: " + cll);  // [30 -> 20 -> 10 -> 40 -> 50]

        System.out.println("Size of list: " + cll.size());        // 5
        System.out.println("Is list empty? " + cll.isEmpty());     // False
        System.out.println("First element: " + cll.first());       // 30
        System.out.println("Last element: " + cll.last());         // 50

        cll.rotate();
        System.out.println("After one rotation: " + cll);          // [20 -> 10 -> 40 -> 50 -> 30]
        cll.rotate();
        System.out.println("After second rotation: " + cll);       // [10 -> 40 -> 50 -> 30 -> 20]

        Integer removed = cll.removeFirst();
        System.out.println("Removed first element: " + removed);
        System.out.println("After remove_first: " + cll);          // [40 -> 50 -> 30 -> 20]

        System.out.println("Length using size(): " + cll.size());
        System.out.println("Boolean value of cll: " + !cll.isEmpty());
        System.out.println("Detailed representation: " + cll);

        CircularlyLinkedList<Integer> cll2 = new CircularlyLinkedList<>();
        for (int val : new int[]{40, 50, 30, 20})
            cll2.addLast(val);
        System.out.println("cll == cll2: " + cll.equals(cll2));    // true
    }
}
