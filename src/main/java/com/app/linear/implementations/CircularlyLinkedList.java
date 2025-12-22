package com.app.linear.implementations;

import java.util.Objects;

/**
 * A generic circularly linked list that stores only a tail reference.
 *
 * @param <E> element type
 */
public class CircularlyLinkedList<E> {

    /**
     * Node of a circularly linked list.
     * Kept package-private static to match textbook style and allow potential iterator access if needed later.
     */
    static class Node<E> {
        /** element stored at this node */
        private E element;
        /** reference to the subsequent node (never null when list non-empty; self when single-node) */
        private Node<E> next;

        /**
         * Constructs a node with element and next reference.
         * @param element element to store
         * @param next    next node reference (may be null during bootstrap)
         */
        Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }

        /** @return the element stored at this node */
        E getElement() {
            return element;
        }

        /** @return the next node reference */
        Node<E> getNext() {
            return next;
        }

        /**
         * Set the next node reference.
         * @param next the new next node
         */
        void setNext(Node<E> next) {
            this.next = next;
        }
    }

    /** we store only tail (head is tail.next when non-empty) */
    private Node<E> tail = null;
    /** number of nodes in the list */
    private int size = 0;

    /** Constructs an initially empty list. */
    public CircularlyLinkedList() { }

    /** @return number of elements in the list */
    public int size() {
        return size;
    }

    /** @return true if the list is empty; false otherwise */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns (but does not remove) the first element (the element after tail).
     * @return first element, or null if the list is empty
     */
    public E first() {
        if (isEmpty()) return null;
        return tail.getNext().getElement(); // head is tail.next
    }

    /**
     * Returns (but does not remove) the last element (the element at tail).
     * @return last element, or null if the list is empty
     */
    public E last() {
        if (isEmpty()) return null;
        return tail.getElement();
    }

    /**
     * Rotates the first element to the back of the list.
     * Has no effect on an empty list.
     */
    public void rotate() {
        if (tail != null) {
            tail = tail.getNext(); // old head becomes new tail
        }
    }

    /**
     * Adds element to the front of the list.
     * @param e element to add
     */
    public void addFirst(E e) {
        if (size == 0) {
            tail = new Node<>(e, null);
            tail.setNext(tail);          // link to itself circularly
        } else {
            Node<E> newest = new Node<>(e, tail.getNext());
            tail.setNext(newest);        // insert between tail and old head
        }
        size++;
    }

    /**
     * Adds element to the end of the list.
     * Implemented by addFirst followed by rotate tail forward one step.
     * @param e element to add
     */
    public void addLast(E e) {
        addFirst(e);
        tail = tail.getNext();           // new element becomes tail
    }

    /**
     * Removes and returns the first element of the list.
     * @return removed element, or null if the list is empty
     */
    public E removeFirst() {
        if (isEmpty()) return null;
        Node<E> head = tail.getNext();
        if (head == tail) {              // only one node
            tail = null;
        } else {
            tail.setNext(head.getNext()); // bypass old head
        }
        size--;
        return head.getElement();
    }

    /**
     * Returns a string representation of the list in the form:
     * [e1 -> e2 -> ... -> en]
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (!isEmpty()) {
            Node<E> current = tail.getNext(); // start at head
            for (int i = 0; i < size; i++) {
                if (i > 0) sb.append(" -> ");
                sb.append(String.valueOf(current.getElement()));
                current = current.getNext();
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Equality with another circular list: true if both lists have the same size
     * and the same element sequence starting from each head position.
     * Note: This compares in head-order (tail.next) for both lists.
     * @param o other object
     * @return true if equal by element sequence; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CircularlyLinkedList<?> other)) return false;
        if (this.size != other.size) return false;
        if (this.isEmpty()) return true;

        Node<E> a = this.tail.getNext();           // head of this
        Node<?> b = other.tail.getNext();          // head of other
        for (int i = 0; i < this.size; i++) {
            if (!Objects.equals(a.getElement(), b.getElement())) return false;
            a = a.getNext();
            b = b.getNext();
        }
        return true;
    }

    /**
     * Hash code consistent with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        int h = 1;
        if (!isEmpty()) {
            Node<E> current = tail.getNext();
            for (int i = 0; i < size; i++) {
                h = 31 * h + Objects.hashCode(current.getElement());
                current = current.getNext();
            }
        }
        return h;
    }
}
