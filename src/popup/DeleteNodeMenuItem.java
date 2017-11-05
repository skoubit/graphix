
package popup;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import graph.Node;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class DeleteNodeMenuItem extends JMenuItem implements NodeMenuListener {

    private Node node;
    private VisualizationViewer vv;
    
    // Constructor
    public DeleteNodeMenuItem() {
        super("Διαγραφή κόμβου");
        
        this.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                vv.getPickedVertexState().pick(node, false);
                vv.getGraphLayout().getGraph().removeVertex(node);
                vv.repaint();
            }
        });
    }
    
    @Override
    public void setNodeAndView(Node node, VisualizationViewer vv) {
        this.node = node;
        this.vv = vv;
        this.setText("Διαγραφή κόμβου " + node.toString());
    }

    
    
}
