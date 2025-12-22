package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.LinkedBinaryTree;
import com.app.nonlinear.interfaces.Position;

public class TestLinkedBinaryTree {

    private static <E> void printTree(LinkedBinaryTree<E> tree, Position<E> node, int level, String label) {
        if (node != null) {
            Position<E> r = tree.right(node);
            if (r != null) printTree(tree, r, level + 1, "R");
            System.out.println("    ".repeat(level) + label + ": " + node.getElement());
            Position<E> l = tree.left(node);
            if (l != null) printTree(tree, l, level + 1, "L");
        }
    }

    public static void test() {
        System.out.println("Creating tree...");
        LinkedBinaryTree<String> tree1 = new LinkedBinaryTree<>();

        System.out.println("Adding root...");
        Position<String> root1 = tree1.addRoot("A");
        System.out.println("Root: " + root1.getElement());

        System.out.println("Adding left and right children...");
        Position<String> left1 = tree1.addLeft(root1, "B");
        Position<String> right1 = tree1.addRight(root1, "C");
        System.out.println("Left child of root: " + tree1.left(root1).getElement());
        System.out.println("Right child of root: " + tree1.right(root1).getElement());
        System.out.println("Tree size: " + tree1.size());

        System.out.println("Replacing root's element with 'Z'...");
        String old1 = tree1.set(root1, "Z");
        System.out.println("Old value: " + old1 + " | New root value: " + root1.getElement());

        System.out.println("Attaching two trees as subtrees of a new node...");
        LinkedBinaryTree<String> t1 = new LinkedBinaryTree<>();
        LinkedBinaryTree<String> t2 = new LinkedBinaryTree<>();
        t1.addRoot("L-subtree");
        t2.addRoot("R-subtree");
        Position<String> newLeaf = tree1.addLeft(left1, "D");
        tree1.attach(newLeaf, t1, t2);
        System.out.println("Left of newLeaf: " + tree1.left(newLeaf).getElement());
        System.out.println("Right of newLeaf: " + tree1.right(newLeaf).getElement());

        System.out.println("Removing a leaf node...");
        String removedValue1 = tree1.remove(right1);
        System.out.println("Removed value: " + removedValue1);
        System.out.println("Tree size after removal: " + tree1.size());

        System.out.println("Removing a node with one child...");
        Position<String> nodeWithOneChild = tree1.left(root1);
        String removedValue2 = tree1.remove(nodeWithOneChild);
        System.out.println("Removed value: " + removedValue2);
        System.out.println("Left of root after removal: " + (tree1.left(root1) != null ? tree1.left(root1).getElement() : "null"));

        LinkedBinaryTree<Integer> tree = new LinkedBinaryTree<>();
        Position<Integer> root = tree.addRoot(10);
        Position<Integer> n1 = tree.addLeft(root, 5);
        Position<Integer> n2 = tree.addRight(root, 15);
        Position<Integer> n3 = tree.addLeft(n1, 3);
        Position<Integer> n4 = tree.addRight(n1, 7);
        Position<Integer> n5 = tree.addLeft(n2, 12);
        Position<Integer> n6 = tree.addRight(n2, 18);
        Position<Integer> n7 = tree.addRight(n4, 8);

        System.out.println("\nBinary Tree Structure:");
        printTree(tree, tree.root(), 0, "Root");

        System.out.println("\nDefault traversal for AbstractBinaryTree (Inorder):");
        for (Position<Integer> pos : tree.positions()) {
            System.out.print(pos.getElement() + " ");
        }
        System.out.println();

        System.out.println("\nPostorder traversal:");
        for (Position<Integer> pos : tree.postorder()) {
            System.out.print(pos.getElement() + " ");
        }
        System.out.println();

        System.out.println("\nBreadth-first traversal:");
        for (Position<Integer> pos : tree.breadthfirst()) {
            System.out.print(pos.getElement() + " ");
        }
        System.out.println();

        System.out.println("\nElement iterator (with default traversal):");
        for (Integer elem : tree) {
            System.out.print(elem + " ");
        }
        System.out.println();
    }
}

