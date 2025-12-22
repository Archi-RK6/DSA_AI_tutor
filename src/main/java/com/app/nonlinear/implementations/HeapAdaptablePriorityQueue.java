package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.AdaptablePriorityQueue;
import com.app.nonlinear.interfaces.Entry;

import java.util.Comparator;

/**
 * An implementation of an adaptable priority queue using an array-based heap.
 */
public class HeapAdaptablePriorityQueue<K, V> extends HeapPriorityQueue<K, V>
        implements AdaptablePriorityQueue<K, V> {

    //---------------- nested AdaptablePQEntry class ----------------
    /** Extension of the PQEntry to include location information. */
    protected static class AdaptablePQEntry<K, V> extends PQEntry<K, V> {
        private int index; // entry’s current index within the heap
        public AdaptablePQEntry(K key, V value, int j) {
            super(key, value);
            index = j;
        }
        public int getIndex() { return index; }
        public void setIndex(int j) { index = j; }
    }
    //----------- end of nested AdaptablePQEntry class -----------

    /** Creates an empty adaptable priority queue using natural ordering of keys. */
    public HeapAdaptablePriorityQueue() { super(); }

    /** Creates an empty adaptable priority queue using the given comparator. */
    public HeapAdaptablePriorityQueue(Comparator<K> comp) { super(comp); }

    // ---------- protected utilities ----------
    /** Validates an entry to ensure it is location-aware and currently in the heap. */
    @SuppressWarnings("unchecked")
    protected AdaptablePQEntry<K, V> validate(Entry<K, V> entry) throws IllegalArgumentException {
        if (!(entry instanceof AdaptablePQEntry))
            throw new IllegalArgumentException("Invalid entry");
        AdaptablePQEntry<K, V> locator = (AdaptablePQEntry<K, V>) entry; // safe
        int j = locator.getIndex();
        if (j < 0 || j >= heap.size() || heap.get(j) != locator)
            throw new IllegalArgumentException("Invalid entry");
        return locator;
    }

    /** Exchanges the entries at indices i and j of the array list and resets their indices. */
    @Override
    @SuppressWarnings("unchecked")
    protected void swap(int i, int j) {
        super.swap(i, j); // perform the swap
        ((AdaptablePQEntry<K, V>) heap.get(i)).setIndex(i); // reset entry's index
        ((AdaptablePQEntry<K, V>) heap.get(j)).setIndex(j); // reset entry's index
    }

    /** Restores the heap property by moving the entry at index j upward or downward. */
    protected void bubble(int j) {
        if (j > 0 && compare(heap.get(j), heap.get(parent(j))) < 0)
            upheap(j);
        else
            downheap(j); // although it might not need to move
    }

    // ---------- public overrides ----------
    /** Inserts a key-value pair and returns the entry created. */
    @Override
    public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
        checkKey(key); // might throw an exception
        Entry<K, V> newest = new AdaptablePQEntry<>(key, value, heap.size());
        heap.add(newest);                  // add to the end of the list
        upheap(heap.size() - 1);           // upheap newly added entry
        return newest;
    }

    /** Removes the given entry from the priority queue. */
    @Override
    public void remove(Entry<K, V> entry) throws IllegalArgumentException {
        AdaptablePQEntry<K, V> locator = validate(entry);
        int j = locator.getIndex();
        int last = heap.size() - 1;
        if (j == last) {                   // entry is at last position
            heap.remove(last);             // so just remove it
        } else {
            swap(j, last);                 // swap entry to last position
            heap.remove(last);             // then remove it
            bubble(j);                     // and fix entry displaced by the swap
        }
    }

    /** Replaces the key of an entry. */
    @Override
    public void replaceKey(Entry<K, V> entry, K key) throws IllegalArgumentException {
        AdaptablePQEntry<K, V> locator = validate(entry);
        checkKey(key);                     // might throw an exception
        locator.setKey(key);               // method inherited from PQEntry
        bubble(locator.getIndex());        // with new key, may need to move entry
    }

    /** Replaces the value of an entry. */
    @Override
    public void replaceValue(Entry<K, V> entry, V value) throws IllegalArgumentException {
        AdaptablePQEntry<K, V> locator = validate(entry);
        locator.setValue(value);           // method inherited from PQEntry
        // no bubbling needed: values do not affect heap order
    }
}
