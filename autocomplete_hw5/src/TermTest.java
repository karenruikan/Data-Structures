import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import org.junit.Test;

public class TermTest {
    Term term1 = new Term("thisiscit594", 3);
    Term term2 = new Term("teststring", 5);
    Term term3 = new Term("tttttttttt", 4);
    
    @Test
    public void testTerm() {
        assertThrows(IllegalArgumentException.class, () -> {
            Term term4 = new Term(null, 3);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Term term5 = new Term("negative", -3);
        });
        
        assertNotNull(term1);
        assertNotNull(term2);
    }

//    @Test
//    public void testByReverseWeightOrder() {
//        Term term1 = new Term("thisiscit594", 3);
//        Term term2 = new Term("teststring", 5);
//        Term term3 = new Term("tttttttttt", 4);
//        List<Term> l = Arrays.asList(term1, term2, term3);
//        Collections.sort(l, ITerm.byReverseWeightOrder());
//        assertEquals(l.get(0).getTerm(), term2.getTerm());
//        assertEquals(l.get(1).getTerm(), term3.getTerm());
//        assertEquals(l.get(2).getTerm(), term1.getTerm());
//    }

    @Test
    public void testByPrefixOrder() {
        List<Term> l = Arrays.asList(term1, term2, term3);
        Collections.sort(l, ITerm.byPrefixOrder(5));
        assertEquals(l.get(0).getTerm(), term2.getTerm());
        assertEquals(l.get(1).getTerm(), term1.getTerm());
        assertEquals(l.get(2).getTerm(), term3.getTerm());
    }

    @Test
    public void testCompareTo() {
        
        assertTrue(term1.compareTo(term2) > 0);
        assertTrue(term2.compareTo(term1) < 0);
        List<Term> l = Arrays.asList(term1, term2, term3);
        Collections.sort(l);
        assertEquals(l.get(0).getTerm(), term2.getTerm());
        assertEquals(l.get(1).getTerm(), term1.getTerm());
        assertEquals(l.get(2).getTerm(), term3.getTerm());
    }

    @Test
    public void testToString() {
        assertEquals("3" + "\t" + "thisiscit594", term1.toString());
        assertEquals("5" + "\t" + "teststring", term2.toString());
    }

    @Test
    public void testGetWeight() {
        assertEquals(3, term1.getWeight());
        assertEquals(5, term2.getWeight());
    }

    @Test
    public void testGetTerm() {
        assertEquals("thisiscit594", term1.getTerm());
        assertEquals("teststring", term2.getTerm());
    }

    @Test
    public void testSetWeight() {
        term1.setWeight(2);
        assertEquals(2, term1.getWeight());
    }

    @Test
    public void testSetTerm() {
        term2.setTerm("nothisis596");
        assertEquals("nothisis596", term2.getTerm());
    }

}
