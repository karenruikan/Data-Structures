

/**
 * ==== Attributes ====
 * - words: number of words
 * - term: the ITerm object
 * - prefixes: number of prefixes 
 * - references: Array of references to next/children Nodes
 * 
 * ==== Constructor ====
 * Node(String query, long weight)
 * 
 * @author Rui Kan
 */
public class Node {
    // Initializes a Node with the given query string and weight
    // (calls the Term constructor)

    private int words;
    private Term term;
    private int prefixes; 
    private Node[] references;
    
    public Node() {

        term = null;
        words = 0;
        prefixes = 0;
        references = new Node[26];
        
    }
    public Node(String query, long weight) {

        if (query == null) {
            throw new java.lang.IllegalArgumentException();
        }
        //exception will be thrown if weight of the word is less than 0
        if (query == null || weight < 0) {
            throw new java.lang.IllegalArgumentException();
        }
        
        this.term = new Term(query, weight);
        this.words = 0;
        this.prefixes = 0;
        this.references = new Node[26];
        
    }

    public Term getTerm() {
        return term;
    }
    
    public int getWords() {
        return words;
    }
    
    public int getPrefixes() {
        return prefixes;
    }
    
    public Node[] getReferences() {
        return references;
    }
    
    public void setTerm(Term term) {
        this.term = term;
    }
    
    public void setWords(int words) {
        this.words = words;
    }
    
    public void setPrefixes(int prefix) {
        this.prefixes = prefix;
    }
    
    public void setReferences(Node[] reference) {
        this.references = reference;
    }
    
}






