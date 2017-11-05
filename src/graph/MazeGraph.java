package graph;



import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.collections15.Transformer;


/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class MazeGraph extends SearchGraph {
    
    // Οι σταθερές για τα περιεχόμενα των κελιών του Maze40
    public final static int
            OBST     = 1,  // κελί με εμπόδιο
            ROBOT    = 2,  // η θέση του ρομπότ
            TARGET   = 3,  // η θέση του στόχου
            EMPTY    = 0;  // άδειο κελί

    // Κρατάμε τον αριθμο γραμμών και στηλών του maze
    private int mazeRows;
    private int mazeCols;
    
    // Ένδειξη αν επιτρέπουμε διαγνώνιες κινήσεις
    private boolean diagonalEdges;
       
    
    // getters
    public int getMazeRows() { return this.mazeRows; }
    public int getMazeCols() { return this.mazeCols; }
    public boolean hasDiagonalEdges() { return this.diagonalEdges; }
       
    
    // setters
    public void setMazeRows(int rows) { this.mazeRows = rows; }
    public void setMazeCols(int cols) { this.mazeCols = cols; }
    public void setDiagonalEdges(boolean b) { this.diagonalEdges = b; }
    
    
    // Default constructor - Απαιτείται για ανάγνωση από xml
    public MazeGraph() {}
    
    
    // Δημιουργία γράφου από grid του Maze40
    // Αν η παράμετρος withDiagonals είναι true
    // δημιουργούμε ακμές και για τις διαγώνιες κινήσεις 
    public MazeGraph(int grid[][], boolean diagonalEdges) {
        
        // Παίρνουμε τις διαστάσεις του grid
        // και αρχικοποιούμε το τοπικό πεδίο και τον πίνακα κόμβων
        this.mazeRows = grid.length;
        this.mazeCols = grid[0].length;
        this.diagonalEdges = diagonalEdges;
        
        // Δημιουργούμε τους τελεστές δράσης 
        // ανάλογα αν είναι με διαγώνιες ή χωρίς διαγώνιες κινήσεις
        if (diagonalEdges) {
            operators.put("Π", new Operator("Π", "ΠΑΝΩ", 1));
            operators.put("ΠΔ", new Operator("ΠΔ", "ΠΑΝΩ-ΔΕΞΙΑ", 2));
            operators.put("Δ", new Operator("Δ", "ΔΕΞΙΑ", 3));
            operators.put("ΚΔ", new Operator("ΚΔ", "ΚΑΤΩ-ΔΕΞΙΑ", 4));
            operators.put("Κ", new Operator("Κ", "ΚΑΤΩ", 5));
            operators.put("ΚΑ", new Operator("ΚΑ", "ΚΑΤΩ-ΑΡΙΣΤΕΡΑ", 6));
            operators.put("Α", new Operator("Α", "ΑΡΙΣΤΕΡΑ", 7));
            operators.put("ΠΑ", new Operator("ΠΑ", "ΠΑΝΩ-ΑΡΙΣΤΕΡΑ", 8));
        }
        else {
            operators.put("Π", new Operator("Π", "ΠΑΝΩ", 1));
            operators.put("Δ", new Operator("Δ", "ΔΕΞΙΑ", 2));
            operators.put("Κ", new Operator("Κ", "ΚΑΤΩ", 3));
            operators.put("Α", new Operator("Α", "ΑΡΙΣΤΕΡΑ", 4));
        }
            
        
         
        
        // Δημιουργούμε έναν πίνακα για τους κόμβους
        // που θα δημιουργηθούν σε αντιστοιχία με το grid του Maze
        Node[][] nodes = new Node[mazeRows][mazeCols];
        
        // Δημιουργούμε Nodes για κάθε κελί που δεν είναι εμπόδιο.
        // Κάθε Node που δημιουργούμε το κρατάμε επίσης σε έναν πίνακα
        // Για να βρούμε τους γείτονές του και να δημιουργήσουμε τις ακμές
        // Υπολογίζουμε και τις συντεταγμένες στο StaticLayou
        int offsetX = 60;
        int offsetY = 60;
        for (int i=0; i<mazeRows; i++) {
            for (int j=0; j<mazeCols; j++) {
                String label = "(" + i + "," + j + ")";   // (i,j)
                if ( grid[i][j] != OBST ) {
                    Node n = new Node(label);
                    n.setRow(i);
                    n.setCol(j);
                    n.setX(j*60.0 + offsetY);
                    n.setY(i*60.0 + offsetX);
                    nodes[i][j] = n;
                    
                    this.addVertex(n);    // Την προσθέτουμε στο γράφο
                    
                    // Αν είναι ο κόμβος-έναρξη ενημερώνουμε την startNode
                    if (grid[i][j] == ROBOT) {
                        n.setStartNode(true);
                    }
                    
                    // Αν είναι κόμβος-στόχος, τον προσθέτουμε στην λίστα
                    if (grid[i][j] == TARGET) {
                        n.setTargetNode(true);
                    }
                }
                else { // είναι εμπόδιο - δεν προστίθεται στο γράφο
                    nodes[i][j] = null;
                }
             
            }
        }
        
        // Προσθέτουμε τις ακμές για πάνω-κάτω-αριστερά-δεξιά
        addStraightEdges(grid, nodes);
        
        // Αν η withDiagonals είναι true προσθέτουμε και διαγώνιες ακμές
        if (diagonalEdges) addDiagonalEdges(grid, nodes);
 
    }
    
    // Προσθήκη ακμών για πάνω-κάτω-δεξιά-αριστέρα
    private void addStraightEdges(int[][] grid, Node[][] nodes) {
        
        // Ελέγχουμε κάθε κελί του grid και αν δεν είναι εμπόδιο
        // παίρνουμε τους γείτονές τους προς τους οποίους μπορεί να κινηθεί
        // και δημιουργούμε μία ακμή στο γράφο
        // Οι υποψήφιοι γείτονες κάθε κελιού είναι πάνω,κάτω,δεξιά, αριστερά
        // Το κόστος όλων των ακμών είναι 1
        // Δεν υπολογίζουμε διαγώνιες κινήσεις
        for (int i=0; i<mazeRows; i++) {
            
            for (int j=0; j<mazeCols; j++)  {
                
                // Αν το κελί είναι εμπόδιο, συνεχίζουμε με το επόμενο
                if (grid[i][j] == OBST) continue;
                
                // Έλεγχος ΕΠΑΝΩ
                if ( (i-1 >= 0) && (grid[i-1][j] != OBST) )
                    this.addEdge(new Edge(1, (Operator)getOperators().get("Π")), nodes[i][j], nodes[i-1][j]);
                
                // Έλεγχος ΚΑΤΩ
                if ( (i+1 < mazeRows) && (grid[i+1][j] != OBST) )
                    this.addEdge(new Edge(1, (Operator)getOperators().get("Κ")), nodes[i][j], nodes[i+1][j]);
                
                // Έλεγχος ΔΕΞΙΑ
                if ( (j+1 < mazeCols) && (grid[i][j+1] != OBST) )
                    this.addEdge(new Edge(1, (Operator)getOperators().get("Δ")), nodes[i][j], nodes[i][j+1]);
 
                // Έλεγχος ΑΡΙΣΤΕΡΑ
                if ( (j-1 >= 0 ) && (grid[i][j-1] != OBST) )
                    this.addEdge(new Edge(1, (Operator)getOperators().get("Α")), nodes[i][j], nodes[i][j-1]);
                 
            }
            
        }
   
    }
    
    // Προσθήκη διαγώνιων ακμών
    private void addDiagonalEdges(int[][] grid, Node[][] nodes) {
        
        // Οι υποψήφιοι γείτονες κάθε κελιού είναι:
        // διαγώνια πάνω-αριστερά, πάνω-δεξιά, κάτω-αριστερά, κάτω-δεξιά
        // Το κόστος όλων των ακμών είναι 1

        for (int i=0; i<mazeRows; i++) {
            
            for (int j=0; j<mazeCols; j++)  {
                
                // Αν το κελί είναι εμπόδιο, συνεχίζουμε με το επόμενο
                if (grid[i][j] == OBST) continue;
                
                // Έλεγχος ΕΠΑΝΩ-ΑΡΙΣΤΕΡΑ
                if ( (i-1 >= 0) && (j-1 >= 0) && (grid[i-1][j-1] != OBST) )
                    this.addEdge(new Edge(Math.sqrt(2.0), (Operator)getOperators().get("ΠΑ")), nodes[i][j], nodes[i-1][j-1]);
                
                // Έλεγχος ΕΠΑΝΩ-ΔΕΞΙΑ
                if ( (i-1 >= 0) && (j+1 < mazeCols) && (grid[i-1][j+1] != OBST) )
                    this.addEdge(new Edge(Math.sqrt(2.0), (Operator)getOperators().get("ΠΔ")), nodes[i][j], nodes[i-1][j+1]);
                
                // Έλεγχος ΚΑΤΩ-ΑΡΙΣΤΕΡΑ
                if ( (i+1 < mazeRows) && (j-1 >= 0) && (grid[i+1][j-1] != OBST) )
                    this.addEdge(new Edge(Math.sqrt(2.0), (Operator)getOperators().get("ΚΑ")), nodes[i][j], nodes[i+1][j-1]);
 
                // Έλεγχος ΚΑΤΩ-ΔΕΞΙΑ
                if ( (i+1 < mazeRows) && (j+1 < mazeCols) && (grid[i+1][j+1] != OBST) )
                    this.addEdge(new Edge(Math.sqrt(2.0), (Operator)getOperators().get("ΚΔ")), nodes[i][j], nodes[i+1][j+1]);
                 
            }
            
        }
   
    } 
    
    

    // Ξαναορίζουμε την getGraphTransformer
    // για να ανακτούμε τα επιπλέον πεδία του γραφήματος της MazeGraph
    public static Transformer<GraphMetadata, Graph<Node, Edge>> getGraphTransformer() {
        
        Transformer<GraphMetadata, Graph<Node, Edge>> graphTransformer = 
                new Transformer<GraphMetadata, Graph<Node, Edge>>() {
                   
                    @Override
                    public Graph<Node, Edge> transform(GraphMetadata metadata) {
                        
                        // mazeRows, mazeCols and diagonalEdges
                        int mazeRows = Integer.parseInt(metadata.getProperty("mazeRows"));
                        int mazeCols = Integer.parseInt(metadata.getProperty("mazeCols"));
                        boolean diagonalEdges = Boolean.parseBoolean(metadata.getProperty("diagonalEdges"));
                        
                        // Δημιουργία του MazeGraph
                        MazeGraph mg = new MazeGraph();
                        mg.setMazeRows(mazeRows);
                        mg.setMazeCols(mazeCols);
                        mg.setDiagonalEdges(diagonalEdges);

                        
                        // Operators 
                        int operators = Integer.parseInt(metadata.getProperty("operators"));
                        for (int i=1; i<=operators; i++) {
                            Operator op = Operator.explode(metadata.getProperty("operator-"+i));
                            mg.getOperators().put(op.getLabel(), op);
                        }
                        
                        
                        // Επιστροφή νέου γραφήματος 
                        return mg;
                    }
                    
                };
        
        return graphTransformer;
    }
        
    
    // Φόρτωμα γραφήματος από GraphML (xml format)
    // Υλοποίηση ως Factory μέθοδο
    public static MazeGraph load ()  {

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
                                   MazeGraph.getGraphTransformer(),
                                   Node.getNodeTransformer(),
                                   Edge.getEdgeTransformer(),
                                   Edge.getHyperedgeTransformer());
        
        // Δημιουργία του γράφου και επιστροφή
        try {
            MazeGraph g = (MazeGraph)graphReader.readGraph();
            
            // Ξαναορίζουμε τους τελεστές των ακμών
            // ώστε να αναφέρονται στο ίδιο αντικείμενο
            // μ' αυτό που διατηρεί το γράφημα στο HashMap operators
            Collection<Edge> edges = g.getEdges();
            for (Edge e : edges) {
                String key = e.getOperator().getLabel();
                e.setOperator((Operator)g.getOperators().get(key));
            }
            
            
            return (MazeGraph)g;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null,
                            "Το επιλεγμένο αρχείο δεν έχει έγκυρη μορφή γραφήματος",
                            "Σφάλμα αρχείου", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
    }
    
    /*
    @Override
    public JPanel getDefaultPanel() {
        
        int width = 650;
        int height = 500;
        
        Layout<Node, Edge> graphLayout = new StaticLayout(this);
        graphLayout.setSize(new Dimension(width, height));
        
        VisualizationViewer<Node, Edge> vv = new VisualizationViewer(graphLayout);
        vv.setPreferredSize(new Dimension(width,height));
        DefaultModalGraphMouse<Node, Edge> graphMouse = new DefaultModalGraphMouse();
        //graphMouse.setMode(DefaultModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.setLayout(new BorderLayout());
        
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
                if (solutionPath.contains(n)) return Color.YELLOW;
                
                // default
                return Color.WHITE;
            }
        };
        
        
        
        vv.getRenderContext().setVertexFillPaintTransformer(nodePaint);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.NW);
         
        // Επιστροφή του panel
        return vv;
        
    }
    */
    
    // Αποθήκευση δεδομένων του Node σε GraphML
    @Override
    public void saveToGraphML(GraphMLWriter<Node, Edge> ml) {
        
        // Καλούμε την parent
        super.saveToGraphML(ml);
        
        // mazeRows
        ml.addGraphData("mazeRows", null, "0", 
            new Transformer<Hypergraph<Node, Edge>, String>() {
                @Override
                public String transform(Hypergraph<Node, Edge> graph) {
                    return Integer.toString(mazeRows);
                }
            });

        // mazeCols
        ml.addGraphData("mazeCols", null, "0", 
            new Transformer<Hypergraph<Node, Edge>, String>() {
                @Override
                public String transform(Hypergraph<Node, Edge> graph) {
                    return Integer.toString(mazeCols);
                }
            });
        
        // diagonalEdges
        ml.addGraphData("diagonalEdges", null, "", 
            new Transformer<Hypergraph<Node, Edge>, String>() {
                @Override
                public String transform(Hypergraph<Node, Edge> graph) {
                    return Boolean.toString(diagonalEdges);
                }
            });
        
    
    }
  
    
    
    
}
