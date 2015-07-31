package de.jfschaefer.layeredgraphlayout.visualizationfx;

import javafx.scene.Node;

/**
 * Created by jfschaefer on 7/31/15.
 */

public abstract class GraphFXNodeFactory<V> {
    public abstract Node getNodeVisualization(V node, double width, double height);
}
