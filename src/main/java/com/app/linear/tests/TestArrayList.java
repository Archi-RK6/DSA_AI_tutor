// file: src/test/java/linear/tests/TestArrayList.java
package com.app.linear.tests;

import java.util.Iterator;
import com.app.linear.implementations.ArrayList;

public class TestArrayList {

    public static void test() {
        ArrayList<Integer> arr = new ArrayList<>();

        arr.add(0, 10);
        arr.add(1, 20);
        arr.add(2, 30);
        System.out.println("Initial list: " + arr);

        System.out.println("Element at index 1: " + arr.get(1));
        arr.set(1, 99);
        System.out.println("After set at index 1: " + arr);

        Integer removed = arr.remove(0);
        System.out.println("Removed element: " + removed);
        System.out.println("After removal: " + arr);

        System.out.println("Size of list: " + arr.size());
        System.out.println("Is list empty? " + arr.isEmpty());
        System.out.println("Boolean value: " + !arr.isEmpty());

        ArrayList<Integer> arr2 = new ArrayList<>();
        arr2.add(0, 99);
        arr2.add(1, 30);
        System.out.println("arr == arr2: " + arr.equals(arr2));

        System.out.println("\nLazy iteration:");
        for (Integer x : arr2) {
            System.out.println(x);
        }

        System.out.println("\nSnapshot iteration:");
        Iterator<Integer> snap = arr2.snapshotIterator();
        while (snap.hasNext()) {
            System.out.println(snap.next());
        }
    }
}
