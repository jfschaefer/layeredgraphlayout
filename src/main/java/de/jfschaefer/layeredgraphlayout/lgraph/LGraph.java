package de.jfschaefer.layeredgraphlayout.lgraph;

import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.Edge;
import de.jfschaefer.layeredgraphlayout.layout.Layout;
import de.jfschaefer.layeredgraphlayout.layout.LayoutConfig;
import de.jfschaefer.layeredgraphlayout.layout.Point;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class LGraph<V, E> {
    protected int numberOfLayers;
    protected ArrayList<Layer> layers;

    protected Map<Node<V, E>, LNode> nodeMap;
    protected Map<Edge<V, E>, ArrayList<LNode>> edgeMap;

    protected LGraphConfig config;

    protected boolean placed;

    public LGraph(LGraphConfig config) {
        numberOfLayers = 0;
        layers = new ArrayList<Layer>();
        nodeMap = new HashMap<Node<V, E>, LNode>();
        edgeMap = new HashMap<Edge<V, E>, ArrayList<LNode>>();
        this.config = config;
        placed = false;
    }

    public void addNode(Node<V, E> node, int layer) {
        assert !placed;
        assert !nodeMap.containsKey(node);
        LNode n = new LNode(layer, false, node.getWidth(), config);
        while (numberOfLayers < layer) {
            layers.add(new Layer(numberOfLayers++, config));
        }
        layers.get(layer).addNode(n);

        nodeMap.put(node, n);
    }

    public void addEdge(Edge<V, E> edge) {
        assert !placed;
        LNode from = nodeMap.get(edge.getFrom());
        LNode to = nodeMap.get(edge.getTo());
        assert from != null;
        assert to != null;
        assert from.getLayer() < to.getLayer();

        ArrayList<LNode> nodes = new ArrayList<LNode>();
        nodes.add(from);
        int layer = from.getLayer() + 1;
        LNode oldNode = from;
        while (layer < to.getLayer()) {
            LNode newNode = new LNode(layer, true, config.getDummyNodeWidth(), config);
            layers.get(layer).addNode(newNode);
            nodes.add(newNode);
            layer ++;

            oldNode.addChild(newNode);
            newNode.addParent(oldNode);
            oldNode = newNode;
        }
        nodes.add(to);
        oldNode.addChild(to);
        to.addParent(oldNode);

        edgeMap.put(edge, nodes);
    }


    /*
        Node placement
     */

    public void treePlacement() {
        // ONLY WORKS WELL FOR TREES

        placed = true;

        // top down
        for (Layer layer : layers) {
            double nextPos = 0d;
            for (LNode node : layer.getElements()) {
                double parentPos;
                if (node.getParents().isEmpty()) {
                    parentPos = 0d;
                } else {
                    parentPos = 0d;
                    for (LNode parent : node.getParents()) {    //parent set should be singleton, but just in case
                        parentPos += parent.getXPos();
                    }
                    parentPos /= node.getParents().size() - node.getWidth() * 0.5;
                }
                node.setXPosLeft(Math.max(nextPos, parentPos));
                nextPos = node.getXPosLeft() + config.getGapBetweenNodes();
            }
        }

        // bottom up
        for (int i = layers.size() - 1; i >= 0; i--) {
            Layer layer = layers.get(i);
            for (LNode node : layer.getElements()) {
                double newPos;
                if (node.getChildren().isEmpty()) {
                    newPos = node.getXPos();
                } else {
                    newPos = 0d;
                    for (LNode child : node.getChildren()) {
                        newPos += child.getPos();
                    }
                    newPos /= node.getChildren().size();
                }
                node.setXPos(newPos);
            }
        }
    }


    /*
        Layout generation
     */

    public Layout<V, E> getLayout(LayoutConfig layoutConfig) {
        Layout<V, E> layout = new Layout<V, E>(layoutConfig);

        for (Map.Entry<Node<V, E>, LNode> entry : nodeMap.entrySet()) {
            LNode lnode = entry.getValue();
            layout.addNode(entry.getKey(), new Point(lnode.getXPos(), lnode.getLayer() * config.getLayerDistance()));
        }

        for (Map.Entry<Edge<V, E>, ArrayList<LNode>> entry : edgeMap.entrySet()) {
            ArrayList<Point> points = new ArrayList<Point>();
            for (LNode lnode : entry.getValue()) {
                points.add(new Point(lnode.getXPos(), lnode.getLayer() * config.getLayerDistance()));
            }
            layout.addEdge(entry.getKey(), points);
        }

        return layout;
    }
}
