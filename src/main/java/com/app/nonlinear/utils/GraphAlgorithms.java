package com.app.nonlinear.utils;

import com.app.nonlinear.implementations.*;
import com.app.nonlinear.interfaces.*;

import java.util.HashSet;
import java.util.Set;

public class GraphAlgorithms {

    public static <V, E> void DFS(Graph<V, E> g, Vertex<V> u, Set<Vertex<V>> known, com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<E>> forest) {
        known.add(u);
        for (Edge<E> e : g.outgoingEdges(u)) {
            Vertex<V> v = g.opposite(u, e);
            if (!known.contains(v)) {
                forest.put(v, e);
                DFS(g, v, known, forest);
            }
        }
    }

    public static <V, E> PositionalList<Edge<E>> constructPath(Graph<V, E> graph, Vertex<V> u, Vertex<V> v, com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<E>> forest) {
        LinkedPositionalList<Edge<E>> path = new LinkedPositionalList<>();
        if (forest.get(v) != null) {
            Vertex<V> walk = v;
            while (walk != u) {
                Edge<E> edge = forest.get(walk);
                path.addFirst(edge);
                walk = graph.opposite(walk, edge);
            }
        }
        return path;
    }

    public static <V, E> com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<E>> DFSComplete(Graph<V, E> g) {
        Set<Vertex<V>> known = new HashSet<>();
        ProbeHashMap<Vertex<V>, Edge<E>> forest = new ProbeHashMap<>();
        for (Vertex<V> u : g.vertices()) {
            if (!known.contains(u)) {
                DFS(g, u, known, forest);
            }
        }
        return forest;
    }

    public static <V, E> void BFS(Graph<V, E> graph, Vertex<V> s, Set<Vertex<V>> known, com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<E>> forest) {
        LinkedPositionalList<Vertex<V>> level = new LinkedPositionalList<>();
        known.add(s);
        level.addLast(s);
        while (!level.isEmpty()) {
            LinkedPositionalList<Vertex<V>> nextLevel = new LinkedPositionalList<>();
            for (Vertex<V> u : level) {
                for (Edge<E> e : graph.outgoingEdges(u)) {
                    Vertex<V> v = graph.opposite(u, e);
                    if (!known.contains(v)) {
                        known.add(v);
                        forest.put(v, e);
                        nextLevel.addLast(v);
                    }
                }
            }
            level = nextLevel;
        }
    }

    public static <V, E> com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<E>> bfsComplete(Graph<V, E> g) {
        ProbeHashMap<Vertex<V>, Edge<E>> forest = new ProbeHashMap<>();
        Set<Vertex<V>> known = new HashSet<>();
        for (Vertex<V> u : g.vertices()) {
            if (!known.contains(u)) {
                BFS(g, u, known, forest);
            }
        }
        return forest;
    }

    public static <V> void transitiveClosure(Graph<V, Integer> g) {
        for (Vertex<V> k : g.vertices()) {
            for (Vertex<V> i : g.vertices()) {
                if (i != k && g.getEdge(i, k) != null) {
                    for (Vertex<V> j : g.vertices()) {
                        if (i != j && j != k && g.getEdge(k, j) != null) {
                            if (g.getEdge(i, j) == null) {
                                g.insertEdge(i, j, null);
                            }
                        }
                    }
                }
            }
        }
    }

    public static <V, E> PositionalList<Vertex<V>> topologicalSort(Graph<V, E> g) {
        LinkedPositionalList<Vertex<V>> topo = new LinkedPositionalList<>();
        LinkedStack<Vertex<V>> ready = new LinkedStack<>();
        ProbeHashMap<Vertex<V>, Integer> inCount = new ProbeHashMap<>();

        for (Vertex<V> u : g.vertices()) {
            int deg = g.inDegree(u);
            inCount.put(u, deg);
            if (deg == 0) ready.push(u);
        }

        while (!ready.isEmpty()) {
            Vertex<V> u = ready.pop();
            topo.addLast(u);
            for (Edge<E> e : g.outgoingEdges(u)) {
                Vertex<V> v = g.opposite(u, e);
                int c = inCount.get(v);
                inCount.put(v, c - 1);
                if (c - 1 == 0) ready.push(v);
            }
        }
        return topo;
    }

