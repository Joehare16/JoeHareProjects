package uk.ac.kent.graph;

import java.awt.*;
import java.util.*;

public class Node {
	protected ArrayList<Edge> edgesFrom = new ArrayList<Edge>();
	protected ArrayList<Edge> edgesTo = new ArrayList<Edge>();
	protected String label = "";
	protected Point.Float centre = new Point.Float(0.0f,0.0f);

	public Node() {
	}

	/** Creates a labelled node. */
	public Node(String inLabel) {
		label = inLabel;
	}

	/** Creates a labelled, centred node. */
	public Node(String inLabel, Point.Float inCentre) {
		label = inLabel;
		centre = inCentre;
	}

	public String getLabel() {return label;}
	public Point.Float getCentre() {return centre;}
	public float getX() {return centre.x;}
	public float getY() {return centre.y;}
	public ArrayList<Edge> getEdgesFrom() {return edgesFrom;}
	public ArrayList<Edge> getEdgesTo() {return edgesTo;}

	public void setLabel(String label) {this.label = label;}
	public void setCentre(Point.Float centre) {this.centre = centre;}
	public void setX(float x) {this.centre.x = x;}
	public void setY(float y) {this.centre.y = y;}

	
	/**
	 * Find connecting nodes, both in and out of this Node.
	 * 
	 * @return all the nodes, without duplicates
	 */
	public ArrayList<Node> connectingNodes() {
		
		ArrayList<Node> ret = new ArrayList<Node>();
		for(Edge e : getEdgesFrom()) {
			if(!ret.contains(e.getTo())) {
				ret.add(e.getTo());
			}
		}
		for(Edge e : getEdgesTo()) {
			if(!ret.contains(e.getFrom())) {
				ret.add(e.getFrom());
			}
		}
		
		return ret;
	}
	
	
	/**
	 * Gives all the connecting edges, both in and out of this Node.
	 * @return all the connecting edges, without duplicates.
	 */
	public ArrayList<Edge> connectingEdges() {
		ArrayList<Edge> ret = new ArrayList<Edge>(getEdgesFrom());
		for(Edge e : getEdgesTo()) {
			if(!ret.contains(e)) {
				ret.add(e);
			}
		}
		return(ret);
	}

	
	/**
	 * Gives all the connecting edges from this node to the argument node.
	 * @return all the connecting edges to the argument node, without duplicates.
	 */
	public ArrayList<Edge> connectingEdges(Node n) {
		ArrayList<Edge> ret = new ArrayList<Edge>();

		// needs to be done twice, once for from list, once for to list
		for(Edge e : edgesFrom) {
			if(e.getTo() == n) {
				ret.add(e);
			}
		}
		for(Edge e : edgesTo) {
			if(e.getFrom() == n) {
				ret.add(e);
			}
		}
		return ret;
	}




}
