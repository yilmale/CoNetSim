package coNetSim;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.awt.Dimension;

import javax.swing.JFrame;

import repast.simphony.engine.schedule.ScheduledMethod;



public class SimpleGraphView {
	Graph<MyNode, MyLink> g;
	Layout<Integer, Integer> layout;
	VisualizationViewer<Integer,Integer> vv;
	JFrame frame;
    /** Creates a new instance of SimpleGraphView */
    public SimpleGraphView() {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        g = new UndirectedSparseMultigraph<MyNode, MyLink>();
        // Add some vertices. From above we defined these to be type Integer.
       // g.addVertex((Integer)1);
       // g.addVertex((Integer)2);
       // g.addVertex((Integer)3); 
        // Note that the default is for undirected edges, our Edges are Strings.
        //g.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
       // g.addEdge("Edge-B", 2, 3);  
         
     
    }
    public void initialize() {
    	layout = new FRLayout2(g);
    	layout.setSize(new Dimension(300,300));
    	vv = new VisualizationViewer<Integer,Integer>(layout);
    	vv.setPreferredSize(new Dimension(500,500));
    	// Show vertex and edge labels
    	vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    	vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    	// Create a graph mouse and add it to the visualization component
    	DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
    	gm.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	vv.setGraphMouse(gm); 
    	frame = new JFrame("Interactive Graph View 1");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(vv);
    	frame.pack();
    	frame.setVisible(true);
    }
    
    public void display() {
    	layout = new FRLayout2(g);
    	layout.setSize(new Dimension(300,300));
    	vv = new VisualizationViewer<Integer,Integer>(layout);
    	vv.setPreferredSize(new Dimension(500,500));
    	// Show vertex and edge labels
    	//vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    	//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    	// Create a graph mouse and add it to the visualization component
    	
    	
    	
    	DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
    	gm.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	vv.setGraphMouse(gm); 
    	//frame = new JFrame("Interactive Graph View 1");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(vv);
    	frame.pack();
    	frame.setVisible(true);
    }
    
    
    @ScheduledMethod(start=0,interval=1)
	public void step() {
    
    }
    
}
