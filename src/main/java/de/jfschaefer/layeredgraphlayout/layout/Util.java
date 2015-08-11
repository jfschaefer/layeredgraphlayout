package de.jfschaefer.layeredgraphlayout.layout;

import de.jfschaefer.layeredgraphlayout.Node;
import de.jfschaefer.layeredgraphlayout.util.Pair;
import de.jfschaefer.layeredgraphlayout.layout.EdgeSegment;

import java.util.ArrayList;

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

    public static Pair<Point, Double> naiveGetLabelPosition(ArrayList<EdgeSegment> edge) {
        final EdgeSegment segment = edge.get(0);
        final double fraction = edge.size() == 1 ? 0.5 : 1d;
        final Vector derivative = segment.getDerivativeAt(fraction);
        double angleRaw = Math.atan2(derivative.y, derivative.x);
        if (angleRaw > Math.PI * 0.5 && angleRaw < Math.PI * 1.5) {
            angleRaw -= Math.PI;
        }
        final double angle = 365d / (2 * Math.PI) * angleRaw;
        final Point position = segment.getAt(fraction);
        return new Pair<Point, Double>(position, angle);
    }
}
