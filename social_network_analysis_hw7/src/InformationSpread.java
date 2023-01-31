import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Dimension;
import java.io.*;
import java.util.*;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

public class InformationSpread implements IInformationSpread {

    private static Graph graph = new GraphL();

    @Override
    public int loadGraphFromDataSet(String filePath, double tau) {

        int edgesAdded = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line = br.readLine();

            String[] title = line.split(" ");
            graph.init(Integer.parseInt(title[0]) + 1);


            while ((line = br.readLine()) != null) {

                String[] edges = line.split(" ");
                int v = Integer.parseInt(edges[0]);
                int w = Integer.parseInt(edges[1]);

                // if the node has deg > 1, then add an edge (v,w) and (w,v)
                if (v != 0) {
                    graph.setValue(v, v);
                }
                
                if (w != 0) {
                    graph.setValue(w, w);
                }
                
                if (v != 0 && w != 0) {
                    graph.setValue(v, v);
                    graph.setValue(w, w);
                    int weight = (int)(Double.parseDouble(edges[2]) * 100);

                    if (weight >= tau * 99.9999) {
                        graph.addEdge(v, w, weight);
                        graph.addEdge(w, v, weight);

                        edgesAdded += 2;
                    }

                }
            }
            return edgesAdded / 2 + 1;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;


    }

    @Override
    public int[] getNeighbors(int id) {
        return this.graph.neighbors(id);
    }

    @Override
    public Collection<Integer> path(int source, int destination) {
        
        List<Integer> path = new ArrayList<Integer>();
        
        //Change the weight of each edge to be "inverse" weight
        for (int i = 1; i < graph.nodeCount(); i++) {
            for (Integer v : getNeighbors(i)) {
                if (graph.hasEdge(i, v)) {
                    int weight = 100 - graph.weight(i, v);
                    graph.removeEdge(i, v);
                    graph.addEdge(i, v, weight);
                }
            }
        }

        // use BFS to find the shortest path
        int[] shrt = new int[graph.nodeCount()];
        int[] pred = new int[graph.nodeCount()];

        for (int i = 1; i < graph.nodeCount(); i++) {
            graph.setValue(i, null);
        }

        shrt[source] = 0;
        for (int i = 1; i < shrt.length; i++) {
            if (i != source) {
                shrt[i] = INFINITY;
            }
        }

        Queue<Integer> queue = new LinkedList<Integer>();
        graph.setValue(source, true);
        queue.add(source);

        while (!queue.isEmpty()) {

            int curr = queue.poll();
            for (Integer v : getNeighbors(curr)) {

                if (graph.getValue(v) == null) {
                    queue.add(v);
                    graph.setValue(v, true);
                }

                if (shrt[curr] + graph.weight(curr, v) < shrt[v]) {
                    pred[v] = curr;
                    shrt[v] = shrt[curr] + graph.weight(curr, v);
                }
            }
        }

        for (int j = destination; pred[j] != 0; j = pred[j]) {
            path.add((Integer) j);
        }

        if (path.size() == 0) {
            return path;
        }

        path.add((Integer) source);
        Collections.reverse(path);

        return path;
    }

    @Override
    public double avgDegree() {

        Double avgD = Double.valueOf(graph.edgeCount()) / Double.valueOf(graph.nodeCount() - 1);

        return avgD;
    }

    @Override
    public double rNumber() {

        return this.avgDegree() * 0.55 * 1;
    }

    @Override
    public int generations(int seed, double threshold) {

        // init some vars to help us track the current level, nodes to visit
        // before we increment level, and the nodes we have visited
        int gen = 0;
        int nodes = 1;
        int nodesLeftToCheck = 1;
        int neighbors = 0;

        if (threshold < 0 || threshold > 1 || seed <= 0 || seed >= graph.nodeCount()) {
            return -1;
        } else if (threshold == 0) {
            return 0;
        }

        for (int i = 1; i < graph.nodeCount(); i++) {
            graph.setValue(i, null);
        }

        // BFS
        Queue<Integer> queue = new LinkedList<Integer>();
        graph.setValue(seed, true);
        queue.add(seed);

        while (!queue.isEmpty()) {

            int curr = queue.poll();
            neighbors += getNeighbors(curr).length;

            for (Integer v : getNeighbors(curr)) {

                if (graph.getValue(v) != null) {
                    neighbors--;
                } else {
                    queue.add(v);
                    nodes++;
                    graph.setValue(v, true);
                }
            }

            nodesLeftToCheck -= 1;
            if (nodesLeftToCheck == 0) {
                gen++;

                // check the threshold
                if ((double) nodes / (graph.nodeCount() - 1) >= threshold) {
                    return gen;
                }
                nodesLeftToCheck += neighbors;
                neighbors = 0;
            }
        }
        return -1;


    }

