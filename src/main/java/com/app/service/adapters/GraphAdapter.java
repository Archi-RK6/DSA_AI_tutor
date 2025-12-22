package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.nonlinear.implementations.AdjacencyMapGraph;
import com.app.nonlinear.interfaces.Edge;
import com.app.nonlinear.interfaces.Vertex;
import com.app.service.DataStructureAdapter;

import java.util.*;
import java.util.stream.Collectors;

public class GraphAdapter implements DataStructureAdapter {

    private final AdjacencyMapGraph<String, String> graph = new AdjacencyMapGraph<>(false);

    private final Map<String, Vertex<String>> vertices = new HashMap<>();

    @Override
    public List<Step> operate(OperationRequest r) {
        final String op = safe(r.getOperation());
        final List<Step> steps = new ArrayList<>();

        switch (op) {

            case "addVertex" -> {
                String label = mustNonEmpty(r.getValue(), "value(vertex)");
                if (!vertices.containsKey(label)) {
                    Vertex<String> v = graph.insertVertex(label);
                    vertices.put(label, v);
                }
                steps.add(new Step("addVertex", snapshot(), null, "Added vertex " + label));
            }

            case "removeVertex" -> {
                String label = mustNonEmpty(r.getValue(), "value(vertex)");
                Vertex<String> v = requireVertex(label);
                graph.removeVertex(v);
                vertices.remove(label);
                steps.add(new Step("removeVertex", snapshot(), null, "Removed vertex " + label));
            }

            case "addEdge" -> {
                String[] p = parseTwoOrThree(r.getValue(), "value('u,v[,w]')");
                String uLabel = p[0], vLabel = p[1];
                String weight  = (p.length >= 3 ? p[2] : (uLabel + "-" + vLabel));
                Vertex<String> u = requireVertex(uLabel);
                Vertex<String> v = requireVertex(vLabel);
                if (graph.getEdge(u, v) != null) {
                    steps.add(new Step("noop", snapshot(), null,
                            "Edge already exists between " + uLabel + " and " + vLabel));
                } else {
                    graph.insertEdge(u, v, weight);
                    steps.add(new Step("addEdge", snapshot(), null,
                            "Added edge " + uLabel + " - " + vLabel + (p.length >= 3 ? (" (w=" + weight + ")") : "")));
                }
            }

            case "removeEdge" -> {
                String[] uv = parseTwo(r.getValue(), "value('u,v')");
                Vertex<String> u = requireVertex(uv[0]);
                Vertex<String> v = requireVertex(uv[1]);
                Edge<String> e = graph.getEdge(u, v);
                if (e == null)
                    throw new IllegalArgumentException("Edge not found between " + uv[0] + " and " + uv[1]);
                graph.removeEdge(e);
                steps.add(new Step("removeEdge", snapshot(), null, "Removed edge " + uv[0] + " - " + uv[1]));
            }

            case "outDegree" -> {
                String label = mustNonEmpty(r.getValue(), "value(vertex)");
                Vertex<String> v = requireVertex(label);
                int d = graph.outDegree(v);
                steps.add(new Step("outDegree", snapshot(), null, "outDegree(" + label + ") = " + d));
            }

            case "inDegree" -> {
                String label = mustNonEmpty(r.getValue(), "value(vertex)");
                Vertex<String> v = requireVertex(label);
                int d = graph.inDegree(v);
                steps.add(new Step("inDegree", snapshot(), null, "inDegree(" + label + ") = " + d));
            }

            case "numVertices" -> {
                int n = graph.numVertices();
                steps.add(new Step("numVertices", snapshot(), null, "numVertices = " + n));
            }

            case "numEdges" -> {
                int m = graph.numEdges();
                steps.add(new Step("numEdges", snapshot(), null, "numEdges = " + m));
            }

            default -> throw new IllegalArgumentException("Unsupported operation for GRAPH: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> lines = new ArrayList<>();
        for (Vertex<String> v : graph.vertices()) {
            String name = v.getElement();
            List<String> neighbors = new ArrayList<>();
            for (Edge<String> e : graph.outgoingEdges(v)) {
                Vertex<String> w = graph.opposite(v, e);
                neighbors.add(w.getElement());
            }
            String adj = neighbors.stream().sorted().collect(Collectors.joining(", "));
            lines.add(adj.isEmpty() ? name : (name + ": " + adj));
        }
        Collections.sort(lines);
        return lines;
    }


    private String safe(String s) { return s == null ? "" : s.trim(); }

    private String mustNonEmpty(String s, String what) {
        String v = safe(s);
        if (v.isEmpty()) throw new IllegalArgumentException("Required: " + what);
        return v;
    }

    private String[] parseTwo(String s, String what) {
        String v = mustNonEmpty(s, what);
        String[] parts = v.split("[,;\\s]+");
        if (parts.length < 2) throw new IllegalArgumentException("Need two ids in " + what);
        return new String[]{parts[0], parts[1]};
    }

    private String[] parseTwoOrThree(String s, String what) {
        String v = mustNonEmpty(s, what);
        String[] parts = v.split("[,;\\s]+");
        if (parts.length < 2) throw new IllegalArgumentException("Need at least two ids in " + what);
        if (parts.length == 2) return new String[]{parts[0], parts[1]};
        return new String[]{parts[0], parts[1], parts[2]};
    }

    private Vertex<String> requireVertex(String label) {
        Vertex<String> v = vertices.get(label);
        if (v == null) throw new IllegalArgumentException("Vertex not found: " + label + ". Use addVertex first.");
        return v;
    }
}
