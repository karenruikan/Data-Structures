import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.Test;


public class InformationSpreadTest {

    InformationSpread is = new InformationSpread();
    String dir = "/autograder/submission/test_graph.mtx";

    @Test
    public void testLoadGraphFromDataSet() {

        int nodesWithEdge = is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(8, nodesWithEdge);

    }

    @Test
    public void testGetNeighbors() {
        is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(2, is.getNeighbors(1).length);
        assertEquals(2, is.getNeighbors(2).length);
        assertEquals(2, is.getNeighbors(3).length);
        assertEquals(0, is.getNeighbors(4).length);
        assertEquals(1, is.getNeighbors(8).length);
    }

    @Test
    public void testPath() {
        is.loadGraphFromDataSet(dir, 0.55);
        
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 3, 7, 2)), is.path(1, 2));
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 3)), is.path(1, 3));
        assertEquals(new ArrayList<Integer>(), is.path(1, 8));
        
        is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 3, 7)), is.path(1, 7));
        assertEquals(new ArrayList<Integer>(Arrays.asList(8, 9, 10, 12)), is.path(8, 12));
    }

    @Test
    public void testAvgDegree() {
        is.loadGraphFromDataSet(dir, 0.55);

        Double avgD = 14.0 / 12.0;
        assertEquals(avgD, is.avgDegree());

    }

    @Test
    public void testRNumber() {
        is.loadGraphFromDataSet(dir, 0.55);

        Double rNum = 14.0 / 12.0 * 0.55 * 1;
        assertEquals(rNum, is.rNumber());
    }

    @Test
    public void testGenerations() {
        is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(1, is.generations(1, 1.0 / 6.0));
        assertEquals(2, is.generations(1, 4.0 / 12.0));
        assertEquals(-1, is.generations(1, 5.0 / 12.0));
        assertEquals(0, is.generations(1, 0));
    }

    @Test
    public void testDegree() {
        is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(2, is.degree(1));
        assertEquals(0, is.degree(6));
        assertEquals(1, is.degree(8));

    }

    @Test
    public void testDegreeNodes() {
        is.loadGraphFromDataSet(dir, 0.55);
        assertEquals(new HashSet<Integer>(Arrays.asList(4, 5, 6, 11)), is.degreeNodes(0));
        assertEquals(new HashSet<Integer>(Arrays.asList(8, 12)), is.degreeNodes(1));
        assertEquals(new HashSet<Integer>(Arrays.asList(1, 2, 3, 7, 9, 10)), is.degreeNodes(2));
    }

    @Test
    public void testGenerationsDegree() {
        is.loadGraphFromDataSet(dir, 0.55);

        assertEquals(-1, is.generationsDegree(1, -1, 1));
        assertEquals(-1, is.generationsDegree(1, 5.0 / 12.0, 3));
        assertEquals(0, is.generationsDegree(1, 5.0 / 12.0, 2));

        assertEquals(1, is.generationsDegree(1, 1.0 / 6.0, 0));
        assertEquals(2, is.generationsDegree(1, 4.0 / 12.0, 1));
        assertEquals(-1, is.generationsDegree(1, 5.0 / 12.0, 0));
        assertEquals(0, is.generationsDegree(1, 0, 0));

    }

    @Test
    public void testRNumberDegree() {
        is.loadGraphFromDataSet(dir, 0.55);

        Double rNumDeg1 = 10.0 / 12.0 * 0.55 * 1;
        assertEquals(rNumDeg1, is.rNumberDegree(1));

        is.loadGraphFromDataSet(dir, 0.55);
        Double rNumDeg2 = 0.0 / 12.0 * 0.55 * 1;
        assertEquals(rNumDeg2, is.rNumberDegree(2));
    }

    @Test
    public void testClustCoeff() {
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 60);
        is.getGraph().addEdge(1, 7, 70);
        assertEquals(0.8333333333333334,is.clustCoeff(1));
        assertEquals(0.8333333333333334,is.clustCoeff(2));
        assertEquals(0, is.clustCoeff(4));
        assertEquals(0, is.clustCoeff(9));
        assertEquals(-1, is.clustCoeff(0));
        assertEquals(-1, is.clustCoeff(14));
    }

    @Test
    public void testClustCoeffNodes() {
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 60);
        is.getGraph().addEdge(1, 7, 70);

        assertEquals(new HashSet<Integer>(
                Arrays.asList(4, 5, 6, 8, 9, 10, 11, 12)), 
                is.clustCoeffNodes(-1, 0));
        assertEquals(new HashSet<Integer>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)), 
                is.clustCoeffNodes(0, 1));

    }

    @Test
    public void testGenerationsCC() {
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 60);
        is.getGraph().addEdge(1, 7, 70);
        
        assertEquals(1, is.generationsCC(1, 2.0 / 12.0, -1, 0));
        
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 60);
        assertEquals(2, is.generationsCC(1, 4.0 / 12.0, -1, 0));
        
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 60);
        is.getGraph().addEdge(1, 7, 70);
        assertEquals(1, is.generationsCC(1, 1.0 / 12.0, 0, 0.5));

    }

    @Test
    public void testRNumberCC() {
        is.loadGraphFromDataSet(dir, 0.55);

        Double rNumCC1 = 14.0 / 12.0 * 0.55 * 1;
        assertEquals(rNumCC1, is.rNumberCC(-1, -0.01));

    }

    @Test
    public void testHighDegLowCCNodes() {
        is.loadGraphFromDataSet(dir, 0.55);

        assertEquals(new HashSet<Integer>(
                Arrays.asList(1, 2, 3, 7, 8, 9, 10, 12)), 
                is.highDegLowCCNodes(1, 1));

        assertEquals(new HashSet<Integer>(
                Arrays.asList()), 
                is.highDegLowCCNodes(4, 1));
    }

    @Test
    public void testGenerationsHighDegLowCC() {
        is.loadGraphFromDataSet(dir, 0.55);

        assertEquals(0, is.generationsHighDegLowCC(1, 2.0 / 12.0, 1, 1));
        
        assertEquals(0, is.generationsHighDegLowCC(1, 2.0 / 12.0, 1, 0.5));
        
        assertEquals(-1, is.generationsHighDegLowCC(1, 2.0 / 12.0, 5, 0));
    }

    @Test
    public void testRNumberDegCC() {
        is.loadGraphFromDataSet(dir, 0.55);
        is.getGraph().addEdge(2, 3, 50);
        is.getGraph().addEdge(1, 7, 50);

        Double rNumDegCC1 = 10.0 / 12.0 * 0.55 * 1;
        assertEquals(rNumDegCC1, is.rNumberDegCC(1, 0));
        
    }

}
