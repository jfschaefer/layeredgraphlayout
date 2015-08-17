package de.jfschaefer.layeredgraphlayout.latex;

import de.jfschaefer.layeredgraphlayout.layout.*;
import de.jfschaefer.layeredgraphlayout.util.Pair;

import java.util.*;

/**
 * Created by jfschaefer on 8/10/15.
 */

public class LatexGenerator<V, E> {
    public static <V, E> String generateLatex(Layout<V, E> layout, Map<V, String> nodeMap, Map<E, String> edgeMap, boolean drawBox) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{tikzpicture}\n");
        de.jfschaefer.layeredgraphlayout.layout.Vector shift = layout.getShift();

        // Step 1: Draw nodes
        for (V node : layout.getNodeSet()) {
            sb.append("\\node (rect) at (");
            Pair<Double, Double> size = layout.getNodeSize(node);
            Point pos = layout.getNodeCenter(node);
            sb.append(pos.x + shift.x);
            sb.append("pt, ");
            sb.append(layout.getHeight() - pos.y - shift.y);
            sb.append("pt) [");
            if (drawBox) {
                sb.append("draw");
            }
            sb.append(", minimum width=");
            sb.append(size.first);
            sb.append("pt, minimum height=");
            sb.append(size.second);
            sb.append("pt] {");
            sb.append(latexEscape(nodeMap.get(node)));
            sb.append("};\n");
        }

        // Step 2: Draw edges
        for (E edge : layout.getEdgeSet()) {
            ArrayList<EdgeSegment> segments = layout.getEdgePosition(edge);
            //for (EdgeSegment segment : segments) {
            for (int i = 0; i < segments.size(); i++) {
                EdgeSegment segment = segments.get(i);
                sb.append("\\draw");

                if (layout.getConfig().getArrowheads() && i == segments.size() - 1) {
                    sb.append("[->]");
                }

                sb.append(" (");
                sb.append(segment.start.x + shift.x);
                sb.append("pt, ");
                sb.append(layout.getHeight() - segment.start.y - shift.y);
                sb.append("pt)");
                if (segment.isBezier()) {
                    sb.append(" .. controls (");
                    sb.append(segment.control1.x + shift.x);
                    sb.append("pt, ");
                    sb.append(layout.getHeight() - segment.control1.y - shift.y);
                    sb.append("pt) and (");
                    sb.append(segment.control2.x + shift.x);
                    sb.append("pt, ");
                    sb.append(layout.getHeight() - segment.control2.y - shift.y);
                    sb.append("pt) .. ");
                } else {
                    sb.append(" -- ");
                }
                sb.append("(");
                sb.append(segment.end.x + shift.x);
                sb.append("pt, ");
                sb.append(layout.getHeight() - segment.end.y - shift.y);
                sb.append("pt);\n");
            }

            String labelString = edgeMap.get(edge);
            if (labelString != null && !labelString.isEmpty()) {
                final Pair<Point, Double> edgePosition = Util.naiveGetLabelPosition(segments);
                final Point position = edgePosition.first;
                final double angle = edgePosition.second;
                sb.append("\\node[rotate=");
                sb.append(-angle);
                sb.append(", align=left] at (");
                sb.append(position.x + shift.x);
                sb.append("pt, ");
                sb.append(layout.getHeight() - position.y - shift.y);
                sb.append("pt) {");
                sb.append(latexEscape(labelString));
                sb.append("\\\\};\n");
            }
        }

        sb.append("\\end{tikzpicture}\n");

        return sb.toString();
    }

    public static String latexEscape(String s) {
        return s.replace("_", "\\_").replace("{", "\\{").replace("}", "\\}");    //TODO: Do proper escaping
    }
}
