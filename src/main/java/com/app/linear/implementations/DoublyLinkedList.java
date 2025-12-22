package com.app.linear.implementations;

import java.util.Iterator;
import java.util.Objects;
import com.app.linear.iterators.DoublyLinkedListIterators;

/**
 * A basic doubly linked list with header/trailer sentinels.
 */
public class DoublyLinkedList<E> implements Iterable<E> {

    // ---------------- nested Node class ----------------
    public static class Node<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E element, Node<E> prev, Node<E> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }

        public E getElement() { return element; }
        public Node<E> getPrev() { return prev; }
        public Node<E> getNext() { return next; }
        public void setPrev(Node<E> p) { prev = p; }
        public void setNext(Node<E> n) { next = n; }
    }
    // ----------- end of nested Node class -----------

    private final Node<E> header;
    private final Node<E> trailer;
    private int size = 0;

    public DoublyLinkedList() {
        header = new Node<>(null, null, null);
        trailer = new Node<>(null, header, null);
        header.setNext(trailer);
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public E first() {
        if (isEmpty()) return null;
        return header.getNext().getElement();
    }

    public E last() {
        if (isEmpty()) return null;
        return trailer.getPrev().getElement();
    }

    public void addFirst(E e) { addBetween(e, header, header.getNext()); }
    public void addLast(E e) { addBetween(e, trailer.getPrev(), trailer); }

    public E removeFirst() {
        if (isEmpty()) return null;
        return remove(header.getNext());
    }

    public E removeLast() {
        if (isEmpty()) return null;
        return remove(trailer.getPrev());
    }

    private void addBetween(E e, Node<E> predecessor, Node<E> successor) {
        Node<E> newest = new Node<>(e, predecessor, successor);
        predecessor.setNext(newest);
        successor.setPrev(newest);
        size++;
    }

    private E remove(Node<E> node) {
        Node<E> predecessor = node.getPrev();
        Node<E> successor = node.getNext();
        predecessor.setNext(successor);
        successor.setPrev(predecessor);
        size--;
        return node.getElement();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> cur = header.getNext();
        boolean firstPrinted = false;
        while (cur != trailer) {
            if (firstPrinted) sb.append(" <-> ");
            sb.append(cur.getElement());
            firstPrinted = true;
            cur = cur.getNext();
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoublyLinkedList<?> other)) return false;
        if (this.size != other.size) return false;

        Node<E> a = this.header.getNext();
        Node<?> b = other.header.getNext();
        while (a != this.trailer && b != other.trailer) {
            if (!Objects.equals(a.getElement(), b.getElement())) return false;
            a = a.getNext();
            b = b.getNext();
        }
        return a == this.trailer && b == other.trailer;
    }

    @Override
    public int hashCode() {
        int h = 1;
        Node<E> cur = header.getNext();
        while (cur != trailer) {
            h = 31 * h + Objects.hashCode(cur.getElement());
            cur = cur.getNext();
        }
        return h;
    }

    /** Default iterator (lazy). */
    @Override
    public Iterator<E> iterator() {
        return new DoublyLinkedListIterators.LazyIterator<>(header, trailer);
    }

    /** Snapshot iterator (immutable). */
    public Iterator<E> snapshotIterator() {
        return new DoublyLinkedListIterators.SnapshotIterator<>(header, trailer);
    }
}
