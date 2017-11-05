
package algorithms;

import edu.uci.ics.jung.algorithms.layout.Layout;

import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.TreeUtils;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import graph.DynamicTreeLayout;
import graph.Edge;
import graph.Node;
import graph.SearchGraph;
import graphix.Util;
import static algorithms.SearchAlgorithm.FINISHED;
import static algorithms.SearchAlgorithm.INIT;
import static algorithms.SearchAlgorithm.RUNNING;
import static algorithms.SearchAlgorithm.TARGET_INTO_CLOSE_LIST;
import static algorithms.SearchAlgorithm.TARGET_INTO_OPEN_LIST;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class HeuristicSearch extends SearchAlgorithm {

    // Σταθερές μεθόδων ευρετικής αναζήτησης
    public static final int GREEDY = 0;
    public static final int ASTAR = 1;
    
    // Το είδος ευρετικής αναζήτησης από τις παραπάνω σταθερές
    private int heuristic;
    
    // Εμφάνιση ή όχι διαδόχων που βρίσκονται στο μονοπάτι από τη ρίζα
    private boolean rootPathNodes = false;
    

    // H Λίστα ανοικτές είναι μια ουρά προτεραιότητας
    private PriorityQueue<Node> openNodes;

    // Η λίστα "ΚΛΕΙΣΤΕΣ" υλοποιείται ως μια απλή ArrayList
    private ArrayList<Node> closeNodes;
    
    // Λίστα διεγραμμένων κόμβων
    private ArrayList<Node> deletedNodes;
    
    
    // Ο constructor δέχεται ένα γράφο στον οποίο
    public HeuristicSearch(SearchGraph searchGraph, int heuristic) {

        // parent contructor
        super(searchGraph);

        // Κρατάμε το είδος της ευρετικής αναζήτησης
        this. heuristic = heuristic;
        
        // Αρχικοποίηση λιστών
        
        // openNodes - Initial capacity = 20 nodes
        Comparator<Node> comparator = new Comparator<Node>() {
            
            @Override
            public int compare(Node n1, Node n2) {
                
                if (n1.getF() > n2.getF()) return 1;
                if (n1.getF() < n2.getF()) return -1;
                return 0;
                
            }
        
        };
        
        this.openNodes = new PriorityQueue(20, comparator);
        
        
        // closeNodes
        closeNodes = new ArrayList();
        
        // deletedNodes
        deletedNodes = new ArrayList();
        
    }
        
    // getters
    public int getHeuristic() { return this.heuristic; }
    public boolean isRootPathNodesShown() { return this.rootPathNodes; }
    
    // setters
    public void showRootPathNodes(boolean b) { this.rootPathNodes = b; }
    
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
        // και θέτουμε f=h
        startNode.setF(startNode.getH());
        openNodes.add(this.startNode);
        this.addVertex(this.startNode); // set root node
        log("ΑΡΧΙΚΟΠΟΙΗΣΗ: Ο κόμβος " + startNode.toString() + " προστέθηκε στις ΑΝΟΙΚΤΕΣ με f=" + startNode.getF() + "\n" );
   
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
        
        // Hash map όπου κρατάμε τις τιμές της f
        // Και την εισερχόμενη ακμή σε κόμβο
        HashMap<Node, Double> hashF = new HashMap();
        HashMap<Node, Edge> hashEdge = new HashMap();

        // Επόμενο βήμα

        // Αφαίρεσε την 1η κατάσταση από τις ΑΝΟΙΚΤΕΣ με την καλύτερη f
        // και πρόσθεσέ την στις ΚΛΕΙΣΤΕΣ
        ++this.steps;
        current = getNextFromOpenNodes();
        closeNodes.add(current);
        log("ΒΗΜΑ " + steps + ": Ο κόμβος " + current.toString() + " με f=" + current.getF() +" βγήκε από τις ΑΝΟΙΚΤΕΣ και μπήκε στις ΚΛΕΙΣΤΕΣ\n");

        // Αν το κριτήριο τερματισμού είναι να μπει ο κόμβος στόχος
        // στις κλειστές, και  αν ο τρέχον κόμβος ανήκει στους στόχους
        // τερματίζει
        if (targetNodes.contains(current) && this.terminationCriteria == TARGET_INTO_CLOSE_LIST) {

            state = FINISHED;
            targetFound = this.current;
            logSuccess();
            return;

        }


        // Παίρνουμε τις εξερχόμενες ακμές της current
        // σε αύξουσα διάταξη ανάλογα με την προτεραιότητα των τελεστών
        ArrayList<Edge> edges = searchGraph.getOutEdges(this.current, true);

        boolean expanded = false;
        
        // Παίρνουμε κάθε διάδοχο (other end of edge)
        // και τον προσθέτουμε σε μια προσωρινή λίστα προς έλεγχο
        // Υπολογίζουμε και την νέα τιμή f και την κρατάμε στο hashF
        ArrayList<Node> tempNodeList = new ArrayList();
        for (Edge e : edges) {
            
          
            
            Node other = searchGraph.getOpposite(current, e);
            hashEdge.put(other, e);
            tempNodeList.add(other);
            
            // Υπολογισμός ευρετικού ανάλογα με το είδος (GREEDY, ASTAR)
            switch (this.heuristic) {
                
                case GREEDY:
                    hashF.put(other, other.getH());
                    break;
                    
                case ASTAR:
                    hashF.put(other, other.getH() + getPathCost(current) + e.getWeight());
                    break;
                    
                default:
                    hashF.put(other, 0.0);
                    break;
            }
            
            
            if (targetNodes.contains(other) && this.terminationCriteria == TARGET_INTO_OPEN_LIST) {
                this.state = FINISHED;
                this.targetFound = other;
            }
        }
        
        // ΒΗΜΑ 6 του αλγορίθμου (σελ. 49)
        // Για κάθε κόμβο/γείτονα που αναπτύχθηκε
        for (Node child : tempNodeList) {
          

            // Αν ο κόμβος ανήκει στο μονοπάτι από την ρίζα στην current
            // και showPathNodes = false, τον αγνοούμε
            List<Node> path = this.getPath(current);
            if (path.contains(child) && !this.rootPathNodes) {
                
                log("ΒΗΜΑ " + steps + ": Ο κόμβος " + child.toString() + " υπάρχει ήδη στο μονοπάτι από την ρίζα - αγνοήθηκε! \n");  
                continue;
            }
            
            
            // Αν δεν ανήκει ούτε στις ανοικτές ούτε στις κλειστές
            // την προσθέτουμε στις ανοικτές με την υπολογισμένη f
            if (!openNodes.contains(child) && !closeNodes.contains(child)) {
                child.setF(hashF.get(child));
                openNodes.add(child);
                log("ΒΗΜΑ " + steps + ": Ο κόμβος " + child.toString() + " μπήκε στις ΑΝΟΙΚΤΕΣ με f=" + child.getF() + "\n");  
                this.addChild(hashEdge.get(child), this.current, child);
                
                // Ένδειξη ότι η current ανέπτυξη τουλάχιστον έναν κόμβο
                // που δεν διαγράφηκε
                expanded = true;
                
            }         
            else if (openNodes.contains(child)) {   
            
                // Αν ανήκει στις ΑΝΟΙΚΤΕΣ κρατάμε αυτόν με την μικρότερη f
                if (child.getF() <= hashF.get(child)) {
                 
                    log("ΒΗΜΑ " + steps + ": Ο κόμβος " + child.toString() + " με f=" + hashF.get(child) + 
                            " διαγράφηκε επειδή υπάρχει ήδη στις ΑΝΟΙΚΤΕΣ με f=" + child.getF() + "\n"); 
                    
                    // Προσθήκη ενός αντιγράφου στο δέντρο με την σήμανση διεγραμμένο
                    Node newNode = new Node(child);
                    newNode.setF(hashF.get(child));
                    deletedNodes.add(newNode);
                    this.addChild(new Edge(hashEdge.get(child)), this.current, newNode);
                    
                }
                else {
                    
                    // Παίρνουμε το subtree του κόμβου child
                    // που υπάρχει ήδη στις ΑΝΟΙΚΤΕΣ
                    Tree<Node, Edge> subtree = Util.getSubTree(this, child);

                    // Διαγράφουμε το subtree
                    // και τους κόμβους από τις ανοικτές ή τις κλειστές
                    // Διατηρούμε το προηγούμενο parent του child
                    // και το προηγούμενο edge που τα συνδέει
                    for (Node n : subtree.getVertices()) {
                        openNodes.remove(n);
                        closeNodes.remove(n);
                    }
                    Node parent = this.getParent(child);
                    Edge parentEdge = this.getParentEdge(child);
                    this.removeVertex(child);
                     
                    // Δημιουργούμε ένα αντίγραφο του subtree
                    // και μαρκάρουμε όλους τους κόμβους ως διεγραμμένους
                    Tree<Node, Edge> clonedTree = Util.cloneTree(subtree);
                    for (Node n : clonedTree.getVertices())
                        deletedNodes.add(n);                    
    
                    // Προσθέτουμε εκ νέου το child στην current και στις ανοικτές
                    // με την νέα f. 
                    child.setF(hashF.get(child));
                    openNodes.add(child);
                    this.addChild(hashEdge.get(child), this.current, child);
                                      
                    // Προσθέτουμε το διεγραμμένο αντίγραφο
                    // στην προηγούμενη θέση της child
                    //this.addChild(new Edge(parentEdge), parent, clonedTree.getRoot());
                    TreeUtils.addSubTree(this, clonedTree, parent, new Edge(parentEdge));
               
                    // log
                    log("ΒΗΜΑ " + steps + ": Το υποδέντρο του κόμβου " + child.toString() + " διαγράφηκε από τις ΑΝΟΙΚΤΕΣ. Ο κόμβος " + child.toString() + " με f=" + child.getF() + " μπήκε εκ νέου στις ΑΝΟΙΚΤΕΣ\n");  
                    
                    // Ένδειξη ότι επεκτάθηκε η current
                    expanded = true;
                    
                }
                
            }
            else {  // υπάρχει στις κλειστές
            
                if (child.getF() <= hashF.get(child)) {
                 
                    log("ΒΗΜΑ " + steps + ": Ο κόμβος " + child.toString() + " με f=" + hashF.get(child) + 
                            " διαγράφηκε επειδή υπάρχει ήδη στις ΚΛΕΙΣΤΕΣ με με f=" + child.getF() + "\n"); 
                    
                    // Προσθήκη ενός αντιγράφου στο δέντρο με την σήμανση διεγραμμένο
                    Node newNode = new Node(child);
                    newNode.setF(hashF.get(child));
                    deletedNodes.add(newNode);
                    this.addChild(new Edge(hashEdge.get(child)), this.current, newNode);
                    
                }
                else {
                    
                    // Παίρνουμε το subtree του κόμβου child
                    // που υπάρχει ήδη στις ΑΝΟΙΚΤΕΣ
                    Tree<Node, Edge> subtree = Util.getSubTree(this, child);

                    // Διαγράφουμε το subtree
                    // και τους κόμβους από τις ανοικτές ή τις κλειστές
                    // Διατηρούμε το προηγούμενο parent του child
                    // και το προηγούμενο edge που τα συνδέει
                    for (Node n : subtree.getVertices()) {
                        openNodes.remove(n);
                        closeNodes.remove(n);
                    }
                    Node parent = this.getParent(child);
                    Edge parentEdge = this.getParentEdge(child);
                     
                    // Δημιουργούμε ένα αντίγραφο του subtree
                    // και μαρκάρουμε όλους τους κόμβους ως διεγραμμένους
                    Tree<Node, Edge> clonedTree = Util.cloneTree(subtree);
                    for (Node n : clonedTree.getVertices())
                        deletedNodes.add(n);

                    // Διαγράφουμε το προηγούμενο υποδέντρο
                    this.removeVertex(child);
                    
                    // Προσθέτουμε εκ νέου το child στην current και στις ανοικτές
                    // με την νέα f
                    child.setF(hashF.get(child));
                    openNodes.add(child);
                    this.addChild(hashEdge.get(child), this.current, child);
                                      
                    
                    
                    // Προσθέτουμε το διεγραμμένο αντίγραφο
                    // στην προηγούμενη θέση της child
                    //this.addChild(new Edge(parentEdge), parent, clonedTree.getRoot());
                    TreeUtils.addSubTree(this, clonedTree, parent, new Edge(parentEdge));
                    
               
                    // log
                    log("ΒΗΜΑ " + steps + ": Το υποδέντρο του κόμβου " + child.toString() + " διαγράφηκε από τις ΚΛΕΙΣΤΕΣ. Ο κόμβος " + child.toString() + " με f=" + child.getF() + " μπήκε εκ νέου στις ΑΝΟΙΚΤΕΣ\n");  
                    
                    // Ένδειξη ότι επεκτάθηκε η current
                    expanded = true;
               
                }
                
            }
        }
            
        
        // Αύξηση του μετρητή κόμβων που ανέπτυξαν τουλάχιστον έναν
        // απόγονο που δε διαγράφηκε
        if (expanded)
            expandedNodes.add(this.current);
        
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

        // tooltips
        vv.setVertexToolTipTransformer(new Transformer<Node, String>() {
            @Override
            public String transform(Node node) {
                return "f=" + String.format("%.2f", node.getF());
            }
            
        });
        
        
        
        //ScalingControl scaler = new CrossoverScalingControl();
        //scaler.scale(vv, 1/1.5f, vv.getCenter());

        
        // Set node colors
        Transformer<Node, Paint> nodePaint = new Transformer<Node, Paint>() {

            @Override
            public Paint transform(Node n) {

                // Αν είναι ο τρέχον κόμβος - άσπρος
                if (n == current) return Color.WHITE;

                // Αν είναι διεγραμμένος κόμβος - κίτρινος
                if (deletedNodes.contains(n)) return Color.MAGENTA;
                
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
        log("ΚΟΜΒΟΙ ΠΟΥ ΑΝΕΠΤΥΞΑΝ ΤΟΥΛΑΧΙΣΤΟΝ ΕΝΑΝ ΚΟΜΒΟ ΠΟΥ ΔΕΝ ΔΙΑΓΡΑΦΗΚΕ: "  + this.expandedNodes.size() + "\n");
        log("ΚΟΜΒΟΙ ΠΟΥ ΔΙΑΓΡΑΦΗΚΑΝ: "  + this.deletedNodes.size() + "\n");
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

    // Επιστροφή του επόμενου κόμβου προς εξέταση
    // Σε περίπτωση ισοβαθμίας επιλέγεται αυτός που έχει μπει πρώτος στις ανοικτές
    private Node getNextFromOpenNodes() {
        
        // Peek at head - Βλέπουμε την ελάχιστη τιμή
        double minF = openNodes.peek().getF();
        
        // Τοποθετούμε σε μια λίστα τους κόμβους που ισοβαθμούν
        // και επιστρέφουμε την αρχή της λίστας
        // (αφού οι νέοι κόμβοι που ισοβαθμουν προστίθενται προς την κεφαλή)
        // Έτσι σε περίπτωση ισοβαθμίας επιστρέφεται ο κόμβος
        // που έχει μπει νωρίτερα στις ανοικτές
        ArrayList<Node> list = new ArrayList();
        for (Node n : openNodes) 
            if (n.getF() == minF) list.add(n);
        
        Node node = list.get(0);
        openNodes.remove(node);
        return list.get(0);
        
        
    }
 

}
