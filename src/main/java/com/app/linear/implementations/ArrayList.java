package com.app.linear.implementations;

import com.app.linear.interfaces.List;
import com.app.linear.iterators.ArrayListIterators;

import java.util.Iterator;
import java.util.Objects;


public class ArrayList<E> implements List<E>, Iterable<E> {

    public static final int CAPACITY = 16; // default array capacity
    private E[] data;                      // generic array used for storage
    private int size = 0;                  // current number of elements

    @SuppressWarnings("unchecked")
    public ArrayList() {
        this.data = (E[]) new Object[CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public ArrayList(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public ArrayList(ArrayList<? extends E> other) {
        this.data = (E[]) new Object[Math.max(CAPACITY, other.size())];
        for (int i = 0; i < other.size(); i++) {
            this.data[i] = other.get(i);
        }
        this.size = other.size();
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public E get(int i) throws IndexOutOfBoundsException {
        checkIndex(i, size);
        return data[i];
    }

    public E set(int i, E e) throws IndexOutOfBoundsException {
        checkIndex(i, size);
        E temp = data[i];
        data[i] = e;
        return temp;
    }

    public void add(int i, E e) throws IndexOutOfBoundsException {
        checkIndex(i, size + 1);
        if (size == data.length)
            resize(2 * data.length);
        for (int k = size - 1; k >= i; k--)
            data[k + 1] = data[k];
        data[i] = e;
        size++;
    }

    public E remove(int i) throws IndexOutOfBoundsException {
        checkIndex(i, size);
        E temp = data[i];
        for (int k = i; k < size - 1; k++)
            data[k] = data[k + 1];
        data[size - 1] = null;
        size--;
        return temp;
    }

    protected void checkIndex(int i, int n) throws IndexOutOfBoundsException {
        if (i < 0 || i >= n)
            throw new IndexOutOfBoundsException("Illegal index: " + i);
    }

    @SuppressWarnings("unchecked")
    protected void resize(int capacity) {
        E[] temp = (E[]) new Object[capacity];
        for (int k = 0; k < size; k++)
            temp[k] = data[k];
        data = temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int k = 0; k < size; k++) {
            if (k > 0) sb.append(", ");
            sb.append(String.valueOf(data[k]));
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ArrayList<?> other)) return false;
        if (this.size != other.size) return false;
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(this.data[i], other.data[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < size; i++) {
            h = 31 * h + Objects.hashCode(data[i]);
        }
        return h;
    }

    /** Default lazy iterator. */
    @Override
    public Iterator<E> iterator() {
        return new ArrayListIterators.LazyIterator<>(this);
    }

    /** Snapshot iterator (iterates over a static snapshot). */
    public Iterator<E> snapshotIterator() {
        return new ArrayListIterators.SnapshotIterator<>(this);
    }
}
