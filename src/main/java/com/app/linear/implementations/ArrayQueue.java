package com.app.linear.implementations;

import java.util.Iterator;
import com.app.linear.interfaces.Queue;
import com.app.linear.iterators.ArrayQueueIterators;

/**
 * Implementation of the queue ADT using a fixed-length circular array.
 * Provides default (lazy) and snapshot iterators.
 */
public class ArrayQueue<E> implements Queue<E>, Iterable<E> {

    public static final int CAPACITY = 1000; // default array capacity

    private final E[] data;  // storage buffer
    private int f = 0;       // index of the front element
    private int sz = 0;      // current number of elements

    @SuppressWarnings("unchecked")
    public ArrayQueue() {
        this.data = (E[]) new Object[CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public ArrayQueue(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    @Override
    public int size() {
        return sz;
    }

    @Override
    public boolean isEmpty() {
        return sz == 0;
    }

    @Override
    public void enqueue(E e) throws IllegalStateException {
        if (sz == data.length) throw new IllegalStateException("Queue is full");
        int avail = (f + sz) % data.length; // modular write position
        data[avail] = e;
        sz++;
    }

    @Override
    public E first() {
        if (isEmpty()) return null;
        return data[f];
    }

    @Override
    public E dequeue() {
        if (isEmpty()) return null;
        E answer = data[f];
        data[f] = null;                 // help garbage collection
        f = (f + 1) % data.length;     // advance front
        sz--;
        return answer;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayQueueIterators.LazyIterator<>(this);
    }

    public Iterator<E> snapshotIterator() {
        return new ArrayQueueIterators.SnapshotIterator<>(this);
    }

    /** Exposed for iterator utilities to read the backing buffer. */
    public E[] buffer() { return data; }

    /** Exposed for iterator utilities to read the front index. */
    public int frontIndex() { return f; }

    /** Exposed for iterator utilities to read the current size. */
    public int count() { return sz; }
}
