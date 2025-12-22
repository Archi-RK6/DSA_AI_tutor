package com.app.linear.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.app.linear.implementations.SinglyLinkedList;

/**
 * Container for singly linked list iterators.
 * Exposes both a lazy iterator and a snapshot iterator as public static classes.
 */
public class SinglyLinkedListIterators {

    /**
     * Lazy iterator over a singly linked list.
     * Traverses on-demand by following next pointers.
     *
     * @param <E> element type
     */
    public static class LazyIterator<E> implements Iterator<E> {
        private SinglyLinkedList.Node<E> current;

        /**
         * Construct a lazy iterator starting at head.
         * @param head head node (may be null)
         */
        public LazyIterator(SinglyLinkedList.Node<E> head) {
            this.current = head;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (current == null) {
                throw new java.util.NoSuchElementException();
            }
            E value = current.getElement();
            current = current.getNext();
            return value;
        }
    }

    /**
     * Snapshot iterator over a singly linked list.
     * Takes a snapshot of elements at construction and iterates over that fixed view.
     *
     * @param <E> element type
     */
    public static class SnapshotIterator<E> implements Iterator<E> {
        private final List<E> snapshot;
        private int index = 0;

        /**
         * Construct a snapshot iterator by copying the list elements starting at head.
         * @param head head node (may be null)
         */
        public SnapshotIterator(SinglyLinkedList.Node<E> head) {
            this.snapshot = new ArrayList<>();
            SinglyLinkedList.Node<E> current = head;
            while (current != null) {
                snapshot.add(current.getElement());
                current = current.getNext();
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public E next() {
            if (index >= snapshot.size()) {
                throw new java.util.NoSuchElementException();
            }
            return snapshot.get(index++);
        }
    }
}