    public static <V> com.app.nonlinear.interfaces.Map<Vertex<V>, Double> shortestPathLengths(Graph<V, ? extends Number> g, Vertex<V> src) {
        ProbeHashMap<Vertex<V>, Double> d = new ProbeHashMap<>();
        ProbeHashMap<Vertex<V>, Double> cloud = new ProbeHashMap<>();
        HeapAdaptablePriorityQueue<Double, Vertex<V>> pq = new HeapAdaptablePriorityQueue<>();
        ProbeHashMap<Vertex<V>, Entry<Double, Vertex<V>>> pqTokens = new ProbeHashMap<>();

        for (Vertex<V> v : g.vertices()) {
            double dist = (v == src) ? 0.0 : Double.POSITIVE_INFINITY;
            d.put(v, dist);
            Entry<Double, Vertex<V>> token = pq.insert(dist, v);
            pqTokens.put(v, token);
        }

        while (!pq.isEmpty()) {
            Entry<Double, Vertex<V>> entry = pq.removeMin();
            double key = entry.getKey();
            Vertex<V> u = entry.getValue();
            cloud.put(u, key);
            pqTokens.remove(u);

            for (Edge<? extends Number> e : g.outgoingEdges(u)) {
                Vertex<V> v = g.opposite(u, (Edge) e);
                if (cloud.get(v) == null) {
                    double wgt = e.getElement().doubleValue();
                    double du = d.get(u);
                    if (du + wgt < d.get(v)) {
                        d.put(v, du + wgt);
                        pq.remove(pqTokens.get(v));
                        pqTokens.put(v, pq.insert(d.get(v), v));
                    }
                }
            }
        }
        return cloud;
    }

    public static <V> com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<Integer>> spTree(Graph<V, Integer> g, Vertex<V> s, com.app.nonlinear.interfaces.Map<Vertex<V>, Double> d) {
        ProbeHashMap<Vertex<V>, Edge<Integer>> tree = new ProbeHashMap<>();
        for (Vertex<V> v : d.keySet()) {
            if (v != s) {
                for (Edge<Integer> e : g.incomingEdges(v)) {
                    Vertex<V> u = g.opposite(v, e);
                    int w = e.getElement();
                    Double dv = d.get(v);
                    Double du = d.get(u);
                    if (du != null && dv != null && Math.abs(dv - (du + w)) < 1e-9) {
                        tree.put(v, e);
                        break;
                    }
                }
            }
        }
        return tree;
    }

    public static <V, E> java.util.List<PositionalList<Vertex<V>>> stronglyConnectedComponents(Graph<V, E> g) {
        HashSet<Vertex<V>> visited = new HashSet<>();
        LinkedStack<Vertex<V>> finishStack = new LinkedStack<>();

        java.util.function.Consumer<Vertex<V>> dfsFirst = new java.util.function.Consumer<Vertex<V>>() {
            @Override public void accept(Vertex<V> u) {
                visited.add(u);
                for (Edge<E> e : g.outgoingEdges(u)) {
                    Vertex<V> v = g.opposite(u, e);
                    if (!visited.contains(v)) this.accept(v);
                }
                finishStack.push(u);
            }
        };

        for (Vertex<V> u : g.vertices()) if (!visited.contains(u)) dfsFirst.accept(u);

        AdjacencyMapGraph<V, E> gt = new AdjacencyMapGraph<>(true);
        ProbeHashMap<Vertex<V>, Vertex<V>> vertexMap = new ProbeHashMap<>();
        for (Vertex<V> v : g.vertices()) vertexMap.put(v, gt.insertVertex(v.getElement()));
        for (Edge<E> e : g.edges()) {
            Vertex<V>[] ends = g.endVertices(e);
            Vertex<V> u = ends[0], v = ends[1];
            gt.insertEdge(vertexMap.get(v), vertexMap.get(u), e.getElement());
        }

        visited.clear();
        java.util.List<PositionalList<Vertex<V>>> result = new java.util.ArrayList<>();

        while (!finishStack.isEmpty()) {
            Vertex<V> u = finishStack.pop();
            Vertex<V> mapped = vertexMap.get(u);
            if (!visited.contains(mapped)) {
                LinkedPositionalList<Vertex<V>> component = new LinkedPositionalList<>();
                LinkedStack<Vertex<V>> stack = new LinkedStack<>();
                stack.push(mapped);
                visited.add(mapped);
                while (!stack.isEmpty()) {
                    Vertex<V> cur = stack.pop();
                    component.addLast(cur);
                    for (Edge<E> e : gt.outgoingEdges(cur)) {
                        Vertex<V> nb = gt.opposite(cur, e);
                        if (!visited.contains(nb)) {
                            visited.add(nb);
                            stack.push(nb);
                        }
                    }
                }
                result.add(component);
            }
        }
        return result;
    }

