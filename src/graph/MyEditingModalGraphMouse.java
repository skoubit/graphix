
package graph;


import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.LabelEditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import java.awt.event.InputEvent;
import org.apache.commons.collections15.Factory;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class MyEditingModalGraphMouse extends EditingModalGraphMouse {
    
    
    public MyEditingModalGraphMouse(RenderContext rc, Factory vertexFactory, Factory edgeFactory) {
        super(rc, vertexFactory, edgeFactory);
    }
    
    // Override to user MyEditinGraphMousePlugin
    protected void loadPlugins() {
        pickingPlugin = new PickingGraphMousePlugin<Node,Edge>();
        animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<Node,Edge>();
        translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
        scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
        rotatingPlugin = new RotatingGraphMousePlugin();
        shearingPlugin = new ShearingGraphMousePlugin();
        editingPlugin = new MyEditingGraphMousePlugin(vertexFactory, edgeFactory);
        labelEditingPlugin = new LabelEditingGraphMousePlugin<Node,Edge>();
        annotatingPlugin = new AnnotatingGraphMousePlugin<Node,Edge>(rc);
        popupEditingPlugin = new EditingPopupGraphMousePlugin<Node,Edge>(vertexFactory, edgeFactory);
        add(scalingPlugin);
        setMode(Mode.EDITING);
    }
    
    
}
