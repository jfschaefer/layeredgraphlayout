package de.jfschaefer.layeredgraphlayout.pgraph;

import de.jfschaefer.layeredgraphlayout.Edge;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PEdge<V, E> {
    public final Edge<V, E> edge;

    public final PNode<V, E> from;
    public final PNode<V, E> to;

    public PEdge(Edge<V, E> edge, PNode<V, E> from, PNode<V, E> to) {
        this.edge = edge;
        this.from = from;
        this.to = to;
    }
}
