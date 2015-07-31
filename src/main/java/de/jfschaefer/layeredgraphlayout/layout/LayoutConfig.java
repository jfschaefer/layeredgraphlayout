package de.jfschaefer.layeredgraphlayout.layout;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class LayoutConfig {
    protected boolean bezier = true;
    protected double controlPointDistance = 0.5d;

    public void setBezier(boolean bezier) {
        this.bezier = bezier;
    }

    public boolean getBezier() {
        return bezier;
    }

    public void setControlPointDistance(double value) {
        controlPointDistance = value;
    }

    public double getControlPointDistance() {
        return controlPointDistance;
    }
}