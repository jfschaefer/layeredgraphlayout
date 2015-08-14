# README

This library was developed as a tool for displaying directed graphs in a computer linguistic project.
It uses layered graph layouts, where the nodes are arranged in layers in a way that all the edges are
directed to a lower.

## What's in this library?

This library uses a variety of graph representations, which support different algorithms.
In order to visualize a graph, you will have to use several representations.
In the end, you can generate a `Layout` object, which contains the node and edge positions.
The library also has tools to create a JavaFX node or LaTeX code from such a `Layout` object.
The provided algorithms are only intended to be used for small graphs (maybe up to 100 nodes).

I'll try to give a short overview over the different representations.

### GenGraph

The `GenGraph` class can be used to store any directed graph.
It provides a simple algorithm that removes cycles in the graph by flipping a few edges.
This is very important, because most algorithms don't work for cyclic graphs.
After all, a layered graph representation actually requires an acyclic graph,
because by definition all edges have to a lower layer.
In the final layout, the edges will of course be flipped back and thus point upwards.
A `GenGraph` object can create a `PGraph`.
This is not intended to be used to turn any directed graph into an acyclic graph.
It's rather supposed to fix rare cases, when there is a cycle.

### PGraph

The `PGraph` class designed to run powerful algorithms to reduce the number of intersecting edges,
because intersections make graphs a lot less readable.
In the `PGraph` representation, a node has an ordered sequence of children.
The algorithms have two ways of manipulating the graph. They can change the order of the children
of nodes, and they can add invisible edges.
Currently, the best algorithm uses _simulated annealing_.
A `PGraph` can generate an `LGraph` representation.

### LGraph

An `LGraph` is a layered graph representation.
Each layer is a list of nodes, which have parents in the previous layer and children in the next layer.
Edges across several layers are represented with dummy nodes in each layer in between, such that
a long edge is a sequence of one-layer edges.
It would be possible to run a sugiyama layout algorithm on this representation, but I haven't implemented it yet.
After all, the simulated annealing appears to deliver much better results for the target graphs.

In this representation, the nodes can also be placed along the x-axis.
Two algorithms are provided for this: One for trees, and one for graphs in general.

After the placement, a `Layout` instance can be generated.

### Layout

A `Layout` contains absolute positions of nodes and edges. It can also be used to create latex output using the
`LatexGenerator` or a JavaFX node by using the `GraphFX` class.

### Tree

The `Tree` class can be used to represent trees (suprise!).
A `Tree` can directed create an `LGraph`.
Even though tree layout is supported a bit, I don't recommend using this library if you just want to
visualize trees.
There should exist much simpler and better algorithms to visualize trees.

## Installation

```Bash
git clone https://github.com/jfschaefer/layeredgraphlayout.git
cd layeredgraphlayout
mvn install
```

## Add dependency

### sbt

If you're using `sbt`, you add the following lines to your `build.sbt` file:
```sbt
libraryDependencies += "de.jfschaefer.layeredgraphlayout" % "layeredgraphlayout" % "1.0-SNAPSHOT"

resolvers += Resolver.mavenLocal
```

## Example use case in Scala

Let's assume that you have some `jgraph` representation of a directed graph that is (at least almost) acyclic.

```Scala
import de.jfschaefer.layeredgraphlayout._
import de.jfschaefer.layeredgraphlayout.visualizationfx.{SimpleGraphFXEdgeFactory, SimpleGraphFXNodeFactory, GraphFX}

import javafx.scene.paint.Color

//... - somehow we got a jgraph, which we're now trying to visualize

// create GenGraph representation and create maps for edge and node labels.
val genGraph = new gengraph.GenGraph[NodeType, EdgeType]()
val edgeLabelMap : java.util.Map[EdgeType, String] = new java.util.HashMap()
val nodeLabelMap : java.util.Map[NodeType, String] = new java.util.HashMap()
for (node : NodeType <- jgraph.vertexSet) {
  genGraph.addNode(node, node.getWidth, node.getHeight)
  nodeLabelMap.put(node, node.getLabel)
}
for (edge : EdgeType <- jgraph.edgeSet) {
  genGraph.addEdge(edge, edge.getSource, edge.getTarget)
  edgeLabelMap.put(edge, edge.getLabel)
}

// generate PGraph representation and run the algorithm on it.
val pGraph = genGraph.generatePGraph()    // removes cycles automatically
pGraph.runSimulatedAnnealing(2000)        // 2000 iterations

// generate LGraph representation
val lGraph = pGraph.generateLGraph(new lgraph.LGraphConfig)
lGraph.graphPlacement()

// generate the layout and create a JavaFX visualization
val mylayout = lGraph.getLayout(new layout.LayoutConfig)

val graphfx = new GraphFX[NodeType, EdgeType](mylayout,
        new SimpleGraphFXNodeFactory[NodeType](nodeLabelMap, "", ""),
        new SimpleGraphFXEdgeFactory[EdgeType](new layout.LayoutConfig, edgeLabelMap, Color.BLACK))
```
