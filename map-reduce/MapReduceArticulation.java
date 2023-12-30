import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

/**
 * Parallelizes the naive articulation points algorithm using MapReduce.
 *
 * @author Ryan Decker
 */
public class MapReduceArticulation {
    public enum Counter {
        COUNT
    }

    /**
     * Configures and executes the MapReduce job.
     *
     * @param args the input directory, output directory, and keywords
     */
    public static void main(String[] args) throws IOException {
        JobConf conf = new JobConf(ArticulationPoints.class);
        conf.setJobName("Articulation Points");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        // n maintains number of vertices
        conf.set("n", args[2]);

        RunningJob job = JobClient.runJob(conf);
        long artPoints = job.getCounters().findCounter(Counter.COUNT).getValue();
        System.out.println(artPoints);
    }

    /**
     * Implements the mapping operation for this program.
     */
    public static class Map
        extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, Text> {
        private JobConf conf;
        private int numVertices;

        /**
         * Configures this mapping object by initializing the job
         * configuration and the keyword hash set.
         *
         * @param conf the job configuration
         */
        public void configure(JobConf conf) {
            this.conf = conf;
            this.numVertices = Integer.parseInt(conf.get("n").toString());
        }

        /**
         * Maps the inverted indexing operation to this file split.
         *
         * @param docId the document ID, not used
         * @param value the text of the document
         * @param output the output collector
         * @param r the reporter
         * @throws IOException
         */
        public void map(LongWritable docId, Text value, OutputCollector<Text, Text> output, Reporter reporter
        ) throws IOException {
            String[] lines = value.toString().strip().split("\n");
            for (String line : lines) {
                if (!line.isBlank()) {
                    for (int i = 0; i < numVertices; i++) {
                        output.collect(new Text(String.valueOf(i)), new Text(line));
                    }
                }
            }
        }
    }

    /**
     * Implements the reduction operation for this program.
     */
    public static class Reduce
        extends MapReduceBase
        implements Reducer<Text, Text, Text, Text> {
        private JobConf conf;
        private int numVertices;

        /**
         * Configures this mapping object by initializing the job
         * configuration and the keyword hash set.
         *
         * @param conf the job configuration
         */
        public void configure(JobConf conf) {
            this.conf = conf;
            this.numVertices = Integer.parseInt(conf.get("n").toString());
        }

        /**
         * Collects each filename and count for key, sums the counts,
         * and collects the key with a list of the filenames and counts.
         *
         * @param key
         * @param values
         * @param output
         * @param r
         * @throws IOException
         */
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter
        ) throws IOException {
            int candidate = Integer.parseInt(key.toString());
            // Create adjacency list
            int[][] edges = new int[numVertices][];
            while (values.hasNext()) {
                String value[] = values.next().toString().strip().split(":");
                int vertex = Integer.parseInt(value[0]);
                String[] neighbors = value[1].split(",");
                edges[vertex] = new int[neighbors.length];
                for (int i = 0; i < neighbors.length; i++) {
                    edges[vertex][i] = Integer.parseInt(neighbors[i]);
                }
            }

            // Do breadth-first-search
            Deque<Integer> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            int start = (candidate + 1) % numVertices;
            queue.addLast(start);
            visited.add(start);
            while (!queue.isEmpty()) {
                int vertex = queue.removeFirst();
                for (int neighbor : edges[vertex]) {
                    if (neighbor != candidate && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.addLast(neighbor);
                    }
                }
            }

            if (visited.size() != numVertices - 1) {
                reporter.incrCounter(Counter.COUNT, 1);
            }
        }
    }
}
