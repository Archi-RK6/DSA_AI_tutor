package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.Entry;
import com.app.nonlinear.interfaces.Position;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * An implementation of a sorted map using a splay tree.
 */
public class SplayTreeMap<K, V> extends TreeMap<K, V> {

    public SplayTreeMap() { super(); }

    public SplayTreeMap(Comparator<K> comp) { super(comp); }

    /** Utility used to rebalance after a map operation (splaying). */
    private void splay(Position<Entry<K, V>> p) {
        while (!isRoot(p)) {
            Position<Entry<K, V>> parent = parent(p);
            Position<Entry<K, V>> grand = parent(parent);
            if (grand == null) {                 // zig case
                rotate(p);
            } else if ((parent == left(grand)) == (p == left(parent))) { // zig-zig
                rotate(parent);  // move parent upward
                rotate(p);       // then move p upward
            } else {                                // zig-zag
                rotate(p);       // move p upward
                rotate(p);       // move p upward again
            }
        }
    }

    // override TreeMap rebalancing hooks
    @Override
    protected void rebalanceAccess(Position<Entry<K, V>> p) {
        if (isExternal(p)) p = parent(p);
        if (p != null) splay(p);
    }

    @Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        splay(p);
    }

    @Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        if (!isRoot(p)) splay(parent(p));
    }

    /**
     * Export structure with stable node ids (supports duplicates):
     *   id|key|leftId|rightId
     */
    public List<String> exportStructure() {
        List<String> out = new ArrayList<>();
        Position<Entry<K, V>> r = root();
        if (r == null || isExternal(r)) return out;

        IdentityHashMap<Position<Entry<K, V>>, String> ids = new IdentityHashMap<>();
        int[] seq = new int[]{0};

        Deque<Position<Entry<K, V>>> q = new ArrayDeque<>();
        q.add(r);

        while (!q.isEmpty()) {
            Position<Entry<K, V>> p = q.removeFirst();
            if (p == null || isExternal(p)) continue;

            String id = idFor(p, ids, seq);
            String key = String.valueOf(p.getElement().getKey());

            String leftId = "";
            String rightId = "";

            Position<Entry<K, V>> L = left(p);
            if (L != null && isInternal(L)) {
                leftId = idFor(L, ids, seq);
                q.addLast(L);
            }

            Position<Entry<K, V>> R = right(p);
            if (R != null && isInternal(R)) {
                rightId = idFor(R, ids, seq);
                q.addLast(R);
            }

            out.add(id + "|" + key + "|" + leftId + "|" + rightId);
        }

        return out;
    }

    private String idFor(Position<Entry<K, V>> p,
                         IdentityHashMap<Position<Entry<K, V>>, String> ids,
                         int[] seq) {
        String id = ids.get(p);
        if (id == null) {
            id = "n" + (++seq[0]);
            ids.put(p, id);
        }
        return id;
    }
}
