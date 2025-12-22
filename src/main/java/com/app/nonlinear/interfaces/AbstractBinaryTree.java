package com.app.nonlinear.interfaces;

import com.app.nonlinear.iterators.AbstractTreeIterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An abstract base class providing some functionality of the BinaryTree interface.
 */
public abstract class AbstractBinaryTree<E> extends AbstractTree<E> implements BinaryTree<E> {

    /** Returns the Position of p's sibling (or null if no sibling exists). */
    public Position<E> sibling(Position<E> p) {
        Position<E> parent = parent(p);
        if (parent == null) return null; // p must be the root
        if (p == left(parent)) // p is a left child
            return right(parent); // (right child might be null)
        else // p is a right child
            return left(parent); // (left child might be null)
    }

    /** Returns the number of children of Position p. */
    public int numChildren(Position<E> p) {
        int count = 0;
        if (left(p) != null)
            count++;
        if (right(p) != null)
            count++;
        return count;
    }

    /** Returns an iterable collection of the Positions representing p's children. */
    public Iterable<Position<E>> children(Position<E> p) {
        List<Position<E>> snapshot = new ArrayList<>(2); // max capacity of 2
        if (left(p) != null)
            snapshot.add(left(p));
        if (right(p) != null)
            snapshot.add(right(p));
        return snapshot;
    }

    public Iterator<E> iterator() {
        return new AbstractTreeIterators.ElementIterator<>(positions());
    }

    // ---------- INORDER (binary-tree specific) ----------
    /** Adds positions of the subtree rooted at p to the given snapshot (inorder). */
    private void inorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        if (left(p) != null) inorderSubtree(left(p), snapshot);
        snapshot.add(p);
        if (right(p) != null) inorderSubtree(right(p), snapshot);
    }

    /** Returns an iterable collection of positions reported in inorder. */
    public Iterable<Position<E>> inorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty()) inorderSubtree(root(), snapshot);
        return snapshot;
    }

    /** For binary trees, make inorder the default order. */
    @Override
    public Iterable<Position<E>> positions() {
        return inorder();
    }
}
