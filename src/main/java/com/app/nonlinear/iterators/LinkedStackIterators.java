package com.app.nonlinear.iterators;

import com.app.linear.implementations.SinglyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class LinkedStackIterators {

    public static class LazyIterator<E> implements Iterator<E> {
        private final Iterator<E> delegate;

        public LazyIterator(SinglyLinkedList<E> list) {
            this.delegate = list.iterator();
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public E next() {
            return delegate.next();
        }
    }

    public static class SnapshotIterator<E> implements Iterator<E> {
        private final List<E> snapshot = new ArrayList<>();
        private int index = 0;

        public SnapshotIterator(SinglyLinkedList<E> list) {
            Iterator<E> it = list.snapshotIterator();
            while (it.hasNext()) {
                snapshot.add(it.next());
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return snapshot.get(index++);
        }
    }
}

