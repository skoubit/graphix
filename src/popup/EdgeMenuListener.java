
package popup;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.geom.Point2D;
import graph.Edge;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public interface EdgeMenuListener {
    void setEdgeAndView(Edge e, VisualizationViewer vv); 
}
