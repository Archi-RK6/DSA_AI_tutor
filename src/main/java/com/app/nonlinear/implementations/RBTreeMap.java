package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.Entry;
import com.app.nonlinear.interfaces.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An implementation of a sorted map using a red-black tree.
 */
public class RBTreeMap<K, V> extends TreeMap<K, V> {

    public RBTreeMap() { super(); }

    public RBTreeMap(Comparator<K> comp) { super(comp); }

    // we use the inherited aux field with convention that 0=black and 1=red
    // (note that new leaves will be black by default, as aux=0)
    private boolean isBlack(Position<Entry<K, V>> p) { return tree.getAux(p) == 0; }
    private boolean isRed(Position<Entry<K, V>> p) { return tree.getAux(p) == 1; }
    private void makeBlack(Position<Entry<K, V>> p) { tree.setAux(p, 0); }
    private void makeRed(Position<Entry<K, V>> p) { tree.setAux(p, 1); }
    private void setColor(Position<Entry<K, V>> p, boolean toRed) { tree.setAux(p, toRed ? 1 : 0); }


    public List<String> exportStructure() {
        List<String> out = new ArrayList<>();
        if (size() == 0) return out;

        // Support duplicate keys in the UI by exporting a stable node id distinct from the key.
        // Format:
        //   id|key|color|leftId|rightId
        java.util.IdentityHashMap<Position<Entry<K, V>>, String> ids = new java.util.IdentityHashMap<>();
        int[] seq = new int[]{0};

        exportRec(root(), out, ids, seq);
        return out;
    }

    private String idFor(Position<Entry<K, V>> p,
                         java.util.IdentityHashMap<Position<Entry<K, V>>, String> ids,
                         int[] seq) {
        if (p == null || isExternal(p)) return "";
        String id = ids.get(p);
        if (id == null) {
            id = "n" + (++seq[0]);
            ids.put(p, id);
        }
        return id;
    }

    private void exportRec(Position<Entry<K, V>> p,
                           List<String> out,
                           java.util.IdentityHashMap<Position<Entry<K, V>>, String> ids,
                           int[] seq) {
        if (p == null || isExternal(p)) return;

        Entry<K, V> e = p.getElement();
        String id = idFor(p, ids, seq);
        String key = String.valueOf(e.getKey());
        String color = isRed(p) ? "R" : "B";

        String leftId  = (left(p)  != null && isInternal(left(p)))  ? idFor(left(p), ids, seq)  : "";
        String rightId = (right(p) != null && isInternal(right(p))) ? idFor(right(p), ids, seq) : "";

        out.add(id + "|" + key + "|" + color + "|" + leftId + "|" + rightId);

        exportRec(left(p), out, ids, seq);
        exportRec(right(p), out, ids, seq);
    }
    // --------------------------------------------------------------------

    /** Rebalancing after insertion. */
    @Override
    protected void rebalanceInsert(Position<Entry<K, V>> p) {
        if (!isRoot(p)) {
            makeRed(p);    // the new internal node is initially red
            resolveRed(p); // fix double-red if any
        }
    }

    /** Remedies a double-red violation at a given red position p. */
    private void resolveRed(Position<Entry<K, V>> p) {
        Position<Entry<K, V>> parent, uncle, grand, middle;
        parent = parent(p);
        if (isRed(parent)) { // double-red problem exists
            uncle = sibling(parent);
            if (isBlack(uncle)) { // Case 1: misshapen 4-node (rotation)
                middle = restructure(p);
                makeBlack(middle);
                makeRed(left(middle));
                makeRed(right(middle));
            } else { // Case 2: overfull 5-node (recoloring)
                makeBlack(parent);
                makeBlack(uncle);
                grand = parent(parent);
                if (!isRoot(grand)) {
                    makeRed(grand);
                    resolveRed(grand);
                }
            }
        }
    }

    /** Rebalancing after deletion. */
    @Override
    protected void rebalanceDelete(Position<Entry<K, V>> p) {
        if (isRed(p)) { // red node can be made black to eliminate double-black problem
            makeBlack(p);
        } else if (!isRoot(p)) {
            Position<Entry<K, V>> sib = sibling(p);
            if (isInternal(sib) && (isBlack(sib) || isInternal(left(sib))))
                remedyDoubleBlack(p); // sibling's subtree has nonzero black height
        }
    }

    /** Remedies a presumed double-black violation at the given (nonroot) position. */
    private void remedyDoubleBlack(Position<Entry<K, V>> p) {
        Position<Entry<K, V>> z = parent(p);
        Position<Entry<K, V>> y = sibling(p);
        if (isBlack(y)) {
            if (isRed(left(y)) || isRed(right(y))) { // Case 1: trinode restructuring
                Position<Entry<K, V>> x = (isRed(left(y)) ? left(y) : right(y));
                Position<Entry<K, V>> middle = restructure(x);
                setColor(middle, isRed(z)); // root of restructured subtree gets z's old color
                makeBlack(left(middle));
                makeBlack(right(middle));
            } else { // Case 2: recoloring
                makeRed(y);
                if (isRed(z)) {
                    makeBlack(z); // resolved
                } else if (!isRoot(z)) {
                    remedyDoubleBlack(z); // propagate upward
                }
            }
        } else { // Case 3: reorient 3-node
            rotate(y);
            makeBlack(y);
            makeRed(z);
            remedyDoubleBlack(p); // restart at p
        }
    }
}
