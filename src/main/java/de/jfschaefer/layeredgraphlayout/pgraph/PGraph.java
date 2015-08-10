package de.jfschaefer.layeredgraphlayout.pgraph;

import de.jfschaefer.layeredgraphlayout.*;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraph;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraphConfig;

import java.util.*;

/**
 * Created by jfschaefer on 8/7/15.
 */

public class PGraph<V, E> {
    protected Map<Node<V, E>, PNode<V, E>> nodeMap;
    // using lists instead of sets for storing is easier, and possibly more efficient in this case
    protected ArrayList<PNode<V, E>> nodes;   //actual nodes (i.e. excluding root)
    protected ArrayList<PEdge<V, E>> edges;   //actual edges (no fake edges)
    protected boolean locked;    //locked means that no new nodes or edges can be added anymore

    protected PNode<V, E> root;

    protected ArrayList<PNode<V, E>> nodesWithSeveralParents;
    protected ArrayList<PNode<V, E>> nodesWithSeveralChildren;

    public PGraph() {
        nodeMap = new HashMap<Node<V, E>, PNode<V, E>>();
        nodes = new ArrayList<PNode<V, E>>();
        edges = new ArrayList<PEdge<V, E>>();
        locked = false;
        root = new PNode<V, E>(null, true);
    }

    public void addNode(Node<V, E> node) {
        assert !locked;
        assert !nodeMap.containsKey(node);
        PNode<V, E> pnode = new PNode<V, E>(node);
        nodeMap.put(node, pnode);
        nodes.add(pnode);
    }

    public void addEdge(Edge<V, E> edge) {
        assert !locked;
        PNode<V, E> pnodeFrom = nodeMap.get(edge.getFrom());
        PNode<V, E> pnodeTo = nodeMap.get(edge.getTo());
        PEdge<V, E> pedge = new PEdge<V, E>(edge, pnodeFrom, pnodeTo);
        pnodeFrom.addChild(pedge);
        pnodeTo.addParent(pedge);
        edges.add(pedge);
    }

    public void lock() {
        if (locked) return;
        locked = true;
        nodesWithSeveralChildren = new ArrayList<PNode<V, E>>();
        nodesWithSeveralParents = new ArrayList<PNode<V, E>>();
        for (PNode<V, E> pnode : nodes) {
            if (pnode.isSource()) {
                PEdge<V, E> fakeEdge = new PEdge<V, E>(root, pnode);
                pnode.addParent(fakeEdge);
                root.addChild(fakeEdge);
            }
            if (pnode.getChildren().size() >= 2) {
                nodesWithSeveralChildren.add(pnode);
            }
            if (pnode.getParents().size() >= 2) {
                nodesWithSeveralParents.add(pnode);
            }
        }
    }

    public LGraph<V, E> generateLGraph(LGraphConfig lconfig) {
        if (!locked) lock();
        LGraph<V, E> lgraph = new LGraph<V, E>(lconfig);

        resetLayers();
        setLayers();

        for (PEdge<V, E> edge : root.getChildren()) {
            lgraph.addNode(edge.to.node, edge.to.getLayer());
            populateLGraph(lgraph, edge.to);
        }

        return lgraph;
    }

    protected void populateLGraph(LGraph<V, E> lgraph, PNode<V, E> node) {
        for (PEdge<V, E> edge : node.getChildren()) {
            if (!lgraph.containsNode(edge.to.node)) {
                lgraph.addNode(edge.to.node, edge.to.getLayer());
                populateLGraph(lgraph, edge.to);
            }
            if (!edge.isFake) {
                lgraph.addEdge(edge.edge);
            }
        }
    }

    protected void setLayers() {
        for (PEdge<V, E> edge : root.getChildren()) {
            setLayersDFS(edge.to, 0);
        }
    }

    protected void setLayersDFS(PNode<V, E> node, int layer) {
        if (layer > node.getLayer()) {
            node.setLayer(layer);
            for (PEdge<V, E> edge : node.getChildren()) {
                setLayersDFS(edge.to, layer + 1);
            }
        }
    }

    protected void resetLayers() {
        for (PNode<V, E> pnode : nodes) {
            pnode.resetLayer();
        }
    }
}

