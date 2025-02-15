package uk.ac.kent.graph;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Graph {
	public static final char ADJACENCYSEPARATOR = ':';
	protected ArrayList<Node> nodes = new ArrayList<Node>();
	protected ArrayList<Edge> edges = new ArrayList<Edge>();
	protected String label = "";
	
	public Graph () {}

	public Graph (String inLabel) {this.label = inLabel;}

	public ArrayList<Node> getNodes() {return nodes;}
	public ArrayList<Edge> getEdges() {return edges;}
	public String getLabel() {return label;}

	public void setLabel(String label) {this.label = label;}

	 /**
	 * Adds a node to the graph. Does not add the node if it already exists in the graph.
	  * @param n the node to add.
	  * @return success or failure.
	  */
	public boolean addNode(Node n)  {
		if(nodes.contains(n)) {
			return false;
		}
		nodes.add(n);
		return true;
	}
	
	/** 
	 * Adds an edge to the graph. Does not add the edge if it is already in the graph, or one of the connecting nodes is not.
	 * @param e the edge to add.
	 * @return success or failure.
	 */
	public boolean addEdge(Edge e) {

		if(edges.contains(e)) {
			return false;
		}
		if(!nodes.contains(e.from) || !nodes.contains(e.to)) {
			return false;
		}
		edges.add(e);
		return true;
	}

	/**
	 * Method to count the occlusion between pairs of nodes in the graph
	 * @param threshold gives the limit to give a positive occlusion result for any two node centres
	 * @return the number of pairs of nodes occluded
	 */
	public int countNodeNodeOcclusion(float threshold) {
		int occlusionCount = 0;
		for(int index1 = 0; index1 < nodes.size()-1; index1++) {
			for(int index2 = index1+1; index2 < nodes.size(); index2++) {
				Node n1 = nodes.get(index1);
				Node n2 = nodes.get(index2);
				float x1 = n1.getCentre().x;
				float x2 = n2.getCentre().x;
				float y1 = n1.getCentre().y;
				float y2 = n2.getCentre().y;
				double distance = (float)Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
				if(distance < threshold) {
					occlusionCount++;
				}
			}
		}
		return occlusionCount;
	}
	
	
	/**
	 * Removes the nodes and edges from the graph, leaving an empty graph.
	 * This is a destructive clear as nodes are removed from the graph, so
	 * loosing their edge connections.
	 */
	public void clear() {
		setLabel("");
		Iterator<Node> ni = nodes.iterator();
		while(ni.hasNext()) {
			Node n = (Node)ni.next();
			removeNode(n);
			ni = nodes.iterator();
		}
	}
	
	
	/**
	 * Removes the edge from the graph. Accounts
	 * for redundant data connecting nodes.
	 * Do not try and manipulate the edge again, it is left without connecting nodes.
	 * @param the Edge to remove.
	 * @return the success of the operation, failure is due to edge not being in the graph.
	 */
	public  boolean removeEdge(Edge removeEdge) {

		if(!edges.contains(removeEdge)) {
			return false;
		}

		// attempt to retain consistency, the edge is left without nodes meaning it cannot be added again.

		removeEdge.from.edgesFrom.remove(removeEdge);
		removeEdge.to.edgesTo.remove(removeEdge);
		removeEdge.from = null;
		removeEdge.to = null;
		
		edges.remove(removeEdge);

		return true;
	}


	
	
	/**
	 * Removes the node from the graph and deletes any connecting edges. Accounts
	 * for redundant data connecting nodes.
	 * @param removeNode The Node to remove.
	 * @return the success of the operation, failure is due to node not being in the graph.
	 */
	public boolean removeNode(Node removeNode) {

		if(!nodes.contains(removeNode)) {
			return false;
		}

		for(Edge e : removeNode.connectingEdges()) {
			removeEdge(e);
		}
		
		nodes.remove(removeNode);
		return true;
	}

	/**
	 * Removes the given node collection. Does nothing if the elements
	 * in the collection are not nodes in the graph.
	 * @param c The nodes to remove.
	 */
	public void removeNodes(Collection<Node> c) {
		for(Node n : c) {
			removeNode(n);
		}
	}
	
	/**
	 * Removes the given edge collection. Does nothing if the elements
	 * in the collection are not edges in the graph.
	 * @param c The edges to remove.
	 */
	public void removeEdges(Collection<Edge> c) {
		for(Edge e : c) {
			removeEdge(e);
		}
	}

	
	
	/**
	 * Randomizes the graph node locations to be within the rectangle defined
	 * by the parameters.
	 * 
	 * @param topleft corner of the rectangle.
	 * @param width width of the rectangle.
	 * @param height height of the rectangle.
	 */
	public void randomizeNodePoints(Point topleft, int width, int height) {
		Random r = new Random(System.currentTimeMillis());
		for(Node n : getNodes()) {
			int x = r.nextInt(width);
			int y = r.nextInt(height);
			n.setCentre(new Point.Float(topleft.x+x,topleft.y+y));
		}
	}

	
	/**
	 * Either returns an existing node with the label, or if there is none
	 * creates a node with the label and adds it to the graph.
	 * If there are multiple nodes with the label, the first in the list is returned.
	 * @param nodeLabel The node label.
	 * @return the found or created node, or null if there is more than one node with the label.
	 */
	public Node addAdjacencyNode(String nodeLabel) {

		for(Node n : getNodes()) {
			if(nodeLabel.equals(n.getLabel())) {
				return n;
			}
		}
		
		Node newNode = new Node(nodeLabel);
		addNode(newNode);

		return newNode;
	}



	
	/**
	 * Creates an unlabelled edge between the nodes with the given labels.
	 * If there are multiple nodes with the label, the first in the node list is used.
	 * If there are no nodes with the label, a new node is created.
	 * @param fromLabel The label of the destination node.
	 * @param toLabel The label of the source node.
	 * @param edgeLabel The label of the edge.
	 * @return the created edge, or null if was not created.
	 */
	public Edge addAdjacencyEdge(String fromLabel, String toLabel) {

		Node fromNode = null;
		Node toNode = null;

		if(!fromLabel.equals("")) {
			fromNode = addAdjacencyNode(fromLabel);
		}

		if(!toLabel.equals("")) {
			toNode = addAdjacencyNode(toLabel);
		}

		if(fromNode != null && toNode != null) {

			Edge e = new Edge(fromNode, toNode);
			addEdge(e);
			return e;
		}

		return null;
	}



	
	/**
	 * This loads the given adjacency list graph file into the graph, deleting any
	 * current nodes and edges. The file is in the form of a simple adjacency list, each line
	 * of the file is an edge, with nodes separated by the {@link #ADJACENCYSEPARATOR}
	 * character. Any empty line
	 * or line with more than one colon is ignored.
	 * Nodes are assumed to have unique labels for the purpose of this loader.
	 * may appear in each line. Any non empty line without a colon is
	 * considered to be a node with no connecting edges.
	 * @param fileName the file name to save to.
	 * @return true if successful, false if there was a formatting problem.
	 */
	public boolean loadAdjacencyFile(String fileName) {

		clear();
		try {
			BufferedReader b = new BufferedReader(new FileReader(fileName));
			String line = b.readLine();
			while(line != null) {

				int separatorInd = line.indexOf(ADJACENCYSEPARATOR);

				// ignore any empty lines and lines with more than 1 separator
				if(line.length() > 0 && separatorInd == line.lastIndexOf(ADJACENCYSEPARATOR)) {
					if(separatorInd >= 0) {
						String n1 = line.substring(0,separatorInd);
						String n2 = line.substring(separatorInd+1);
						addAdjacencyEdge(n1,n2);
					} else {
						// no separator, so just add a singleton node
						addAdjacencyNode(line);
					}
				}
				line = b.readLine();
			}

			b.close();

		} catch(IOException e){
			System.out.println("An IO exception occured when executing loadAdjacencyFile("+fileName+") in Graph.java: "+e+"\n");
			System.exit(1);
		}

		return true;
	}
	
	
	


}
