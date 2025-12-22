package com.app.linear.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.app.linear.implementations.ArrayList;

public class ArrayListIterators {

    /** Lazy iterator: iterates over the live list */
    public static class LazyIterator<E> implements Iterator<E> {
        private final ArrayList<E> list;
        private int index = 0;

        public LazyIterator(ArrayList<E> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return list.get(index++);
        }
    }

    /** Snapshot iterator: iterates over a captured copy of elements using another ArrayList. */
    public static class SnapshotIterator<E> implements Iterator<E> {
        private final ArrayList<E> snapshot;
        private int i = 0;

        public SnapshotIterator(ArrayList<E> list) {
            this.snapshot = new ArrayList<>(list.size());
            for (int k = 0; k < list.size(); k++) {
                snapshot.add(k, list.get(k));
            }
        }

        @Override
        public boolean hasNext() {
            return i < snapshot.size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return snapshot.get(i++);
        }
    }
}
