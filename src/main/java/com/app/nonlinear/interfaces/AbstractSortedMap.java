package com.app.nonlinear.interfaces;

import nonlinear.comparators.DefaultComparator;

import java.util.Comparator;

/**
 * Minimal base class for sorted maps with comparator support and compare helpers.
 * Provides the compare(K, Entry) utility expected by SortedTableMap.
 */
public abstract class AbstractSortedMap<K, V> extends AbstractMap<K, V> {

    private final Comparator<K> comp;

    /** Uses natural ordering of keys. */
    @SuppressWarnings("unchecked")
    protected AbstractSortedMap() {
        this.comp = new DefaultComparator<>();
    }

    /** Uses the provided comparator for key ordering. */
    protected AbstractSortedMap(Comparator<K> comp) {
        this.comp = (comp != null) ? comp : new DefaultComparator<>();
    }

    /** Compare two keys. */
    protected int compareKeys(K a, K b) {
        return comp.compare(a, b);
    }

    /** Compare a key to an entry's key. */
    protected int compare(K key, Entry<K, V> entry) {
        return comp.compare(key, entry.getKey());
    }

    /** Access to the comparator (if needed by subclasses). */
    protected Comparator<K> comparator() {
        return comp;
    }
}

