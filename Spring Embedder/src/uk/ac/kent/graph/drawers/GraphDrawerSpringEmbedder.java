package uk.ac.kent.graph.drawers;

import java.awt.Point;
import java.util.Random;

import uk.ac.kent.graph.*;

/**
 * Code to complete as part of assessment 2 for COMP6442.
 */
public class GraphDrawerSpringEmbedder extends GraphDrawer {


/** Full constructor. */
	public GraphDrawerSpringEmbedder(int key, String s, int mnemomic) {
		super(key,s,mnemomic);
	}


/** Draws the graph. */
	public void layout() {
		
		Random random = new Random(System.currentTimeMillis());
		
		for(Node n : getGraph().getNodes()) {
			
			float x = 100+random.nextInt(400);
			float y = 100+random.nextInt(400);
			Point.Float centre = new Point.Float(x,y);
			n.setCentre(centre);
		}

		
		// TODO your code here
		

	}



	
}




