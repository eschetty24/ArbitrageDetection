package nets150.group.maven.eclipse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;


public class Graph {
    
    public static int totalEdges; 

    Map<Integer, Double>[] graph;

    @SuppressWarnings("unchecked")
    void buildGraph(int n) {
        graph = new HashMap[n];
        for (int i = 0; i < graph.length; i++) {
            graph[i] = new HashMap<>();
        }
    }

    
    public Graph(int n) {
        totalEdges = 0; 
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        buildGraph(n);
    }

   
    public int getSize() {
        return graph.length;
    }

    
    public boolean hasEdge(int u, int v) {
        checkVertices(u, v);
        return graph[u].containsKey(v);
    }

    
    public double getWeight(int u, int v) {
        checkVertices(u, v);
        if (!(graph[u].containsKey(v))) {
            throw new NoSuchElementException();
        }
        return graph[u].get(v);
    }

    void checkVertices(int u, int v) {
        boolean inRangeU = u >= 0 && u < graph.length;
        boolean inRangeV = v >= 0 && v < graph.length;
        if (!(inRangeU) || !(inRangeV)) {
            throw new IllegalArgumentException();
        }
        boolean inGraphU = graph[u] != null;
        boolean inGraphV = graph[v] != null;
        if (!(inGraphU) || !(inGraphV)) {
            throw new IllegalArgumentException();
        }
    }

    
    public boolean addEdge(int u, int v, double weight, boolean isUndirected) {
        checkVertices(u, v);
        if (u == v) {
            throw new IllegalArgumentException();
        }
        if (hasEdge(u, v)) {
            if (graph[u].get(v) == weight) {
                return false;
            }
        }
        graph[u].put(v, weight);
        if (isUndirected) {
            graph[v].put(u, -1 * weight);
        }
        totalEdges++;
        return true;
    }

   
    public Set<Integer> outNeighbors(int v) {
        if (v < 0 || v >= graph.length || graph[v] == null) {
            throw new IllegalArgumentException();
        }
        Set<Integer> out = new HashSet<>();
        Map<Integer, Double> outNeighbors = graph[v];
        for (Entry<Integer, Double> vertex : outNeighbors.entrySet()) {
            out.add(vertex.getKey());
        }
        return out;
    }

    // methods for testing
    public void setEdge(int u, int v, double w) {
        graph[u].put(v, w);
    }

}