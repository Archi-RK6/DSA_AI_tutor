package com.app.nonlinear.iterators;

import com.app.nonlinear.interfaces.Position;
import com.app.nonlinear.interfaces.Tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class AbstractTreeIterators {

    public static class ElementIterator<E> implements Iterator<E> {
        private final Iterator<Position<E>> posIterator;

        public ElementIterator(Iterable<Position<E>> positions) {
            this.posIterator = positions.iterator();
        }

        @Override
        public boolean hasNext() {
            return posIterator.hasNext();
        }

        @Override
        public E next() {
            return posIterator.next().getElement();
        }

        @Override
        public void remove() {
            posIterator.remove();
        }
    }

    public static class PreorderIterator<E> implements Iterator<Position<E>> {
        private final List<Position<E>> snapshot = new ArrayList<>();
        private int index = 0;

        public PreorderIterator(Tree<E> tree) {
            if (!tree.isEmpty()) {
                preorder(tree, tree.root());
            }
        }

        private void preorder(Tree<E> tree, Position<E> p) {
            snapshot.add(p);
            for (Position<E> c : tree.children(p)) {
                preorder(tree, c);
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public Position<E> next() {
            return snapshot.get(index++);
        }
    }

    public static class PostorderIterator<E> implements Iterator<Position<E>> {
        private final List<Position<E>> snapshot = new ArrayList<>();
        private int index = 0;

        public PostorderIterator(Tree<E> tree) {
            if (!tree.isEmpty()) {
                postorder(tree, tree.root());
            }
        }

        private void postorder(Tree<E> tree, Position<E> p) {
            for (Position<E> c : tree.children(p)) {
                postorder(tree, c);
            }
            snapshot.add(p);
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public Position<E> next() {
            return snapshot.get(index++);
        }
    }

    public static class BreadthFirstIterator<E> implements Iterator<Position<E>> {
        private final List<Position<E>> snapshot = new ArrayList<>();
        private int index = 0;

        public BreadthFirstIterator(Tree<E> tree) {
            if (!tree.isEmpty()) {
                Deque<Position<E>> q = new ArrayDeque<>();
                q.addLast(tree.root());
                while (!q.isEmpty()) {
                    Position<E> p = q.removeFirst();
                    snapshot.add(p);
                    for (Position<E> c : tree.children(p)) {
                        q.addLast(c);
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public Position<E> next() {
            return snapshot.get(index++);
        }
    }
}
