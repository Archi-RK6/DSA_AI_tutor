package com.app.nonlinear.interfaces;

import com.app.nonlinear.iterators.AbstractTreeIterators;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
/**
 * An abstract base class providing some functionality of the Tree interface.
 */
public abstract class AbstractTree<E> implements Tree<E> {

    public boolean isInternal(Position<E> p) {
        return numChildren(p) > 0;
    }

    public boolean isExternal(Position<E> p) {
        return numChildren(p) == 0;
    }

    public boolean isRoot(Position<E> p) {
        return p == root();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int depth(Position<E> p) {
        if (isRoot(p))
            return 0;
        else
            return 1 + depth(parent(p));
    }

    /** Returns the height of the tree. */
    private int heightBad() { // works, but quadratic worst-case time
        int h = 0;
        for (Position<E> p : positions())
            if (isExternal(p)) // only consider leaf positions
                h = Math.max(h, depth(p));
        return h;
    }

    /** Returns the height of the subtree rooted at Position p. */
    public int height(Position<E> p) {
        int h = 0; // base case if p is external
        for (Position<E> c : children(p))
            h = Math.max(h, 1 + height(c));
        return h;
    }

    // ---------- PREORDER ----------
    /** Adds positions of the subtree rooted at p to the given snapshot (preorder). */
    private void preorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        snapshot.add(p);
        for (Position<E> c : children(p)) {
            preorderSubtree(c, snapshot);
        }
    }

    /** Returns an iterable collection of positions in preorder. */
    public Iterable<Position<E>> preorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty()) preorderSubtree(root(), snapshot);
        return snapshot;
    }

    // ---------- POSTORDER ----------
    /** Adds positions of the subtree rooted at p to the given snapshot (postorder). */
    private void postorderSubtree(Position<E> p, List<Position<E>> snapshot) {
        for (Position<E> c : children(p)) {
            postorderSubtree(c, snapshot);
        }
        snapshot.add(p);
    }

    /** Returns an iterable collection of positions in postorder. */
    public Iterable<Position<E>> postorder() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty()) postorderSubtree(root(), snapshot);
        return snapshot;
    }

    // ---------- BREADTH-FIRST ----------
    /** Returns an iterable collection of positions in breadth-first order. */
    public Iterable<Position<E>> breadthfirst() {
        List<Position<E>> snapshot = new ArrayList<>();
        if (!isEmpty()) {
            Deque<Position<E>> fringe = new ArrayDeque<>();
            fringe.addLast(root());
            while (!fringe.isEmpty()) {
                Position<E> p = fringe.removeFirst();
                snapshot.add(p);
                for (Position<E> c : children(p)) {
                    fringe.addLast(c);
                }
            }
        }
        return snapshot;
    }

    /** Default traversal for generic trees: preorder (can be overridden). */
    public Iterable<Position<E>> positions() {
        return preorder();
    }

    /** Iterator over elements via positions(). */
    public Iterator<E> iterator() {
        return new AbstractTreeIterators.ElementIterator<>(this.positions());
    }
}
