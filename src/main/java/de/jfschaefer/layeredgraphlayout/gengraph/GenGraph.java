package de.jfschaefer.layeredgraphlayout.gengraph;

import de.jfschaefer.layeredgraphlayout.Edge;
import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.pgraph.PGraph;

import java.util.*;

/**
 * Created by user on 7/30/15.
 */

public class GenGraph<V, E> {
    protected Map<V, Node<V, E>> nodeMap;
    protected Map<E, Edge<V, E>> edgeMap;
    protected boolean locked = false;
    protected boolean cyclesRemoved = false;

    public GenGraph() {
        nodeMap = new HashMap<V, Node<V, E>>();
        edgeMap = new HashMap<E, Edge<V, E>>();
    }

    public void addNode(V node, double width, double height) {
        assert !locked;
        nodeMap.put(node, new Node<V, E>(node, width, height));
    }

    public void addEdge(E edge, V from, V to) {
        assert !locked;
        Node<V, E> nfrom = nodeMap.get(from);
        Node<V, E> nto = nodeMap.get(to);
        edgeMap.put(edge, new Edge<V, E>(edge, nfrom, nto));
    }

    public final Collection<Node<V, E>> getNodes() {
        return nodeMap.values();
    }

    public final Collection<Edge<V, E>> getEdges() {
        return edgeMap.values();
    }

    public final Set<V> getOriginalNodes() {
        return nodeMap.keySet();
    }

    public final Set<E> getOriginalEdges() {
        return edgeMap.keySet();
    }

    public void removeCyclesGreedy() {
        if (cyclesRemoved) return;
        assert !locked;
        Set<Node<V, E>> remainingNodes = new HashSet<Node<V, E>>();
        for (Node<V, E> n : getNodes()) {
            remainingNodes.add(n);
        }

        while (!remainingNodes.isEmpty()) {
            //remove remaining sources and sinks
            int size;
            do {
                size = remainingNodes.size();
                Set<Node<V, E>> done = new HashSet<Node<V, E>>();
                for (Node<V, E> node : remainingNodes) {
                    if (node.isSinkIn(remainingNodes) || node.isSourceIn(remainingNodes)) {
                        done.add(node);
                    }
                }
                remainingNodes.removeAll(done);
            } while (size != remainingNodes.size());

            //reverse a few edges to make one more node a source/sink.
            if (remainingNodes.isEmpty()) break;
            double bestRatio = 1d;     // worse than worst case
            Node<V, E> chosenNode = null;
            boolean indegreeGToutdegree = false;        // in degree greater than out degree
            for (Node<V, E> node : remainingNodes) {
                int indegree = node.inDegreeIn(remainingNodes);
                int outdegree = node.outDegreeIn(remainingNodes);
                assert indegree != 0;    //otherwise it would have been a source in the previous step
                assert outdegree != 0;   //otherwise it would have been a sink in the previous step
                double thisRatio = ((double) Math.min(indegree, outdegree)) / (indegree + outdegree);
                if (thisRatio < bestRatio) {
                    bestRatio = thisRatio;
                    chosenNode = node;
                    indegreeGToutdegree = (indegree > outdegree);
                }
            }
            assert chosenNode != null;
            Set<Edge<V, E>> edgesToBeFlipped = new HashSet<Edge<V, E>>();
            if (indegreeGToutdegree) {   //flip outgoing edges
                for (Edge<V, E> edge : chosenNode.getOutgoingEdges()) {
                    if (remainingNodes.contains(edge.getTo())) {
                        edgesToBeFlipped.add(edge);
                    }
                }
            } else {    //flip ingoing edges
                for (Edge<V, E> edge : chosenNode.getIngoingEdges()) {
                    if (remainingNodes.contains(edge.getFrom())) {
                        edgesToBeFlipped.add(edge);
                    }
                }
            }
            for (Edge e : edgesToBeFlipped) {
                e.flip();
            }
        }

        locked = true;
        cyclesRemoved = true;
    }

    public PGraph<V, E> getAsPGraph() {
        removeCyclesGreedy();
        PGraph<V, E> result = new PGraph<V, E>();
        for (Node<V, E> node : getNodes()) {
            result.addNode(node);
        }
        for (Edge<V, E> edge : getEdges()) {
            result.addEdge(edge);
        }
        return result;
    }
}
