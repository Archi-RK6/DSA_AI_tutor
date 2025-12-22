package com.app.linear.implementations;
import com.app.linear.interfaces.Stack;

public class ArrayStack<E> implements Stack<E> {
    public static final int CAPACITY = 1000;
    private final E[] data;
    private int t = -1;

    @SuppressWarnings("unchecked")
    public ArrayStack() {
        this.data = (E[]) new Object[CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        this.data = (E[]) new Object[capacity];
    }

    @Override
    public int size() {
        return t + 1;
    }

    @Override
    public boolean isEmpty() {
        return t == -1;
    }

    @Override
    public void push(E e) {
        if (size() == data.length) {
            throw new IllegalStateException("Stack is full");
        }
        data[++t] = e;
    }

    @Override
    public E top() {
        if (isEmpty()) return null;
        return data[t];
    }

    @Override
    public E pop() {
        if (isEmpty()) return null;
        E answer = data[t];
        data[t] = null; // help GC
        t--;
        return answer;
    }
}