    public static <V> com.app.nonlinear.interfaces.Map<Vertex<V>, Edge<Integer>> primMst(Graph<V, Integer> g) {
        ProbeHashMap<Vertex<V>, Double> d = new ProbeHashMap<>();
        ProbeHashMap<Vertex<V>, Edge<Integer>> connect = new ProbeHashMap<>();
        HeapAdaptablePriorityQueue<Double, Vertex<V>> pq = new HeapAdaptablePriorityQueue<>();
        ProbeHashMap<Vertex<V>, Entry<Double, Vertex<V>>> pqTokens = new ProbeHashMap<>();
        ProbeHashMap<Vertex<V>, Edge<Integer>> mst = new ProbeHashMap<>();

        Vertex<V> start = null;
        for (Vertex<V> v : g.vertices()) { start = v; break; }

        for (Vertex<V> v : g.vertices()) {
            double dist = (v == start) ? 0.0 : Double.POSITIVE_INFINITY;
            d.put(v, dist);
            pqTokens.put(v, pq.insert(dist, v));
        }

        while (!pq.isEmpty()) {
            Entry<Double, Vertex<V>> entry = pq.removeMin();
            Vertex<V> u = entry.getValue();
            pqTokens.remove(u);
            if (connect.get(u) != null) {
                mst.put(u, connect.get(u));
            }
            for (Edge<Integer> e : g.incomingEdges(u)) {
                Vertex<V> v = g.opposite(u, e);
                if (pqTokens.get(v) != null) {
                    double w = e.getElement();
                    if (w < d.get(v)) {
                        d.put(v, w);
                        connect.put(v, e);
                        pq.remove(pqTokens.get(v));
                        pqTokens.put(v, pq.insert(d.get(v), v));
                    }
                }
            }
        }
        return mst;
    }

    public static <V> PositionalList<Edge<Integer>> kruskalMst(Graph<V, Integer> g) {
        LinkedPositionalList<Edge<Integer>> tree = new LinkedPositionalList<>();
        HeapPriorityQueue<Integer, Edge<Integer>> pq = new HeapPriorityQueue<>();
        Partition<V> forest = new Partition<>();
        ProbeHashMap<Vertex<V>, Object> positions = new ProbeHashMap<>();

        for (Vertex<V> v : g.vertices()) {
            positions.put(v, forest.makeCluster((V) v));
        }

        for (Edge<Integer> e : g.edges()) {
            pq.insert(e.getElement(), e);
        }

        int size = g.numVertices();

        while (tree.size() != size - 1 && !pq.isEmpty()) {
            Entry<Integer, Edge<Integer>> entry = pq.removeMin();
            Edge<Integer> edge = entry.getValue();
            Vertex<V>[] ends = g.endVertices(edge);
            Vertex<V> u = ends[0], v = ends[1];
            Position<V> a = forest.find((Position<V>) positions.get(u));
            Position<V> b = forest.find((Position<V>) positions.get(v));
            if (!a.equals(b)) {
                tree.addLast(edge);
                forest.union(a, b);
            }
        }
        return tree;
    }
}
