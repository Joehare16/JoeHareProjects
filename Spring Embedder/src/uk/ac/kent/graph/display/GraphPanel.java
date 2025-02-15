package uk.ac.kent.graph.display;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import uk.ac.kent.graph.drawers.*;
import uk.ac.kent.graph.*;

/**
 * A panel on which a graph is displayed.
 * <p>
 * Functions:
 * <br>
 * - Add a node with double left button click on the background.
 * <br>
 * - Add an edge with a right button drag (picks closest nodes to start
 *   and end of the drag, but does not add self sourcing nodes).
 * <br>
 * - Drag a node with a left button drag on a node.
 * <br>
 * - Select a node with a single left button click on a node or a left button drag on the background,
 *   add new nodes to the selection by pressing the control key whilst selecting.
 * <br>
 * - Delete the selection with Del or Backspace
 *
 * @author Peter Rodgers
 */
public class GraphPanel extends JPanel implements MouseInputListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	public static final Color PANEL_BACKGROUND_COLOR = Color.white;
	public static final Color SELECTED_PANEL_AREA_COLOR = Color.gray;
	private static final BasicStroke SELECTED_PANEL_AREA_STROKE = new BasicStroke(1.0f);
	public static final String LABEL_FONT_NAME = "Arial";
	public static final int LABEL_FONT_STYLE = Font.BOLD;
	public static final int LABEL_FONT_SIZE = 12;
	public static final Dimension BUTTON_SIZE = new Dimension(78,32);
	public static final Dimension LARGE_BUTTON_SIZE = new Dimension(116,32);
	
	public static final Point.Float ZERO_OFFSET = new Point.Float(0.0f,0.0f);
	public static final int OFFSET_INCREMENT = 10;
	private static final Color EDGE_LINE_COLOR = Color.black;
	private static final Color EDGE_SELECTED_LINE_COLOR = Color.blue;
	private static final Color EDGE_TEXT_COLOR = Color.black;
	private static final Color EDGE_SELECTED_TEXT_COLOR = Color.blue;
	private static final BasicStroke EDGE_STROKE = new BasicStroke(2.0f);;
	private static final BasicStroke EDGE__SELECTED_STROKE = new BasicStroke(4.0f);

	public static final String NODE_SHAPE_STRING = "Ellipse";
	public static final Color NODE_FILL_COLOR = Color.white;
	public static final  Color NODE_BORDER_COLOR = Color.black;
	public static final  Color NODE_TEXT_COLOR = Color.black;
	public static final  BasicStroke NODE_STROKE = new BasicStroke(2.0f);
	public static final Color NODE_SELECTED_FILL_COLOR = Color.black;
	public static final  Color NODE_SELECTED_BORDER_COLOR = Color.black;
	public static final  Color NODE_SELECTED_TEXT_COLOR = Color.white;
	public static final  BasicStroke NODE_SELECTED_STROKE = new BasicStroke(2.0f);
	public static final  float NODE_HEIGHT = 30.0f;
	public static final  float NODE_WIDTH = 30.0f;

	
	public static float arrowAngle = 40;
	public static float arrowLength = 20;
	protected Color textColor = Color.black;
	protected Color selectedTextColor = Color.blue;

	
	/** Indicates if parallel edges should be separated when displayed. */
	protected boolean separateParallel = true;
	private Graph graph;
	private ArrayList<GraphDrawer> graphDrawerList = new ArrayList<GraphDrawer>();
	protected GraphSelection selection;
	protected boolean dragSelectionFlag = false;
	protected Node dragNode = null;
	protected Node selectNode = null;
	protected Edge selectEdge = null;
	protected Node newEdgeNode = null;
	protected Point.Float newEdgePoint = null;
	protected Point.Float pressedPoint = null;
	protected Point.Float lastPoint = null;
	protected Point.Float dragSelectPoint = null;
	protected Frame containerFrame = null;
	protected Color panelBackgroundColor = PANEL_BACKGROUND_COLOR;
	protected Color selectedPanelAreaColor = SELECTED_PANEL_AREA_COLOR;
	protected BasicStroke selectedPanelAreaStroke = SELECTED_PANEL_AREA_STROKE;

	public GraphPanel(Graph inGraph, Frame inContainerFrame) {
		super();
		graph = inGraph;
		containerFrame = inContainerFrame;
		setup(graph);
	}
	
	protected void setup(Graph graph) {
		selection = new GraphSelection(graph);
		setBackground(panelBackgroundColor);
		addMouseListener(this);
		addKeyListener(this);
	}

	public boolean getSeparateParallel() {return separateParallel;}
	public Graph getGraph() {return graph;}
	public GraphSelection getSelection() {return selection;}
	public ArrayList<GraphDrawer> getGraphDrawerList() {return graphDrawerList;}
	public Frame getContainerFrame() {return containerFrame;}

	public void setSeparateParallel(boolean flag) {separateParallel = flag;}
	public void setGraph(Graph g) {
		graph = g;
		repaint();
	}

	/** Add a drawing algorithm to the panel. */
	public void addGraphDrawer(GraphDrawer gd) {
		graphDrawerList.add(gd);
		gd.setGraphPanel(this);
	}

	/** Removes a drawing algorithm from the panel. */
	public void removeGraphDrawer(GraphDrawer gd) {
		graphDrawerList.remove(gd);
		gd.setGraphPanel(null);
	}

	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
	
		//paint background
		super.paintComponent(g2);
		
		//draw the edges
		if(!separateParallel) {
			paintOverlaidEdges(g2,graph);
		} else {
			paintSeparateEdges(g2,graph);
		}

		//draw the new edge drag
		if (newEdgePoint != null) {
			g2.setColor(selectedPanelAreaColor);
			Point.Float centre = newEdgeNode.getCentre();
			g2.drawLine((int)centre.x,(int)centre.y,(int)newEdgePoint.x,(int)newEdgePoint.y);
		}

		//draw the nodes
		for(Node n : graph.getNodes()) {
			paintNode(g2,n);
		}

		// draw the area selection
		if (dragSelectPoint != null) {
			g2.setColor(selectedPanelAreaColor);
	        g2.setStroke(selectedPanelAreaStroke);
			Shape r = convertPointsToRectangle(pressedPoint,dragSelectPoint);
			g2.draw(r);
		}		
		
	}


	/** This is used when parallel edges are overlaid */
	protected void paintOverlaidEdges(Graphics2D g2, Graph g) {

		for(Edge e : graph.getEdges()) {
			paintEdge(g2,e,ZERO_OFFSET);
		}
	}
	
	
	/** This is used when parallel edges are displayed separately */
	protected void paintSeparateEdges(Graphics2D g2, Graph g) {

		// set up the lists of parallel edges
		ParallelEdgeList parallelList = new ParallelEdgeList(g);

		// iterate through the lists displaying the edges
		// first consider any edge type order in the nodes neighbouring the parallel edges.
		// second order by edge type priority
		// third, choose randomly

		parallelList.setAllSorted(false);

		for(ParallelEdgeTuple tuple : parallelList.getParallelList()) {
			ParallelEdgeTuple sortedTuple = null;

			// check for current order from neighbouring nodes
			//sortedTuple = getSortedNeigbour(tuple);
			// TBD this will order the edges based on the neighbours ordering. Is this needed?

			// order by sorting
			if(sortedTuple == null) {
				tuple.sortList();
			}

			// set up the offset values

			Node n1 = tuple.getFromNode();
			Node n2 = tuple.getToNode();

			float x = n1.getX()-n2.getX();
			float y = n1.getY()-n2.getY();

			float incrementX = 0.0f;
			float incrementY = 0.0f;
			float divisor = Math.abs(x)+Math.abs(y);

			if (divisor != 0) {
				incrementX = y/divisor;
				incrementY = -x/divisor;
			}

			incrementX *= OFFSET_INCREMENT;
			incrementY *= OFFSET_INCREMENT;

			// find a sensible starting offset
			float numberOfEdges = tuple.getList().size();
			Point.Float offset = new Point.Float((-((numberOfEdges-1)*incrementX)/2),(-((numberOfEdges-1)*incrementY)/2));

			// display the edges given the order
			for(Edge e : tuple.getList()) {
				paintEdge(g2,e,offset);
				offset.x += (int)incrementX;
				offset.y += (int)incrementY;
			}
			tuple.setSorted(true);
		}
	}

	
	/** Draws an edge on the graphics */
	public void paintEdge(Graphics2D g2, Edge e, Point.Float offset) {

		if(!selection.contains(e)) {
			g2.setColor(EDGE_LINE_COLOR);
		} else {
			g2.setColor(EDGE_SELECTED_LINE_COLOR);
		}
		if(!selection.contains(e)) {
	        g2.setStroke(EDGE_STROKE);
		} else {
	        g2.setStroke(EDGE__SELECTED_STROKE);
		}

		Shape edgeShape = generateEdgeShape(e,offset);
		g2.draw(edgeShape);

		// draw the label if required
		if(!e.getLabel().equals("")) {

			//if there are edge bends, put the label at the middle edge bend

			float n1X = e.getFrom().getCentre().x+offset.x;
			float n1Y = e.getFrom().getCentre().y+offset.y;
			float n2X = e.getTo().getCentre().x+offset.x;
			float n2Y = e.getTo().getCentre().y+offset.y;
			float x = 0;
			float y = 0;
			if(n1X-n2X > 0) {
				x = n2X+(n1X-n2X)/2;
			} else {
				x = n1X+(n2X-n1X)/2;
			}
			if(n1Y-n2Y > 0) {
				y = n2Y+(n1Y-n2Y)/2;
			} else {
				y = n1Y+(n2Y-n1Y)/2;
			}
			
			Font font = new Font(LABEL_FONT_NAME,LABEL_FONT_STYLE,LABEL_FONT_SIZE);
			FontRenderContext frc = g2.getFontRenderContext();
			TextLayout labelLayout = new TextLayout(e.getLabel(), font, frc);

			g2.setColor(PANEL_BACKGROUND_COLOR);
			Rectangle2D bounds = labelLayout.getBounds();
			bounds.setRect(bounds.getX()+x-2, bounds.getY()+y-2, bounds.getWidth()+4,bounds.getHeight()+4);
			g2.fill(bounds);

			if(!selection.contains(e)) {
				g2.setColor(EDGE_TEXT_COLOR);
			} else {
				g2.setColor(EDGE_SELECTED_TEXT_COLOR);
			}
			labelLayout.draw(g2,x,y);
		}

	}
	
	
	/** Draws a node on the graphics */
	public void paintNode(Graphics2D g2, Node n) {

		Point.Float centre = n.getCentre();

		if(!selection.contains(n)) {
			g2.setColor(NODE_FILL_COLOR);
		} else {
			g2.setColor(NODE_SELECTED_FILL_COLOR);
		}
		if(!selection.contains(n)) {
			g2.setStroke(NODE_STROKE);
		} else {
			g2.setStroke(NODE_SELECTED_STROKE);
		}

		Shape nodeShape = generateNodeShape(n,NODE_SHAPE_STRING);
		g2.fill(nodeShape);

		if(!selection.contains(n)) {
			g2.setColor(NODE_BORDER_COLOR);
		} else {
			g2.setColor(NODE_SELECTED_BORDER_COLOR);
		}

		g2.draw(nodeShape);

		if(!n.getLabel().equals("")) {
			if(!selection.contains(n)) {
				g2.setColor(NODE_TEXT_COLOR);
			} else {
				g2.setColor(NODE_SELECTED_TEXT_COLOR);
			}

			Font font = new Font(LABEL_FONT_NAME,LABEL_FONT_STYLE,LABEL_FONT_SIZE);
			FontRenderContext frc = g2.getFontRenderContext();
			TextLayout labelLayout = new TextLayout(n.getLabel(), font, frc);

			Rectangle2D labelBounds = labelLayout.getBounds();
			int labelX = (int)Math.round(centre.x-(labelBounds.getWidth()/2));
			int labelY = (int)Math.round(centre.y+(labelBounds.getHeight()/2));

			labelLayout.draw(g2,labelX,labelY);
		}
		
	}


	/**
	 * This converts two points to a rectangle, with first two
	 * coordinates always the top left of the rectangle
	 */
	public Shape convertPointsToRectangle(Point.Float p1, Point.Float p2) {
		float x1,x2,y1,y2;

		if (p1.x < p2.x) {
			x1 = p1.x;
			x2 = p2.x;
		} else {
			x1 = p2.x;
			x2 = p1.x;
		}
		if (p1.y < p2.y) {
			y1 = p1.y;
			y2 = p2.y;
		} else {
			y1 = p2.y;
			y2 = p1.y;
		}
		return (new Rectangle2D.Float(x1,y1,x2-x1,y2-y1));

	}


	public void mouseClicked(MouseEvent event) {
		
		Point.Float eventPoint = new Point.Float(event.getX(),event.getY());

		// left button only
		if (!SwingUtilities.isLeftMouseButton(event)) {
			selection.clear();
			repaint();
			return;
		}

		selectNode = findNodeNearPoint(eventPoint,1);
		if (selectNode == null) {

			selectEdge = findEdgeNearPoint(eventPoint,3);
			if (selectEdge == null) {
				// no node or edge selected so add a node on double click
				if (event.getClickCount() > 1) {
					graph.addNode(new Node("",eventPoint));
					selection.clear();
				} else {
					// single click might have been a missed selection
					if (!event.isControlDown()) {
						selection.clear();
					}
				}
				repaint();
			} else {
				if (event.getClickCount() == 1) {
					// edge selected, so add it to the selection
					if (!event.isControlDown()) {
						selection.clear();
					}
					selection.addEdge(selectEdge);
					repaint();
				}
			}
		} else {
			if (event.getClickCount() == 1) {
				// node selected
				if (!event.isControlDown()) {
					selection.clear();
				}
				selection.addNode(selectNode);
				repaint();
			}
			selectNode = null;
		}
		
		event.consume();
	}
	
	

	public void mousePressed(MouseEvent event) {

		requestFocus();
		pressedPoint = new Point.Float((float)event.getPoint().x,(float)event.getPoint().y);
		lastPoint = new Point.Float((float)event.getPoint().x,(float)event.getPoint().y);
		addMouseMotionListener(this);
		if (SwingUtilities.isLeftMouseButton(event)) {

			Node chosenNode = findNodeNearPoint(pressedPoint,1);
			if(chosenNode != null) {
				if(selection.contains(chosenNode)) {
					// if its a selected node then we are dragging a selection
					dragSelectionFlag = true;
				} else {
					// otherwise just drag the node
					dragNode = chosenNode;
				}
			} else {
				// no node chosen, so drag an area selection
				dragSelectPoint = new Point.Float((float)event.getPoint().x,(float)event.getPoint().y);
			}
			// move Node to end of graph list
			moveNodeToGraphEnd(getGraph(),dragNode);
			repaint();
		}
		if (SwingUtilities.isRightMouseButton(event)) {
			newEdgeNode = closestNode(pressedPoint);
			// move Node to end of graph list
			moveNodeToGraphEnd(getGraph(),newEdgeNode);
			newEdgePoint = new Point.Float((float)event.getPoint().x,(float)event.getPoint().x);
			repaint();
		}
		event.consume();
	}




	public void mouseReleased(MouseEvent event) {

		Point.Float eventPoint = new Point.Float(event.getX(),event.getY());

		removeMouseMotionListener(this);
		if(pressedPoint.distance(event.getPoint()) < 1) {
			// dont do anything if no drag occurred
			dragSelectionFlag = false;
			dragNode = null;
			dragSelectPoint = null;
			newEdgeNode = null;
			newEdgePoint = null;
			return;
		}

		// select all in the area
		if (dragSelectPoint != null) {

			// if no control key modifier, then replace current selection
			if (!event.isControlDown()) {
				selection.clear();
			}

			Shape r = convertPointsToRectangle(pressedPoint,eventPoint);

			for(Node node : graph.getNodes()) {
				Point.Float centre = node.getCentre();

				if(r.contains(centre) && !selection.contains(node)) {
					selection.addNode(node);
				}
			}

			for(Edge edge : graph.getEdges()) {
				Rectangle edgeBounds = generateEdgeShape(edge,ZERO_OFFSET).getBounds();

				//rectangles with zero dimension dont get included, so quick hack
				edgeBounds.grow(1,1);

				if(r.contains(edgeBounds) && !selection.contains(edge)) {
					selection.addEdge(edge);
				}
			}

			dragSelectPoint = null;
			repaint();
		}

		// finish the selection drag
		if (dragSelectionFlag) {
			dragSelectionFlag = false;
			repaint();
		}

		// finish the node drag
		if (dragNode != null) {
			dragNode = null;
			repaint();
		}

		// finish adding an edge
		if (newEdgeNode != null) {
			Node toNode = closestNode(eventPoint);
			// dont add a self sourcing edge
			if (newEdgeNode != toNode) {
				graph.addEdge(new Edge(newEdgeNode,toNode));
			}
			newEdgeNode = null;
			newEdgePoint = null;
			// move Node to end of graph list
			moveNodeToGraphEnd(getGraph(),toNode);
			repaint();
		}
		event.consume();
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseDragged(MouseEvent event) {

		Point.Float eventPoint = new Point.Float(event.getX(),event.getY());

		if (dragSelectPoint != null) {
			dragSelectPoint = eventPoint;
			repaint();
		}

		if (newEdgePoint != null) {
			newEdgePoint = eventPoint;
			repaint();
		}

		if (dragSelectionFlag) {
			float deltaX = eventPoint.x-lastPoint.x;
			float deltaY = eventPoint.y-lastPoint.y;

			for(Node n : selection.getNodes()) {
				Point.Float centre = n.getCentre();
				centre.setLocation(centre.x+deltaX,centre.y+deltaY);
			}
			lastPoint = eventPoint;

			repaint();
		}

		if (dragNode != null) {

			float deltaX = eventPoint.x-lastPoint.x;
			float deltaY = eventPoint.y-lastPoint.y;

			Point.Float centre = dragNode.getCentre();
			centre.setLocation(centre.x+deltaX,centre.y+deltaY);

			lastPoint = eventPoint;

			repaint();
		}
		event.consume();
	}


	public void mouseMoved(MouseEvent event) {
	}


	public void keyTyped(KeyEvent event) {
	}


	public void keyPressed(KeyEvent event) {
	}


	public void keyReleased(KeyEvent event) {
		// this stuff would be in keyTyped, but it doesnt register delete
		if((event.getKeyChar() == KeyEvent.VK_BACK_SPACE) || (event.getKeyChar() == KeyEvent.VK_DELETE)) {
			graph.removeEdges(selection.getEdges());
			graph.removeNodes(selection.getNodes());
			selection.clear();
			repaint();
		}
	}
	
	
	/**
	 * Gives a new shape object to draw a node. At the moment
	 * rectangles and ellipses are supported
	 * @param e The node.
	 * @param shapeString A string describing the shape of the node, currently "Ellipse" and "Rectangle are supported". Anything that is not one of these defaults to "Ellipse".
	 * @return The shape for the node.
	 */
	public Shape generateNodeShape(Node n, String shapeString) {
		float height = NODE_HEIGHT;
		float width = NODE_WIDTH;

		Shape shape = null;
		
		Point.Float centre = n.getCentre();
		
		if(shapeString.equals("Ellipse")) {
			shape = new Ellipse2D.Float(centre.x-width/2,centre.y-height/2,width,height);
		 } else {
			shape = new Rectangle2D.Float(centre.x-width/2,centre.y-height/2,width,height);
		}

		return(shape);
	}

	
	/**
	 * Generate a line to draw an edge.	
	 * @param e The edge.
	 * @param offset A parameter for separating parallel edges.
	 * @return The shape for the edge.
	 */
	public Shape generateEdgeShape(Edge e, Point.Float offset) {

		Point.Float fromPoint = new Point.Float(e.getFrom().getCentre().x,e.getFrom().getCentre().y);
		Point.Float toPoint = new Point.Float(e.getTo().getCentre().x,e.getTo().getCentre().y);
		fromPoint.x += offset.x;
		fromPoint.y += offset.y;
		toPoint.x += offset.x;
		toPoint.y += offset.y;
		
		Line2D.Float line = new Line2D.Float(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);

		return line;
	}
	
	
	

	/** Calculates the angle between the lines x1 to y1 and x2 to y2.
	 * @param x1 X coordinate of point 1.
	 * @param y1 Y coordinate of point 1.
	 * @param x2 X coordinate of point 2.
	 * @param y2 Y coordinate of point 2.
	 * @return The angle of the line.
	 */
	public static float calculateAngle(float x1, float y1, float x2, float y2) {
		float dx = x2-x1;
		float dy = y2-y1;
		float angle= 0.0f;

		float PI = (float)Math.PI;
		
		// Calculate angle
		if (dx == 0.0f) {
			if (dy == 0.0f) {
				angle = 0.0f;
			} else if (dy > 0.0f) {
				angle = PI / 2.0f;
			} else
				angle =PI * 3.0f / 2.0f;
		} else if (dy == 0.0f) {
			if (dx > 0.0f) {
				angle = 0.0f;
			} else {
				angle = PI;
			}
		} else {
			if  (dx < 0.0f) {
				angle = (float)Math.atan(dy/dx) + PI;
			} else if (dy < 0.0f) {
				angle = (float)Math.atan(dy/dx) + (2*PI);
			} else {
				angle = (float)Math.atan(dy/dx);
			}
		}
 
		// Convert to degrees
		angle = angle * 180f / PI;
				
		return angle;
	}


	/** takes the point and returns a new one moved by the distance and angle.
	 * @param startPoint The point to move.
	 * @param distance The distance to move the point by.
	 * @param angle The angle to move the point.
	 * @return The moved point.
	 */
	public static Point2D.Float movePoint(Point2D startPoint, float distance, float angle) {
		float newX = (float)(startPoint.getX() + distance * Math.cos((double)angle * Math.PI / 180));
		float newY = (float)(startPoint.getY() + distance * Math.sin((double)angle * Math.PI / 180));
		return new Point2D.Float(newX, newY);
	}


	
	/**
	 * Finds a node within the passed point, or returns null
	 * if the argument point is not over a node. The padding
	 * refers to the distance the point can be from the 
	 * node, and must be greater than 0. If there is more
	 * than one node, it finds the
	 * last one in the collection, which hopefully should
	 * be the one on top of the display.
	 * @param p The point to test.
	 * @param padding The x and y distance from the node to test. 
	 * @return The nearest node, or null if there are no nodes within the padding of p.
	 */
	public Node findNodeNearPoint(Point.Float p, float padding) {

		Node returnNode = null;

		for(Node n : getGraph().getNodes()) {
			Shape nodeShape = generateNodeShape(n,NODE_SHAPE_STRING);
			Rectangle r = new Rectangle((int)(p.x-padding),(int)(p.y-padding),(int)(padding*2),(int)(padding*2));
			if(nodeShape.intersects(r)) {
				returnNode = n;
			}
		}
		return(returnNode);
	}

	
	
	/**
	 * Finds an edge close to the passed point, or returns null
	 * if the argument point is not over an edge. The padding
	 * refers to the distance the point can be from the 
	 * edge, and must be greater than 0. If there is more
	 * than one edge, it finds the
	 * last one in the collection, which hopefully should
	 * be the one on top of the display.
	 * @param p The point to test.
	 * @param padding The x and y distance from the edge to test. 
	 * @return The nearest edge, or null if there are no nodes within the padding of p.
	 */
	public Edge findEdgeNearPoint(Point.Float p, float padding) {
		for(Edge e : getGraph().getEdges()) {
			float distance = pointLineDistance(p, e.getFrom().getCentre(), e.getTo().getCentre());
			if(distance <= padding) {
				return e;
			}
		}
		
		return null;
	}


	
	
	/** 
	 * Find the distance from p0 to the line created from p1 p2.
	 * If the perpendicular from po to the line is not on the line
	 * then the distance is to the end of the line, either p1 or p2
	 * @param p the point to test.
	 * @param p1 the first point of the line.
	 * @param p2 the second point of the line.
	 * @return nearest distance from p to a line segment between p1 and p2.
	 */
	public static float pointLineDistance(Point.Float p, Point.Float p1, Point.Float p2) {
		
		Point.Float perpPoint = perpendicularPoint(p, p1, p2);
		if(pointIsWithinBounds(perpPoint, p1, p2)) {
			float distance = distance(p,perpPoint);
			return distance;
		}
		
		float distance1 = distance(p,p1);
		float distance2 = distance(p,p2);
		
		if(distance1 < distance2) {
			return distance1;
		}
		return distance2;
	}
	
	
	/**
	 * Checks to see if the p is within the rectangle given
	 * by p1 and p2. Can be used to see if a point is on a line
	 * @param p The point to test.
	 * @param p1 The left top of the rectangle.
	 * @param p2 The right bottom of the rectangle.
	 * @return true if the point is in the rectangle, false otherwise.
	 */
	public static boolean pointIsWithinBounds(Point.Float p, Point.Float p1, Point.Float p2) {
		float left = p1.x;
		float right = p2.x;
		if(p1.x > p2.x) {
			left = p2.x;
			right = p1.x;
		}
		float top = p1.y;
		float bottom = p2.y;
		if(p1.y > p2.y) {
			top = p2.y;
			bottom = p1.y;
		}
		
		if(p.x < left) {return false;}
		if(p.x > right) {return false;}
		if(p.y < top) {return false;}
		if(p.y > bottom) {return false;}
		
		return true;

	}
	



	/**
	 * Get the point on the line between p1 and p2 formed by the
	 * perpendicular from p.
	 * @param p the point to test.
	 * @param p1 the first point of the line.
	 * @param p2 the second point of the line.
	 */
	public static Point.Float perpendicularPoint(Point.Float p, Point.Float p1, Point.Float p2) {
		float x = p.x;
		float y = p.y;
		float x1 = p1.x;
		float y1 = p1.y;
		float x2 = p2.x;
		float y2 = p2.y;
		
		float edgeSlope = (y2-y1)/(x2-x1);
		if(edgeSlope == 0.0) {
			//flat line, perpendicular slope will be infinity (straight down)
			// so point of intersect will be straight down to the line from the point
			return(new Point2D.Float(x,y1));
		}
		float perpendicularSlope = -1/edgeSlope;
		float perpendicularYintersect = y-perpendicularSlope*x;
		Point2D.Float intersectPoint = intersectionPointOfTwoLines(p1,p2,p,new Point2D.Float(0.0f,perpendicularYintersect));

		return intersectPoint;
	}
	
	
	/**
	 * Intersection point of two lines, first line given by p1 and
	 * p2, second line given by p3 and p4.
	 * @param p1 First point of line 1.
	 * @param p2 Second point of line 1.
	 * @param p3 First point of line 2.
	 * @param p4 Second point of line 2.
	 * @return the intersection point, or null if the lines are parallel.
	 */
	public static Point2D.Float intersectionPointOfTwoLines(Point2D.Float p1, Point2D.Float p2, Point2D.Float p3, Point2D.Float p4) {

		float x1 = p1.x;
		float y1 = p1.y;
		float x2 = p2.x;
		float y2 = p2.y;
		float x3 = p3.x;
		float y3 = p3.y;
		float x4 = p4.x;
		float y4 = p4.y;

		float x = x1+ (x2-x1)*(((x4-x3)*(y1-y3)-(y4-y3)*(x1-x3))/((y4-y3)*(x2-x1)-(x4-x3)*(y2-y1)));
		float y = y1+ (y2-y1)*(((y4-y3)*(x1-x3)-(x4-x3)*(y1-y3))/((x4-x3)*(y2-y1)-(y4-y3)*(x2-x1)));
		
		if(x == Float.NaN) {return null;}
		if(y == Float.NaN) {return null;}
		if(x == Float.POSITIVE_INFINITY) {return null;}
		if(y == Float.POSITIVE_INFINITY) {return null;}
		if(x == Float.NEGATIVE_INFINITY) {return null;}
		if(y == Float.NEGATIVE_INFINITY) {return null;}

		return new Point2D.Float(x,y);
	}

	

  	/** 
  	 * Get the distance between two points.
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @return The distance between p1 and p2.
	 */
	public static float distance(Point2D.Float p1, Point2D.Float p2){
			float rise = p1.y - p2.y;
			float run = p1.x - p2.x;
			float distance = (float)Math.sqrt(Math.pow(rise, 2)+ Math.pow(run, 2));
			return distance;
	}
	

	
	/**
	 * Finds the closest node in the graph to the point, or returns null
	 * if there are no nodes in the graph. If distances are equal, the firs
	 * node encountered is returned.
	 * @param p the point to test.
	 * @return the closest node to p.
	 */
	public Node closestNode(Point.Float p) {

		Node returnNode = null;

		double closestDistance = Double.MAX_VALUE;

		for(Node n : getGraph().getNodes()) {
			Point.Float centre = n.getCentre();
			double distance = centre.distance(p);

			if(distance < closestDistance) {
				returnNode = n;
				closestDistance = distance;
			}
		}
		return returnNode;
	}


	
	/**
	 * Moves the node to the end of the graphs node list. Mostly for raising the node in the display.
	 * @param g The graph containing the node.
	 * @param n The node to move to end of the list.
	 * @return true if successful, false if not, because n is not in the node list.
	 */
	private static boolean moveNodeToGraphEnd(Graph g, Node n) {
		ArrayList<Node> nodes = g.getNodes();
		if (!nodes.contains(n)) {
			return false;
		}
		// need to remove the node directly from the list
		// to avoid connecting edge removal
		nodes.remove(n);
		nodes.add(n);
		return true;
		
	}



}