    @Override
    public int degree(int n) {

        if (n >= graph.nodeCount() || n <= 0) {
            return -1;
        }

        return getNeighbors(n).length;
    }

    @Override
    public Collection<Integer> degreeNodes(int d) {
        Set<Integer> nodes = new HashSet<Integer>();

        for (int i = 1; i < graph.nodeCount(); i++) {
            if (this.degree(i) == d) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    @Override
    public int generationsDegree(int seed, double threshold, int d) {

        Collection<Integer> degreeNodes = degreeNodes(d);

        if (degreeNodes.size() == 0) {
            return -1;
        } else if (degreeNodes.contains(seed)) {
            return 0;
        }

        //remove nodes with degree d
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (degreeNodes.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }

        return this.generations(seed, threshold);

    }

    @Override
    public double rNumberDegree(int d) {
        
        Collection<Integer> degreeNodes = degreeNodes(d);
        //remove nodes with degree d
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (degreeNodes.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }
        
        return this.rNumber();
    }

    @Override
    public double clustCoeff(int n) {
        
        if (n <= 0 || n >= graph.nodeCount()) {
            return -1;
        } else if (degree(n) <= 1) {
            return 0;
        }
        
        double edges = 0;
        double all = degree(n) * (degree(n) - 1);
        
        for (int v : getNeighbors(n)) {
            for (int w : getNeighbors(n)) {
                if (graph.hasEdge(v, w)) {
                    edges++;
                }
            }
        }
        
        return edges / all;
    }

    @Override
    public Collection<Integer> clustCoeffNodes(double low, double high) {
        Set<Integer> ccNodes = new HashSet<Integer>();
        
        for (int i = 1; i < graph.nodeCount(); i++) {
            if (clustCoeff(i) >= low && clustCoeff(i) <= high) {
                ccNodes.add(i);
            }
        }
        
        return ccNodes;
    }

    @Override
    public int generationsCC(int seed, double threshold, double low, double high) {
        
        if (threshold < 0 || threshold > 1 || seed <= 0 || seed >= graph.nodeCount()) {
            return -1;
        }
        
        Collection<Integer> ccNodes = clustCoeffNodes(low, high);
        
        if (ccNodes.size() == 0) {
            return -1;
        } else if (ccNodes.contains(seed)) {
            return 0;
        }
        
      //remove nodes with cc
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (ccNodes.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }
        
        return this.generations(seed, threshold);
        
    }

    @Override
    public double rNumberCC(double low, double high) {
        Collection<Integer> ccNodes = clustCoeffNodes(low, high);
        
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (ccNodes.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }
        
        return this.rNumber();
    }

    @Override
    public Collection<Integer> highDegLowCCNodes(int lowBoundDegree, double upBoundCC) {
        Set<Integer> nodes = new HashSet<Integer>();

        for (int i = 1; i < graph.nodeCount(); i++) {
            if (clustCoeff(i) <= upBoundCC && degree(i) >= lowBoundDegree) {
                nodes.add(i);
            }
        }
        return nodes;
    }

    @Override
    public int generationsHighDegLowCC(int seed, 
            double threshold, int lowBoundDegree, double upBoundCC) {
        Collection<Integer> degreeCC = highDegLowCCNodes(lowBoundDegree, upBoundCC);
        
        if (seed <= 0 || seed >= graph.nodeCount() || threshold < 0 || threshold > 1) {
            return -1;
        }
        
        if (degreeCC.size() == 0) {
            return -1;
        } else if (degreeCC.contains(seed)) {
            return 0;
        }
        
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (degreeCC.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }
        
        return this.generations(seed, threshold);
        
    }

    @Override
    public double rNumberDegCC(int lowBoundDegree, double upBoundCC) {
        Collection<Integer> degreeCC = highDegLowCCNodes(lowBoundDegree, upBoundCC);
        
        for (int v = 1; v < graph.nodeCount(); v++) {
            if (degreeCC.contains(v)) {
                for (int w : getNeighbors(v)) {
                    graph.removeEdge(v, w);
                    graph.removeEdge(w, v);
                }
            }
        }
        
        return this.rNumber();
        
    }
    
    public Graph getGraph() {
        return graph;
    }
    
}
