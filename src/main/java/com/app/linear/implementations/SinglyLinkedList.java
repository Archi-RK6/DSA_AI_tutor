package com.app.linear.implementations;

import java.util.Objects;
import com.app.linear.iterators.SinglyLinkedListIterators;

/**
 * A generic singly linked list, mirroring the behavior of the provided Python version.
 * Supports basic operations: size, isEmpty, first, last, addFirst, addLast, removeFirst,
 * string representation, equality check, and two iterator styles (lazy/snapshot).
 *
 * @param <E> element type
 */
public class SinglyLinkedList<E> implements Iterable<E> {

    /**
     * Node of a singly linked list.
     * Exposed as public static so external iterator classes may consume the head safely.
     */
    public static class Node<E> {
        /** element stored at this node */
        private E element;
        /** reference to the subsequent node */
        private Node<E> next;

        /**
         * Constructs a node with element and next reference.
         * @param element element to store
         * @param next next node reference (may be null)
         */
        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }

        /** @return the element stored at this node */
        public E getElement() {
            return element;
        }

        /** @return the next node reference (may be null) */
        public Node<E> getNext() {
            return next;
        }

        /**
         * Set the next node reference.
         * @param next the new next node (may be null)
         */
        public void setNext(Node<E> next) {
            this.next = next;
        }
    }

    /** head node of the list (or null if empty) */
    private Node<E> head = null;
    /** last node of the list (or null if empty) */
    private Node<E> tail = null;
    /** number of nodes in the list */
    private int size = 0;

    /** Constructs an initially empty list. */
    public SinglyLinkedList() { }

    /** @return number of elements in the list */
    public int size() {
        return size;
    }

    /** @return true if the list is empty, false otherwise */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns (but does not remove) the first element.
     * @return first element, or null if the list is empty
     */
    public E first() {
        if (isEmpty()) {
            return null;
        }
        return head.getElement();
    }

    /**
     * Returns (but does not remove) the last element.
     * @return last element, or null if the list is empty
     */
    public E last() {
        if (isEmpty()) {
            return null;
        }
        return tail.getElement();
    }

    /**
     * Adds element to the front of the list.
     * @param e element to add
     */
    public void addFirst(E e) {
        head = new Node<>(e, head);
        if (size == 0) {
            tail = head;
        }
        size++;
    }

    /**
     * Adds element to the end of the list.
     * @param e element to add
     */
    public void addLast(E e) {
        Node<E> newest = new Node<>(e, null);
        if (isEmpty()) {
            head = newest;
        } else {
            tail.setNext(newest);
        }
        tail = newest;
        size++;
    }

    /**
     * Removes and returns the first element of the list.
     * @return removed element, or null if the list is empty
     */
    public E removeFirst() {
        if (isEmpty()) {
            return null;
        }
        E answer = head.getElement();
        head = head.getNext();
        size--;
        if (size == 0) {
            tail = null;
        }
        return answer;
    }

    /**
     * Default iterator (lazy).
     * Equivalent to Python's __iter__ returning a lazy iterator.
     */
    @Override
    public java.util.Iterator<E> iterator() {
        return new SinglyLinkedListIterators.LazyIterator<>(head);
    }

    /**
     * Snapshot iterator: iterates over a snapshot of current elements.
     * @return iterator that won’t be affected by subsequent mutations
     */
    public java.util.Iterator<E> snapshotIterator() {
        return new SinglyLinkedListIterators.SnapshotIterator<>(head);
    }

    /**
     * Exposes the head node for external iterators (mirrors Python passing head).
     * Read-only usage for iteration is expected.
     * @return head node (may be null)
     */
    public Node<E> getHead() {
        return head;
    }

    /**
     * Returns a string representation of the list in the form:
     * [e1 -> e2 -> ... -> en]
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Node<E> current = head;
        boolean first = true;
        while (current != null) {
            if (!first)
                sb.append(" -> ");
            sb.append(String.valueOf(current.getElement()));
            first = false;
            current = current.getNext();
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Equality with another list: True if both contain the same elements in the same order.
     * Mirrors Python __eq__ behavior.
     * @param o other object
     * @return true if equal by element sequence; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SinglyLinkedList<?> other))
            return false;
        if (this.size != other.size)
            return false;
        Node<E> a = this.head;
        Node<?> b = other.head;
        while (a != null && b != null) {
            if (!Objects.equals(a.getElement(), b.getElement())) {
                return false;
            }
            a = a.getNext();
            b = b.getNext();
        }
        return a == null && b == null;
    }
}

