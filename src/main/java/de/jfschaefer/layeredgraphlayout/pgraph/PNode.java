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

    public void removeChild(PEdge<V, E> child) {
        assert child.isFake;
        assert child.from == this;
        assert children.contains(child);
        children.remove(child);
    }

    public void removeParent(PEdge<V, E> parent) {
        assert parent.isFake;
        assert parent.to == this;
        assert parents.contains(parent);
        parents.remove(parent);
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

    public void swapChildren(int i1, int i2) {
        assert i1 != i2;
        PEdge<V, E> child1 = children.get(i1);
        PEdge<V, E> child2 = children.get(i2);
        int i2adapted = i2 > i1 ? i2 - 1 : i2;
        children.remove(i1);
        children.remove(i2adapted);
        children.add(i2adapted, child1);
        children.add(i1, child2);
    }
}

