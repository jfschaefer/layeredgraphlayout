package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class EdgeSegment {
    public final Point start;
    public final Point control1;
    public final Point control2;
    public final Point end;
    public final boolean bezier;

    public EdgeSegment(Point start, Point end) {
        this.start = start;
        this.control1 = null;
        this.control2 = null;
        this.end = end;
        bezier = false;
    }

    public EdgeSegment(Point start, Point control1, Point control2, Point end) {
        this.start = start;
        this.control1 = control1;
        this.control2 = control2;
        this.end = end;
        bezier = true;
    }

    public EdgeSegment addVector(Vector v) {
        if (bezier) {
            return new EdgeSegment(start.addVector(v), control1.addVector(v), control2.addVector(v), end.addVector(v));
        } else {
            return new EdgeSegment(start.addVector(v), end.addVector(v));
        }
    }
}
