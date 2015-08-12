package de.jfschaefer.layeredgraphlayout.lgraph;

import de.jfschaefer.layeredgraphlayout.util.Pair;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Layer {
    protected int layer;
    protected ArrayList<LNode> elements;

    protected LGraphConfig config;

    public Layer(int layer, LGraphConfig config) {
        this.layer = layer;
        elements = new ArrayList<LNode>();
        this.config = config;
    }

    public void addNode(LNode node) {
        node.setPos(elements.size());
        elements.add(node);
    }

    ArrayList<LNode> getElements() {
        return elements;
    }
    ArrayList<Pair<LNode, LNode>> getChildConnections() {
        ArrayList<Pair<LNode, LNode>> connections = new ArrayList<Pair<LNode, LNode>>();
        for (LNode parent : elements) {
            for (LNode child : parent.getChildren()) {
                connections.add(new Pair<LNode, LNode>(parent, child));
            }
        }
        return connections;
    }
}
