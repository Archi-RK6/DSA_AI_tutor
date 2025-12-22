package nonlinear.tests;

import com.app.nonlinear.implementations.AVLTreeMap;
import com.app.nonlinear.interfaces.Entry;

public class TestAVLTreeMap {

    private static <K, V> String fmt(Entry<K, V> e) {
        return (e == null) ? "null" : "Key: " + e.getKey() + ", Value: " + e.getValue();
    }

    public static void test() {
        System.out.println("Creating AVLTreeMap...");
        AVLTreeMap<Integer, String> avl = new AVLTreeMap<>();

        System.out.println("\nInserting entries:");
        avl.put(50, "A");
        avl.put(30, "B");
        avl.put(70, "C");
        avl.put(20, "D");
        avl.put(40, "E");
        avl.put(60, "F");
        avl.put(80, "G");

        System.out.println("\nRetrieving entries:");
        System.out.println("Get key 30: " + avl.get(30));
        System.out.println("Get key 70: " + avl.get(70));
        System.out.println("Get key 100 (not present): " + avl.get(100));

        System.out.println("\nCurrent size of map: " + avl.size());

        System.out.println("\nEntries in AVL Tree (in sorted order):");
        for (Entry<Integer, String> e : avl.entrySet()) {
            System.out.println(fmt(e));
        }

        System.out.println("\nTesting floor and ceiling:");
        System.out.println("Floor entry of 65: " + fmt(avl.floorEntry(65)));
        System.out.println("Ceiling entry of 65: " + fmt(avl.ceilingEntry(65)));

        System.out.println("\nRemoving entries:");
        System.out.println("Remove key 30: " + avl.remove(30));
        System.out.println("Remove key 70: " + avl.remove(70));

        System.out.println("\nEntries after deletions:");
        for (Entry<Integer, String> e : avl.entrySet()) {
            System.out.println(fmt(e));
        }

        System.out.println("\nFinal size of map: " + avl.size());
        System.out.println("=== End ===");
    }
}

