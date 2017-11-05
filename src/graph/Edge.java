
package graph;

import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import org.apache.commons.collections15.Transformer;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class Edge implements Comparable<Edge> {
    
    // Πεδία της κλάσης
    private double weight;      // Το βάρος της ακμής
    private Operator operator;  // Ο τελεστής δράσης
    
    // flag διαγραφής ακμής κατά την σχεδίαση - δεν αποθηκεύεται
    private boolean deleted = false;
    
    // Default constructor
    public Edge() {
        this.weight = 1;
    }

    // Constructor
    public Edge(double weight, Operator operator) {
        this.weight = weight;
        this.operator = operator;
    }
    
    // constructor - αρχικοποίηση από άλλο edge
    public Edge(Edge edge) {
        this.weight = edge.weight;
        this.operator = edge.operator;
        
    }

    
    // getters
    public double getWeight() { return this.weight; }
    public Operator getOperator() { return this.operator; }
    public boolean isDeleted() { return this.deleted; }
    
    // setters
    public void setWeight(double weight) { this.weight = weight; }
    public void setOperator(Operator operator) { this.operator = operator; }
    public void setDeleted(boolean b) { this.deleted = b; }
    
    // Override toString() method
    @Override
    public String toString() {
        return this.operator.getLabel();
    }
    
    
    // Edge Transformer για την ανάκτηση από xml
    public static Transformer<EdgeMetadata, Edge> getEdgeTransformer() {
        
        Transformer<EdgeMetadata, Edge> edgeTransformer = 
                new Transformer<EdgeMetadata, Edge>() {
                    @Override
                    public Edge transform(EdgeMetadata metadata) {
                        double weight = Double.parseDouble(metadata.getProperty("weight"));
                        String operator = metadata.getProperty("operator");
                        return new Edge(weight, Operator.explode(operator));
                        
                    }

                };
        
        return edgeTransformer;
    }
    
    
    // Hyperedge Transformer - Απαιτείται αλλά δεν χρησιμοποιείται
    public static Transformer<HyperEdgeMetadata, Edge> getHyperedgeTransformer() {
        
        Transformer<HyperEdgeMetadata, Edge> hyperEdgeTransformer = 
                new Transformer<HyperEdgeMetadata, Edge>() {
                    @Override
                    public Edge transform(HyperEdgeMetadata metadata) {
                        double weight = Double.parseDouble(metadata.getProperty("weight"));
                        String operator = metadata.getProperty("operator");
                        return new Edge(weight, Operator.explode(operator));
                        
                    }

                };
        
        return hyperEdgeTransformer;
    }

    
    // για την σύγκριση και ταξινόμηση ακμών με βάση την προτεραιότητα
    // του τελεστή δράσης
    @Override
    public int compareTo(Edge e) {
        
        return this.operator.getPriority() - e.getOperator().getPriority();
        
    }
        
    // Αποθήκευση δεδομένων του Node σε GraphML
    public static void saveToGraphML(GraphMLWriter<Node, Edge> ml) {
        
        // weight
        ml.addEdgeData("weight", null, "0",
            new Transformer<Edge, String>() {
                @Override
                public String transform(Edge edge) {
                    return Double.toString(edge.getWeight());
                }
            });

        // operator
        ml.addEdgeData("operator", null, "",
            new Transformer<Edge, String>() {
                @Override
                public String transform(Edge edge) {
                    return edge.getOperator().implode();
                }
            });
      
    }
    
    
}
