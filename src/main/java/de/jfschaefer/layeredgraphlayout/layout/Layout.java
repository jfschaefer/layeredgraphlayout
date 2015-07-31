package de.jfschaefer.layeredgraphlayout.layout;

import de.jfschaefer.layeredgraphlayout.util.Pair;
import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.Edge;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Layout<V, E> {
    protected Map<V, Pair<Node<V, E>, Point>> nodeMap;
    protected Map<E, Pair<Edge<V, E>, ArrayList<EdgeSegment>>> edgeMap;
    protected LayoutConfig config;

    protected boolean locked;
    protected Vector shift;

    protected double minX = Double.MAX_VALUE;
    protected double minY = Double.MAX_VALUE;
    protected double maxX = Double.MIN_VALUE;
    protected double maxY = Double.MIN_VALUE;

    public Layout(LayoutConfig config) {
        nodeMap = new HashMap<V, Pair<Node<V, E>, Point>>();
        edgeMap = new HashMap<E, Pair<Edge<V, E>, ArrayList<EdgeSegment>>>();
        this.config = config;

        locked = false;
        shift = new Vector(0d, 0d);
    }

    public void addNode(Node<V, E> node, Point center) {
        assert !locked;
        nodeMap.put(node.getOriginalNode(), new Pair<Node<V, E>, Point>(node, center));
        updateBoundaries(Util.getTopLeftCorner(node, center));
        updateBoundaries(Util.getBottomRightCorner(node, center));
    }

    public void addEdge(Edge<V, E> edge, ArrayList<Point> points) {
        assert !locked;
        ArrayList<EdgeSegment> segments = new ArrayList<EdgeSegment>();
        for (int i = 0; i < points.size() - 1; i++) {
            Point a = points.get(i);
            updateBoundaries(a);
            Point b = points.get(i+1);
            EdgeSegment newSegment;
            if (config.getBezier()) {
                Point c1;
                if (i == 0) {
                    Vector v = a.vectorTo(b);
                    c1 = a.addVector(v.scaled(config.getControlPointDistance()));
                } else {
                    Point z = points.get(i-1);
                    Vector v = z.vectorTo(b);
                    c1 = a.addVector(v.scaled(config.getControlPointDistance() * 0.5));
                }
                Point c2;
                if (i == points.size() - 2) {
                    Vector v = b.vectorTo(a);
                    c2 = b.addVector(v.scaled(config.getControlPointDistance()));
                } else {
                    Point c = points.get(i+2);
                    Vector v = c.vectorTo(a);
                    c2 = b.addVector(v.scaled(config.getControlPointDistance() * 0.5));
                }
                newSegment = new EdgeSegment(a, c1, c2, b);
            } else {
                newSegment = new EdgeSegment(a, b);
            }
            segments.add(newSegment);
        }
        updateBoundaries(points.get(points.size() - 1));
        if (edge.isFlipped()) {
            Collections.reverse(segments);
        }
        edgeMap.put(edge.getOriginalEdge(), new Pair<Edge<V, E>, ArrayList<EdgeSegment>>(edge, segments));
    }

    void lock() {
        locked = true;
        shift = new Vector(-minX + config.getGraphPadding(), -minY + config.getGraphPadding());
    }

    public Point getNodeCenter(V node) {
        return nodeMap.get(node).second;
    }

    public Pair<Double, Double> getNodeSize(V node) {
        Node<V, E> n = nodeMap.get(node).first;
        return new Pair<Double, Double>(n.getWidth(), n.getHeight());
    }

    public Point getNodeTopLeft(V node) {
        Node<V, E> n = nodeMap.get(node).first;
        Point center = nodeMap.get(node).second;
        return Util.getTopLeftCorner(n, center);
    }

    public Point getNodeBottomRight(V node) {
        Node<V, E> n = nodeMap.get(node).first;
        Point center = nodeMap.get(node).second;
        return Util.getBottomRightCorner(n, center);
    }

    public ArrayList<EdgeSegment> getEdgePosition(E edge) {
        return edgeMap.get(edge).second;
    }

    public Vector getShift() {
        if (!locked) lock();
        return shift;
    }

    public double getWidth() {
        return maxX - minX + 2 * config.getGraphPadding();
    }

    public double getHeight() {
        return maxY - minY + 2 * config.getGraphPadding();
    }

    public void updateBoundaries(Point p) {
        assert !locked;
        if (p.x < minX) minX = p.x;
        if (p.x > maxX) maxX = p.x;
        if (p.y < minY) minY = p.y;
        if (p.y > maxY) maxY = p.y;
    }

    public final Set<V> getNodeSet() {
        return nodeMap.keySet();
    }

    public final Set<E> getEdgeSet() {
        return edgeMap.keySet();
    }
}
