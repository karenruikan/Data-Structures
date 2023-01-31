import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class NetworkBuilder implements INetworkBuilder {
    private Graph graph;
    private final String initialQueryTerm;

    private final String url      = "https://en.wikipedia.org/w/api.php?action=query&titles=";
    private final String modifier = "&format=xml&prop=links&pllimit=max";

    private Map<String, HashSet<String>> titleToTitle = new HashMap<String, HashSet<String>>();
    private Map<String, Integer> titleToId = new HashMap<String, Integer>();
    private Map<Integer, String> idToTitle = new HashMap<Integer, String>();
    
    
    // Set of getter functions 
    public Map<String, HashSet<String>> getTitleToTitle() {
        return titleToTitle;
    }

    public Map<String, Integer> getTitleToId() {
        return titleToId;
    }

    public Map<Integer, String> getIdToTitle() {
        return idToTitle;
    }

    public Graph getGraph(){
        return graph;
    }

    /**
     * Constructor for the NetworkBuilder which takes in a seed term of the extra string title of a Wikipedia 
     * page and sets the degree of depth to scrape webpages. For example, suppose we have a depth of 3:
     * (Level 1) We will find all the Wikipedia links from the seed Wikipedia webpage  
     * (Level 2) We will find all the Wikipedia links from each of those level 1 webpages
     * (Level 3) We will find all the Wikipedia links from each of those level 2 webpages.
     * @param initialQueryTerm - exact (including uppercase and special characters) Wikipedia webpage titles
     */
    public NetworkBuilder(String initialQueryTerm){
        // properly converts and store the wikipedia webpage title
        this.initialQueryTerm = initialQueryTerm;
    }

    public void buildGraphFromFile(String filePath){
        graph = new GraphL();
        int nodes = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String   fileData = reader.readLine();
            String[] fileDataArr = fileData.split(" ");

            nodes = Integer.parseInt(fileDataArr[0]);
            graph.init(nodes + 1);

            while(true){
                String data = reader.readLine();

                if(data == null){
                    break;
                }
                String[] dataArr = data.split(" ");
                int nodeStart = Integer.parseInt(dataArr[0]);
                int nodeEnd   = Integer.parseInt(dataArr[1]);
                double weight    = Double.parseDouble(dataArr[2]);
                int multWeight = (int)(weight * 100);
                graph.addEdge(nodeStart, nodeEnd, multWeight);
                graph.addEdge(nodeEnd, nodeStart, multWeight);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Comparator<Entry<String, Float>> byWeight() {
        return (s1, s2) -> {
            if (s1.getValue().compareTo(s2.getValue()) == 0) {
                return s1.getKey().compareTo(s2.getKey());
            }
            return s2.getValue().compareTo(s1.getValue());
        };
    }

    @Override
    public Set<String> retrieveData(String currentQuery) throws IOException {
        String currentQueryFormatted = url + currentQuery + modifier;
        Document wikiInit = Jsoup.connect(currentQueryFormatted).get();
        titleToTitle.put(currentQuery, new HashSet<String>());
        return parseXml(wikiInit);
    }

    private Set<String> parseXml(Document data){
        Set<String> connectionsSet = new HashSet<String>();

        Elements connections = data.getElementsByTag("links");
        String[] connectionsArray = connections.toString().split("/>");

        for(String currentConnection : connectionsArray){
            String[] connectionArray = currentConnection.split("\"");

            if(connectionArray.length > 3) {
                String title = connectionArray[3];
                connectionsSet.add(title);
            }
        }
        return connectionsSet;
    }

    @Override
    public void runBFSPreferred(int maxIterations, int minNum, boolean runWithPreferred){
        Queue<String> queue = new LinkedList<String>();
        String currentQuery = initialQueryTerm;

        try {
            Set<String> connections = retrieveData(currentQuery);
            titleToTitle.put(currentQuery, (HashSet<String>) connections);
            queue.addAll(connections);

            for(int i = 0; i < maxIterations; i++){
                currentQuery = queue.poll();
                connections = retrieveData(currentQuery);
                Set<String> selectedConnections = getPreferredConnections(connections, minNum, runWithPreferred);
                titleToTitle.put(currentQuery, (HashSet<String>) selectedConnections);
                queue.addAll(selectedConnections);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    private Set<String> getPreferredConnections(Set<String> connections, int minNum, boolean runWithPreferred){
        Set<String> selectedConnections = new HashSet<String>();
        if(runWithPreferred) {
            for (String connection : connections) {
                for (HashSet<String> values : titleToTitle.values()) {
                    if (values.contains(connection)) {
                        selectedConnections.add(connection);
                    }
                }
            }
        }
        if(selectedConnections.size() < minNum){
            selectedConnections = populateToMinNumberConnections(selectedConnections, connections, minNum);
        }

        return selectedConnections;
    }

    private Set<String> populateToMinNumberConnections(Set<String> selectedConnections, Set<String> connections, int minNum){
        int count = selectedConnections.size();
        for(String connection : connections) {
            if(count == minNum){
                break;
            }
            selectedConnections.add(connection);
            count++;
        }

        return selectedConnections;
    }

    @Override
    public void removeWebpage(String title){
        int id = titleToId.get(title);
        for(Integer connection : graph.neighbors(id)){
            graph.removeEdge(id, connection);
            graph.removeEdge(connection, id);
        }
    }

    @Override
    public int degree(int n) {
        return graph.neighbors(n).length;
    }

    @Override
    public double avgDegree() {
        int n = graph.nodeCount() - 1;
        int totalDegree = 0;
        for(int id = 0; id < n; id++){
            totalDegree += degree(id);
        }
        return (double)totalDegree / (graph.nodeCount() - 1);
    }
    
    @Override
    public void convertTitleToIndex() {
        int uniqueId = 1;

        if(titleToId.size() != 0){
            return;
        }

        //Encode all the keys first
        for(String key : titleToTitle.keySet()){
            if(!titleToId.containsKey(key)) {
                titleToId.put(key, uniqueId);
                idToTitle.put(uniqueId, key);
                uniqueId++;
            }
        }

        //Encode all values next
        for(String key : titleToTitle.keySet()){
            for(String value : titleToTitle.get(key)) {
                if (!titleToId.containsKey(value)) {
                    titleToId.put(value, uniqueId);
                    idToTitle.put(uniqueId, value);
                    uniqueId++;
                }
            }
        }
    }

    @Override
    public void buildGraph(){
        Graph graph = new GraphL();
        graph.init(titleToId.size() + 1);

        for(Entry<String, HashSet<String>> toConnection : titleToTitle.entrySet()){
            for(String fromConnection : toConnection.getValue()) {
                System.out.println("Constructing Edge: " + toConnection.getKey() + ", " + fromConnection);
                graph.addEdge(titleToId.get(toConnection.getKey()), titleToId.get(fromConnection), 1);
                graph.addEdge(titleToId.get(fromConnection), titleToId.get(toConnection.getKey()), 1);
            }
        }
        this.graph = graph;
    }

    private int getNumLinksTo(String title){
        int count = 0;
        for(HashSet<String> values : titleToTitle.values()){
            if(values.contains(title)){
                count++;
            }
        }
        return count;
    }

    @Override
    public void buildLeapProbMatrix(){
        int n = graph.nodeCount() -  1;
        float leapProb = 0.1f/n;

        for(int i = 1; i < n; i++){
            int[] neighbors = graph.neighbors(i);
            for(int neighbor : neighbors){
                int prob = calcProb(leapProb, neighbors, i);
                graph.setWeight(i, neighbor, prob);
                graph.setWeight(neighbor, i, prob);
            }
        }
    }

    private int calcProb(float leapProb, int[] neighbors, int i){
        return (int)(leapProb + ((0.9f * ((((float)getNumLinksTo(idToTitle.get(i)) == 0.0 ? leapProb : (float)getNumLinksTo(idToTitle.get(i))) / neighbors.length)))) * 100);
    }

    @Override
    public float[] runSimulation(int numIterations){

        int n = graph.nodeCount() - 1;
        float[] freqs = new float[n];
        int currentPage = (int)Math.floor(Math.random() * n);

        for(int i = 0; i < numIterations; i++){
            double move = Math.random();
            double sum  = 0;

            for(int target = 1; target < n; target++){
                sum += ((float)graph.weight(currentPage, target) / 100);
                if(move < sum){
                    currentPage = target;
                    break;
                }
            }

            freqs[currentPage]++;
        }
        return freqs;
    }

    @Override
    public void webPageResults(float[] pageRanks, String[] removePages) {
        System.out.println("Degree of graph: " + avgDegree());
        if (removePages.length != 0) {
            for(String toRemove : removePages) {
                removeWebpage(toRemove);
            }
        }
        System.out.println("Degree of graph: " + avgDegree());

        int pageCount = titleToTitle.keySet().size();
        for(Set<String> values : titleToTitle.values()){
            pageCount += values.size();
        }
        System.out.println("Scanned Size " + pageCount);
        System.out.println("Graph Size " + (graph.nodeCount() - 1));
        TreeSet<Entry<String, Float>> pageRankData = new TreeSet<Entry<String, Float>>(byWeight());
        for(int i = 0; i < pageRanks.length; i++){
            pageRankData.add(new AbstractMap.SimpleEntry<String, Float>(idToTitle.get(i + 1), pageRanks[i]));
        }
        System.out.println(pageRankData);
    }

    public void outputGraphToFile(String filePath){
        FileWriter writer = null;

        for(int i = 1; i < graph.nodeCount(); i++){
            graph.setValue(i, false);
        }


        try {
            writer = new FileWriter(new File(filePath));
            Queue<Integer> queue = new LinkedList<Integer>();
            queue.add(titleToId.get(initialQueryTerm));
            graph.setValue(titleToId.get(initialQueryTerm), true);

            while(!queue.isEmpty()){
                int currentQuery = queue.poll();
                int[] neighbors = graph.neighbors(currentQuery);

                for (int neighbor : neighbors) {
                    if(!((boolean) graph.getValue(neighbor))) {
                        writer.write(currentQuery + "," + neighbor + "," + graph.weight(currentQuery, neighbor) + "\n");
                        writer.write(neighbor + "," + currentQuery + "," + graph.weight(neighbor, currentQuery) + "\n");
                        queue.add(neighbor);
                        graph.setValue(neighbor, true);
                    }
                }
            }
            writer.close();
        } catch(IOException ignored){}
    }
}
