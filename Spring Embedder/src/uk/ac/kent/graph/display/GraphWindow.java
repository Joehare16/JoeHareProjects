package uk.ac.kent.graph.display;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import uk.ac.kent.graph.*;
import uk.ac.kent.graph.drawers.*;



/** Graph layout window using GraphPanel */
public class GraphWindow extends JFrame implements ActionListener {

	protected GraphPanel gp = null;
	protected GraphWindow gw = null;
	protected File currentFile = null;
	protected File startDirectory;
	protected int width = 600;
	protected int height = 600;

	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that creates a graph window with a graph panel. The graph panel contains
	 * the graph g.
	 * @param g The graph to appear in the graph panel.
	 */
	public GraphWindow(Graph g) {
		super("Graph Editor "+g.getLabel());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		String startDirectoryName = System.getProperty("user.dir");
		startDirectory = new File(startDirectoryName);
		
		gw = this;
		
		gp = new GraphPanel(g,this);
		getContentPane().add(gp);

		initLayout();
		initMenu();

		setSize(width,height);

		Dimension frameDim = Toolkit.getDefaultToolkit().getScreenSize();
		int posX = (frameDim.width - getSize().width)/2;
		int posY = (frameDim.height - getSize().height)/2;
		setLocation(posX, posY);

		gp.requestFocus();
 	}



/** Trival accessor. */
	public Graph getGraph() {return gp.getGraph();}
/** Trival accessor. */
	public GraphPanel getGraphPanel() {return gp;}


	private void initLayout() {
		gp.addGraphDrawer(new GraphDrawerSpringEmbedder(KeyEvent.VK_S,"Spring Embedder",KeyEvent.VK_S));
	}

	private void initMenu() {
	
		JMenuBar menuBar = new JMenuBar();

		setJMenuBar(menuBar);

// File Menu
		JMenu fileMenu = new JMenu("File");

		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
	
		JMenuItem fileNewItem = new JMenuItem("New",KeyEvent.VK_N);
		fileNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(fileNewItem);

		JMenuItem fileOpenAdjacencyItem = new JMenuItem("Open Adjacency File...");
		fileMenu.add(fileOpenAdjacencyItem);

		JMenuItem fileExitItem = new JMenuItem("Exit",KeyEvent.VK_X);
		fileExitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		fileMenu.add(fileExitItem);

		fileExitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileExit();
			}
		});
		
		fileNewItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileNew();
			}
		});

		fileOpenAdjacencyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileOpenAdjacency();
			}
		});



		JMenu layoutMenu = new JMenu("Layout");
        layoutMenu.setMnemonic(KeyEvent.VK_L);
		menuBar.add(layoutMenu);

		for(GraphDrawer d : gp.getGraphDrawerList()) {
	        JMenuItem menuItem = new JMenuItem(d.getMenuText(), d.getMnemonicKey());
			menuItem.setAccelerator(KeyStroke.getKeyStroke(d.getAcceleratorKey(),0));
			menuItem.addActionListener(this);
			layoutMenu.add(menuItem);
		}
		
	}

	protected void fileNew() {
		if (currentFile != null) {
			if (!currentFile.isDirectory()) {
				currentFile = currentFile.getParentFile();
			}
		}
		getGraph().clear();
		gp.update(gp.getGraphics());
	}

	
	protected void fileOpenAdjacency() {
		JFileChooser chooser = null;
		if (currentFile == null) {
			chooser = new JFileChooser(startDirectory);
		} else {
			chooser = new JFileChooser(currentFile);
		}
			
		int returnVal = chooser.showOpenDialog(gw);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			getGraph().clear();
			currentFile = chooser.getSelectedFile();
			getGraph().loadAdjacencyFile(currentFile.getAbsolutePath());
			gp.update(gp.getGraphics());
		}
	}


	public void fileExit() {
		System.exit(0);
	}


	protected void editSelectAll() {
		gp.getSelection().addNodes(getGraph().getNodes());
		gp.getSelection().addEdges(getGraph().getEdges());
		gp.repaint();
	}


	public void actionPerformed(ActionEvent event) {
		JMenuItem source = (JMenuItem)(event.getSource());

		for(GraphDrawer d : gp.getGraphDrawerList()) {
			if (d.getMenuText().equals(source.getText())) {
				d.layout();
				repaint();
				return;
			}
		}
	}

}



