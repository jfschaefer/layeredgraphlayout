package de.jfschaefer.layeredgraphlayout.visualizationfx;

import de.jfschaefer.layeredgraphlayout.layout.*;
import de.jfschaefer.layeredgraphlayout.layout.Vector;
import de.jfschaefer.layeredgraphlayout.util.Pair;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.util.*;

/**
 * Created by jfschaefer on 8/3/15.
 */

public class SimpleGraphFXEdgeFactory<E> extends GraphFXEdgeFactory<E> {
    protected Map<E, String> labelMap;
    protected Paint color;

    public SimpleGraphFXEdgeFactory(LayoutConfig layoutConfig, Map<E, String> labelMap, Paint color) {
        super(layoutConfig);
        this.labelMap = labelMap;
        this.color = color;
    }

    public Node getEdgeVisualization(E edge, ArrayList<EdgeSegment> segments) {
        Group g = new Group();

        // Step 1: Line
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

        // Step 2: Arrow head
        if (layoutConfig.getArrowheads()) {
            Polygon arrowhead = new Polygon(0d, 0d, 4d, 8d, 0d, 4d, -4d, 8d);
            EdgeSegment segment = segments.get(segments.size() - 1);
            double fraction = 1d;
            Vector derivative = segment.getDerivativeAt(fraction);
            double angle = Math.atan2(derivative.y, derivative.x);
            Point position = segment.getAt(fraction);
            arrowhead.setLayoutX(position.x);
            arrowhead.setLayoutY(position.y);
            arrowhead.getTransforms().add(new Rotate(365d / (2 * Math.PI) * angle + 90, 0, 0));
            arrowhead.setFill(color);
            g.getChildren().add(arrowhead);
        }

        // Step 3: Label
        String labelString = labelMap.get(edge);
        if (labelString != null && !labelString.isEmpty()) {
            final Pair<Point, Double> edgePosition = Util.naiveGetLabelPosition(segments);
            final Point position = edgePosition.first;
            final double angle = edgePosition.second;

            final Label label = new Label(labelString);
            label.setStyle("-fx-font-size: 0.8em;");
            label.setTextFill(color);
            label.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
                public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds newBounds) {
                    label.getTransforms().clear();
                    label.setLayoutX(position.x - 0.5 * newBounds.getWidth());
                    label.setLayoutY(position.y - label.getHeight());
                    label.getTransforms().add(new Rotate(angle, label.getWidth() * 0.5, label.getHeight()));
                }
            });
            label.setLayoutX(position.x - 0.5 * label.getWidth());
            label.setLayoutY(position.y - label.getHeight());
            label.getTransforms().add(new Rotate(angle, label.getWidth() * 0.5, label.getHeight()));
            g.getChildren().add(label);
        }

        return g;
    }
}
