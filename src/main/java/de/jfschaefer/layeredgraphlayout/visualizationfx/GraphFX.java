package de.jfschaefer.layeredgraphlayout.visualizationfx;

import de.jfschaefer.layeredgraphlayout.layout.*;

import de.jfschaefer.layeredgraphlayout.util.Pair;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class GraphFX<V, E> extends Group {
    public GraphFX(Layout<V, E> layout, GraphFXNodeFactory<V> nodeFactory, GraphFXEdgeFactory<E> edgeFactory) {
        Vector shift = layout.getShift();
        /* Rectangle r = new Rectangle(layout.getWidth(), layout.getHeight(), new Color(1d, 1d, 0d, 0.5));
        getChildren().add(r); */

        for (E edge : layout.getEdgeSet()) {
            ArrayList<EdgeSegment> segments = layout.getEdgePosition(edge);
            Node n = edgeFactory.getEdgeVisualization(edge, segments);
            getChildren().add(n);
            n.translateXProperty().set(n.translateXProperty().doubleValue() + shift.x);
            n.translateYProperty().set(n.translateYProperty().doubleValue() + shift.y);
        }

        for (V node : layout.getNodeSet()) {
            Pair<Double, Double> size = layout.getNodeSize(node);
            Node n = nodeFactory.getNodeVisualization(node, size.first, size.second);
            Point pos = layout.getNodeCenter(node);
            n.translateXProperty().set(n.translateXProperty().doubleValue() + shift.x + pos.x);
            n.translateYProperty().set(n.translateYProperty().doubleValue() + shift.y + pos.y);
            getChildren().add(n);
        }
    }
}
