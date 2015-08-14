package de.jfschaefer.layeredgraphlayout.tree;

import de.jfschaefer.layeredgraphlayout.Edge;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraphConfig;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraph;
import de.jfschaefer.layeredgraphlayout.lgraph.LNode;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Tree<V, E> {
    Map<V, TreeNode<V, E>> nodeMap;
    TreeNode<V, E> root;
    public Tree(V root, double width, double height) {
        this.root = new TreeNode<V, E>(root, width, height);
        nodeMap = new HashMap<V, TreeNode<V, E>>();
        nodeMap.put(root, this.root);
    }

    public void addChild(V parent, E edge, V child, double width, double height) {
        TreeNode<V, E> p = nodeMap.get(parent);
        assert p != null;
        TreeNode<V, E> c = new TreeNode<V, E>(child, width, height);
        nodeMap.put(child, c);
        Edge<V, E> e = new Edge<V, E>(edge, p, c);
        p.addChild(e);
    }

    public LGraph<V, E> generateLGraph(LGraphConfig lconfig) {
        LGraph<V, E> lgraph = new LGraph<V, E>(lconfig);

        lgraph.addNode(root, 0);
        populateLGraph(lgraph, root, 0);

        return lgraph;
    }

    private void populateLGraph(LGraph<V, E> lgraph, TreeNode<V, E> node, int layer) {
        for (Edge<V, E> edge : node.getChildren()) {
            TreeNode<V, E> child = (TreeNode<V, E>)edge.getTo();
            lgraph.addNode(child, layer+1);
            lgraph.addEdge(edge);
            populateLGraph(lgraph, child, layer+1);
        }
    }
}
