
package popup;

import javax.swing.JPopupMenu;
import graphix.MainFrame;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class MyPopupMenus {
    
    // Edge popup
    public static JPopupMenu edgePopup(MainFrame frame) {
        
        JPopupMenu popup = new JPopupMenu("Μενού ακμών");
        popup.add(new EditEdgeMenuItem(frame));
        popup.add(new DeleteEdgeMenuItem());
        
        return popup;
        
        
    }
    
    
    // Node popup
    public static JPopupMenu nodePopup(MainFrame frame) {
        
        JPopupMenu popup = new JPopupMenu("Μενού κόμβων");
        popup.add(new EditNodeMenuItem(frame));
        popup.add(new DeleteNodeMenuItem());
        
        return popup;
        
        
    }
    
}
