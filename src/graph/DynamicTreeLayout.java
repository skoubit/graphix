
package graph;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.util.TreeUtils;
import java.awt.Point;
import java.util.Collection;


/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class DynamicTreeLayout extends TreeLayout<Node, Edge> {

    public DynamicTreeLayout(Forest<Node, Edge> g) {
        this(g, DEFAULT_DISTX, DEFAULT_DISTY);
    }

    public DynamicTreeLayout(Forest<Node, Edge> g, int distx) {
        this(g, distx, DEFAULT_DISTY);
    }

    public DynamicTreeLayout(Forest<Node, Edge> g, int distx, int disty) {
        super(g, distx, disty);
    }

    @Override
    protected void buildTree() {
        alreadyDone.clear(); // This was missing and prevented the layout to update positions

        this.m_currentPoint = new Point(20, 20);
        Collection<Node> roots = TreeUtils.getRoots(graph);
        if (roots.size() > 0 && graph != null) {
            calculateDimensionX(roots);
            for (Node v : roots) {
                calculateDimensionX(v);
                m_currentPoint.x += this.basePositions.get(v) / 2 + this.distX;
                buildTree(v, this.m_currentPoint.x);
            }
        }
    }

    private int calculateDimensionX(Node v) {
        int localSize = 0;
        int childrenNum = graph.getSuccessors(v).size();

        if (childrenNum != 0) {
            for (Node element : graph.getSuccessors(v)) {
                localSize += calculateDimensionX(element) + distX;
            }
        }
        localSize = Math.max(0, localSize - distX);
        basePositions.put(v, localSize);

        return localSize;
    }

    private int calculateDimensionX(Collection<Node> roots) {
        int localSize = 0;
        for (Node v : roots) {
            int childrenNum = graph.getSuccessors(v).size();

            if (childrenNum != 0) {
                for (Node element : graph.getSuccessors(v)) {
                    localSize += calculateDimensionX(element) + distX;
                }
            }
            localSize = Math.max(0, localSize - distX);
            basePositions.put(v, localSize);
        }

        return localSize;
    }
}
