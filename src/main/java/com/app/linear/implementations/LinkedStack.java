package com.app.linear.implementations;

import java.util.Iterator;
import com.app.linear.interfaces.Stack;
import com.app.linear.iterators.LinkedStackIterators;

public class LinkedStack<E> implements Stack<E>, Iterable<E> {
    private final SinglyLinkedList<E> list = new SinglyLinkedList<>();

    public LinkedStack() { }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void push(E element) {
        list.addFirst(element);
    }

    @Override
    public E top() {
        return list.first();
    }

    @Override
    public E pop() {
        return list.removeFirst();
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedStackIterators.LazyIterator<>(list);
    }

    public Iterator<E> snapshotIterator() {
        return new LinkedStackIterators.SnapshotIterator<>(list);
    }
}
