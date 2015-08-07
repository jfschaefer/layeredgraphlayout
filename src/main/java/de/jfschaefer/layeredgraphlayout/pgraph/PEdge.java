package de.jfschaefer.layeredgraphlayout.pgraph;

import de.jfschaefer.layeredgraphlayout.Edge;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PEdge<V, E> {
    public final Edge<V, E> edge;

    public final PNode<V, E> from;
    public final PNode<V, E> to;

    public final boolean isFake;

    public PEdge(Edge<V, E> edge, PNode<V, E> from, PNode<V, E> to, boolean isFake) {
        this.edge = edge;
        this.from = from;
        this.to = to;
        this.isFake = isFake;
    }

    public PEdge(Edge<V, E> edge, PNode<V, E> from, PNode<V, E> to) {
        this(edge, from, to, false);
    }

    public PEdge(PNode<V, E> from, PNode<V, E> to) {
        this(null, from, to, true);
    }
}
