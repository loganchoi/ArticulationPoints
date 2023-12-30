package edu.uwb.rjdecker;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class GraphInjector {
    public static int[][] edges;

    public static void populateEdges(int graphSize, String inputPath) throws IOException {
        Scanner fileReader = new Scanner(new File(inputPath));
        edges = new int[graphSize][];
        for (int i = 0; i < graphSize; i++) {
            String[] tokens = fileReader.nextLine().split(":");
            int vertex = Integer.parseInt(tokens[0]);
            String[] neighbors = tokens[1].split(",");
            edges[vertex] = new int[neighbors.length];
            for (int j = 0; j < neighbors.length; j++) {
                edges[vertex][j] = Integer.parseInt(neighbors[j]);
            }
        }
    }
}
