package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Point {
    public final double x;
    public final double y;
    public static final Point ORIGIN = new Point(0d, 0d);

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector vectorTo(Point p) {
        return new Vector(p.x - x, p.y - y);
    }

    public Point addVector(Vector v) {
        return new Point(x + v.x, y + v.y);
    }
}
