
package popup;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import graph.Edge;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class DeleteEdgeMenuItem extends JMenuItem implements EdgeMenuListener {
    
    private Edge edge;
    private VisualizationViewer vv;
    
    // consructor
    public DeleteEdgeMenuItem() {
        super("Διαγραφή ακμής");
        this.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                vv.getPickedEdgeState().pick(edge, false);
                vv.getGraphLayout().getGraph().removeEdge(edge);
                vv.repaint();
            }
        });
    }

    @Override
    public void setEdgeAndView(Edge edge, VisualizationViewer vv) {
        
        this.edge = edge;
        this.vv = vv;
        this.setText("Διαγραφή ακμής " + edge.toString());
    }


    
}
