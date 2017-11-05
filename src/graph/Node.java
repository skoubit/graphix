
package graph;

import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.NodeMetadata;
import org.apache.commons.collections15.Transformer;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class Node {
    
    // Πεδία της κλάσης
    private String label;       // Η ετικέτα του κόμβου
    private double h = 0;       // Το ευρετικό του κόμβου
    private int row;            // Η γραμμή στο maze
    private int col;            // Η στήλη στο maze
    private double x;           // H θέση στο layout
    private double y;           
        
    // flags για το αν ο κόμβος είναι ο κόμβος έναρξη
    // ή ανήκει στους κόμβους στόχους
    private boolean startNode = false;
    private boolean targetNode = false;
    
    // flag διαγραφής κατά τη σχεδίαση - δεν αποθηκεύεται
    private boolean deleted = false;
    
    // Βοηθητικά πεδία για την εκτέλεση των αλγορίθμων αναζήτησης
    // δεν αποθηκεύονται
     private double f;           // Η συνάρτηση αξιολόγησης
                                // f = h (greedy)
                                // f = g + h (A*)
    
    
    // default contructor
    public Node() {
        this.label = "";
        this.x = 0; 
        this.y = 0;
    } 
     
    // Constructor - Αρχικοποιούμε μόνο την ετικέτα
    public Node(String label) {
        this.label = label;
        this.x = 0; 
        this.y = 0;
    }
    
    // Constructor - Αρχικοποιούμε εττικέτα και ευρετικό
    public Node(String label, int h) {
        this(label);
        this.h = h;
        
    }
    
    // Constructor - Αρχικοποιούμε από άλλο Node
    public Node(Node node) {
        
        this.label = node.getLabel();
        this.h = node.getH();
        this.startNode = node.isStartNode();
        this.targetNode = node.isTargetNode();
        this.row = node.getRow();
        this.col = node.getCol();
        this.f = node.getF();
        this.h = node.getH();
        this.x = 0; 
        this.y = 0;
        
    }
    
    // Getters
    public String getLabel() { return label; }
    public double getH() { return this.h; }
    public int getRow() { return this.row; }
    public int getCol() { return this.col; }
    public boolean isStartNode() { return this.startNode; }
    public boolean isTargetNode() { return this.targetNode; }
    public double getF() { return this.f; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public boolean isDeleted() { return this.deleted; }
    
    
    // Setters
    public void setLabel(String label) { this.label = label; }
    public void setH(double h) { this.h = h; }
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
    public void setStartNode(boolean b) { this.startNode = b; }
    public void setTargetNode(boolean b) { this.targetNode = b; }
    public void setF(double f) { this.f = f; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDeleted(boolean b) { this.deleted = b; }
    
    
    // Override toString() method
    @Override
    public String toString() {
        return this.label;
    }
    
    
    // Node transformer για την ανάκτηση από xml
    public static Transformer<NodeMetadata, Node> getNodeTransformer() {
        
        Transformer<NodeMetadata, Node> nodeTransformer = 
                new Transformer<NodeMetadata, Node>() {
                    @Override
                    public Node transform(NodeMetadata metadata) {
                        Node node = new Node("");
                        node.setLabel(metadata.getProperty("label"));
                        node.setH(Double.parseDouble(metadata.getProperty("h")));
                        node.setStartNode(Boolean.parseBoolean(metadata.getProperty("startNode")));
                        node.setTargetNode(Boolean.parseBoolean(metadata.getProperty("targetNode")));
                        node.setRow(Integer.parseInt(metadata.getProperty("row")));
                        node.setCol(Integer.parseInt(metadata.getProperty("col")));
                        node.setX(Double.parseDouble(metadata.getProperty("x")));
                        node.setY(Double.parseDouble(metadata.getProperty("y")));
                        
                        
                        return node;
                    }
                    
                };
    
        return nodeTransformer;
        
    }
    
    
    // Αποθήκευση δεδομένων του Node σε GraphML
    public static void saveToGraphML(GraphMLWriter<Node, Edge> ml) {
        
        // label
        ml.addVertexData("label", null, "",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return node.getLabel();
                }
            });


        // startNode
        ml.addVertexData("startNode", null, "",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Boolean.toString(node.isStartNode());
                }
            });

        // targetNode
        ml.addVertexData("targetNode", null, "",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Boolean.toString(node.isTargetNode());
                }
            });

        // h - τιμή ευρετικού
        ml.addVertexData("h", null, "0",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Double.toString(node.getH());
                }
            });

        // row
        ml.addVertexData("row", null, "0",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Integer.toString(node.getRow());
                }
            });

        // col
        ml.addVertexData("col", null, "0",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Integer.toString(node.getCol());
                }
            });

        // x
        ml.addVertexData("x", null, "0",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Double.toString(node.getX());
                }
            });

        // y
        ml.addVertexData("y", null, "0",
            new Transformer<Node, String>() {
                @Override
                public String transform(Node node) {
                    return Double.toString(node.getY());
                }
            });


    }
    
    
}
