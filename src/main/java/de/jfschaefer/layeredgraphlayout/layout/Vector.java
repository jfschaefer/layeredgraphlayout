package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Vector {
    public final double x;
    public final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector scaled(double l) {
        return new Vector(l * x, l * y);
    }
}
