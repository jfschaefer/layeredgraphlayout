package de.jfschaefer.layeredgraphlayout.visualizationfx;

import de.jfschaefer.layeredgraphlayout.layout.EdgeSegment;

import de.jfschaefer.layeredgraphlayout.layout.LayoutConfig;
import javafx.scene.Node;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public abstract class GraphFXEdgeFactory<E> {
    protected final LayoutConfig layoutConfig;

    public GraphFXEdgeFactory(LayoutConfig config) {
        layoutConfig = config;
    }

    public abstract Node getEdgeVisualization(E edge, ArrayList<EdgeSegment> segments);
}
