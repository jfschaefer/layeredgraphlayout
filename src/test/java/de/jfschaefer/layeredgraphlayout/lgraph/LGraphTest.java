package de.jfschaefer.layeredgraphlayout.lgraph;

import de.jfschaefer.layeredgraphlayout.Edge;
import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Created by jfschaefer on 8/12/15.
 */

public class LGraphTest extends TestCase {
    public LGraphTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LGraphTest.class);
    }

    public void testIntersectionCounting() {
        LGraph<String, String> lgraph = new LGraph<String, String>(new LGraphConfig());
        String a1 = "Hello";
        String a2 = "World";
        String b1 = "Welcome";
        String b2 = "Home";
        String e1 = "Hello Home";
        String e2 = "World Welcome";

        Node<String, String> na1 = new Node<String, String>(a1, 100, 20);
        Node<String, String> na2 = new Node<String, String>(a2, 100, 20);
        Node<String, String> nb1 = new Node<String, String>(b1, 100, 20);
        Node<String, String> nb2 = new Node<String, String>(b2, 100, 20);
        Edge<String, String> ee1 = new Edge<String, String>(e1, na1, nb2);
        Edge<String, String> ee2 = new Edge<String, String>(e2, na2, nb1);
        lgraph.addNode(na1, 0);
        lgraph.addNode(na2, 0);
        lgraph.addNode(nb1, 1);
        lgraph.addNode(nb2, 1);
        lgraph.addEdge(ee1);
        lgraph.addEdge(ee2);
        ArrayList<Pair<LNode, LNode>> connections = lgraph.layers.get(0).getChildConnections();
        assertEquals(connections.size(), 2);
        assertEquals(lgraph.getNumberOfIntersections(), 1);
        assertEquals(lgraph.getNumberOfDummyNodes(), 0);   // why not ;)
    }
}
