package de.jfschaefer.layeredgraphlayout.visualizationfx;

import de.jfschaefer.layeredgraphlayout.layout.EdgeSegment;

import javafx.scene.Node;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public abstract class GraphFXEdgeFactory<E> {
    public abstract Node getEdgeVisualization(E edge, ArrayList<EdgeSegment> segments);
}
