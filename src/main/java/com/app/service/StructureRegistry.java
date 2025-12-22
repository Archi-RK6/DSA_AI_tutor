package com.app.service;

import com.app.service.adapters.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StructureRegistry {

    public enum Type {
        ARRAY_LIST,
        SINGLY_LINKED_LIST,
        DOUBLY_LINKED_LIST,
        CIRCULARLY_LINKED_LIST,
        ARRAY_STACK,
        LINKED_STACK,
        ARRAY_QUEUE,
        LINKED_QUEUE,
        LINKED_POSITIONAL_LIST,
        GRAPH,
        BINARY_SEARCH_TREE,
        AVL_TREE,
        RED_BLACK_TREE,
        SPLAY_TREE,
        UNSORTED_TABLE_MAP,
        SORTED_TABLE_MAP,
        CHAIN_HASH_MAP,
        PROBE_HASH_MAP,
        UNSORTED_PRIORITY_QUEUE,
        SORTED_PRIORITY_QUEUE,
        HEAP_PRIORITY_QUEUE,
        PARTITION
    }

    public DataStructureAdapter newAdapter(Type type) {
        return switch (type) {
            case ARRAY_LIST -> new ArrayListAdapter();
            case SINGLY_LINKED_LIST -> new SinglyLinkedListAdapter();
            case DOUBLY_LINKED_LIST -> new DoublyLinkedListAdapter();
            case CIRCULARLY_LINKED_LIST -> new CircularlyLinkedListAdapter();
            case ARRAY_STACK -> new ArrayStackAdapter();
            case LINKED_STACK -> new LinkedStackAdapter();
            case ARRAY_QUEUE -> new ArrayQueueAdapter();
            case LINKED_QUEUE -> new LinkedQueueAdapter();
            case LINKED_POSITIONAL_LIST -> new LinkedPositionalListAdapter();
            case GRAPH -> new GraphAdapter();
            case BINARY_SEARCH_TREE -> new TreeMapAdapter();
            case AVL_TREE -> new AVLTreeMapAdapter();
            case RED_BLACK_TREE -> new RBTreeMapAdapter();
            case SPLAY_TREE -> new SplayTreeMapAdapter();
            case UNSORTED_TABLE_MAP -> new UnsortedTableMapAdapter();
            case SORTED_TABLE_MAP -> new SortedTableMapAdapter();
            case CHAIN_HASH_MAP -> new ChainHashMapAdapter();
            case PROBE_HASH_MAP -> new ProbeHashMapAdapter();
            case UNSORTED_PRIORITY_QUEUE -> new UnsortedPriorityQueueAdapter();
            case SORTED_PRIORITY_QUEUE -> new SortedPriorityQueueAdapter();
            case HEAP_PRIORITY_QUEUE -> new HeapPriorityQueueAdapter();
            case PARTITION -> new PartitionAdapter();
        };
    }

    public static final Map<String, Type> ALIASES = Map.ofEntries(
            Map.entry("array_list", Type.ARRAY_LIST),
            Map.entry("arraylist", Type.ARRAY_LIST),
            Map.entry("list", Type.ARRAY_LIST),
            Map.entry("sll", Type.SINGLY_LINKED_LIST),
            Map.entry("singly", Type.SINGLY_LINKED_LIST),
            Map.entry("singly_linked_list", Type.SINGLY_LINKED_LIST),
            Map.entry("dll", Type.DOUBLY_LINKED_LIST),
            Map.entry("doubly", Type.DOUBLY_LINKED_LIST),
            Map.entry("doubly_linked_list", Type.DOUBLY_LINKED_LIST),
            Map.entry("cll", Type.CIRCULARLY_LINKED_LIST),
            Map.entry("circular", Type.CIRCULARLY_LINKED_LIST),
            Map.entry("circularly_linked_list", Type.CIRCULARLY_LINKED_LIST),
            Map.entry("array_stack", Type.ARRAY_STACK),
            Map.entry("linked_stack", Type.LINKED_STACK),
            Map.entry("stack", Type.LINKED_STACK),
            Map.entry("array_queue", Type.ARRAY_QUEUE),
            Map.entry("linked_queue", Type.LINKED_QUEUE),
            Map.entry("queue", Type.LINKED_QUEUE),
            Map.entry("linked_positional_list", Type.LINKED_POSITIONAL_LIST),
            Map.entry("positional_list", Type.LINKED_POSITIONAL_LIST),
            Map.entry("lpl", Type.LINKED_POSITIONAL_LIST),
            Map.entry("positional", Type.LINKED_POSITIONAL_LIST),
            Map.entry("graph", Type.GRAPH),
            Map.entry("adjacency_graph", Type.GRAPH),
            Map.entry("bst", Type.BINARY_SEARCH_TREE),
            Map.entry("binary_search_tree", Type.BINARY_SEARCH_TREE),
            Map.entry("tree_map", Type.BINARY_SEARCH_TREE),
            Map.entry("avl", Type.AVL_TREE),
            Map.entry("avl_tree", Type.AVL_TREE),
            Map.entry("red_black_tree", Type.RED_BLACK_TREE),
            Map.entry("red-black_tree", Type.RED_BLACK_TREE),
            Map.entry("rb_tree", Type.RED_BLACK_TREE),
            Map.entry("rbt", Type.RED_BLACK_TREE),
            Map.entry("splay", Type.SPLAY_TREE),
            Map.entry("splay_tree", Type.SPLAY_TREE),
            Map.entry("unsorted_table_map", Type.UNSORTED_TABLE_MAP),
            Map.entry("unsorted_table", Type.UNSORTED_TABLE_MAP),
            Map.entry("sorted_table_map", Type.SORTED_TABLE_MAP),
            Map.entry("sorted_table", Type.SORTED_TABLE_MAP),
            Map.entry("chain_hash_map", Type.CHAIN_HASH_MAP),
            Map.entry("separate_chaining", Type.CHAIN_HASH_MAP),
            Map.entry("chaining", Type.CHAIN_HASH_MAP),
            Map.entry("probe_hash_map", Type.PROBE_HASH_MAP),
            Map.entry("open_addressing", Type.PROBE_HASH_MAP),
            Map.entry("linear_probing", Type.PROBE_HASH_MAP),
            Map.entry("unsorted_pq", Type.UNSORTED_PRIORITY_QUEUE),
            Map.entry("unsorted_priority_queue", Type.UNSORTED_PRIORITY_QUEUE),
            Map.entry("sorted_pq", Type.SORTED_PRIORITY_QUEUE),
            Map.entry("sorted_priority_queue", Type.SORTED_PRIORITY_QUEUE),
            Map.entry("priority_queue", Type.SORTED_PRIORITY_QUEUE),
            Map.entry("heap_pq", Type.HEAP_PRIORITY_QUEUE),
            Map.entry("heap_priority_queue", Type.HEAP_PRIORITY_QUEUE),
            Map.entry("partition", Type.PARTITION),
            Map.entry("disjoint_set", Type.PARTITION),
            Map.entry("union_find", Type.PARTITION)
    );

    public Type parse(String raw) {
        if (raw == null) throw new IllegalArgumentException("type is required");
        String k = raw.trim().toLowerCase();
        if (ALIASES.containsKey(k)) return ALIASES.get(k);
        return Type.valueOf(raw.trim().toUpperCase());
    }
}
