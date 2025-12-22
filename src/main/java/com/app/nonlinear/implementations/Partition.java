package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.Position;

public class Partition<E> {

    private class Locator<E> implements Position<E> {
        public E element;
        public int size;
        public Locator<E> parent;

        public Locator(E elem) {
            element = elem;
            size = 1;
            parent = this;
        }

        public E getElement() { return element; }
    }

    public Position<E> makeCluster(E e) {
        return new Locator<E>(e);
    }

    public Position<E> find(Position<E> p) {
        Locator<E> loc = validate(p);
        if (loc.parent != loc)
            loc.parent = (Locator<E>) find(loc.parent);
        return loc.parent;
    }

    public void union(Position<E> p, Position<E> q) {
        Locator<E> a = (Locator<E>) find(p);
        Locator<E> b = (Locator<E>) find(q);
        if (a != b)
            if (a.size > b.size) {
                b.parent = a;
                a.size += b.size;
            } else {
                a.parent = b;
                b.size += a.size;
            }
    }

    @SuppressWarnings("unchecked")
    private Locator<E> validate(Position<E> p) {
        if (!(p instanceof Locator))
            throw new IllegalArgumentException("Invalid position");
        return (Locator<E>) p;
    }
}
