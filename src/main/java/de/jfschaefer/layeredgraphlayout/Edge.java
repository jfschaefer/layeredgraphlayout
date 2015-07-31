package de.jfschaefer.layeredgraphlayout;

/**
 * Created by jfschaefer on 7/30/15.
 */

public class Edge<V, E> {
    protected E originalEdge;
    protected Node<V, E> from, to;
    protected boolean flipped = false;
    protected boolean locked = false;

    public Edge(E original, Node<V, E> from, Node<V, E> to) {
        originalEdge = original;
        this.from = from;
        this.to = to;
        from.addOutgoingEdge(this);
        to.addIngoingEdge(this);
    }

    public E getOriginalEdge() {
        return originalEdge;
    }

    public Node<V, E> getFrom() {
        return flipped ? to : from;
    }

    public Node<V, E> getTo() {
        return flipped ? from : to;
    }

    public Node<V, E> getRealFrom() {
        return from;
    }

    public Node<V, E> getRealTo() {
        return to;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void flip() {
        assert !locked;
        getFrom().reverseOutgoingEdge(this);
        getTo().reverseIngoingEdge(this);
        flipped = !flipped;
    }

    public void lock() {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }
}
