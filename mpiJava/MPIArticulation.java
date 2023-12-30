import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import mpi.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MPIArticulation {
    public static void main(String[] args) throws MPIException ,FileNotFoundException {
        String filePath = "./output_matrix.txt";
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        int graphSize = scanner.nextInt();
        int[][] edges = new int[graphSize][graphSize];
        for(int i =0; i< graphSize;i++){
            for(int j =0; j<graphSize;j++){
                edges[i][j] = scanner.nextInt();
            }
        }
        scanner.close();

        long startTime = System.currentTimeMillis(); 
        MPI.Init(args);
        int my_rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int rows = edges.length;
        MPI.COMM_WORLD.Bcast(new int[]{rows}, 0, 1, MPI.INT, 0);

        for (int i = 0; i< rows; i++){
            MPI.COMM_WORLD.Bcast(edges[i], 0, edges[i].length, MPI.INT, 0);
        }

        int vertices = edges.length / size;
        int start = my_rank * vertices;
        int end = start + vertices;

        int articulationPoints = 0;
        // Try removing each vertex 
        for (int target = start; target < end; target++) {
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
                System.out.println("Rank " + my_rank + " - Articulation Point: " + target);
            }
        }
        
        int[] sendPts = new int[]{articulationPoints};
        int[] recvPts = new int[1];
        MPI.COMM_WORLD.Reduce(sendPts,0,recvPts, 0, 1, MPI.INT, MPI.SUM, 0);

        if (my_rank == 0){
            System.out.println();
            System.out.println("Total Articulation Points: " + recvPts[0]);
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("Total Time: " + endTime + " milliseconds");
        }
        MPI.Finalize();
    }
}
