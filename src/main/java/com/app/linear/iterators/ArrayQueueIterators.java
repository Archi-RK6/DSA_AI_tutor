package com.app.linear.iterators;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.app.linear.implementations.ArrayQueue;

/**
 * Iterator utilities for ArrayQueue:
 * - LazyIterator: reads directly from the live circular buffer
 * - SnapshotIterator: iterates over a captured snapshot
 */
public class ArrayQueueIterators {

    /** Lazy iterator over the live queue contents (front to rear). */
    public static class LazyIterator<E> implements Iterator<E> {
        private final ArrayQueue<E> queue;
        private int index = 0;

        public LazyIterator(ArrayQueue<E> queue) {
            this.queue = queue;
        }

        @Override
        public boolean hasNext() {
            return index < queue.count();
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            E[] buf = queue.buffer();
            int pos = (queue.frontIndex() + index) % buf.length;
            E result = buf[pos];
            index++;
            return result;
        }
    }

    /** Snapshot iterator that iterates over a captured list of elements. */
    public static class SnapshotIterator<E> implements Iterator<E> {
        private final List<E> snapshot = new ArrayList<>();
        private int i = 0;

        public SnapshotIterator(ArrayQueue<E> queue) {
            E[] buf = queue.buffer();
            int front = queue.frontIndex();
            int n = queue.count();
            for (int k = 0; k < n; k++) {
                int pos = (front + k) % buf.length;
                snapshot.add(buf[pos]);
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

