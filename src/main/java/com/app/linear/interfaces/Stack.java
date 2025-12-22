package com.app.linear.interfaces;

/**
 * A collection of objects that are inserted and removed according to the last-in
 * first-out principle. Although similar in purpose, this interface differs from
 * java.util.Stack.
 *
 * @author Michael T. Goodrich
 * @author Roberto Tamassia
 * @author Michael H. Goldwasser
 */
public interface Stack<E> {
    /** Returns the number of elements in the stack. */
    int size();

    /** Tests whether the stack is empty. */
    boolean isEmpty();

    /** Inserts an element at the top of the stack. */
    void push(E e);

    /** Returns, but does not remove, the element at the top of the stack. */
    E top();

    /** Removes and returns the top element from the stack. */
    E pop();
}
