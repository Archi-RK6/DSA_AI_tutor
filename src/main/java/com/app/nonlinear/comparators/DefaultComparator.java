package nonlinear.comparators;

import java.util.Comparator;

/**
 * Default comparator that assumes natural ordering of elements.
 */
public class DefaultComparator<E> implements Comparator<E> {
    @SuppressWarnings("unchecked")
    public int compare(E a, E b) throws ClassCastException {
        return ((Comparable<E>) a).compareTo(b);
    }
}
