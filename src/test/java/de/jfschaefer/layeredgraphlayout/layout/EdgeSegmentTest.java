package de.jfschaefer.layeredgraphlayout.layout;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by user jfschaefer 8/10/15.
 */

public class EdgeSegmentTest extends TestCase {
    public static final double EPSILON = 1e-10;

    public EdgeSegmentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(EdgeSegmentTest.class);
    }

    public void testConstructorLogic() {
        EdgeSegment a = new EdgeSegment(new Point(0, 0), new Point(2, 1));
        EdgeSegment b = new EdgeSegment(new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1));
        assertFalse(a.isBezier());
        assertTrue(b.isBezier());
    }

    public void testLinearGetAt() {
        EdgeSegment linear = new EdgeSegment(new Point(2, 1), new Point(5, -2));
        Point p = linear.getAt(2d/3d);
        assertTrue(p.x + EPSILON > 4d && p.x - EPSILON < 4d);
        assertTrue(p.y + EPSILON > -1d && p.y - EPSILON < 4d);
    }

    public void testBezierGetAt() {
        // TODO: Write a test case
    }

    public void testLinearReverse() {
        Point a = new Point(2, 1);
        Point b = new Point(5, 4);
        EdgeSegment s = new EdgeSegment(a, b);
        EdgeSegment r = s.reversed();
        assertFalse(r.isBezier());
        assertEquals(a, r.end);
        assertEquals(b, r.start);
    }

    public void testBezierReverse() {
        Point a = new Point(0, 23);
        Point c1 = new Point(21, 2);
        Point c2 = new Point(-5, 2);
        Point b = new Point(-14, 3);
        EdgeSegment s = new EdgeSegment(a, c1, c2, b);
        EdgeSegment r = s.reversed();
        assertTrue(r.isBezier());
        assertEquals(r.start, b);
        assertEquals(r.control1, c2);
        assertEquals(r.control2, c1);
        assertEquals(r.end, a);
    }
}
