import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NetworkBuilderTest {

    private final String SEED_WIKI_PAGE_TITLE = "Java_(programming_language)";

    public INetworkBuilder createNetworkBuilder(){
        return new NetworkBuilder(SEED_WIKI_PAGE_TITLE);
    }

    @Test
    public void allTestDrive() {

        // KEY INPUTS //
        int throttleLinks = 100;
        int numMarkovSimulations = 10000;
        
        // SIMULATION INPUT //
        String removeNone = null;
        String removalOne = "High-level programming language";
        
        // RUN EVERYTHING //
        INetworkBuilder builder = createNetworkBuilder();
        builder.runBFSPreferred(throttleLinks, 20, true);

        testConnection(builder);
        builder.convertTitleToIndex();
        testConvertTitleToIndex(builder);
        builder.buildGraph();
        testGraph(builder);
        builder.buildLeapProbMatrix();
        builder.outputGraphToFile("graph.csv");
        testProbLeapMat(builder);
        testSimulation(builder);

        float[] pageRank = builder.runSimulation(numMarkovSimulations);
        builder.webPageResults(pageRank, new String[]{removalOne});
    }

    @Test
    private void testConnection(INetworkBuilder builder){
        Assertions.assertTrue(builder.getTitleToTitle().size() > 0);
    }

    @Test
    private void testConvertTitleToIndex(INetworkBuilder builder){
        Assertions.assertTrue(builder.getTitleToId().containsKey(SEED_WIKI_PAGE_TITLE));
        Assertions.assertTrue(builder.getTitleToId().get(SEED_WIKI_PAGE_TITLE) > 0);
    }

    @Test
    private void testGraph(INetworkBuilder builder){
        Assertions.assertEquals(builder.getTitleToId().size(), builder.getGraph().nodeCount() - 1);
    }

    @Test
    private void testProbLeapMat(INetworkBuilder builder){
        for(int i = 1; i < builder.getGraph().nodeCount(); i++){
            for(int j = 1; j < builder.getGraph().nodeCount(); j++){
                if(i != j && builder.getGraph().hasEdge(i, j)) {
                    Assertions.assertTrue(builder.getGraph().weight(i, j) != 100);
                }
            }
        }
    }

    @Test
    private void testSimulation(INetworkBuilder builder){
        Assertions.assertTrue(builder.runSimulation(100).length > 0);
    }
}