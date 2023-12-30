/*

 	MASS Java Software License
	© 2012-2015 University of Washington

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	The following acknowledgment shall be used where appropriate in publications, presentations, etc.:

	© 2012-2015 University of Washington. MASS was developed by Computing and Software Systems at University of
	Washington Bothell.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.

*/

package edu.uwb.rjdecker;

import java.util.Date;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.Agent;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

/**
 * @author Akbarbek Rakhmatullaev
 * @author Logan Choi
 * @author Ryan Decker
 */
public class MassArticulation {
    private static final String NODE_FILE = "/home/NETID/rjdecker/mass_quickstart/MassArticulation/nodes.xml";

    private static int GRAPH_SIZE = 4000;
    public static final String INPUT_DIR = "/home/NETID/rjdecker/mass_quickstart/MassArticulation/src/main/java/edu/uwb/rjdecker/";

    @SuppressWarnings("unused")		// some unused variables left behind for easy debugging
    public static void main(String[] args) throws IOException {
        long startTime = new Date().getTime();
        System.out.println("Before init");

        // Populate the global graph
        GraphInjector.populateEdges(GRAPH_SIZE, INPUT_DIR + "graph" + GRAPH_SIZE + ".txt");

        // Initalize the MASS library
        MASS.setNodeFilePath(NODE_FILE);
        MASS.setLoggingLevel(LogLevel.DEBUG);

        // Start MASS
        MASS.getLogger().debug("MassArticulation initializing MASS library...");
        MASS.init();

        MASS.getLogger().debug("MassArticulation creating Places...");
        Places places = new Places(1, SimplePlace.class.getName(), null, 1);
        MASS.getLogger().debug("Places created");

        // create Agents (number of Agents = x * y in this case), in Places
        MASS.getLogger().debug("Quickstart creating Agents...");
        Agents agents = new Agents(1, ArticulationAgent.class.getName(), null, places, GRAPH_SIZE);
        MASS.getLogger().debug("Agents created");

        // Call all
        int articulationPoints = 0;
        Object[] results = (Object[]) agents.callAll(0, null);
        System.out.print("Articulation points: ");
        for (int i = 0; i < GRAPH_SIZE; i++) {
            if ((int) results[i] >= 0) {
                articulationPoints++;
                System.out.print(results[i] + " ");
            }
        }
        System.out.println();
        System.out.println("Total articulation points: " + articulationPoints);

        MASS.getLogger().debug("MassArticulation is instruction MASS to finish operations...");
        MASS.finish();
        MASS.getLogger().debug("MASS has finished.");

        long execTime = new Date().getTime() - startTime;
        System.out.println("Execution time = " + execTime + " milliseconds");
    }
}
