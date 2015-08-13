package de.jfschaefer.layeredgraphlayout.pgraph;

import de.jfschaefer.layeredgraphlayout.*;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraph;
import de.jfschaefer.layeredgraphlayout.lgraph.LGraphConfig;

import java.lang.reflect.Array;
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

    // these lists ignore fake edges and shall not change after the graph is locked
    protected ArrayList<PNode<V, E>> nodesWithSeveralParents;
    protected ArrayList<PNode<V, E>> nodesWithSeveralChildren;
    protected ArrayList<PNode<V, E>> nodesWithSeveralChildrenScaled;  //nodes with very many children are contained multiple times
    protected ArrayList<PNode<V, E>> nodesWithoutChildren;

    // excluding the ones from the root and the ones attaching sources somewhere
    protected ArrayList<PEdge<V, E>> fakeEdges;
    protected ArrayList<PEdge<V, E>> sourceFakeEdges;

    public static final LGraphConfig defaultLGraphConfig = new LGraphConfig();
    public static final Random random = new Random();

    private final LGraph<V, E> tmpLGraph = new LGraph<V, E>(defaultLGraphConfig);

    public PGraph() {
        nodeMap = new HashMap<Node<V, E>, PNode<V, E>>();
        nodes = new ArrayList<PNode<V, E>>();
        edges = new ArrayList<PEdge<V, E>>();
        locked = false;
        root = new PNode<V, E>(null, true);
        fakeEdges = new ArrayList<PEdge<V, E>>();
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
        nodesWithSeveralChildrenScaled = new ArrayList<PNode<V, E>>();
        nodesWithSeveralParents = new ArrayList<PNode<V, E>>();
        nodesWithoutChildren = new ArrayList<PNode<V, E>>();
        sourceFakeEdges = new ArrayList<PEdge<V, E>>();
        for (PNode<V, E> pnode : nodes) {
            if (pnode.isSource()) {
                PEdge<V, E> fakeEdge = new PEdge<V, E>(root, pnode);
                pnode.addParent(fakeEdge);
                root.addChild(fakeEdge);
                sourceFakeEdges.add(fakeEdge);
            }
            if (pnode.getChildren().size() >= 2) {
                nodesWithSeveralChildren.add(pnode);
                int i = pnode.getChildren().size();
                while (--i > 0) {
                    nodesWithSeveralChildrenScaled.add(pnode);
                }
            }
            if (pnode.getParents().size() >= 2) {
                nodesWithSeveralParents.add(pnode);
            }
            if (pnode.getChildren().size() == 0) {
                nodesWithoutChildren.add(pnode);
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

    /* The difference to generateLGraph is that it tries to be really efficient, not visually appealing,
       by pulling nodes down etc. It's just supposed to be used for assessments, e.g. for the simulated annealing.
     */
    public LGraph<V, E> generateLGraphEfficiently(LGraphConfig lconfig) {
        if (!locked) lock();
        LGraph<V, E> lgraph = new LGraph<V, E>(lconfig);
        resetLayers();
        for (PEdge<V, E> edge : root.getChildren()) {
            setLayersDFS(edge.to, 0);
        }

        for (PEdge<V, E> edge : root.getChildren()) {
            lgraph.addNode(edge.to.node, edge.to.getLayer());
            populateLGraph(lgraph, edge.to);
        }

        return lgraph;
    }

    private void fillTmpLGraph() {
        tmpLGraph.reset();
        resetLayers();
        for (PEdge<V, E> edge : root.getChildren()) {
            setLayersDFS(edge.to, 0);
        }

        for (PEdge<V, E> edge : root.getChildren()) {
            tmpLGraph.addNode(edge.to.node, edge.to.getLayer());
            populateLGraph(tmpLGraph, edge.to);
        }
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


    /*
            SIMULATED ANNEALING
     */

    public double runSimulatedAnnealing(int iterations) {
        if (!locked) lock();
        double temp = 1;
        double coolingFactor = Math.pow(0.001, 1d / iterations);
        double currentEnergy = getEnergy();
        if (nodes.isEmpty()) return currentEnergy;
        Collections.shuffle(nodesWithSeveralChildrenScaled, random);
        Collections.shuffle(sourceFakeEdges, random);

        while (temp > 0.001) {
            double action = random.nextDouble();
            if (action < 0.19) {          // add fake edge
                currentEnergy = tryAddFakeEdge(temp, currentEnergy);
                assert currentEnergy == getEnergy();
            } else if (action < 0.36) { // remove fake edge (slightly higher probability - we don't want too many fake edges) - update: apparently, that hypothesis is wrong
                currentEnergy = tryRemoveFakeEdge(temp, currentEnergy);
                assert currentEnergy == getEnergy();
            } else if (action < 0.7) {  // swap two children of some node
                currentEnergy = trySwapChildren(temp, currentEnergy);
                assert currentEnergy == getEnergy();
            } else {                       // change where one of the sources is attached to
                currentEnergy = tryChangeSource(temp, currentEnergy);
                assert currentEnergy == getEnergy();
            }
            temp *= coolingFactor;
        }
        return currentEnergy;
    }

    public static int quadraticRandint(int limit) {
        //return (int)(random.nextDouble() * limit);
        return (int)(random.nextDouble() * random.nextDouble() * limit);
        //return 0;
    }

    public double getEnergy() {
        fillTmpLGraph();
        double energy = tmpLGraph.getNumberOfIntersections();
        //energy += 1 - Math.exp(-Math.sqrt(lgraph.getNumberOfDummyNodes()));  // removing intersections has strict priority
        energy += 0.5 * tmpLGraph.getNumberOfDummyNodes() / (double)nodes.size();
        energy += 0.2 * Math.sqrt(tmpLGraph.getDensityMeasure());
        return energy;
    }

    public double tryChangeSource(double temp, double currentEnergy) {
        PEdge<V, E> fakeEdge = sourceFakeEdges.get(random.nextInt(sourceFakeEdges.size()));
        PNode<V, E> child = fakeEdge.to;
        int oldIndex = fakeEdge.from.getChildren().indexOf(fakeEdge);
        child.removeParent(fakeEdge);
        fakeEdge.from.removeChild(fakeEdge);
        PEdge<V, E> fakeEdge2;

        int fakeEdge2Swap = -1;

        if (fakeEdge.from.isRoot || random.nextDouble() < 0.4) {  // try attaching it somewhere else
            PNode<V, E> parent;
            if (Math.random() < 0.5 || nodesWithSeveralChildrenScaled.size() == 0) {      // try some completely random node
                parent = nodes.get(random.nextInt(nodes.size()));
            } else {
                parent = nodesWithSeveralChildrenScaled.get(random.nextInt(nodesWithSeveralChildrenScaled.size()));
            }
            fakeEdge2 = new PEdge<V, E>(parent, child);
            parent.addChild(fakeEdge2);
            if (parent.getChildren().size() > 1) {
                fakeEdge2Swap = random.nextInt(parent.getChildren().size()-1);
                parent.swapChildren(parent.getChildren().size() - 1, fakeEdge2Swap);
            }
            child.addParent(fakeEdge2);
        } else {    // try attaching it back to root.
            fakeEdge2 = new PEdge<V,E>(root, child);
            root.addChild(fakeEdge2);
            if (root.getChildren().size() > 1) {
                fakeEdge2Swap = random.nextInt(root.getChildren().size() - 1);
                root.swapChildren(root.getChildren().size() - 1, fakeEdge2Swap);
            }
            child.addParent(fakeEdge2);
        }
        if (!graphHasCycle(fakeEdge2.to)) {
            double newEnergy = getEnergy();
            if (acceptChange(currentEnergy, newEnergy, temp)) {
                sourceFakeEdges.remove(fakeEdge);
                sourceFakeEdges.add(fakeEdge2);
                return newEnergy;
            }
        }


        fakeEdge2.from.removeChild(fakeEdge2);
        child.removeParent(fakeEdge2);
        if (fakeEdge2Swap != -1) {
            PEdge<V, E> e = fakeEdge2.from.getChildren().get(fakeEdge2.from.getChildren().size()-1);
            fakeEdge2.from.getChildren().remove(fakeEdge2.from.getChildren().size()-1);
            fakeEdge2.from.getChildren().add(fakeEdge2Swap, e);
        }
        //fakeEdge.from.addChild(fakeEdge);
        fakeEdge.from.getChildren().add(oldIndex, fakeEdge);
        child.addParent(fakeEdge);
        return currentEnergy;
    }

    public double trySwapChildren(double temp, double currentEnergy) {
        if (nodesWithSeveralChildrenScaled.size() > 0) {
            PNode<V, E> parent = nodesWithSeveralChildrenScaled.get(quadraticRandint(nodesWithSeveralChildrenScaled.size()));
            nodesWithSeveralChildrenScaled.remove(parent);
            nodesWithSeveralChildrenScaled.add(parent);   // move it to the end - less likely to be picked again in near future
            int numberOfChildren = parent.getChildren().size();
            int i1 = random.nextInt(numberOfChildren);
            int i2 = random.nextInt(numberOfChildren - 1);
            i2 = i2 >= i1 ? i2 + 1 : i2;
            parent.swapChildren(i1, i2);
            double newEnergy = getEnergy();
            if (acceptChange(currentEnergy, newEnergy, temp)) {
                return newEnergy;
            } else {
                parent.swapChildren(i1, i2);  //swap back
                return currentEnergy;
            }
        }
        return currentEnergy;
    }

    public double tryAddFakeEdge(double temp, double currentEnergy) {
        // it's sufficient to only add fake edges from leaves to nodes with multiple parents
        if (nodesWithoutChildren.size() > 0 && nodesWithSeveralParents.size() > 0) {
            PNode<V, E> from = nodesWithoutChildren.get(random.nextInt(nodesWithoutChildren.size()));
            PNode<V, E> to = nodesWithSeveralParents.get(random.nextInt(nodesWithSeveralParents.size()));
            PEdge<V, E> fakeEdge = new PEdge<V, E>(from, to);
            from.addChild(fakeEdge);
            to.addParent(fakeEdge);

            if (!graphHasCycle(to)) {
                double newEnergy = getEnergy();
                if (acceptChange(currentEnergy, newEnergy, temp)) {
                    fakeEdges.add(fakeEdge);
                    return newEnergy;
                }
            }
            // otherwise undo changes
            from.removeChild(fakeEdge);
            to.removeParent(fakeEdge);
            return currentEnergy;
        }
        return currentEnergy;
    }

    public boolean graphHasCycle(PNode<V, E> root) {
        return cycleDetector(root, new HashMap<PNode<V, E>, Boolean>());
    }

    boolean cycleDetector(PNode<V, E> node, HashMap<PNode<V, E>, Boolean> map) {
        if (!map.containsKey(node)) {
            map.put(node, true);
        } else if (map.get(node)) {
            return true;
        } else {
            map.put(node, true);
        }
        for (PEdge<V, E> edge : node.getChildren()) {
            if (cycleDetector(edge.to, map)) {
                return true;
            }
        }
        map.put(node, false);
        return false;
    }

    public double tryRemoveFakeEdge(double temp, double currentEnergy) {
        if (fakeEdges.size() > 0) {
            PEdge<V, E> choice = fakeEdges.get(random.nextInt(fakeEdges.size()));
            ArrayList<PEdge<V, E>> toBeRemoved = new ArrayList<PEdge<V, E>>();
            if (Math.random() < 0.5) {    //remove single edge
                toBeRemoved.add(choice);
            } else {
                for (PEdge<V, E> parent : choice.to.getParents()) {
                    if (parent.isFake && !parent.from.isRoot) {
                        toBeRemoved.add(parent);
                    }
                }
            }
            ArrayList<Integer> positions = new ArrayList<Integer>(toBeRemoved.size());
            for (PEdge<V, E> fakeEdge : toBeRemoved) {
                int position = fakeEdge.from.getChildren().indexOf(fakeEdge);
                fakeEdge.from.getChildren().remove(position);
                positions.add(position);
                fakeEdge.to.removeParent(fakeEdge);
            }
            double newEnergy = getEnergy();
            if (acceptChange(currentEnergy, newEnergy, temp)) {
                fakeEdges.removeAll(toBeRemoved);
                return newEnergy;
            } else {
                for (int i = positions.size() - 1; i >= 0; i--) {
                    PEdge<V, E> fakeEdge = toBeRemoved.get(i);
                    fakeEdge.from.getChildren().add(positions.get(i), fakeEdge);
                    fakeEdge.to.addParent(fakeEdge);
                }
                return currentEnergy;
            }
        }
        return currentEnergy;
    }

    public boolean acceptChange(double oldEnergy, double newEnergy, double temp) {
        if (newEnergy < oldEnergy) return true;

        return Math.exp((oldEnergy - newEnergy)/temp) > Math.random();
    }
}

