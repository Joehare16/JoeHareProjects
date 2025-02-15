package uk.ac.kent.graph;

public class Edge {
	protected Node from;
	protected Node to;
	protected String label = "";
	protected float weight = 0.0f;

	/**
	 * Pass two nodes at either end of the edge.
	 * It creates an edge between the two nodes and
	 * maintains the redundant data structures.
	 */
	public Edge(Node inFrom, Node inTo) {
		from = inFrom;
		from.edgesFrom.add(this);
		
		to = inTo;
		to.edgesTo.add(this);
	}

	public Node getFrom() {return from;}
	public Node getTo() {return to;}
	public String getLabel() {return label;}
	public double getWeight() {return weight;}

	public void setLabel(String label) {this.label = label;}
	public void setWeight(float weight) {this.weight = weight;}

	/**
	 * Gives the other end of the edge to the argument node
	 * @return the node at the other end of the edge, or null if the passed node is not connected to the edge
	 */
		public Node getOppositeEnd(Node n) {

			Node ret = null;
			if (getFrom() == n) {
				ret = getTo();
			}
			if (getTo() == n) {
				ret = getFrom();
			}

			return(ret);
		}

}
