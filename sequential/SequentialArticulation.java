import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implements the brute-force algorithm for finding articulation points
 * in a graph as a sequential program.
 */
public class SequentialArticulation {
    public static final int GRAPH_SIZE = 4000;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try (Scanner fileReader = new Scanner(new File("data/graph" + GRAPH_SIZE + ".txt"))) {
            int[][] edges = new int[GRAPH_SIZE][GRAPH_SIZE];
            for (int i = 0; i < GRAPH_SIZE; i++) {
                String[] neighbors = fileReader.nextLine().split("\\s+");
                for (int j = 0; j < GRAPH_SIZE; j++) {
                    edges[i][j] = Integer.parseInt(neighbors[j]);
                }
            }
            System.out.println(findArticulationPoints(edges));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime);
    }

    public static int findArticulationPoints(int[][] edges) {
        int articulationPoints = 0;
        // Try removing each vertex i
        for (int target = 0; target < edges.length; target++) {
            Deque<Integer> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            queue.addLast((target + 1) % edges.length);
            visited.add((target + 1) % edges.length);

            while (!queue.isEmpty()) {
                int vertex = queue.removeFirst();
                for (int neighbor = 0; neighbor < edges.length; neighbor++) {
                    if (neighbor != target && edges[vertex][neighbor] > 0 && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.addLast(neighbor);
                    }
                }
            }

            if (visited.size() != edges.length - 1) {
                articulationPoints++;
            }
        }
        return articulationPoints;
    }
}
