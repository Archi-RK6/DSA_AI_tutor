package com.app.nonlinear.implementations;

import com.app.nonlinear.interfaces.Edge;
import com.app.nonlinear.interfaces.Graph;
import com.app.nonlinear.interfaces.Map;
import com.app.nonlinear.interfaces.Position;
import com.app.nonlinear.interfaces.PositionalList;
import com.app.nonlinear.interfaces.Vertex;

import java.util.ArrayList;

public class AdjacencyMapGraph<V, E> implements Graph<V, E> {

    private class InnerVertex implements Vertex<V> {
        private V element;
        private Position<Vertex<V>> pos;
        private Map<Vertex<V>, Edge<E>> outgoing, incoming;
        public InnerVertex(V elem, boolean graphIsDirected) {
            element = elem;
            outgoing = new ProbeHashMap<>();
            if (graphIsDirected)
                incoming = new ProbeHashMap<>();
            else
                incoming = outgoing;
        }
        public V getElement() { return element; }
        public void setPosition(Position<Vertex<V>> p) { pos = p; }
        public Position<Vertex<V>> getPosition() { return pos; }
        public Map<Vertex<V>, Edge<E>> getOutgoing() { return outgoing; }
        public Map<Vertex<V>, Edge<E>> getIncoming() { return incoming; }
    }

    private class InnerEdge implements Edge<E> {
        private E element;
        private Position<Edge<E>> pos;
        private Vertex<V>[] endpoints;
        @SuppressWarnings("unchecked")
        public InnerEdge(Vertex<V> u, Vertex<V> v, E elem) {
            element = elem;
            endpoints = (Vertex<V>[]) new Vertex[]{u, v};
        }
        public E getElement() { return element; }
        public Vertex<V>[] getEndpoints() { return endpoints; }
        public void setPosition(Position<Edge<E>> p) { pos = p; }
        public Position<Edge<E>> getPosition() { return pos; }
    }

    private boolean isDirected;
    private LinkedPositionalList<Vertex<V>> vertices = new LinkedPositionalList<>();
    private LinkedPositionalList<Edge<E>> edges = new LinkedPositionalList<>();

    public AdjacencyMapGraph(boolean directed) { isDirected = directed; }

    public int numVertices() { return vertices.size(); }

    public Iterable<Vertex<V>> vertices() { return (Iterable<Vertex<V>>) vertices; }

    public int numEdges() { return edges.size(); }

    public Iterable<Edge<E>> edges() { return (Iterable<Edge<E>>) edges; }

    public int outDegree(Vertex<V> v) {
        InnerVertex vert = validate(v);
        return vert.getOutgoing().size();
    }

    public Iterable<Edge<E>> outgoingEdges(Vertex<V> v) {
        InnerVertex vert = validate(v);
        return vert.getOutgoing().values();
    }

    public int inDegree(Vertex<V> v) {
        InnerVertex vert = validate(v);
        return vert.getIncoming().size();
    }

    public Iterable<Edge<E>> incomingEdges(Vertex<V> v) {
        InnerVertex vert = validate(v);
        return vert.getIncoming().values();
    }

    public Edge<E> getEdge(Vertex<V> u, Vertex<V> v) {
        InnerVertex origin = validate(u);
        return origin.getOutgoing().get(v);
    }

    public Vertex<V>[] endVertices(Edge<E> e) {
        InnerEdge edge = validate(e);
        return edge.getEndpoints();
    }

    public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException {
        InnerEdge edge = validate(e);
        Vertex<V>[] endpoints = edge.getEndpoints();
        if (endpoints[0] == v)
            return endpoints[1];
        else if (endpoints[1] == v)
            return endpoints[0];
        else
            throw new IllegalArgumentException("v is not incident to this edge");
    }

    public Vertex<V> insertVertex(V element) {
        InnerVertex v = new InnerVertex(element, isDirected);
        v.setPosition(vertices.addLast(v));
        return v;
    }

    public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E element) throws IllegalArgumentException {
        if (getEdge(u, v) == null) {
            InnerEdge e = new InnerEdge(u, v, element);
            e.setPosition(edges.addLast(e));
            InnerVertex origin = validate(u);
            InnerVertex dest = validate(v);
            origin.getOutgoing().put(v, e);
            dest.getIncoming().put(u, e);
            return e;
        } else
            throw new IllegalArgumentException("Edge from u to v exists");
    }

    public void removeVertex(Vertex<V> v) {
        InnerVertex vert = validate(v);
        ArrayList<Edge<E>> outSnapshot = new ArrayList<>();
        for (Edge<E> e : vert.getOutgoing().values()) outSnapshot.add(e);
        for (Edge<E> e : outSnapshot) removeEdge(e);
        if (isDirected) {
            ArrayList<Edge<E>> inSnapshot = new ArrayList<>();
            for (Edge<E> e : vert.getIncoming().values()) inSnapshot.add(e);
            for (Edge<E> e : inSnapshot) removeEdge(e);
        }
        vertices.remove(vert.getPosition());
    }

    public void removeEdge(Edge<E> e) {
        InnerEdge ie = validate(e);
        Vertex<V>[] uv = ie.getEndpoints();
        InnerVertex u = validate(uv[0]);
        InnerVertex v = validate(uv[1]);
        u.getOutgoing().remove(uv[1]);
        v.getIncoming().remove(uv[0]);
        edges.remove(ie.getPosition());
    }

    @SuppressWarnings("unchecked")
    private InnerVertex validate(Vertex<V> v) throws IllegalArgumentException {
        if (!(v instanceof AdjacencyMapGraph.InnerVertex))
            throw new IllegalArgumentException("Invalid vertex");
        InnerVertex iv = (InnerVertex) v;
        if (iv.getPosition() == null) throw new IllegalArgumentException("Vertex no longer in graph");
        return iv;
    }

    @SuppressWarnings("unchecked")
    private InnerEdge validate(Edge<E> e) throws IllegalArgumentException {
        if (!(e instanceof AdjacencyMapGraph.InnerEdge))
            throw new IllegalArgumentException("Invalid edge");
        InnerEdge ie = (InnerEdge) e;
        if (ie.getPosition() == null) throw new IllegalArgumentException("Edge no longer in graph");
        return ie;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Position<Vertex<V>> pv : vertices.positions()) {
            Vertex<V> v = pv.getElement();
            sb.append("Vertex ").append(v.getElement()).append("\n");
            sb.append(" [outgoing] ").append(outDegree(v)).append(" adjacencies:\n");
            for (Edge<E> e : outgoingEdges(v)) {
                Vertex<V> opp = opposite(v, e);
                sb.append(" (").append(opp.getElement()).append(", ").append(e.getElement()).append(")\n");
            }
            if (isDirected) {
                sb.append(" [incoming] ").append(inDegree(v)).append(" adjacencies:\n");
                for (Edge<E> e : incomingEdges(v)) {
                    Vertex<V> opp = opposite(v, e);
                    sb.append(" (").append(opp.getElement()).append(", ").append(e.getElement()).append(")\n");
                }
            }
        }
        return sb.toString();
    }

}
