package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.AbstractPriorityQueue;
import com.app.nonlinear.interfaces.Entry;
import com.app.nonlinear.interfaces.Position;

import java.util.Comparator;

/**
 * An implementation of a priority queue with an unsorted list.
 */
public class UnsortedPriorityQueue<K, V> extends AbstractPriorityQueue<K, V> {

    /** Primary collection of priority queue entries. */
    private LinkedPositionalList<Entry<K, V>> list = new LinkedPositionalList<Entry<K, V>>();

    /** Creates an empty priority queue based on the natural ordering of its keys. */
    public UnsortedPriorityQueue() { super(); }

    /** Creates an empty priority queue using the given comparator to order keys. */
    public UnsortedPriorityQueue(Comparator<K> comp) { super(comp); }

    /** Returns the Position of an entry having minimal key. */
    private Position<Entry<K, V>> findMin() { // only called when nonempty
        Position<Entry<K, V>> small = list.first();
        for (Position<Entry<K, V>> walk : list.positions())
            if (compare(walk.getElement(), small.getElement()) < 0)
                small = walk; // found an even smaller key
        return small;
    }

    /** Inserts a key-value pair and returns the entry created. */
    public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
        checkKey(key);
        Entry<K, V> newest = new PQEntry<>(key, value);
        list.addLast(newest);
        return newest;
    }

    /** Returns (but does not remove) an entry with minimal key. */
    public Entry<K, V> min() {
        if (list.isEmpty()) return null;
        return findMin().getElement();
    }

    /** Removes and returns an entry with minimal key. */
    public Entry<K, V> removeMin() {
        if (list.isEmpty()) return null;
        return list.remove(findMin());
    }

    /** Returns the number of items in the priority queue. */
    public int size() { return list.size(); }
}

