package de.jfschaefer.layeredgraphlayout.layout;

import de.jfschaefer.layeredgraphlayout.Node;

/**
 * Created by jfschaefer on 7/31/15.
 */

public class Util {
    public static Point getTopLeftCorner(Node n, Point center) {
        return new Point(center.x - 0.5 * n.getWidth() , center.y - 0.5 * n.getHeight());
    }
    public static Point getBottomRightCorner(Node n, Point center) {
        return new Point(center.x + 0.5 * n.getWidth(), center.y + 0.5 * n.getHeight());
    }
}
