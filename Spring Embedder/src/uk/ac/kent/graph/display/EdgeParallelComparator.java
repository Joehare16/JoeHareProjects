package uk.ac.kent.graph.display;

import java.util.Comparator;

import uk.ac.kent.graph.*;

/**
 * Orders edges by their labels.
 */
public class EdgeParallelComparator implements Comparator<Edge> {

	public int compare(Edge e1, Edge e2) {

		int ret = e1.getLabel().compareTo(e2.getLabel());

		return(ret);
	}
}


