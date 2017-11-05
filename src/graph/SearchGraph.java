/*
 * Γράφος αναζήτησης - γενική περίπτωση
 */
package graph;


import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.collections15.Transformer;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class SearchGraph extends DirectedSparseGraph<Node, Edge> {
    
    
    // Πεδία της κλάσης 
    protected HashMap<String, Operator> operators;            // Τελεστές μετάβασης
    
    // Λίστες κόμβων και ακμών που αποτελούν την λύση
    // (διαδρομή από την αρχική στην τελική)
    protected ArrayList<Node> solutionNodes;
    protected ArrayList<Edge> solutionEdges;
    
    
    // Constructor
    public SearchGraph() {
        operators = new HashMap();
        solutionNodes = new ArrayList();
        solutionEdges = new ArrayList();
    }

    // getters
    public HashMap getOperators() { return this.operators; }
    public ArrayList<Node> getSolutionNodes() { return this.solutionNodes; }
    public ArrayList<Edge> getSolutionEdges() { return this.solutionEdges; }
    
    // setters
    public void setOperators(HashMap<String, Operator> operators) {
        this.operators = operators;
    }
    
    // Προσθήκη ενός κόμβου στη λίστα solutionNodes
    public void addSolutionNode(Node n) {
        this.solutionNodes.add(n);
    }
    
    // Προσθήκη μιας ακμής στη λίστα solutionEdges
    public void addSolutionEdge(Edge e) {
        this.solutionEdges.add(e);
    }
    
    // Αρχικοποίηση της solutionPath
    public void resetSolutionPath() {
        this.solutionNodes = new ArrayList();
        this.solutionEdges = new ArrayList();
    }

    
    // Φόρτωμα γραφήματος από GraphML (xml format)
    // Υλοποίηση ως Factory μέθοδο
    public static SearchGraph load ()  {

        // Επιλογή αρχείου
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Άνοιγμα αρχείου XML");
        FileNameExtensionFilter filter;
        filter = new FileNameExtensionFilter("Αρχεία XML", "xml");
        fc.setFileFilter(filter);
        
        int returnVal = fc.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) return null;
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(
                    new FileReader(fc.getSelectedFile()));
        } catch (FileNotFoundException ex) {
           ex.printStackTrace();
        }
        
        
        
        
        // Create the graphMLReader2
        GraphMLReader2<Graph<Node, Edge>, Node, Edge> graphReader = 
                new GraphMLReader2(fileReader,
                                   SearchGraph.getGraphTransformer(),
                                   Node.getNodeTransformer(),
                                   Edge.getEdgeTransformer(),
                                   Edge.getHyperedgeTransformer());
        
        // Δημιουργία του γράφου και επιστροφή
        try {
            SearchGraph g = (SearchGraph)graphReader.readGraph();
            
            // Ξαναορίζουμε τους τελεστές των ακμών
            // ώστε να αναφέρονται στο ίδιο αντικείμενο
            // μ' αυτό που διατηρεί το γράφημα στο HashMap operators
            Collection<Edge> edges = g.getEdges();
            for (Edge e : edges) {
                String key = e.getOperator().getLabel();
                e.setOperator((Operator)g.getOperators().get(key));
            }
            
            
            return (SearchGraph)g;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null,
                            "Το επιλεγμένο αρχείο δεν έχει έγκυρη μορφή γραφήματος",
                            "Σφάλμα αρχείου", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
    }
 
    
    // Graph Transformer για ανάκτηση από xml
    public static Transformer<GraphMetadata, Graph<Node, Edge>> getGraphTransformer() {
        
        Transformer<GraphMetadata, Graph<Node, Edge>> graphTransformer = 
                new Transformer<GraphMetadata, Graph<Node, Edge>>() {
                   
                    @Override
                    public Graph<Node, Edge> transform(GraphMetadata metadata) {
                        SearchGraph sg = new SearchGraph();
                        int operators = Integer.parseInt(metadata.getProperty("operators"));
                        for (int i=1; i<=operators; i++) {
                            Operator op = Operator.explode(metadata.getProperty("operator-"+i));
                            sg.getOperators().put(op.getLabel(), op);
                        }
                        
                        
                        // Επιστροφή νέου γραφήματος με τους τελεστές δράσης
                        return sg;
                    }
                    
                };
        
        return graphTransformer;
    }
        
    
    // Ανάκτηση των εξερχόμενων ακμών του κόμβου node
    // Αν asc = true αύξουσα, διαφορετικά φθίνουσα
    public ArrayList<Edge> getOutEdges(Node node, boolean asc) {
        
        // Παίρνουμε την λίστα των εξερχόμενων ακμών της node
        // Την προσθέτουμε σε μία ArrayList και την ταξινομούμε
        Collection<Edge> edges = this.getOutEdges(node);
        
        ArrayList<Edge> edgeList = new ArrayList();
        for (Edge e : edges) {
            edgeList.add(e);
        }
        
        // sort ascending
        Collections.sort(edgeList);
        
        // Αν asc = false reverse list
        if (!asc) Collections.reverse(edgeList);
            
        // Επιστροφή
        return edgeList;
        
        
        
    }
    
    
    // Επιστρέφει τον κόμβο έναρξης
    public Node getStartNode() {
        
        Collection<Node> nodes = this.getVertices();
        
        for (Node n : nodes) {
            if (n.isStartNode()) return n;
        }
        
        // Δεν έχει οριστεί κόμβος -έναρξης
        return null;
        
    }
    
    // Επιστρέφει μια λίστα με τους κόμβους-στόχους
    public ArrayList<Node> getTargetNodes() {
        
        Collection<Node> nodes = this.getVertices();
        
        ArrayList<Node> targetNodes = new ArrayList();
        for (Node n : nodes) {
            if (n.isTargetNode()) targetNodes.add(n);
        }
        
        
        return targetNodes;
        
        
    }
    
    // Επιστροφή ενός JPanel απεικόνισης του γράφου
    // Override στις derrived κλάσεις για εξειδίκευση απεικόνισης
    public JPanel getDefaultPanel() {
        
        Layout<Node, Edge> graphLayout = new StaticLayout(this);
        //graphLayout.setSize(new Dimension(width, height));
        
        VisualizationViewer<Node, Edge> vv = new VisualizationViewer(graphLayout);
        //vv.setPreferredSize(new Dimension(width,height));
        DefaultModalGraphMouse<Node, Edge> graphMouse = new DefaultModalGraphMouse();
        //graphMouse.setMode(DefaultModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.setLayout(new BorderLayout());
        
        // Θέτουμε τις συντεταγμένες των κόμβων
        for (Node n : this.getVertices()) {
            graphLayout.setLocation(n, new Point2D.Double(n.getX(), n.getY()));
        }
        
        
        /*
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
        graphLayout.setInitializer(locationTransformer);
        */
        
        // change vertex color
        Transformer<Node, Paint> nodePaint = new Transformer<Node, Paint>() {
        
            @Override
            public Paint transform(Node n) {
                
                // Αν είναι ο κόμβος έναρξη τον βάφουμε κόκκινο
                // Αν είναι κόμβος-στόχος, τον βάφουμε μπλε
                // Οι υπόλοιπο κόμβοι είναι άσπροι
                if (n.isStartNode()) return Color.RED;
                if (n.isTargetNode()) return Color.GREEN;
                
                // Αν είναι κόμβος που ανήκει στην διαδρομή - λύση -> κίτρινο
                if (solutionNodes.contains(n)) return Color.YELLOW;
                
                // default
                return Color.WHITE;
            }
        };
        
        // Change Edge color (if into solution path)
        Transformer<Edge, Paint> edgePaint = new Transformer<Edge, Paint>() {
        
            @Override
            public Paint transform(Edge e) {
                
                // Αν η ακμή ανήκει στην διαδρομή-λύση -> κίτρινη
                // διαφορετικά μαύρη
                if (solutionEdges.contains(e)) return Color.YELLOW;
                
                // default
                return Color.BLACK;
            
            };
            
        };
        
        // Change Edge thickness (if into solution path)
        Transformer<Edge, Stroke> edgeStroke = new Transformer<Edge, Stroke>() {
        
            @Override
            public Stroke transform(Edge e) {
                
                // Αν η ακμή ανήκει στην διαδρομή-λύση -> κίτρινη
                // διαφορετικά μαύρη
                if (solutionEdges.contains(e)) {
                    return new BasicStroke(5);
                    
                }
                
                // default
                return new BasicStroke(1);
            
                
            
            }
        
        };
                
        
        vv.getRenderContext().setVertexFillPaintTransformer(nodePaint);
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);
        //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.NW);
         
        // Επιστροφή του panel
        return vv;
        
        
    }
   
         
    // Αποθήκευση δεδομένων του Node σε GraphML
    public void saveToGraphML(GraphMLWriter<Node, Edge> ml) {
   
        // Προσθέτουμε τους τελεστές δράσης
        // Προσθέτουμε στην αρχή το πλήθος τους
        // και κάθε τελεστής κωδικοποιείται ως operator-n,
        // όπου n=1,2,...hash.size
        int countOper = operators.size();
        ml.addGraphData("operators", null, "", 
            new Transformer<Hypergraph<Node, Edge>, String>() {
                @Override
                public String transform(Hypergraph<Node, Edge> graph) {
                    return Integer.toString(countOper);
                }
            });

        int i = 0;
        for (String key : operators.keySet()) {

            i++;
            Operator oper = operators.get(key);

            ml.addGraphData("operator-" + i, null, "", 
            new Transformer<Hypergraph<Node, Edge>, String>() {
                @Override
                public String transform(Hypergraph<Node, Edge> graph) {
                    return oper.implode();
                }
            });

        }

        // Προσθέτουμε τα δεδομένα των κόμβων και ακμών
        Node.saveToGraphML(ml);
        Edge.saveToGraphML(ml);
        
                
        
                
    }
    
    
    // Ενημέρωση των συντεταγμέων των κόμβων στο layout
    public void updateNodePositions(AbstractLayout<Node, Edge> layout) {
        
        for (Node node : this.getVertices()) {
            node.setX(layout.getX(node));
            node.setY(layout.getY(node));
        }
        
    }
    
    
}
