 
package algorithms;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import graph.Edge;
import graph.Node;
import graph.SearchGraph;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public abstract class SearchAlgorithm extends DelegateTree<Node, Edge> {

    // Σταθερές για το κριτήριο τερματισμού
    public static int TARGET_INTO_OPEN_LIST = 0;
    public static int TARGET_INTO_CLOSE_LIST = 1;
    
    // Σταθερές κατάστασης του αλγορίθμου
    public static int INIT = 0;
    public static int RUNNING = 1;
    public static int FINISHED = 2;    


    // Ο γράφος στον οποίο θα εφαρμοστεί η αναζήτηση κατά βάθος
    // μαζί με τον κόμβο έναρξης,  τους κόμβους-στόχους 
    protected SearchGraph searchGraph;
    protected Node startNode;
    protected ArrayList<Node> targetNodes = new ArrayList();

    // Ο τρέχον κόμβος που εξετάζεται
    protected Node current;
    
    // Κατάσταση αλγορίθμου, καταμέτρηση βημάτων και κόμβων που αναπτύχθηκαν
    protected int state;
    protected int steps = 0;
    protected ArrayList<Node> expandedNodes;
    
    // Κριτήριο τερματισμού
    protected int terminationCriteria = TARGET_INTO_CLOSE_LIST;
    
    // Ο κόμβος στόχος - τίθεται κατά τον τερματισμό του αλγορίθμου
    // αν είναι null - αποτυχία
    protected Node targetFound = null;
    
    // Log activities
    protected JTextArea logger = null;
    
    // Ο constructor δέχεται ένα γράφο στον οποίο
    public SearchAlgorithm(SearchGraph searchGraph) {
        
        super(new DirectedOrderedSparseMultigraph<Node, Edge>());
                
        // Αρχικοποίηση
        this.searchGraph = searchGraph;
        this.startNode = searchGraph.getStartNode();
        this.targetNodes = searchGraph.getTargetNodes();
        this.expandedNodes = new ArrayList();
        this.terminationCriteria = DepthFirstSearch.TARGET_INTO_OPEN_LIST;
        this.state = INIT;
        this.steps = 0;
        this.current = null;
      
        
    }
    
    
    // getters
    public int getState() {  return this.state; }
    public int getTerminationCriteria() { return this.terminationCriteria; }
    public Node getTargetFound() { return this.targetFound; }
    public SearchGraph getSearchGraph() { return this.searchGraph; }
    
    // setters
    public void setLogger(JTextArea logger) { this.logger = logger; }
    public void setTerminationCriteria(int c) { this.terminationCriteria = c; }
    
    // Επιστροφή της λίστας των κόμβων/στόχων ως string 
    public String getTargetNodesStr() {
        
        String targetNodesStr = "";
        
        for (Node n : this.targetNodes)
            targetNodesStr = targetNodesStr + n.getLabel() + ",";
        
        // Αφαιρούμε το τελικό ',' και επιστρέφουμε
        if (targetNodesStr.length() > 1) 
            return targetNodesStr.substring(0, targetNodesStr.length()-1);
        else
            return "";
        
     }
    
    
    // log message
    public void log(String message) {
        
        // Αν δεν έχει οριστεί logger επιστροφή
        if (this.logger == null) return;
        
        this.logger.append(message);
    }
    
    // Επιστροφή μονοπατιού σε μορμή String από τη ρίζα στον κόμβο node
    public String getPathStr(Node node) {
        
        List<Node> path = this.getPath(node);
        String pathStr = "";
        for (Node n : path) {
            pathStr = pathStr + n.toString() + "->";
        }
       
        return pathStr.substring(0, pathStr.length()-2);
    }
    
        
    // Επιστροφή κόστος μονοπατιού από την ρίζα μέχρι τον κόμβο node
    public double getPathCost(Node node) {
        
        // Παίρνουμε το μονοπάτι από τη ρίζα στον node
        List<Node> path = this.getPath(node);
        
        // Για κάθε ακμή αθροίζουμε τα βάρη
        double cost = 0;
        
        for (Node n : path) {
            
            if (isRoot(n)) continue;    // ο root δεν έχει parent
            
            Edge e = getParentEdge(n);
            cost += e.getWeight();
            
        }
        
        return cost;
       
    }
    
    
    // Οι μέθοδοις start() και step() υλοποιούνται στις derrived classes
    public abstract void start();
    public abstract void step();
    public abstract JPanel getDefaultPanel();
    
}
