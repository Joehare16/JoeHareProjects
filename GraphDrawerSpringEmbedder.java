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
		
		//FORCES
		//strength of attraction
		final float k = 0.5f;
		//strength of node-node repulsion
		final float r = 1000000f;
		//border repulsion
		final float z  = 10000f;
		//amount of movement
		final float f = 0.05f;
		
		//number of iterations
		final int m = 200;
		
		//BORDERS
		//top
		final int top = 0;
		//left
		final int left = 0;
		//right
		final int right = 500;
		//bottom
		final int bottom = 500;
				
		for(int i = 0; i < m; i++)
		{
			
			for (Node n : getGraph().getNodes())
			{
				//the total force in the x and y direction
				float fx = 0;
				float fy = 0;
				
				//attraction every node connected to n
				for(Node neighbour : n.connectingNodes())
				{
					float distanceX =  neighbour.getX() - n.getX();
					float distanceY =  neighbour.getY() - n.getY();
					//eucledian distance between nodes
					float distance = (float)Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
					
					float attractiveForce = (-k) * distance;
					
					//find the direction of the attraction
					float forceX = attractiveForce * (distanceX / distance);
					float forceY = attractiveForce * (distanceY / distance);
					
					//System.out.println(forceX + "A" + forceY);
					
					//add to total
					fx -= forceX;
					fy -= forceY;
				}
			//repulsion from every node in graph 
				for(Node otherNode : getGraph().getNodes())
				{
					if(otherNode != n) {
					float distanceX =  n.getX() - otherNode.getX();
					float distanceY =  n.getY() - otherNode.getY();
					//eucledian distance between nodes
					float distance = (float)Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
					
					if(distance > 0.1f) {
					float repulsiveForce =  r / (distance*distance);
					
					//find the direction of the attraction
					float forceX = repulsiveForce * (distanceX / distance);
					float forceY = repulsiveForce * (distanceY / distance);
					
					//System.out.println(forceX + "R" + forceY);
					
					//add to total
					fx += forceX;
					fy += forceY;
					}
					}
				}
				  float distanceTop = n.getY();
			        if (distanceTop < 100) {
			            float borderForceTop = z / (distanceTop * distanceTop);
			            
			            fy += borderForceTop;
			        }

			        // Left border repulsion
			        float distanceLeft = n.getX();
			        if (distanceLeft < 100) {
			            float borderForceLeft = z / (distanceLeft * distanceLeft);
			            fx += borderForceLeft;
			            
			        }

			        // Bottom border repulsion
			        float distanceBottom = bottom - n.getY(); 
			        if (distanceBottom < 100) {
			            float borderForceBottom = z / (distanceBottom * distanceBottom);
			          
			            fy -= borderForceBottom;
			        }

			        
			        float distanceRight = right - n.getX(); 
			        if (distanceRight < 100) {
			            float borderForceRight = z / (distanceRight * distanceRight);
			            fx -= borderForceRight;
			        }
				   n.setX(n.getX() + f * fx);
			       n.setY(n.getY() + f * fy);
			     
			}
			  getGraphPanel().update(getGraphPanel().getGraphics());
			
		}
		

	}



	
}




