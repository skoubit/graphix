
package algorithms;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import graph.DynamicTreeLayout;
import graph.Edge;
import graph.Node;
import graph.SearchGraph;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class BreadthFirstSearch extends SearchAlgorithm {

    // Η λίστα "ΑΝΟΙΚΤΕΣ" είαι μια δομή FIFO (first in first out)
    // Υλοποιείται ως συνδεδεμένη λίστα
    private LinkedList<Node> openNodes;
    
    // Η λίστα "ΚΛΕΙΣΤΕΣ" υλοποιείται ως μια απλή ArrayList
    private ArrayList<Node> closeNodes;
    
    // Ο constructor δέχεται ένα γράφο στον οποίο
    public BreadthFirstSearch(SearchGraph searchGraph) {
        
        // parent contructor
        super(searchGraph);

        // Αρχικοποίηση λιστών
        openNodes = new LinkedList();
        closeNodes = new ArrayList();
        
    }
    
    
    
    @Override
    public void start() {
        
         // Αν δεν υπάρχει κόμβος έναρξης  τερματισμός (αποτυχία)
        if (this.startNode == null) {
            this.state = FINISHED;
            log("ΣΦΑΛΜΑ: Δεν υπάρχει κόμβος έναρξης - Αδύνατη αναζήτηση");
            return;
        }
        
        if (this.state != INIT) return;
        
        this.state = RUNNING;
        
        log("ΑΡΧΙΚΟΠΟΙΗΣΗ: Κόμβος έναρξης->" + this.startNode.toString() + "\n");
        log("ΑΡΧΙΚΟΠΟΙΗΣΗ: Κόμβοι στόχοι->" + getTargetNodesStr() + "\n");
        
        // Προσθέτουμε την αρχική κατάσταση στις ανοικτές
        openNodes.add(startNode);
        this.addVertex(this.startNode); // set root node
        log("ΑΡΧΙΚΟΠΟΙΗΣΗ: Ο κόμβος " + startNode.toString() + " προστέθηκε στις ΑΝΟΙΚΤΕΣ\n" );

        // Αρχικοποιούμε την solutionPath της searchGraph
        this.searchGraph.resetSolutionPath();

    }

    @Override
    public void step() {
        
        // Αν είναι σε κατάσταση τερματισμού - επιστροφή
        if (this.state == FINISHED) {
            return;
        }

        // Αν η λίστα ΑΝΟΙΚΤΕΣ είναι κενή - αποτυχία
        if (openNodes.isEmpty()) {

            this.state = FINISHED;
            logFail();
            return;

        }
        
        // Επόμενο βήμα

        // Αφαίρεσε την 1η κατάσταση από τις ΑΝΟΙΚΤΕΣ
        // και πρόσθεσέ την στις ΚΛΕΙΣΤΕΣ
        ++this.steps;
        this.current = openNodes.removeFirst();
        closeNodes.add(this.current);
        log("ΒΗΜΑ " + this.steps + ": Ο κόμβος " + this.current.toString() + " βγήκε από τις ΑΝΟΙΚΤΕΣ και μπήκε στις ΚΛΕΙΣΤΕΣ\n");
        
        // Αν το κριτήριο τερματισμού είναι να μπει ο κόμβος στόχος
        // στις κλειστές και ο τρέχον κόμβος ανήκει στους στόχους
        // τερματίζει
        if (targetNodes.contains(this.current) && this.terminationCriteria == TARGET_INTO_CLOSE_LIST) {

            this.state = FINISHED;
            this.targetFound = this.current;
            logSuccess();
            return;

        }


        // Παίρνουμε τις εξερχόμενες ακμές της current 
        // σε αύξουσα διάταξη ανάλογα με την προτεραιότητα
        ArrayList<Edge> edges = searchGraph.getOutEdges(this.current, true);
        
        boolean expanded = false;

        for (Edge e : edges) {

            // Παίρνουμε το άλλο άκρο
            // Αν είναι στις ανοικτές ή στις κλειστές το αγνοούμε
            Node other = searchGraph.getOpposite(this.current, e);
            if (openNodes.contains(other) || closeNodes.contains(other)) {
                continue;
            }
            else {

                expanded = true;
                
                // Προσθήκη στις ανοικτές και στο tree
                openNodes.add(other);
                this.addChild(e, this.current, other);
                log("ΒΗΜΑ " + this.steps + ": Ο κόμβος " + other.toString() + " μπήκε στις ΑΝΟΙΚΤΕΣ\n");

                // Αν ο κόμβος other ανήκει στους στόχους και το κριτήριο
                // τερματισμού είναι να μπει στις ανοιχτές τερματίζουμε
                if (targetNodes.contains(other) && this.terminationCriteria == TARGET_INTO_OPEN_LIST) {

                    this.state = FINISHED;
                    this.targetFound = other;
                    
                }


            }


        }

        // Αύξηση του μετρητή κόμβων που αναπτύχθηκαν
        if (expanded)
            this.expandedNodes.add(this.current);
        
        // Έλεγχος αν μπήκαμε σε κατάσταση τερματισμού
        if (this.state == FINISHED) {
            logSuccess();
        }
        
    }

    @Override
    public JPanel getDefaultPanel() {
        
        // Αν δεν υπάρχουν κόμβοι, επιστροφή
        if (this.getVertexCount() == 0) return null;
        
        int width = 650;
        int height = 500;
        
        
        Layout<Node, Edge> treeLayout = new DynamicTreeLayout((Forest<Node, Edge>)this);
        //treeLayout.setSize(new Dimension(width, height));
        
        VisualizationViewer<Node, Edge> vv = new VisualizationViewer(treeLayout);
        vv.setPreferredSize(new Dimension(width,height));
        DefaultModalGraphMouse<Node, Edge> graphMouse = new DefaultModalGraphMouse();
        //graphMouse.setMode(DefaultModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.setLayout(new BorderLayout());
        
       
      
        //GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        
        
        // Position vertices in a grid
        Transformer<Node, Point2D> locationTransformer = new Transformer<Node, Point2D>() {
            
            @Override
            public Point2D transform(Node node) {
                int row = node.getRow();
                int col = node.getCol();
                int offsetX = 60;
                int offsetY = 60;
                return new Point2D.Double((double)(col*60 + offsetY), (double)(row*60 + offsetX));
            }
            
        };
        //treeLayout.setInitializer(locationTransformer);
        
        
        // Set node colors
        Transformer<Node, Paint> nodePaint = new Transformer<Node, Paint>() {
        
            @Override
            public Paint transform(Node n) {
                
                // Αν είναι ο τρέχον κόμβος - άσπρος
                if (n == current) return Color.WHITE;
                
                // Αν είναι κόμβος έναρξης - κόκκινος
                if (n.isStartNode()) return Color.RED;
                
                // Αν είναι κόμβος-στόχος - πράσινος
                if (n.isTargetNode()) return Color.GREEN;
                
                // Αν ανήκει στις ανοικτές μπλε
                if (openNodes.contains(n)) return Color.BLUE; 
                
                // Αν ανήκει στις κλειστές μαύρος
                if (closeNodes.contains(n)) return Color.BLACK; 
                
                return Color.WHITE;
            }
        };
        
  
        
        
        vv.getRenderContext().setVertexFillPaintTransformer(nodePaint);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.NW);
        //vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        
        return vv;
        
        
    }
    
    
     // log fail message
    // Μύνημα αποτυχίας εύρεσης κόμβου-στόχου
    private void logFail() {
        
        log("ΤΕΡΜΑΤΙΣΜΟΣ ΑΝΑΖΗΤΗΣΗΣ: Δεν βρέθηκε κόμβος/στόχος\n");
        log("ΑΝΟΙΚΤΕΣ: " + getOpenNodesStr() + "\n");
        log("ΚΛΕΙΣΤΕΣ: " + getCloseNodesStr() + "\n");
        log("ΒΗΜΑΤΑ: " + this.steps + "\n");
        log("ΚΟΜΒΟΙ ΠΟΥ ΑΝΑΠΤΥΧΘΗΚΑΝ: "  + this.expandedNodes.size() + "\n");
        
        
    }
    
    // log state - Εκτύπωση τρέχουσας κατάστασης
    private void logSuccess() {
        
        log("\n*********************************************\n");
        log("ΤΕΡΜΑΤΙΣΜΟΣ ΑΝΑΖΗΤΗΣΗΣ: Βρέθηκε κόμβος/στόχος!!!\n");
        log("ΑΝΟΙΚΤΕΣ: " + getOpenNodesStr() + "\n");
        log("ΚΛΕΙΣΤΕΣ: " + getCloseNodesStr() + "\n");
        log("ΒΡΕΘΗΚΕ Ο ΚΟΜΒΟΣ/ΣΤΟΧΟΣ: " + targetFound.toString() + "\n");
        log("ΒΗΜΑΤΑ: " + this.steps + "\n");
        log("ΚΟΜΒΟΙ ΠΟΥ ΑΝΑΠΤΥΧΘΗΚΑΝ: "  + this.expandedNodes.size() + "\n");
        log("ΜΟΝΟΠΑΤΙ: " + getPathStr(this.targetFound) + "\n");
        log("ΒΑΘΟΣ ΚΟΜΒΟΥ/ΣΤΟΧΟΥ: " + getDepth(this.targetFound) + "\n");
        log("ΚΟΣΤΟΣ ΜΟΝΟΠΑΤΙΟΥ: " + getPathCost(this.targetFound) + "\n");
        log("*********************************************\n");
        
        // Ενημερώνουμε το searchGraph για την διαδρομή-λύση
        for (Node n : this.getPath(this.targetFound)) {
            this.searchGraph.addSolutionNode(n);
            if (n != this.getRoot()) {
                Edge parentEdge = this.getParentEdge(n);
                this.searchGraph.addSolutionEdge(parentEdge);
            }
                     
        }
        
        
    }
    
    
     // Επιστροφή της λίστας ΑΝΟΙΚΤΕΣ ως string 
    public String getOpenNodesStr() {
        
        String openNodesStr = "";
        
        for (Node n : this.openNodes)
            openNodesStr = openNodesStr + n.getLabel() + ",";
        
        // Αφαιρούμε το τελικό ',' και επιστρέφουμε
        if (openNodesStr.length() >1) 
            return openNodesStr.substring(0, openNodesStr.length()-1);
        else
            return "";
        
        
     }
    
    // Επιστροφή της λίστας ΚΛΕΙΣΤΕΣ ως string 
    public String getCloseNodesStr() {
        
        String closeNodesStr = "";
        
        for (Node n : this.closeNodes)
            closeNodesStr = closeNodesStr + n.getLabel() + ",";
        
        // Αφαιρούμε το τελικό ',' και επιστρέφουμε
        if (closeNodesStr.length() >1) 
            return closeNodesStr.substring(0, closeNodesStr.length()-1);
        else
            return "";
        
     }
    
    
}
