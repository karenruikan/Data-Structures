
import java.awt.Dimension;
import java.io.*;
import java.util.*;
import org.apache.commons.collections15.Transformer;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

class Edge
{
    private final String name;

    Edge(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}

public class GraphVisualization{

    public static void main(String[] args) throws IOException {
        
        String dir = "/Users/karenkan/MCIT/CIT594/hw8/graph.csv";
                
        DirectedGraph<String, Edge> g = new DirectedSparseGraph<String, Edge>();
        
        BufferedReader br = new BufferedReader(new FileReader(dir));

        String line = br.readLine();

        String[] title = line.split(" ");

        while ((line = br.readLine()) != null) {

            String[] edges = line.split(",");
            String v = edges[0];
            String w = edges[1];
            g.addVertex(v);
            g.addVertex(w);
            String e = edges[2];
            g.addEdge(new Edge(e), v, w);
        
        }
        
        VisualizationImageServer<String, Edge> vs =
                new VisualizationImageServer<String, Edge>(
                        new CircleLayout<String, Edge>(g), new Dimension(1000, 1000));
        
        Transformer<String, String> transformer = new Transformer<String, String>() {
            @Override
            public String transform(String arg0) {
            return arg0;
            }
        };
        
        class EdgeLabelTransformer implements Transformer<Edge, String>
        {
            @Override
            public String transform(Edge edge)
            {
                return edge.toString();
            }
        }
        
        vs.getRenderContext().setVertexLabelTransformer(transformer);
        vs.getRenderContext().setEdgeLabelTransformer(new EdgeLabelTransformer());
        
            JFrame frame = new JFrame("Result Graph");
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(vs);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
      }
    
    
}
