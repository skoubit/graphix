
package popup;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import graphix.MainFrame;
import graph.Edge;
import graph.EdgeForm;
import graph.SearchGraph;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class EditEdgeMenuItem extends JMenuItem implements EdgeMenuListener, MenuPointListener {
     
    private Edge edge;
    private VisualizationViewer vv;
    Point2D point;
    SearchGraph searchGraph;
    
    // setters
    public void setPoint(Point2D point) { this.point = point; }
    
    @Override
    public void setEdgeAndView(Edge edge, VisualizationViewer vv) {
        this.edge = edge;
        this.vv = vv;
    }
    
    // consructor
    public EditEdgeMenuItem(MainFrame frame) {
        super("Επεξεργασία ακμής...");
        
        this.searchGraph = frame.getSearchGraph();
        
        this.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                EdgeForm edgeForm = new EdgeForm(frame, true, edge, searchGraph.getOperators());
                edgeForm.setLocation((int)point.getX() + frame.getX(), (int)point.getY() + frame.getY());
                edgeForm.setVisible(true);
                
                // Αν πατήσαμε ΟΚ ενημερώνουμε το view
                if (edgeForm.isOk()) 
                    vv.repaint();
               
                
            }
        });
    }
    
    
    
    
    
    
    
    
}
