package nfp121.View;

import java.util.List;
import javax.swing.JComboBox;


public class Memento {
    
    // The article stored in memento Object
    private int article;
    // Save a new article String to the memento Object
    public Memento(int articleSave) { article = articleSave; }
    // Return the value stored in article
    public int getSavedArticle() { return article; }
    
}
