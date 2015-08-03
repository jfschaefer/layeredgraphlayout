package de.jfschaefer.layeredgraphlayout.visualizationfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class SimpleGraphFXNodeFactory<V> extends GraphFXNodeFactory<V> {
    Map<V, String> nodeLabelMap;
    String backgroundStyleClass = "";
    String labelStyleClass = "";

    public SimpleGraphFXNodeFactory(Map<V, String> labelMap, String backgroundStyleClass, String labelStyleClass) {
        nodeLabelMap = labelMap;
        this.backgroundStyleClass = backgroundStyleClass;
        this.labelStyleClass = labelStyleClass;
    }

    public Node getNodeVisualization(V node, double width, double height) {
        Group g = new Group();
        Rectangle rect = new Rectangle(width, height);
        if (backgroundStyleClass.isEmpty()) {
            rect.setFill(Color.WHITE);
            rect.setStroke(Color.BLACK);
        } else {
            rect.getStyleClass().add(backgroundStyleClass);
        }
        final Label label = new Label(nodeLabelMap.get(node));
        if (labelStyleClass.isEmpty()) {
            // Keep default style??
        } else {
            label.getStyleClass().add(labelStyleClass);
        }

        g.getChildren().add(rect);
        rect.translateXProperty().set(-0.5 * width);
        rect.translateYProperty().set(-0.5 * height);
        g.getChildren().add(label);
        label.translateXProperty().set(-0.5 * label.boundsInLocalProperty().getValue().getWidth());
        label.translateYProperty().set(-0.5 * label.boundsInLocalProperty().getValue().getHeight());

        label.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                label.translateXProperty().set(-0.5 * label.boundsInLocalProperty().getValue().getWidth());
                label.translateYProperty().set(-0.5 * label.boundsInLocalProperty().getValue().getHeight());
            }
        });

        return g;
    }
}
