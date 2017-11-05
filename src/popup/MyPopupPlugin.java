package popup;


import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;
import graph.Edge;
import graph.Node;


/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class MyPopupPlugin extends AbstractPopupGraphMousePlugin  {

    // Τα popup menu για κόμβους και ακμές
    private JPopupMenu nodePopup;
    private JPopupMenu edgePopup;
    
    // constructors
    public MyPopupPlugin(int modifiers) {
       super(modifiers);
    }
    
    
    public MyPopupPlugin() {
        this(MouseEvent.BUTTON3_MASK);
    }
    
    // getters
    public JPopupMenu getNodePopup() { return this.nodePopup; }
    public JPopupMenu getEdgePopup() { return this. edgePopup; }
    
    // setters
    public void setNodePopup(JPopupMenu popup) { this.nodePopup = popup; }
    public void setEdgePopup(JPopupMenu popup) { this.edgePopup = popup; }
    

 
    // Υλοποίηση abstract μεθόδου.
    @Override
    protected void handlePopup(MouseEvent e) {
        
        final VisualizationViewer<Node, Edge> vv =
                (VisualizationViewer<Node, Edge>)e.getSource();
        Point2D p = e.getPoint();
        
        GraphElementAccessor<Node, Edge> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            final Node node = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            if(node != null) {
                updateNodeMenu(node, vv, p);
                nodePopup.show(vv, e.getX(), e.getY());
            } else {
                final Edge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                if(edge != null) {
                    updateEdgeMenu(edge, vv, p);
                    edgePopup.show(vv, e.getX(), e.getY());
                  
                }
            }
        }
        
        
    }
    
    private void updateNodeMenu(Node node, VisualizationViewer vv, Point2D point) {
        
        if (nodePopup == null) return;
        Component[] menuComps = nodePopup.getComponents();
        
        for (Component comp: menuComps) {
            if (comp instanceof NodeMenuListener) {
                ((NodeMenuListener)comp).setNodeAndView(node, vv);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        
        
        }
    }
    
    private void updateEdgeMenu(Edge edge, VisualizationViewer vv, Point2D point) {
        
        if (edgePopup == null) return;
        Component[] menuComps = edgePopup.getComponents();
        
        for (Component comp: menuComps) {
            if (comp instanceof EdgeMenuListener) {
                ((EdgeMenuListener)comp).setEdgeAndView(edge, vv);
            }
            if (comp instanceof MenuPointListener) {
                ((MenuPointListener)comp).setPoint(point);
            }
        }
        
    }
    
    
    
}
