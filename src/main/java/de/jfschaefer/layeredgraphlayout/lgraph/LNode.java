package de.jfschaefer.layeredgraphlayout.lgraph;

import java.util.*;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class LNode {
    protected boolean dummy;
    protected int layer;
    protected double width;
    protected double height;
    // The pixel offset of the center
    protected double xPos = 0d;

    protected Set<LNode> parents;
    protected Set<LNode> children;

    protected LGraphConfig config;

    protected int pos = -1;

    public LNode(int layer, boolean isDummy, double width, double height, LGraphConfig config) {
        this.layer = layer;
        dummy = isDummy;
        this.width = width;
        this.height = height;
        parents = new HashSet<LNode>();
        children = new HashSet<LNode>();
        this.config = config;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void addChild(LNode child) {
        children.add(child);
    }

    public void addParent(LNode parent) {
        parents.add(parent);
    }

    public final Set<LNode> getChildren() {
        return children;
    }

    public final Set<LNode> getParents() {
        return parents;
    }

    public ArrayList<LNode> generateSortedChildren() {
        ArrayList<LNode> list = new ArrayList<LNode>();
        for (LNode child : getChildren()) {
            list.add(child);
        }
        list.sort(new Comparator<LNode>() {
            public int compare(LNode o1, LNode o2) {
                return o1.getPos() - o2.getPos();
            }
        });
        return list;
    }

    public LNode getLeftMostChild() {
        LNode leftmost = null;
        int leftMostPos = Integer.MAX_VALUE;
        for (LNode child : children) {
            if (child.getPos() < leftMostPos) {
                leftmost = child;
                leftMostPos = child.getPos();
            }
        }
        return leftmost;
    }

    public LNode getRightMostChild() {
        LNode rightmost = null;
        int rightMostPos = Integer.MIN_VALUE;
        for (LNode child : children) {
            if (child.getPos() > rightMostPos) {
                rightmost = child;
                rightMostPos = child.getPos();
            }
        }
        return rightmost;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isDummy() {
        return dummy;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xpos) {
        xPos = xpos;
    }

    public double getXPosLeft() {
        return xPos - 0.5 * width;
    }

    public double getXPosRight() {
        return xPos + 0.5 * width;
    }

    public void setXPosLeft(double pos) {
        xPos = pos + 0.5 * width;
    }
}
