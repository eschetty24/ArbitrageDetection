package nets150.group.maven.eclipse;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class BellmanFordTest {

    private Graph g;

    @Before
    public void init() {
        g = new Graph(4);
        g.addEdge(0, 1, 2, true);
        g.addEdge(0, 3, 5, true);
        g.addEdge(1, 2, 3, true);
        g.addEdge(2, 3, -1, true);

    }

    @Test
    public void testGetNegativePath() {
        BellmanFord bf = new BellmanFord(g, 1.0);
        bf.run(0);
        Object[] expected = new Object[] { 1, 2, 3, 0, 1 };
        Object[] path = bf.getNegativeCycles().toArray();
        assertArrayEquals(expected, path);
    }

    @Test
    public void testGetNegativePathMultipleCycles() {
        g.addEdge(0, 2, -6, true);
        BellmanFord bf = new BellmanFord(g, 1.0);
        bf.run(0);
        Object[] expected = new Object[] { 2, 3, 0, 2 };
        Object[] path = bf.getNegativeCycles().toArray();
        assertArrayEquals(expected, path);
    }

    @Test
    public void testGetNegativePathMultipleCycles2() {
        g.addEdge(0, 2, 6, true);
        BellmanFord bf = new BellmanFord(g, 1.0);
        bf.run(0);
        Object[] expected = new Object[] { 1, 2, 0, 1 };
        Object[] path = bf.getNegativeCycles().toArray();
        assertArrayEquals(expected, path);
    }

    @Test
    public void testGetNegativePathNoCycle() {
        g.addEdge(0, 2, 6, true);
        g.addEdge(0, 1, 3, true);
        BellmanFord bf = new BellmanFord(g, 1.0);
        bf.run(0);
        assertNull(bf.getNegativeCycles());
    }

}
