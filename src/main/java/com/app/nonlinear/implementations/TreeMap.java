package com.app.nonlinear.implementations;

// TreeMap.java
import com.app.nonlinear.interfaces.AbstractSortedMap;
import com.app.nonlinear.interfaces.Entry;
import com.app.nonlinear.interfaces.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;

/**
 * An implementation of a sorted map using a binary search tree.
 */
public class TreeMap<K, V> extends AbstractSortedMap<K, V> {

    // Underlying tree structure with balancing hooks.
    protected BalanceableBinaryTree<K, V> tree = new BalanceableBinaryTree<>();

    /** Constructs an empty map using the natural ordering of keys. */
    public TreeMap() {
        super();
        tree.addRoot(null); // create a sentinel leaf as root
    }

    /** Constructs an empty map using the given comparator to order keys. */
    public TreeMap(Comparator<K> comp) {
        super(comp);
        tree.addRoot(null); // create a sentinel leaf as root
    }

    /** Returns the number of entries in the map. */
    public int size() {
        return (tree.size() - 1) / 2; // only internal nodes have entries
    }

    // ---------- shorthand delegates to the underlying tree ----------

    protected Position<Entry<K, V>> root() { return tree.root(); }
    protected Position<Entry<K, V>> parent(Position<Entry<K, V>> p) { return tree.parent(p); }
    protected Position<Entry<K, V>> left(Position<Entry<K, V>> p) { return tree.left(p); }
    protected Position<Entry<K, V>> right(Position<Entry<K, V>> p) { return tree.right(p); }
    protected Position<Entry<K, V>> sibling(Position<Entry<K, V>> p) { return tree.sibling(p); }
    protected boolean isRoot(Position<Entry<K, V>> p) { return tree.isRoot(p); }
    protected boolean isExternal(Position<Entry<K, V>> p) { return tree.isExternal(p); }
    protected boolean isInternal(Position<Entry<K, V>> p) { return tree.isInternal(p); }
    protected Entry<K, V> set(Position<Entry<K, V>> p, Entry<K, V> e) { return tree.set(p, e); }
    protected void remove(Position<Entry<K, V>> p) { tree.remove(p); }
    protected Position<Entry<K, V>> addLeft(Position<Entry<K, V>> p, Entry<K, V> e) { return tree.addLeft(p, e); }
    protected Position<Entry<K, V>> addRight(Position<Entry<K, V>> p, Entry<K, V> e) { return tree.addRight(p, e); }

    // rotation/restructure are exposed by BalanceableBinaryTree
    protected void rotate(Position<Entry<K, V>> p) { tree.rotate(p); }
    protected Position<Entry<K, V>> restructure(Position<Entry<K, V>> x) { return tree.restructure(x); }

    /** Key-check helper leveraging the comparator. */
    protected void checkKey(K key) throws IllegalArgumentException {
        try {
            // ensures key is comparable by this map's comparator
            comparator().compare(key, key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incompatible key");
        }
    }

    /** Utility used when inserting a new entry at a leaf of the tree. */
    private void expandExternal(Position<Entry<K, V>> p, Entry<K, V> entry) {
        tree.set(p, entry);     // store new entry at p
        tree.addLeft(p, null);  // add new sentinel leaves as children
        tree.addRight(p, null);
    }

    /** Returns the position in p's subtree having given key (or else the terminal leaf). */
    private Position<Entry<K, V>> treeSearch(Position<Entry<K, V>> p, K key) {
        if (isExternal(p)) return p; // key not found; return the final leaf
        int comp = compareKeys(key, p.getElement().getKey());
        if (comp == 0) return p;     // key found
        else if (comp < 0) return treeSearch(left(p), key);
        else return treeSearch(right(p), key);
    }

    /**
     * Like treeSearch, but for insertion: never stops on equality.
     * If comp == 0, we go RIGHT. This enables duplicates as separate nodes.
     */
    private Position<Entry<K, V>> treeSearchForInsert(Position<Entry<K, V>> p, K key) {
        if (isExternal(p)) return p;
        int comp = compareKeys(key, p.getElement().getKey());
        if (comp < 0) return treeSearchForInsert(left(p), key);
        return treeSearchForInsert(right(p), key); // comp >= 0 goes right (duplicates)
    }

    /** Returns the value associated with the specified key (or else null). */
    public V get(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        rebalanceAccess(p); // hook for balanced subclasses
        if (isExternal(p)) return null;
        return p.getElement().getValue();
    }

    /**
     * Associates the given value with the given key.
     * ✅ Duplicates allowed: we ALWAYS insert a new node (equal keys go to the right subtree).
     * Returns null (we do not override existing values anymore).
     */
    public V put(K key, V value) throws IllegalArgumentException {
        checkKey(key);
        Entry<K, V> newEntry = new MapEntry<>(key, value);

        Position<Entry<K, V>> p = treeSearchForInsert(root(), key); // ALWAYS ends at an external node
        expandExternal(p, newEntry);
        rebalanceInsert(p);     // hook
        return null;
    }

    /** Removes the entry having key k (if any) and returns its associated value. */
    public V remove(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isExternal(p)) {         // key not found
            rebalanceAccess(p);      // hook
            return null;
        } else {
            V old = p.getElement().getValue();
            if (isInternal(left(p)) && isInternal(right(p))) { // two internal children
                Position<Entry<K, V>> replacement = treeMax(left(p));
                set(p, replacement.getElement());
                p = replacement;
            }
            Position<Entry<K, V>> leaf = (isExternal(left(p)) ? left(p) : right(p));
            Position<Entry<K, V>> sib = sibling(leaf);
            remove(leaf);
            remove(p);               // sibling is promoted into p's place
            rebalanceDelete(sib);    // hook
            return old;
        }
    }

