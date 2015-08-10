package de.jfschaefer.layeredgraphlayout;

import java.util.*;

/**
 * Created by jfschaefer on 7/30/15.
 */

public class Node<V, E> {
    protected V originalNode;
    protected double width, height;
    protected Set<Edge<V, E>> outgoingEdges;
    protected Set<Edge<V, E>> ingoingEdges;

    public Node(V original, double width, double height) {
        originalNode = original;
        this.width = width;
        this.height = height;
        outgoingEdges = new HashSet<Edge<V, E>>();
        ingoingEdges = new HashSet<Edge<V, E>>();
    }

    void addIngoingEdge(Edge<V, E> edge) {
        ingoingEdges.add(edge);
    }

    void addOutgoingEdge(Edge<V, E> edge) {
        outgoingEdges.add(edge);
    }

    public void reverseIngoingEdge(Edge<V, E> edge) {
        assert ingoingEdges.contains(edge);
        ingoingEdges.remove(edge);
        outgoingEdges.add(edge);
    }

    public void reverseOutgoingEdge(Edge<V, E> edge) {
        assert outgoingEdges.contains(edge);
        outgoingEdges.remove(edge);
        ingoingEdges.add(edge);
    }

    public final Set<Edge<V, E>> getOutgoingEdges() {
        return outgoingEdges;
    }

    public final Set<Edge<V, E>> getIngoingEdges() {
        return ingoingEdges;
    }

    public boolean isSink() {
        return outgoingEdges.isEmpty();
    }

    public boolean isSource() {
        return ingoingEdges.isEmpty();
    }

    public boolean isSinkIn(Collection<Node<V, E>> subset) {
        for (Edge<V, E> edge : outgoingEdges) {
            if (subset.contains(edge.getTo())) {
                return false;
            }
        }
        return true;
    }

    public boolean isSourceIn(Collection<Node<V, E>> subset) {
        for (Edge<V, E> edge : ingoingEdges) {
            if (subset.contains(edge.getFrom())) {
                return false;
            }
        }
        return true;
    }

    public int inDegree() {
        return ingoingEdges.size();
    }

    public int outDegree() {
        return outgoingEdges.size();
    }

    public int inDegreeIn(Collection<Node<V, E>> subset) {
        int indegree = 0;
        for (Edge<V, E> edge : ingoingEdges) {
            if (subset.contains(edge.getFrom())) {
                indegree ++;
            }
        }
        return indegree;
    }

    public int outDegreeIn(Collection<Node<V, E>> subset) {
        int outdegree = 0;
        for (Edge<V, E> edge : outgoingEdges) {
            if (subset.contains(edge.getTo())) {
                outdegree ++;
            }
        }
        return outdegree;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public V getOriginalNode() {
        return originalNode;
    }
}

