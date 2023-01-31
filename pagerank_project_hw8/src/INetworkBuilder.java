import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface INetworkBuilder {
    
    /**
     * Performs a one level BFS and a throttled DFS to traverse wikipedia pages for links.
     * Context: we need to generate a dense graph of connected webpages. Wikipedia is a dense but vast graph
     * Doing a BFS or DFS at 2 levels get on the order of 10^5 pages. Therefore, we do a one level deep BFS
     * Then we do a controlled DFS where from each webpage scraped by BFS, we grab the "max iterations" or 
     * the ceiling number of webpages from the DFS. This effectively throttles the computation load but 
     * also gets us a dense graph
     * @param maxIterations - maximum number webpage link to obtain for each webpage
     */
    void runBFSPreferred(int maxIterations, int minNum, boolean runWithPreferred);
    
    /**
     * Parses a Wikipedia HTML document. This calls the Wikipedia API which returns the list of links on a 
     * wikipedia page. These links are then subsequently processed to that a set of links that a page 
     * connects is returned.  
     * @param currentQuery is the exact string title of a webpage
     * @return Set containing the webpages that are connected / linked to the currentQuery webpage
     * @throws IOException
     */
    Set<String> retrieveData(String currentQuery) throws IOException;
    
    /**
     * buildGraph() takes the title to title connections Map scraped from Wikipedia 
     * and enters it into the GraphL instance
     * @return
     */
    public void buildGraph();
    
    /**
     * Converts initial Map of webpages and their respective connected webpages 
     * and indexes each webpage as an integer for usage with GraphL
     */
    public void convertTitleToIndex();
    
    /**
     * Removes a webpage to simulated a targeted attack on disrupting Page Rank
     * @param title the exact string title of the wikipedia page
     */
    void removeWebpage(String title);
    
    /**
     * Returns the degree of a webpage (i.e., the links to and from a webpage). Each webpage
     * is associated with a unique integer id (similar to GraphL implementation on HW7)
     * @param n - the node id
     * @return
     */
    int degree(int n);
    
    /**
     * Returns the average number of degrees for a node in the graph
     * @return
     */
    double avgDegree();
    
    /**
     * Builds the leap probability matrix which will contain the initial values for the 
     * iterative calculation of the PageRank values for each page. 
     * Since the PageRank Algorithm is an iterative calculation of probabilities, the leapProMatrix
     * seeds each webpage with a probability and allows the Markov calculation to proceed
     */
    void buildLeapProbMatrix();
    
    /**
     * Runs the state simulations in a Markov chain. The Markov chain requires running simulations 
     * to reach a more precise value of the PageRank, since PageRank is an iterative formmula. 
     * Suggest to run at least 10,000 simulations
     * @param numIterations - number of iterations 
     * @return
     */
    float[] runSimulation(int numIterations);
    
    
    /**
     * Takes in the set of calculated pagerank and allows for a webpage remove
     * @param pageranks
     * @param removePages
     */
    public void webPageResults(float[] pageranks, String[] removePages);

    Map<String, HashSet<String>> getTitleToTitle();
    Map<String, Integer> getTitleToId();
    Map<Integer, String> getIdToTitle();
    Graph getGraph();
    void outputGraphToFile(String filePath);
}
