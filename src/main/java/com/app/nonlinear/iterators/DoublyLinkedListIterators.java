package com.app.nonlinear.iterators;

import com.app.linear.implementations.DoublyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator utilities for {@link DoublyLinkedList}.
 * Provides both lazy and snapshot iterator variants.
 */
public class DoublyLinkedListIterators {

    /**
     * Lazy iterator: walks the live list from header.next to trailer.
     * Mutations during iteration may affect traversal.
     */
    public static class LazyIterator<E> implements Iterator<E> {
        private DoublyLinkedList.Node<E> current;
        private final DoublyLinkedList.Node<E> trailer;

        public LazyIterator(DoublyLinkedList.Node<E> header, DoublyLinkedList.Node<E> trailer) {
            this.current = header.getNext();
            this.trailer = trailer;
        }

        @Override
        public boolean hasNext() {
            return current != trailer;
        }

        @Override
        public E next() {
            E value = current.getElement();
            current = current.getNext();
            return value;
        }
    }

    /**
     * Snapshot iterator: iterates over an immutable copy of current elements.
     * Mutations after creation do not affect iteration.
     */
    public static class SnapshotIterator<E> implements Iterator<E> {
        private final List<E> snapshot;
        private int index = 0;

        public SnapshotIterator(DoublyLinkedList.Node<E> header, DoublyLinkedList.Node<E> trailer) {
            this.snapshot = new ArrayList<>();
            DoublyLinkedList.Node<E> cur = header.getNext();
            while (cur != trailer) {
                snapshot.add(cur.getElement());
                cur = cur.getNext();
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public E next() {
            return snapshot.get(index++);
        }
    }
}
