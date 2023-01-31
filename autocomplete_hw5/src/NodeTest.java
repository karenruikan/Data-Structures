import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class NodeTest {

    Node node1 = new Node();
    Node node2 = new Node("teststring", 5);
    @Test
    public void testNode() {
        assertTrue(node1 != null);
        assertTrue(node2 != null);
 
    }

    @Test
    public void testGetTerm() {
        assertNull(node1.getTerm());
        assertEquals("teststring", node2.getTerm().getTerm());
        assertEquals(5, node2.getTerm().getWeight());
    }
    
    @Test
    public void testGetWords() {
        assertEquals(0, node1.getWords());
        assertEquals(0, node2.getWords());
    }

    @Test
    public void testGetPrefixes() {
        assertEquals(0, node1.getPrefixes());
        assertEquals(0, node2.getPrefixes());
    }

    @Test
    public void testGetReferences() {
        assertNull(node1.getReferences()[0]);
        assertNull(node2.getReferences()[0]);
    }
    
    @Test
    public void testSetTerm() {
        node1.setTerm(new Term("cit594", 4));
        assertEquals(4, node1.getTerm().getWeight());
        assertEquals("cit594", node1.getTerm().getTerm());
    }

    @Test
    public void testSetWords() {
        node1.setWords(10);
        assertEquals(10, node1.getWords());
    }
    
    @Test
    public void testSetPrefixes() {
        node1.setPrefixes(3);
        assertEquals(3, node1.getPrefixes());
    }
    
    @Test
    public void testSetReferences() {
        Node[] ref1 = null;
        ref1 = new Node[26];
        node1.setReferences(ref1);
        assertEquals(ref1, node1.getReferences());
    }
}
