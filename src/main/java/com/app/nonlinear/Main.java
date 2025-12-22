package com.app.nonlinear;

import com.app.nonlinear.tests.*;

public class Main {

    private static void printSection(String title, int barLength) {
        StringBuilder bar = new StringBuilder();
        bar.append("#".repeat(Math.max(0, barLength)));
        System.out.println();
        System.out.println(bar + " " + title.toUpperCase() + " " + bar);
        System.out.println();
    }

    public static void main(String[] args) {
        printSection("Linked Binary Tree Test", 60);
        TestLinkedBinaryTree.test();

        printSection("Unsorted Priority Queue Test", 40);
        TestUnsortedPriorityQueue.test();

        printSection("Sorted Priority Queue Test", 40);
        nonlinear.tests.TestSortedPriorityQueue.test();

        printSection("Heap Priority Queue Test", 60);
        TestHeapPriorityQueue.test();

        printSection("Chain Hash Map Test", 60);
        TestChainHashMap.test();

        printSection("Probe Hash Map Test", 60);
        TestProbeHashMap.test();

        printSection("Sorted Table Map Test", 60);
        TestSortedTableMap.test();

        printSection("Heap Adaptable Priority Queue Test", 60);
        TestHeapAdaptablePriorityQueue.test();

        printSection("Splay Tree Map Test", 60);
        TestSplayTreeMap.test();

        printSection("RB-Tree Map Test", 60);
        TestSplayTreeMap.test();

        printSection("AVL Tree Map Test", 60);
        nonlinear.tests.TestAVLTreeMap.test();

        printSection("Adjacency Map Graph Test", 60);
        TestAdjacencyMapGraph.test();
    }
}
