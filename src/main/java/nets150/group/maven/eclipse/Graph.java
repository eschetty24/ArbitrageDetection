package nets150.group.maven.eclipse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Contains the API necessary for a simple, (optionally) weighted directed
 * graph. We call the graph "optionally weighted" because it can be used by
 * algorithms that use weights (like Dijkstra's) and by algorithms that do not
 * (like BFS). An algorithm like BFS would simply ignore any weights present.
 * <p>
 * By convention, the n vertices will be labeled 0,1,...,n-1. The edge weights
 * can be any int value. Since we are labeling vertices from 0 to n-1, you may
 * find arrays/arraylists helpful! Self loops and parallel edges are not
 * allowed. Your implementation should use O(m + n) space. Please DO NOT use
 * adjacency matrices!
 * <p>
 * Also note that the runtimes given are expected runtimes. As a result, you
 * should be implementing your graph using a HashMap as the primary data
 * structure for the adjacency list.
 * <p>
 * Notice that this class also supports undirected graph. Which means you can
 * implement an undirected graph as each undirected edge between u and v being
 * two directed edge from u to v and from v to u.
 */
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

    /**
     * Initializes a graph of size {@code n}. All valid vertices in this graph thus
     * have integer indices in the half-open range {@code [0, n)}, n > 0.
     * <p/>
     * Do NOT modify this constructor header.
     *
     * @param n the number of vertices in the graph
     * @throws IllegalArgumentException if {@code n} is zero or negative
     * @implSpec This method should run in expected O(n) time
     */
    public Graph(int n) {
        totalEdges = 0; 
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        buildGraph(n);
    }

    /**
     * Returns the number of vertices in the graph.
     * <p/>
     * Do NOT modify this method header.
     *
     * @return the number of vertices in the graph
     * @implSpec This method should run in expected O(1) time.
     */
    public int getSize() {
        return graph.length;
    }

    /**
     * Determines if there's an directed edge from u to v.
     * <p/>
     * Do NOT modify this method header.
     *
     * @param u a vertex
     * @param v a vertex
     * @return {@code true} if the {@code u-v} edge is in this graph
     * @throws IllegalArgumentException if a specified vertex does not exist
     * @implSpec This method should run in expected O(1) time.
     */
    public boolean hasEdge(int u, int v) {
        checkVertices(u, v);
        return graph[u].containsKey(v);
    }

    /**
     * Returns the weight of an the directed edge {@code u-v}.
     * <p/>
     * Do NOT modify this method header.
     *
     * @param u source vertex
     * @param v target vertex
     * @return the edge weight of {@code u-v}
     * @throws NoSuchElementException   if the {@code u-v} edge does not exist
     * @throws IllegalArgumentException if a specified vertex does not exist
     * @implSpec This method should run in expected O(1) time.
     */
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

    /**
     * Creates an edge from {@code u} to {@code v} if it does not already exist. A
     * call to this method should <em>not</em> modify the edge weight if the
     * {@code u-v} edge already exists.
     * <p/>
     * Do NOT modify this method header.
     *
     * @param u      the source vertex to connect
     * @param v      the target vertex to connect
     * @param weight the edge weight
     * @return {@code true} if the graph changed as a result of this call, false
     *         otherwise (i.e., if the edge is already present)
     * @throws IllegalArgumentException if a specified vertex does not exist or if u
     *                                  == v
     * @implSpec This method should run in expected O(1) time
     */
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

    /**
     * Returns the out-neighbors of the specified vertex.
     * <p/>
     * Do NOT modify this method header.
     *
     * @param v the vertex
     * @return all out neighbors of the specified vertex or an empty set if there
     *         are no out neighbors
     * @throws IllegalArgumentException if the specified vertex does not exist
     * @implSpec This method should run in expected O(outdeg(v)) time.
     */
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