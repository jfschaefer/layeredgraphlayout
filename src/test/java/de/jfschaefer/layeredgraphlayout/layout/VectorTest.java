package de.jfschaefer.layeredgraphlayout.layout;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by jfschaefer on 8/10/15.
 */

public class VectorTest extends TestCase {
    public static final double EPSILON = 1e-10;

    public VectorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(VectorTest.class);
    }

    public void testScale() {
        Vector a = new Vector(2d, 5d);
        Vector b = a.scaled(-0.5);
        assertTrue(b.x + EPSILON > -1d && b.x - EPSILON < -1d);
        assertTrue(b.y + EPSILON > -2.5 && b.y - EPSILON < -2.5);
    }
}
