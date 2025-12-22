package com.app.linear.tests;

import java.util.Iterator;
import com.app.linear.implementations.LinkedStack;

public class TestLinkedStack {

    public static void test() {
        LinkedStack<Integer> stack = new LinkedStack<>();

        System.out.println("Is stack empty? " + stack.isEmpty());   // True
        System.out.println("Size of stack: " + stack.size());       // 0

        stack.push(10);
        stack.push(20);
        stack.push(30);
        System.out.println("After push operations (top is 30):");
        System.out.println("Top element: " + stack.top());          // 30
        System.out.println("Size of stack: " + stack.size());       // 3
        System.out.println("Is stack empty? " + stack.isEmpty());   // False

        Integer popped = stack.pop();
        System.out.println("Popped element: " + popped);            // 30
        System.out.println("Top element after pop: " + stack.top()); // 20
        System.out.println("Size after pop: " + stack.size());      // 2

        stack.pop();
        stack.pop();
        System.out.println("After popping all elements:");
        System.out.println("Is stack empty? " + stack.isEmpty());   // True
        System.out.println("Size of stack: " + stack.size());       // 0

        System.out.println("Top of empty stack: " + stack.top());   // null
        System.out.println("Pop from empty stack: " + stack.pop()); // null

        stack.push("A".hashCode()); // keeping Integer type; just a demo
        stack.push("B".hashCode());
        stack.push("C".hashCode());

        System.out.println("\nLazy iteration");
        for (Integer item : stack) {
            System.out.println(item);
        }
        System.out.println("Size after Lazy iterator: " + stack.size());

        System.out.println("\nSnapshot iteration");
        Iterator<Integer> snap = stack.snapshotIterator();
        while (snap.hasNext()) {
            System.out.println(snap.next());
        }
        System.out.println("Size after Snapshot iterator: " + stack.size());
    }
}

