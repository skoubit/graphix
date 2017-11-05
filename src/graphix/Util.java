
package graphix;

import graph.Node;
import graph.Edge;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.TreeUtils;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class Util {

    // Υπολογισμος απόστασης Manhattan μεταξύ δύο κόμβων
    public static int manhattan(Node n1, Node n2) {
        
        return  Math.abs(n1.getRow() - n2.getRow()) + 
                Math.abs(n1.getCol() - n2.getCol());
    
    }
    
    // Υπολογισμός ευκλείδιας απόστασης (1000πλασιο) μεταξύ δύο κόμβων
    public static double eucledian(Node n1, Node n2) {
        
        double dx = n1.getRow() - n2.getRow();
        double dy = n1.getCol() - n2.getCol();
            
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    
    
    // Επιστροφή των απογόνων εντός κόμβου (ανδρομική κλήση)
    public static ArrayList<Node> getDescentants(Tree<Node, Edge> tree, Node node) {
        
        // Η λίστα απογόνων προς επιστροφή
        ArrayList<Node> descentants = new ArrayList();
        
        // Πρόσθεσε την node στην λίστα
        descentants.add(node);
        
        // Παίρνουμε τα παιδιά της node
        Collection<Node> childs = tree.getSuccessors(node);
        
        // Προσθέτουμε τους απογόνους κάθε παιδιού της node στη λίστα
        // (αναδρομική κλήση)
        for (Node n1 : childs) {
            
            ArrayList<Node> n1Desc = getDescentants(tree, n1);
            for (Node n2 : n1Desc) {
                descentants.add(n2);
            }
            
            
        }
        
        return descentants;
    }
    
    
    // Get Subtree at Node root
    public static Tree<Node, Edge> getSubTree(Tree<Node, Edge> tree, Node root) {
        
        DelegateTree<Node, Edge> subtree = new DelegateTree(new DirectedOrderedSparseMultigraph<Node, Edge>());
      	subtree.addVertex(root);
        TreeUtils.growSubTree(tree, subtree, root);
	
        // Επιστροφή
        return subtree;
        
    }
    
    // Δημιουργία αντιγράφου ενός δέντρου
    public static Tree<Node, Edge> cloneTree(Tree<Node, Edge> tree) {
        
        DelegateTree<Node, Edge> newTree = new DelegateTree(new DirectedOrderedSparseMultigraph<Node, Edge>());
        
        // Αντιγράφουμε τη ρίζα
        Node root = tree.getRoot();
        Node newRoot = new Node(root);
        newTree.addVertex(newRoot);
        
        // Για κάθε εξερχόμενη ακμή από τη ρίζα
        // δημιουργούμε αντίγραφα των υποδέντρων και τα προσθέτουμε
        Collection<Edge> outEdges = tree.getOutEdges(root);
        
        for (Edge e : outEdges) {
            
            Edge newEdge  = new Edge(e);
            Node newChild = new Node(tree.getOpposite(root, e));
            newTree.addChild(newEdge, newRoot, newChild);
            
            Tree<Node, Edge> subtree = Util.getSubTree(tree, tree.getOpposite(root, e));
            Tree<Node, Edge> clonedSubtree = cloneTree(subtree);
            
            TreeUtils.growSubTree(newTree, clonedSubtree, newChild);
            
             
        }
        
        
        return newTree;
        
    }
    
    
    // Προβολή μηνύματος στον χρήστη
    public static void showMessage(String title, String message) {
        
        JOptionPane.showMessageDialog(null, message,
                title, JOptionPane.WARNING_MESSAGE);
    }
    
}
    