    /** Returns the position with the maximum key in the subtree rooted at p. */
    protected Position<Entry<K, V>> treeMax(Position<Entry<K, V>> p) {
        Position<Entry<K, V>> walk = p;
        while (isInternal(walk)) walk = right(walk);
        return parent(walk); // parent of the external leaf
    }

    /** Returns the entry having the greatest key (or null if map is empty). */
    public Entry<K, V> lastEntry() {
        if (isEmpty()) return null;
        return treeMax(root()).getElement();
    }

    /** Returns the entry with greatest key less than or equal to given key (if any). */
    public Entry<K, V> floorEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p)) return p.getElement(); // exact match
        while (!isRoot(p)) {
            if (p == right(parent(p))) return parent(p).getElement(); // parent has next lesser key
            else p = parent(p);
        }
        return null; // none
    }

    /** Returns the entry with greatest key strictly less than given key (if any). */
    public Entry<K, V> lowerEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p) && isInternal(left(p))) return treeMax(left(p)).getElement(); // predecessor
        while (!isRoot(p)) {
            if (p == right(parent(p))) return parent(p).getElement();
            else p = parent(p);
        }
        return null;
    }

    /** Returns the entry with least key greater than or equal to given key (if any). */
    public Entry<K, V> ceilingEntry(K key) throws IllegalArgumentException {
        checkKey(key);
        Position<Entry<K, V>> p = treeSearch(root(), key);
        if (isInternal(p)) return p.getElement();
        while (!isRoot(p)) {
            if (p == left(parent(p))) return parent(p).getElement();
            p = parent(p);
        }
        return null;
    }

    /** Returns all entries using inorder traversal. */
    public Iterable<Entry<K, V>> entrySet() {
        ArrayList<Entry<K, V>> buffer = new ArrayList<>(size());
        for (Position<Entry<K, V>> p : tree.inorder())
            if (isInternal(p)) buffer.add(p.getElement());
        return buffer;
    }

    /** Returns entries with keys in range [fromKey, toKey). */
    public Iterable<Entry<K, V>> subMap(K fromKey, K toKey) {
        ArrayList<Entry<K, V>> buffer = new ArrayList<>(size());
        if (compareKeys(fromKey, toKey) < 0)
            subMapRecurse(fromKey, toKey, root(), buffer);
        return buffer;
    }

    private void subMapRecurse(K fromKey, K toKey, Position<Entry<K, V>> p, ArrayList<Entry<K, V>> buffer) {
        if (isInternal(p)) {
            if (compareKeys(p.getElement().getKey(), fromKey) < 0) {
                subMapRecurse(fromKey, toKey, right(p), buffer);
            } else {
                subMapRecurse(fromKey, toKey, left(p), buffer);
                if (compareKeys(p.getElement().getKey(), toKey) < 0) {
                    buffer.add(p.getElement());
                    subMapRecurse(fromKey, toKey, right(p), buffer);
                }
            }
        }
    }

    // ==== Viz: export structure as lines "id|key|leftId|rightId" (supports duplicate keys) ====
    public java.util.List<String> exportStructure() {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (isEmpty()) return out;

        IdentityHashMap<Position<Entry<K, V>>, String> ids = new IdentityHashMap<>();
        int[] seq = new int[]{0};

        exportRecurse(root(), out, ids, seq);
        return out;
    }

    private String idFor(Position<Entry<K, V>> p,
                         IdentityHashMap<Position<Entry<K, V>>, String> ids,
                         int[] seq) {
        if (p == null || isExternal(p)) return "";
        String id = ids.get(p);
        if (id == null) {
            id = "n" + (++seq[0]);
            ids.put(p, id);
        }
        return id;
    }

    private void exportRecurse(Position<Entry<K, V>> p,
                               java.util.List<String> out,
                               IdentityHashMap<Position<Entry<K, V>>, String> ids,
                               int[] seq) {
        if (p == null || isExternal(p)) return;

        String id = idFor(p, ids, seq);
        String key = String.valueOf(p.getElement().getKey());

        Position<Entry<K, V>> L = left(p);
        Position<Entry<K, V>> R = right(p);

        String leftId  = (L != null && isInternal(L)) ? idFor(L, ids, seq) : "";
        String rightId = (R != null && isInternal(R)) ? idFor(R, ids, seq) : "";

        out.add(id + "|" + key + "|" + leftId + "|" + rightId);

        exportRecurse(L, out, ids, seq);
        exportRecurse(R, out, ids, seq);
    }

    // -------- hooks for balanced-tree subclasses (no-ops here) --------
    protected void rebalanceAccess(Position<Entry<K, V>> p) {}
    protected void rebalanceInsert(Position<Entry<K, V>> p) {}
    protected void rebalanceDelete(Position<Entry<K, V>> p) {}
}
