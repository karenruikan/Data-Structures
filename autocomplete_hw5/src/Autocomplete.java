import java.util.*;
import java.io.*;

public class Autocomplete implements IAutocomplete {

    Node rootNode;
    int maxSuggestions;

    public Autocomplete() {
        this.rootNode = new Node();
    }

    @Override
    public void addWord(String word, long weight) {
        this.rootNode.setPrefixes(this.rootNode.getPrefixes() + 1);
        word = word.trim();
        if (word.matches("[a-zA-Z]+")) {

            word = word.toLowerCase();
            Node parent = rootNode;

            for (int i = 0; i < word.length(); i++) {
                
                Node[] children = parent.getReferences(); 
                
                int idx = word.charAt(i) - 'a';
                Node child = children[idx];
                if (child == null) {
                    child = new Node();
                    
                    Node[] newChildren = children.clone(); 
                    newChildren[idx] = child;
                    
                    parent.setReferences(newChildren);
                }

                child.setPrefixes(child.getPrefixes() + 1);

                if (i == word.length() - 1) {
                    child.setTerm(new Term(word, weight));
                    child.setWords(1);
                }

                parent = child;
            }

        }
    }


    @Override
    public Node buildTrie(String filename, int k) {
        File inFile = new File(filename);
        BufferedReader bReader = null;

        try {
            bReader = new BufferedReader(new FileReader(inFile));
            // header
            bReader.readLine();
            while (true) {
                String line = bReader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                String[] splited = line.split("\\s+");

                if (splited.length > 1) {
                    if (splited[1].matches("[a-zA-Z]+")) {
                        long weight = Long.valueOf(splited[0]);
                        String word = splited[1].toLowerCase();

                        addWord(word, weight);
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        this.maxSuggestions = k;
        return rootNode;

    }

    @Override
    public int numberSuggestions() {

        return maxSuggestions;
    }

    @Override
    public Node getSubTrie(String prefix) {
        if (prefix == null || prefix == "" || prefix.trim() == "") {
            return null;
        }
        prefix = prefix.trim().toLowerCase();

        if (prefix.matches("[a-zA-Z]+")) {

            Node [] children = rootNode.getReferences();
            for (int i = 0; i < prefix.length(); i++) {
                int idx = prefix.charAt(i) - 'a';
                Node child = children[idx];
                if (child == null) {
                    break;
                }
                children = child.getReferences();
                if (i == prefix.length() - 1) {
                    return child;
                }
            }
        }
        return null;
    }



    @Override
    public int countPrefixes(String prefix) {
        Node subTrie = getSubTrie(prefix);
        
        return subTrie != null ? subTrie.getPrefixes() : 0;
    }


    @Override
    public List<ITerm> getSuggestions(String prefix) {

        List<ITerm> suggestions = new ArrayList<ITerm>();

        Node node = getSubTrie(prefix);
        
        if (node == null) {
            return suggestions;
        }

        List<Node> stack = new ArrayList<Node>();
        stack.add(node);

        while (stack.size() > 0) {
            Node current = stack.remove(0);

            if (current.getWords() == 1) {
                suggestions.add(current.getTerm());
            }

            for (Node n: current.getReferences()) {
                if (n != null) {
                    stack.add(n);
                }
            }
        }
        Collections.sort(suggestions);
        return suggestions;
    }

}
