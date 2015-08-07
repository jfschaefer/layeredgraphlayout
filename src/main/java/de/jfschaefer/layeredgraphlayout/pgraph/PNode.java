package de.jfschaefer.layeredgraphlayout.pgraph;


import de.jfschaefer.layeredgraphlayout.Node;

import java.util.*;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PNode<V, E> {
    public final Node<V, E> node;
    protected ArrayList<PEdge<V, E>> children;
    protected ArrayList<PEdge<V, E>> parents;  // logically, it doesn't have an order, but practically, it's easier this way

    public final boolean isRoot;
    protected int layer;    // Warning: layer isn't maintained all the time, just when an lgraph is computed.

    public PNode(Node<V, E> node, boolean isRoot) {
        this.node = node;
        children = new ArrayList<PEdge<V, E>>();
        parents = new ArrayList<PEdge<V, E>>();
        this.isRoot = isRoot;
        layer = -1;
    }

    public PNode(Node<V, E> node) {
        this(node, false);
    }

    public void addChild(PEdge<V, E> child) {
        children.add(child);
    }

    public void addParent(PEdge<V, E> parent) {
        parents.add(parent);
    }

    public boolean isSource() {
        return parents.isEmpty();
    }

    public boolean isSink() {
        return children.isEmpty();
    }

    public final ArrayList<PEdge<V, E>> getChildren() {
        return children;
    }

    public final ArrayList<PEdge<V, E>> getParents() {
        return parents;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void resetLayer() {
        layer = -1;
    }

    public boolean layerIsSet() {
        return layer != -1;
    }

    public int getLayer() {
        return layer;
    }
}

