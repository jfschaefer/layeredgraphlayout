package de.jfschaefer.layeredgraphlayout.tree;

import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.Edge;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class TreeNode<V, E> extends Node<V, E> {
    protected ArrayList<Edge<V, E>> children;
    public TreeNode(V original, double width, double height) {
        super(original, width, height);
        children = new ArrayList<Edge<V, E>>();
    }

    public void addChild(Edge<V, E> child) {
        children.add(child);
    }

    public Collection<Edge<V, E>> getChildren() {
        return children;
    }
}
