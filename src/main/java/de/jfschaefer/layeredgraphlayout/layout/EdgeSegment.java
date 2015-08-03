package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class EdgeSegment {
    public final Point start;
    public final Point control1;
    public final Point control2;
    public final Point end;
    public final boolean bezier;   //true, if cubic bezier curve, false, if linear

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

    public boolean isBezier() {
        return bezier;
    }

    public Point getStart() {
        return start;
    }

    public Point getControl1() {
        return control1;
    }

    public Point getControl2() {
        return control2;
    }

    public Point getEnd() {
        return end;
    }

    public Point getAt(double fraction) {
        if (bezier) {
            Vector p0 = Point.ORIGIN.vectorTo(start);
            Vector p1 = Point.ORIGIN.vectorTo(control1);
            Vector p2 = Point.ORIGIN.vectorTo(control2);
            Vector p3 = Point.ORIGIN.vectorTo(end);
            return Point.ORIGIN.addVector(p0.scaled((1-fraction)*(1-fraction)*(1-fraction)))
                    .addVector(p1.scaled(3*(1-fraction)*(1-fraction)*fraction))
                    .addVector(p2.scaled(3*(1-fraction)*fraction*fraction))
                    .addVector(p3.scaled(fraction*fraction*fraction));
        } else {
            return start.addVector(start.vectorTo(end).scaled(fraction));
        }
    }

    public Vector getDerivativeAt(double fraction) {
        if (bezier) {
            Vector p0 = Point.ORIGIN.vectorTo(start);
            Vector p1 = Point.ORIGIN.vectorTo(control1);
            Vector p2 = Point.ORIGIN.vectorTo(control2);
            Vector p3 = Point.ORIGIN.vectorTo(end);

            //return start.vectorTo(control1).scaled
            //TODO: Use actual derivative
            return start.vectorTo(end);
        } else {
            return start.vectorTo(end);
        }
    }
}
