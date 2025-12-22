package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.AbstractHashMap;
import com.app.nonlinear.interfaces.Entry;

import java.util.ArrayList;

/**
 * Open-addressing hash map with linear probing.
 */
public class ProbeHashMap<K, V> extends AbstractHashMap<K, V> {

    private MapEntry<K, V>[] table;                 // array of entries (all initially null)
    private final MapEntry<K, V> DEFUNCT = new MapEntry<>(null, null); // sentinel

    public ProbeHashMap() { super(); }
    public ProbeHashMap(int cap) { super(cap); }
    public ProbeHashMap(int cap, int p) { super(cap, p); }

    /** Creates an empty table having length equal to current capacity. */
    @SuppressWarnings("unchecked")
    protected void createTable() {
        table = (MapEntry<K, V>[]) new MapEntry[capacity];
    }

    /** Returns true if location is either empty or the “defunct” sentinel. */
    private boolean isAvailable(int j) {
        return (table[j] == null || table[j] == DEFUNCT);
    }

    /** Returns index with key k, or -(a+1) such that k could be added at index a. */
    private int findSlot(int h, K k) {
        int avail = -1;     // no slot available yet
        int j = h;          // start index
        do {
            if (isAvailable(j)) {
                if (avail == -1) avail = j;    // first available slot
                if (table[j] == null) break;   // empty slot ends search
            } else if (table[j].getKey().equals(k)) {
                return j;                      // successful match
            }
            j = (j + 1) % capacity;            // linear probe (cyclic)
        } while (j != h);
        return -(avail + 1);                    // search failed
    }

    /** Returns value associated with key k in bucket h, or else null. */
    protected V bucketGet(int h, K k) {
        int j = findSlot(h, k);
        if (j < 0) return null;
        return table[j].getValue();
    }

    /** Associates key k with value v in bucket h; returns old value. */
    protected V bucketPut(int h, K k, V v) {
        int j = findSlot(h, k);
        if (j >= 0) { // existing entry
            return table[j].setValue(v);
        }
        table[-(j + 1)] = new MapEntry<>(k, v);
        n++;
        return null;
    }

    /** Removes entry having key k from bucket h (if any). */
    protected V bucketRemove(int h, K k) {
        int j = findSlot(h, k);
        if (j < 0) return null;
        V answer = table[j].getValue();
        table[j] = DEFUNCT; // mark slot as deactivated
        n--;
        return answer;
    }

    /** Returns an iterable collection of all key-value entries of the map. */
    public Iterable<Entry<K, V>> entrySet() {
        ArrayList<Entry<K, V>> buffer = new ArrayList<>();
        for (int h = 0; h < capacity; h++)
            if (!isAvailable(h)) buffer.add(table[h]);
        return buffer;
    }
}
