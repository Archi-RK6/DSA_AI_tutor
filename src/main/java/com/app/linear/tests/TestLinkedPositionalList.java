package com.app.linear.tests;

import java.util.Iterator;

import com.app.linear.implementations.LinkedPositionalList;
import com.app.linear.interfaces.Position;

public class TestLinkedPositionalList {

    public static void test() {
        LinkedPositionalList<String> plist = new LinkedPositionalList<>();

        Position<String> p1 = plist.addFirst("Alpha");
        Position<String> p2 = plist.addAfter(p1, "Beta");
        Position<String> p3 = plist.addLast("Gamma");
        Position<String> p4 = plist.addBefore(p2, "Delta");
        Position<String> p5 = plist.addAfter(p3, "Epsilon");

        System.out.println("Initial state:");
        Position<String> p = plist.first();
        while (p != null) {
            System.out.printf("Position ID: %-10d | Element: %s%n",
                    System.identityHashCode(p), p.getElement());
            p = plist.after(p);
        }

        System.out.println("\nList size: " + plist.size());

        System.out.println("\nNavigating around 'Gamma':");
        System.out.println("Before Gamma: " + plist.before(p3));
        System.out.println("After  Gamma: " + plist.after(p3));

        System.out.println("\nReplacing element at p2 (Beta) with 'Omega'");
        String old = plist.set(p2, "Omega");
        System.out.println("Old value was: " + old);

        System.out.println("\nRemoving element at p1 (Alpha)");
        String removed = plist.remove(p1);
        System.out.println("Removed value: " + removed);

        System.out.println("\nInserting 'Zeta' before Epsilon and 'Theta' after Delta");
        plist.addBefore(p5, "Zeta");
        plist.addAfter(p4, "Theta");

        System.out.println("\nFinal list state:");
        p = plist.first();
        while (p != null) {
            System.out.printf("Position ID: %-10d | Element: %s%n",
                    System.identityHashCode(p), p.getElement());
            p = plist.after(p);
        }

        System.out.println("\nIs list empty? " + plist.isEmpty());
        System.out.println("Final list size: " + plist.size());

        System.out.println("\nstr " + plist.toString());

        p1 = plist.addLast("A");
        p2 = plist.addLast("B");
        p3 = plist.addLast("C");

        System.out.println("\nElement iteration:");
        for (String e : plist)
            System.out.println(e);

        System.out.println("\nRemoving elements during position iteration:");
        Iterator<Position<String>> it = plist.positions().iterator();
        while (it.hasNext()) {
            Position<String> pos = it.next();
            System.out.println("Removing: " + pos.getElement());
            it.remove();
        }

        System.out.println("\nAfter removal, list is:");
        for (String e : plist)
            System.out.println(e);

        LinkedPositionalList<Integer> plist2 = new LinkedPositionalList<>();
        plist2.addLast(26);
        plist2.addLast(19);
        plist2.addLast(16);
        plist2.addLast(1);
        plist2.addLast(12);

        System.out.println("\nLinked Positional List Before Sorting (integers):");
        for (Integer e : plist2)
            System.out.println(e);

        LinkedPositionalList.insertionSort(plist2);

        System.out.println("\nLinked Positional List After Sorting (integers):");
        for (Integer e : plist2)
            System.out.println(e);
    }
}
