package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.AbstractMap;
import com.app.nonlinear.interfaces.Entry;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Map implementation backed by an unsorted ArrayList of entries.
 */
public class UnsortedTableMap<K, V> extends AbstractMap<K, V> {

    /** Underlying storage for the map of entries. */
    private final ArrayList<MapEntry<K, V>> table = new ArrayList<>();

    /** Constructs an initially empty map. */
    public UnsortedTableMap() {}

    // private utility: linear search
    /** Returns the index of an entry with equal key, or -1 if none found. */
    private int findIndex(K key) {
        int n = table.size();
        for (int j = 0; j < n; j++)
            if (table.get(j).getKey().equals(key))
                return j;
        return -1;
    }

    /** Returns the number of entries in the map. */
    public int size() { return table.size(); }

    /** Returns the value associated with the specified key (or else null). */
    public V get(K key) {
        int j = findIndex(key);
        if (j == -1) return null;
        return table.get(j).getValue();
    }

    /** Associates given value with given key, replacing a previous value (if any). */
    public V put(K key, V value) {
        int j = findIndex(key);
        if (j == -1) {
            table.add(new MapEntry<>(key, value));
            return null;
        } else {
            return table.get(j).setValue(value);
        }
    }

    /** Removes the entry with the specified key (if any) and returns its value. */
    public V remove(K key) {
        int j = findIndex(key);
        int n = size();
        if (j == -1) return null;
        V answer = table.get(j).getValue();
        if (j != n - 1)
            table.set(j, table.get(n - 1)); // move last entry into j
        table.remove(n - 1);
        return answer;
    }

    // Support for entrySet
    private class EntryIterator implements java.util.Iterator<Entry<K, V>> {
        private int j = 0;
        public boolean hasNext() { return j < table.size(); }
        public Entry<K, V> next() {
            if (j == table.size()) throw new NoSuchElementException();
            return table.get(j++);
        }
        public void remove() { throw new UnsupportedOperationException(); }
    }
    private class EntryIterable implements Iterable<Entry<K, V>> {
        public java.util.Iterator<Entry<K, V>> iterator() { return new EntryIterator(); }
    }

    /** Returns an iterable collection of all key-value entries of the map. */
    public Iterable<Entry<K, V>> entrySet() { return new EntryIterable(); }
}
