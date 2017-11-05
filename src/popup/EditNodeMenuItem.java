
package popup;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import graphix.MainFrame;
import graph.Node;
import graph.NodeForm;
import graph.SearchGraph;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class EditNodeMenuItem extends JMenuItem implements NodeMenuListener, MenuPointListener {
    
    private Node node;
    private VisualizationViewer vv;
    Point2D point;
    SearchGraph searchGraph;
    
    // setters
    public void setPoint(Point2D point) { this.point = point; }
    
    @Override
    public void setNodeAndView(Node node, VisualizationViewer vv) {
        this.node = node;
        this.vv = vv;
    }
    
    // consructor
    public EditNodeMenuItem(MainFrame frame) {
        super("Επεξεργασία κόμβου...");
        
        this.searchGraph = frame.getSearchGraph();
        
        this.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                NodeForm nodeForm = new NodeForm(frame, true, node);
                nodeForm.setLocation((int)point.getX() + frame.getX(), (int)point.getY() + frame.getY());
                nodeForm.setVisible(true);
                
                // Αν πατήσαμε ΟΚ ελέγχουμε μήπως ο κόμβος έγινε αρχικός 
                // και ενημερώνουμε τους υπόλοιπους
                if (nodeForm.isOk())  {
                    
                    if (node.isStartNode()) {
                        for (Node n : searchGraph.getVertices())
                            if (n != node && n.isStartNode()) n.setStartNode(false);
                    }
                    
                    // update the view
                    vv.repaint();
                    
                }
                
                
                
            }
        });
    }
    
    
}
