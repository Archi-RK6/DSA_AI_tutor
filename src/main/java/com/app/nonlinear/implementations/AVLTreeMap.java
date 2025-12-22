package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.Entry;
import com.app.nonlinear.interfaces.Position;

import java.util.Comparator;

/**
 * An implementation of a sorted map using an AVL tree.
 */
public class AVLTreeMap<K, V> extends TreeMap<K, V> {

    public AVLTreeMap() { super(); }

    public AVLTreeMap(Comparator<K> comp) { super(comp); }

    /** Returns the height of the given tree position. */
    protected int height(Position<Entry<K, V>> p) { return tree.getAux(p); }

    /** Recomputes the height of the given position based on its children's heights. */
    protected void recomputeHeight(Position<Entry<K, V>> p) {
        tree.setAux(p, 1 + Math.max(height(left(p)), height(right(p))));
    }

    /** Returns whether a position has balance factor between −1 and 1 inclusive. */
    protected boolean isBalanced(Position<Entry<K, V>> p) {
        return Math.abs(height(left(p)) - height(right(p))) <= 1;
    }

    /** Returns a child of p with height no smaller than that of the other child. */
    protected Position<Entry<K, V>> tallerChild(Position<Entry<K, V>> p) {
        if (height(left(p)) > height(right(p))) return left(p);
        if (height(left(p)) < height(right(p))) return right(p);
        // equal height children; break tie while matching parent's orientation
        if (isRoot(p)) return left(p);
        if (p == left(parent(p))) return left(p);
        else return right(p);
    }

    /**
     * Utility used to rebalance after an insert or removal operation.
     * Traverses upward from p, performing a trinode restructuring when imbalance
     * is found, continuing until balance is restored.
     */
    protected void rebalance(Position<Entry<K, V>> p) {
        int oldHeight, newHeight;
        do {
            oldHeight = height(p);      // height before update
            if (!isBalanced(p)) {       // imbalance detected
                p = restructure(tallerChild(tallerChild(p)));
                recomputeHeight(left(p));
                recomputeHeight(right(p));
            }
            recomputeHeight(p);
            newHeight = height(p);
            p = parent(p);
        } while (p != null && oldHeight != newHeight);
    }

    /** Overrides the rebalancing hook called after an insertion. */
    @Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        rebalance(p);
    }

    /** Overrides the rebalancing hook called after a deletion. */
    @Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        if (!isRoot(p)) rebalance(parent(p));
    }
}
