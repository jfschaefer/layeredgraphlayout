package de.jfschaefer.layeredgraphlayout.lgraph;

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
        elements.add(node);
    }
}
