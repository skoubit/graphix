/*
 * Οι τελεστές κίνησης είναι στιγμιότυπα αυτής της κλάσης
 */
package graph;

import java.util.StringTokenizer;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class Operator {
    
    // πεδία
    private String label;
    private String description;
    private int priority;           // προτεραιότητα εφαρμογής τελεστή
    
    // Constructor
    public Operator(String label, String description, int priority) {
        this.label = label;
        this.description = description;
        this.priority = priority;
    }
    
    // Getters
    public String getLabel() { return label; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    
    // Setters
    public void setLabel(String label) { this.label = label; }
    public void setDescription(String description) {this.description = description; }
    public void setPriority(int priority) { this.priority = priority; }
    
   
    // Συνδυασμός δεδομένων σε string για την αποθήκευση σε xml
    public String implode() {
        return this.label + "," + this.description + "," + this.priority;
    }
    
    
    // Δημιουργία ενός operator από ένα string που επιστρέφει η implode
    public static Operator explode(String exp) {
        
        StringTokenizer st = new StringTokenizer(exp, ",");
        String label = st.nextToken();
        String description = st.nextToken();
        int priority = Integer.parseInt(st.nextToken());
        
        return new Operator(label, description, priority);
    }
    
}
