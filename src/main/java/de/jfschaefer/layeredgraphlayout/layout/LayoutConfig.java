package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class LayoutConfig {
    protected boolean bezier = true;
    protected boolean arrowheads = true;
    protected double controlPointDistance = 0.5d;
    protected double graphPadding = 16d;

    public void setBezier(boolean bezier) {
        this.bezier = bezier;
    }

    public boolean getBezier() {
        return bezier;
    }

    public void setArrowheads(boolean arrowheads) {
        this.arrowheads = arrowheads;
    }

    public boolean getArrowheads() {
        return arrowheads;
    }

    public void setControlPointDistance(double value) {
        controlPointDistance = value;
    }

    public double getControlPointDistance() {
        return controlPointDistance;
    }

    public double getGraphPadding() {
        return graphPadding;
    }

    public void setGraphPadding(double padding) {
        graphPadding = padding;
    }
}
