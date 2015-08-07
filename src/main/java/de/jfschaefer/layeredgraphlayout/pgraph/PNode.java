package de.jfschaefer.layeredgraphlayout.pgraph;


import de.jfschaefer.layeredgraphlayout.Node;

import java.util.*;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PNode<V, E> {
    public final Node<V, E> node;
    protected ArrayList<PEdge<V, E>> children;
    protected ArrayList<PEdge<V, E>> parents;  // logially, it doesn't have an order, but practically, it's easier this way

    public PNode(Node<V, E> node) {
        this.node = node;
        children = new ArrayList<PEdge<V, E>>();
        parents = new ArrayList<PEdge<V, E>>();
    }

    public void addChild(PEdge<V, E> child) {
        children.add(child);
    }

    public void addParent(PEdge<V, E> parent) {
        parents.add(parent);
    }
}
