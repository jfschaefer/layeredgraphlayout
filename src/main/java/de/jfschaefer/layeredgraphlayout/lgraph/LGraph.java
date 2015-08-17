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

    public void reset() {
        nodeMap.clear();
        edgeMap.clear();
        numberOfDummyNodes = 0;
        placed = false;
        for (Layer layer : layers) {
            layer.getElements().clear();
        }
    }

    public int getNumberOfDummyNodes() {
        return numberOfDummyNodes;
    }

    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    public int getDensityMeasure() {  // a value indicating how for children/parents of nodes are distributed
        int value = 0;
        for (Layer layer : layers) {
            for (LNode node : layer.getElements()) {
                if (!node.getChildren().isEmpty()) {
                    int minchild = Integer.MAX_VALUE;
                    int maxchild = Integer.MIN_VALUE;
                    for (LNode child : node.getChildren()) {
                        if (child.getPos() < minchild) {
                            minchild = child.getPos();
                        }
                        if (child.getPos() > maxchild) {
                            maxchild = child.getPos();
                        }
                    }
                    int v = maxchild - minchild - node.getChildren().size() + 1;
                    value += v * v;   // large gaps are much worse
                }
                if (!node.getParents().isEmpty()) {
                    int minparent = Integer.MAX_VALUE;
                    int maxparent = Integer.MIN_VALUE;
                    for (LNode parent : node.getParents()) {
                        if (parent.getPos() < minparent) {
                            minparent = parent.getPos();
                        }
                        if (parent.getPos() > maxparent) {
                            maxparent = parent.getPos();
                        }
                    }
                    int v = maxparent - minparent - node.getParents().size() + 1;
                    value += v * v;
                }
            }
        }
        return value;
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
        for (int i = 0; i < layers.size(); i++) {
            Layer layer = layers.get(i);
            ArrayList<Pair<LNode, LNode>> connections = layer.getChildConnections();
            for (int k = 0; k < connections.size(); k++) {
                for (int l = k + 1; l < connections.size(); l++) {
                    Pair<LNode, LNode> a = connections.get(k);
                    Pair<LNode, LNode> b = connections.get(l);
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
        // ONLY WORKS FOR TREES, ROOT NODE HAS TO BE IN FIRST LAYER
        recursiveWideTreePlacement(layers.get(0).getElements().get(0), 0d);
    }

    public Pair<Double, Double> recursiveWideTreePlacement(LNode root, double next) {
        if (root.getChildren().isEmpty()) {
            root.setXPosLeft(next);
            return new Pair<Double, Double>(root.getXPosLeft(), root.getXPosRight());
        } else {
            double left = next;
            double right = next;
            double newnext = next;
            for (LNode child : root.generateSortedChildren()) {
                Pair<Double, Double> pos = recursiveWideTreePlacement(child, newnext);
                right = pos.second;
                newnext = right + config.getGapBetweenNodes();
            }
            double myPos = 0.5 * (left + right);
            root.setXPos(myPos);
            if (root.getPos() > 0) {
                LNode prev = layers.get(root.getLayer()).getElements().get(root.getPos() - 1);
                double shift = prev.getXPosRight() + config.getGapBetweenNodes() - root.getXPosLeft();
                if (shift > 0) {
                    shiftSubTree(root, shift);
                    left += shift;
                    right += shift;
                }
            }
            if (root.getXPosRight() > right) {
                right = root.getXPosRight();
            }
            if (root.getXPosLeft() < left) {
                left = root.getXPosLeft();
            }
            return new Pair<Double, Double>(left, right);
        }
    }

    public void shiftSubTree(LNode root, double shift) {
        root.setXPos(root.getXPos() + shift);
        for (LNode child : root.getChildren()) {
            shiftSubTree(child, shift);
        }
    }

    public void graphPlacement() {
        placed = true;

        // top down (places the nodes in a valid way, possible with larger gaps)
        for (Layer layer : layers) {
            double nextPos = 0d;
            for (LNode node : layer.getElements()) {
                node.setXPosLeft(nextPos);
                nextPos = node.getXPosRight() + config.getGapBetweenNodes() + config.getSpecialPaddingA() * node.getWidth();
            }
        }

        // shift entire layers left/right to relax macroscopic tensions
        for (int i = 0; i < layers.size() - 1; i++) {
            Layer layer = layers.get(i);
            int n = 0;
            double tension = 0d;
            for (Pair<LNode, LNode> connection : layer.getChildConnections()) {
                n++;
                tension += connection.first.getXPos() - connection.second.getXPos();
            }
            tension /= n;
            for (LNode lnode : layers.get(i+1).getElements()) {
                lnode.setXPos(lnode.getXPos() + tension);
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
            LNode lnode = entry.getValue().get(0);
            assert !lnode.isDummy();
            assert entry.getValue().size() > 1;
            points.add(new Point(Math.max(lnode.getXPosLeft(), Math.min(lnode.getXPosRight(),
                        lnode.getXPos() + (entry.getValue().get(1).getXPos() - lnode.getXPos()) * 0.5 * lnode.getHeight() / config.getLayerDistance())),
                    lnode.getLayer() * config.getLayerDistance() + 0.5 * lnode.getHeight()));
            LNode prev = lnode;
            for (int i = 1; i < entry.getValue().size(); i++) {
                lnode = entry.getValue().get(i);
                /* points.add(new Point(lnode.getXPos(), lnode.getLayer() * config.getLayerDistance() +
                        0.5 * (i == 0 ? 1 : -1) * (!lnode.isDummy() ? lnode.getHeight() : 0)));
                        */
                if (!lnode.isDummy()) {
                    assert i == entry.getValue().size() - 1;
                    points.add(new Point(Math.max(lnode.getXPosLeft(), Math.min(lnode.getXPosRight(),
                                lnode.getXPos() - (lnode.getXPos() - prev.getXPos()) * 0.5 * lnode.getHeight() / config.getLayerDistance())),
                            lnode.getLayer() * config.getLayerDistance() - 0.5 * lnode.getHeight()));
                } else {
                    points.add(new Point(lnode.getXPos(), lnode.getLayer() * config.getLayerDistance()));
                }
                prev = lnode;
            }
            layout.addEdge(entry.getKey(), points);
        }

        return layout;
    }

}
