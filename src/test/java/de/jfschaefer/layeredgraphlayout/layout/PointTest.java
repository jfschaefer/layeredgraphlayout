package de.jfschaefer.layeredgraphlayout.layout;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by jfschaefer on 8/10/15.
 */

public class PointTest extends TestCase {
    public static final double EPSILON = 1e-10;

    public PointTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(PointTest.class);
    }

    public void testVectorTo() {
        Point p = new Point(2.4, -1.8);
        Point q = new Point(-1.2, 4d);
        Vector d = p.vectorTo(q);
        assertTrue(d.x + EPSILON > -3.6 && d.x - EPSILON < -3.6);
        assertTrue(d.y + EPSILON > 5.8 && d.y - EPSILON < 5.8);
    }

    public void testAddVector() {
        Point p = new Point(1d, 2d);
        Vector v = new Vector(3d, 9d);
        Point q = p.addVector(v);
        assertTrue(q.x + EPSILON > 4d && q.x - EPSILON < 4d);
        assertTrue(q.y + EPSILON > 11d && q.y - EPSILON < 11d);
    }
}
