package de.jfschaefer.layeredgraphlayout.visualizationfx;

import de.jfschaefer.layeredgraphlayout.layout.EdgeSegment;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

import java.util.*;

/**
 * Created by jfschaefer on 8/3/15.
 */

public class SimpleGraphFXEdgeFactory<E> extends GraphFXEdgeFactory<E> {
    Map<E, String> labelMap;
    Paint color;
    public SimpleGraphFXEdgeFactory(Map<E, String> labelMap, Paint color) {
        this.labelMap = labelMap;
        this.color = color;
    }

    public Node getEdgeVisualization(E edge, ArrayList<EdgeSegment> segments) {
        Group g = new Group();

        for (EdgeSegment segment : segments) {
            if (segment.isBezier()) {
                CubicCurve curve = new CubicCurve(segment.getStart().x, segment.getStart().y,
                        segment.getControl1().x, segment.getControl1().y,
                        segment.getControl2().x, segment.getControl2().y,
                        segment.getEnd().x, segment.getEnd().y);
                curve.setFill(Color.TRANSPARENT);
                curve.setStroke(color);
                g.getChildren().add(curve);
            } else {
                Line line = new Line(segment.getStart().x, segment.getStart().y,
                        segment.getEnd().x, segment.getEnd().y);
                line.setStroke(color);
                g.getChildren().add(line);
            }
        }
        return g;
    }
}
