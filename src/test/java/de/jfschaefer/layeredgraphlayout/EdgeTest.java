package de.jfschaefer.layeredgraphlayout;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by jfschaefer on 7/30/15.
 */

public class EdgeTest extends TestCase {
    public EdgeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(EdgeTest.class);
    }

    public void testFlipEdge()
    {
        Node<String, String> nodeA = new Node<String, String>("A", 1d, 1d);
        Node<String, String> nodeB = new Node<String, String>("B", 1d, 1d);
        String edge = "edge_a_b";
        Edge e = new Edge<String, String>(edge, nodeA, nodeB);

        assertTrue(nodeA.getOutgoingEdges().contains(e));
        assertTrue(nodeB.getIngoingEdges().contains(e));
        assertTrue(nodeA.getIngoingEdges().isEmpty());
        assertTrue(nodeB.getOutgoingEdges().isEmpty());

        assertEquals(e.getFrom(), nodeA);
        assertEquals(e.getTo(), nodeB);
        assertFalse(e.isFlipped());

        e.flip();
        assertTrue(nodeB.getOutgoingEdges().contains(e));
        assertTrue(nodeA.getIngoingEdges().contains(e));
        assertTrue(nodeB.getIngoingEdges().isEmpty());
        assertTrue(nodeA.getOutgoingEdges().isEmpty());

        assertEquals(e.getFrom(), nodeB);
        assertEquals(e.getTo(), nodeA);
        assertEquals(e.getRealFrom(), nodeA);
        assertEquals(e.getRealTo(), nodeB);
        assertTrue(e.isFlipped());
    }
}
