import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class DocumentsProcessorTest {

    DocumentsProcessor dp = new DocumentsProcessor();
    String dirPath = "/autograder/submission";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testProcessDocuments() {

        Map<String, List<String>> m = dp.processDocuments(dirPath, 3);

        assertEquals(2, m.size());
        assertEquals(7, m.get("file1.txt").size());
        assertEquals(6, m.get("file2.txt").size());

        assertEquals("applebananapear", m.get("file1.txt").get(0));
        assertEquals("bananapearcherry", m.get("file1.txt").get(1));
        assertEquals("pearcherryorange", m.get("file1.txt").get(2));
        assertEquals("cherryorangei", m.get("file1.txt").get(3));
        assertEquals("orangeilike", m.get("file1.txt").get(4));
        assertEquals("ilikemany", m.get("file1.txt").get(5));
        assertEquals("likemanythings", m.get("file1.txt").get(6));

        assertEquals("catdograbbit", m.get("file2.txt").get(0));
        assertEquals("dograbbittiger", m.get("file2.txt").get(1));
        assertEquals("rabbittigeri", m.get("file2.txt").get(2));
        assertEquals("tigerilike", m.get("file2.txt").get(3));
        assertEquals("ilikemany", m.get("file2.txt").get(4));
        assertEquals("likemanythings", m.get("file2.txt").get(5));
    }

    @Test
    public void testtoreNWordSequences() {

        Map m = dp.processDocuments(dirPath, 3);

        List<Tuple<String, Integer>> listOfTuples = dp.storeNWordSequences(m, "./outputfile2.txt");

        assertEquals(2, listOfTuples.size());
        assertEquals(101, listOfTuples.get(0).getRight());
        assertEquals(77, listOfTuples.get(1).getRight());
    }

    @Test
    public void testComputeSimilarities() {

        String nwordfilepath3 = "./outputfile3.txt";
        Map m = dp.processDocuments(dirPath, 3);

        List<Tuple<String, Integer>> fileIndex = dp.storeNWordSequences(m, nwordfilepath3);

        TreeSet<Similarities> similaritiesTree = dp.computeSimilarities(nwordfilepath3, fileIndex);

        assertEquals(1, similaritiesTree.size());
        assertEquals(2, similaritiesTree.first().getCount());

    }

    @Test
    public void testPrintSimilarities() {

        String nwordfilepath4 = "./outputfile4.txt";
        Map m = dp.processDocuments(dirPath, 3);

        List<Tuple<String, Integer>> fileIndex = dp.storeNWordSequences(m, nwordfilepath4);

        TreeSet<Similarities> similaritiesTree = dp.computeSimilarities(nwordfilepath4, fileIndex);

        dp.printSimilarities(similaritiesTree, 1);
    }

    @Test
    public void testProcessAndStore() {

        String nwordfilepath5 = "./outputfile5.txt";

        List<Tuple<String, Integer>> fileIndex = dp.processAndStore(dirPath, nwordfilepath5, 3);

        assertEquals(2, fileIndex.size());
        assertEquals(101, fileIndex.get(0).getRight());
        assertEquals(77, fileIndex.get(1).getRight());

    }

}
