package de.jfschaefer.layeredgraphlayout.gengraph;

import de.jfschaefer.layeredgraphlayout.Edge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by jfschaefer on 8/14/15.
 */

public class GenGraphTest extends TestCase {
    public GenGraphTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GenGraphTest.class);
    }

    public void testRemoveCyclesGreedy() {
        GenGraph<String, String> graph = new GenGraph<String, String>();

        String[] nodes = {"n1", "n2", "n3"};
        String[] edges = {"e1", "e2", "e3"};
        graph.addNode(nodes[0], 10d, 10d);
        graph.addNode(nodes[1], 10d, 10d);
        graph.addNode(nodes[2], 10d, 10d);
        graph.addEdge(edges[0], nodes[0], nodes[1]);
        graph.addEdge(edges[1], nodes[1], nodes[2]);
        graph.addEdge(edges[2], nodes[2], nodes[0]);
        graph.removeCyclesGreedy();
        int counter = 0;
        assertEquals(graph.getEdges().size(), 3);
        for (Edge edge : graph.getEdges()) {
            if (edge.isFlipped()) {
                counter ++;
            }
        }
        assertEquals(counter, 1);   // if it's 0 or 3, there is a cycle, if it's 2, unnecessarily many edges have been flipped
    }
}
