package com.app.nonlinear.tests;

import com.app.nonlinear.implementations.AdjacencyMapGraph;
import com.app.nonlinear.utils.GraphAlgorithms;
import com.app.nonlinear.interfaces.Vertex;

import java.util.ArrayList;
import java.util.List;

public class TestAdjacencyMapGraph {

    public static void test() {
        System.out.println("Creating undirected graph:");
        AdjacencyMapGraph<String, String> graph = new AdjacencyMapGraph<>(false);

        Vertex<String> v1 = graph.insertVertex("A");
        Vertex<String> v2 = graph.insertVertex("B");
        Vertex<String> v3 = graph.insertVertex("C");

        graph.insertEdge(v1, v2, "AB");
        graph.insertEdge(v2, v3, "BC");
        graph.insertEdge(v3, v1, "CA");

        System.out.println(graph);

        graph = new AdjacencyMapGraph<>(false);

        v1 = graph.insertVertex("A");
        v2 = graph.insertVertex("B");
        v3 = graph.insertVertex("C");

        graph.insertEdge(v1, v2, "AB");
        graph.insertEdge(v2, v3, "BC");
        graph.insertEdge(v3, v1, "CA");

        System.out.println(graph);

        System.out.println("\nCreating directed graph:");
        AdjacencyMapGraph<String, Integer> digraph = new AdjacencyMapGraph<>(true);

        List<Vertex<String>> vertices = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            vertices.add(digraph.insertVertex("V" + i));
        }
        for (int i = 0; i < vertices.size() - 1; i++) {
            digraph.insertEdge(vertices.get(i), vertices.get(i + 1), 1);
        }

        System.out.println(digraph);

        GraphAlgorithms.transitiveClosure(digraph);
    }
}
