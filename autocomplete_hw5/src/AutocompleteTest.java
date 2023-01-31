import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.Test;

public class AutocompleteTest {
    String path = "/autograder/submission/pokemon.txt";
    Autocomplete ac = new Autocomplete();
    Node n = ac.buildTrie(path, 5);


    @Test 
    public void testAutocomplete() {
        assertTrue(ac != null);
    }

    @Test
    public void testAddWord() {
        ac.addWord("abnormal", 7);
        ac.addWord("because", 8);
        Node subTrie1 = ac.getSubTrie("Mew");
        Node subTrie2 = ac.getSubTrie("char");
        assertEquals(2, subTrie1.getPrefixes());
        assertEquals(3, subTrie2.getPrefixes());
    }

    @Test
    public void testBuildTrie() {
        assertTrue(ac.getSubTrie("ab") != null);
        assertEquals(8, ac.getSubTrie("be").getPrefixes());
    }

    @Test
    public void testGetSubTrie() {
        assertEquals(8, ac.getSubTrie("be").getPrefixes());
        assertEquals(3, ac.getSubTrie("ab").getPrefixes());
    }

    @Test
    public void testCountPrefixes() {
        int num = ac.countPrefixes("ab");
        assertEquals(3, num);
        int num1 = ac.countPrefixes("");
        int num2 = ac.countPrefixes(null);
        assertEquals(0, num1);
        assertEquals(0, num2);
    }

    @Test
    public void testGetSuggestions() {
        List<ITerm> suggestions1 = ac.getSuggestions("Mew");
        assertEquals(2, suggestions1.size());
        
        List<ITerm> suggestions2 = ac.getSuggestions("@##");
        assertEquals(0, suggestions2.size());
        
        List<ITerm> suggestions3 = ac.getSuggestions("");
        assertEquals(0, suggestions3.size());
        
        List<ITerm> suggestions4 = ac.getSuggestions(null);
        assertEquals(0, suggestions4.size());

    }
}
