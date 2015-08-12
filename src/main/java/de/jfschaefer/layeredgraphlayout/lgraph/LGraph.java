package de.jfschaefer.layeredgraphlayout.lgraph;

import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.Edge;
import de.jfschaefer.layeredgraphlayout.layout.Layout;
import de.jfschaefer.layeredgraphlayout.layout.LayoutConfig;
import de.jfschaefer.layeredgraphlayout.layout.Point;
import de.jfschaefer.layeredgraphlayout.util.Pair;

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
    protected int numberOfDummyNodes;

    protected final double EPSILON = 1e-5;   //that's more than sufficient

    public LGraph(LGraphConfig config) {
        numberOfLayers = 0;
        layers = new ArrayList<Layer>();
        nodeMap = new HashMap<Node<V, E>, LNode>();
        edgeMap = new HashMap<Edge<V, E>, ArrayList<LNode>>();
        this.config = config;
        placed = false;
        numberOfDummyNodes = 0;
    }

    public int getNumberOfDummyNodes() {
        return numberOfDummyNodes;
    }

    public void addNode(Node<V, E> node, int layer) {
        assert !placed;
        assert !nodeMap.containsKey(node);
        LNode n = new LNode(layer, false, node.getWidth(), node.getHeight(), config);
        while (numberOfLayers <= layer) {
            layers.add(new Layer(numberOfLayers++, config));
        }
        layers.get(layer).addNode(n);

        nodeMap.put(node, n);
    }

    public boolean containsNode(Node<V, E> node) {
        return nodeMap.containsKey(node);
    }

    public int getNumberOfIntersections() {
        int intersections = 0;
        for (int i = 0; i < layers.size() - 1; i++) {
            Layer layer = layers.get(i);
            ArrayList<Pair<LNode, LNode>> connections = layer.getChildConnections();
            for (Pair<LNode, LNode> a : connections) {
                for (Pair<LNode, LNode> b : connections) {
                    if (a == b) continue;
                    if (a.first.getPos() < b.first.getPos() && a.second.getPos() > b.second.getPos()) intersections++;
                    else if (a.first.getPos() > b.first.getPos() && a.second.getPos() < b.second.getPos()) intersections++;
                }
            }
        }
        return intersections;
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
            LNode newNode = new LNode(layer, true, config.getDummyNodeWidth(), 0d, config);
            layers.get(layer).addNode(newNode);
            nodes.add(newNode);
            layer ++;
            numberOfDummyNodes++;

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
        boolean somethingHasChanged;

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
                    parentPos /= node.getParents().size();
                    parentPos -= node.getWidth() * 0.5;
                }
                node.setXPosLeft(Math.max(nextPos, Math.max(parentPos, node.getXPosLeft())));
                nextPos = node.getXPosRight() + config.getGapBetweenNodes();
            }
        }

        int maxIterations = 36;

        do {
            somethingHasChanged = false;

            // bottom up
            for (int i = layers.size() - 1; i >= 0; i--) {
                Layer layer = layers.get(i);
                for (int j = 0; j < layer.getElements().size(); j++) {
                    LNode node = layer.getElements().get(j);
                    double newPos;
                    if (node.getChildren().isEmpty()) {
                        newPos = node.getXPos();
                    } else {
                        newPos = 0d;
                        for (LNode child : node.getChildren()) {
                            newPos += child.getXPos();
                        }
                        newPos /= node.getChildren().size();
                    }

                    node.setXPos(Math.max(newPos, node.getXPos()));

                    for (int k = j + 1; k < layer.getElements().size(); k++) {
                        LNode prev = layer.getElements().get(k - 1);
                        LNode cur = layer.getElements().get(k);

                        double delta = cur.getXPosLeft() - (prev.getXPosRight() + config.getGapBetweenNodes());
                        if (delta < 0) {
                            cur.setXPos(cur.getXPos() - delta);
                        } else {
                            break;
                        }
                    }
                }
            }

            /* // fix nodes too close   -   apparently, never needed
            for (Layer layer : layers) {
                for (int j = 1; j < layer.getElements().size(); j++) {
                    LNode prev = layer.getElements().get(j - 1);
                    LNode cur = layer.getElements().get(j);
                    double delta = cur.getXPosLeft() - (prev.getXPosRight() + config.getGapBetweenNodes());
                    if (delta < 0) {
                        somethingHasChanged = true;
                        cur.setXPos(cur.getXPos() - delta);
                    }
                }
            } */

            // different top down
            for (Layer layer : layers) {
                for (int j = 0; j < layer.getElements().size(); j++) {
                    LNode node = layer.getElements().get(j);
                    double idealPos;
                    if (node.getChildren().isEmpty()) {
                        idealPos = node.getXPos();
                    } else {
                        idealPos = 0d;
                        for (LNode child : node.getChildren()) {
                            idealPos += child.getXPos();
                        }
                        idealPos /= node.getChildren().size();
                    }
                    double shift = idealPos - node.getXPos();
                    if (shift < -0.4) {
                        for (LNode child : node.getChildren()) {
                            child.setXPos(child.getXPos() - shift);
                        }
                    }
                }
            }

            // fix nodes too close
            for (Layer layer : layers) {
                for (int j = 1; j < layer.getElements().size(); j++) {
                    LNode prev = layer.getElements().get(j - 1);
                    LNode cur = layer.getElements().get(j);
                    double delta = cur.getXPosLeft() - (prev.getXPosRight() + config.getGapBetweenNodes());
                    if (delta < -EPSILON) {
                        somethingHasChanged = true;
                        cur.setXPos(cur.getXPos() - delta);
                    }
                }
            }
        } while (somethingHasChanged && --maxIterations != 0);

        if (maxIterations == 0) {
            System.err.println("Warning: de.jfschaefer.layeredgraphlayout.lgraph.LGraph.treePlacement: Reached max. iterations");
        }
    }


    public void graphPlacement() {
        // TODO: This is just short dummy algorithm - implement something reasonable instead
        placed = true;

        // top down (places the nodes in a valid way, large gaps)
        for (Layer layer : layers) {
            double nextPos = 0d;
            for (LNode node : layer.getElements()) {
                node.setXPosLeft(nextPos);
                nextPos = node.getXPosRight() + config.getGapBetweenNodes() + 0 * node.getWidth();
            }
        }

        for (int repeat = 3; --repeat > 0; ) {
            // top down (places according to average of parents and children, left->right)
            for (Layer layer : layers) {
                for (int i = 0; i < layer.getElements().size(); i++) {
                    LNode node = layer.getElements().get(i);
                    double perfectPos = 0d;
                    for (LNode parent : node.getParents()) {
                        perfectPos += parent.getXPos();
                    }
                    for (LNode parent : node.getChildren()) {
                        perfectPos += parent.getXPos();
                    }
                    int n = node.getParents().size() + node.getChildren().size();
                    if (n != 0) {
                        perfectPos /= n;
                    }
                    if (perfectPos > node.getXPos()) {
                        if (i + 1 < layer.getElements().size()) {
                            LNode next = layer.getElements().get(i+1);
                            node.setXPos(Math.min(perfectPos, next.getXPosLeft() - config.getGapBetweenNodes() - 0.5 * node.getWidth()));
                        } else {
                            node.setXPos(perfectPos);
                        }
                    } else {
                        if (i > 0) {
                            LNode prev = layer.getElements().get(i-1);
                            node.setXPos(Math.max(perfectPos, prev.getXPosRight() + config.getGapBetweenNodes() + 0.5 * node.getWidth()));
                        } else {
                            node.setXPos(perfectPos);
                        }
                    }
                }
            }

            // top down (places according to average of parents and children, right->left)
            for (Layer layer : layers) {
                //for (int i = 0; i < layer.getElements().size(); i++) {
                for (int i = layer.getElements().size() - 1; i >= 0; i--) {
                    LNode node = layer.getElements().get(i);
                    double perfectPos = 0d;
                    for (LNode parent : node.getParents()) {
                        perfectPos += parent.getXPos();
                    }
                    for (LNode parent : node.getChildren()) {
                        perfectPos += parent.getXPos();
                    }
                    int n = node.getParents().size() + node.getChildren().size();
                    if (n != 0) {
                        perfectPos /= n;
                    }
                    if (perfectPos > node.getXPos()) {
                        if (i + 1 < layer.getElements().size()) {
                            LNode next = layer.getElements().get(i+1);
                            node.setXPos(Math.min(perfectPos, next.getXPosLeft() - config.getGapBetweenNodes() - 0.5 * node.getWidth()));
                        } else {
                            node.setXPos(perfectPos);
                        }
                    } else {
                        if (i > 0) {
                            LNode prev = layer.getElements().get(i-1);
                            node.setXPos(Math.max(perfectPos, prev.getXPosRight() + config.getGapBetweenNodes() + 0.5 * node.getWidth()));
                        } else {
                            node.setXPos(perfectPos);
                        }
                    }
                }
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
            for (int i = 0; i < entry.getValue().size(); i++) {
                LNode lnode = entry.getValue().get(i);
                points.add(new Point(lnode.getXPos(), lnode.getLayer() * config.getLayerDistance() +
                        0.5 * (i == 0 ? 1 : -1) * (!lnode.isDummy() ? lnode.getHeight() : 0)));
            }
            layout.addEdge(entry.getKey(), points);
        }

        return layout;
    }

}
