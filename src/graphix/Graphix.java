
package graphix;


import javax.swing.UIManager;

/**
 * ΕΑΠ - ΠΛΗ31 - 4η ΕΡΓΑΣΙΑ 2015-2016
 * @author Tsakiridis Sotiris
 */
public class Graphix {
    
    public static void main(String[] args) {
        
        
        // Set Nimbus Look & Feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        // Τροποποίηση της γλώσσας στα πλήκτρα του διαλόγου JOptionPane
        UIManager.put("OptionPane.yesButtonText", "Ναι");
        UIManager.put("OptionPane.noButtonText", "Όχι");
        
        // Μετάφραση στα ελληνικά του JFileChooser
        UIManager.put("FileChooser.openButtonText","Άνοιγμα");
        UIManager.put("FileChooser.cancelButtonText","Ακύρωση");
        UIManager.put("FileChooser.saveButtonText","Αποθήκευση");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Ακύρωση");
        UIManager.put("FileChooser.saveButtonToolTipText", "Αποθήκευση");
        UIManager.put("FileChooser.openButtonToolTipText", "Άνοιγμα");
        UIManager.put("FileChooser.lookInLabelText", "Αναζήτηση σε :");
        UIManager.put("FileChooser.fileNameLabelText", "Όνομα αρχείου:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Τύπος αρχείου:");
        UIManager.put("FileChooser.upFolderToolTipText", "Επάνω");
        UIManager.put("FileChooser.homeFolderToolTipText", "Αρχικός Φάκελος");
        UIManager.put("FileChooser.newFolderToolTipText", "Νέος Φάκελος");
        UIManager.put("FileChooser.listViewButtonToolTipText","Προβολή λίστας");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Προβολή λεπτομερειών");
  

        
        MainFrame mft = new MainFrame();
        mft.setVisible(true);
        
    } // end main()
    
}
