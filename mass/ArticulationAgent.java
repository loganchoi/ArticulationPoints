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

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import edu.uw.bothell.css.dsl.MASS.Agent;

@SuppressWarnings("serial")
public class ArticulationAgent extends Agent {
    /**
     * This constructor will be called upon instantiation by MASS
     * The Object supplied MAY be the same object supplied when Places was created
     * @param obj
     */
    public ArticulationAgent(Object obj) {}

    /**
     * This method is called when "callAll" is invoked from the master node
     */
    public Object callMethod(int method, Object object) {
        return bfs();
    }

    /**
     *
     * @return
     */
    public Object bfs() {
        int target = getAgentId();
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        queue.addLast((target + 1) % GraphInjector.edges.length);
        visited.add((target + 1) % GraphInjector.edges.length);

        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            for (int neighbor : GraphInjector.edges[vertex]) {
                if (neighbor != target && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.addLast(neighbor);
                }
            }
        }
        if (visited.size() != GraphInjector.edges.length - 1) {
            return getAgentId();
        }
        return -1;
    }
}
