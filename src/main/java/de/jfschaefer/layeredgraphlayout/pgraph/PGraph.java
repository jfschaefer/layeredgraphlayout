package de.jfschaefer.layeredgraphlayout.pgraph;

import de.jfschaefer.layeredgraphlayout.*;

import java.util.*;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PGraph<V, E> {
    protected Map<Node<V, E>, PNode<V, E>> nodeMap;

    // using lists instead of sets for storing is easier, and possibly more efficient in this case
    protected ArrayList<PNode<V, E>> nodes;
    protected ArrayList<PEdge<V, E>> edges;

    public PGraph() {
        nodeMap = new HashMap<Node<V, E>, PNode<V, E>>();
        nodes = new ArrayList<PNode<V, E>>();
        edges = new ArrayList<PEdge<V, E>>();
    }

    public void addNode(Node<V, E> node) {
        assert !nodeMap.containsKey(node);
        PNode<V, E> pnode = new PNode<V, E>(node);
        nodeMap.put(node, pnode);
        nodes.add(pnode);
    }

    public void addEdge(Edge<V, E> edge) {
        PNode<V, E> pnodeFrom = nodeMap.get(edge.getFrom());
        PNode<V, E> pnodeTo = nodeMap.get(edge.getTo());
        PEdge<V, E> pedge = new PEdge<V, E>(edge, pnodeFrom, pnodeTo);
        pnodeFrom.addChild(pedge);
        pnodeTo.addParent(pedge);
        edges.add(pedge);
    }
}
