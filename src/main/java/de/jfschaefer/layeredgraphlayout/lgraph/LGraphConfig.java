package de.jfschaefer.layeredgraphlayout.lgraph;

import java.io.Serializable;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class LGraphConfig implements Serializable {
    protected double dummyNodeWidth = 10d;
    protected double gapBetweenNodes = 10d;

    public void setDummyNodeWidth(double width) {
        dummyNodeWidth = width;
    }

    public double getDummyNodeWidth() {
        return dummyNodeWidth;
    }

    public void setGapBetweenNodes(double gap) {
        gapBetweenNodes = gap;
    }

    public double getGapBetweenNodes() {
        return gapBetweenNodes;
    }
}
