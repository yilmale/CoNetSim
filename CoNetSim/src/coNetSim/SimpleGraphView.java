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
import edu.uci.ics.jung.visualization.renderers.Renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

import java.util.Iterator;



public class SimpleGraphView {
	Graph<Integer, Integer> g;
	Layout<Integer, Integer> layout;
	VisualizationViewer<Integer,Integer> vv;
	JFrame frame;
	DefaultModalGraphMouse gm;
	MyLink myLinks[];
	double activations[];
	int nodeCount;
    /** Creates a new instance of SimpleGraphView */
    public SimpleGraphView() {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        g = new UndirectedSparseMultigraph<Integer, Integer>();
        myLinks = new MyLink[10];
        activations = new double[10];
        for (int i=0;i<10; i++) activations[i]=0;
        nodeCount=0;
    }
    
    public void addNode(CoNetNode cNode) {
    	g.addVertex((Integer)cNode.id);		
    }
    
    public void addEdge(int source, int target, double eWeight) {
    	int edgeCount=g.getEdgeCount();
    	MyLink nLink = new MyLink(eWeight,edgeCount);
    	myLinks[edgeCount]=nLink;
    	g.addEdge(g.getEdgeCount(),source, target);
    }
    
    
    public void initialize() {
    	layout = new FRLayout2(g);
    	layout.setSize(new Dimension(300,300));
    	vv = new VisualizationViewer<Integer,Integer>(layout);
    	vv.setPreferredSize(new Dimension(500,500));
    	// Show vertex and edge labels
    	vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
    	vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    	//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    	// Create a graph mouse and add it to the visualization component
    	gm = new DefaultModalGraphMouse();
    	gm.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	vv.setGraphMouse(gm); 
    	frame = new JFrame("Interactive Graph View 1");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add(vv);
    	frame.pack();
    	frame.setVisible(true);
    }
    
    public void display() {
    	
    	// Show vertex and edge labels
    	//vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    	//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    	// Create a graph mouse and add it to the visualization component
    	Relaxer relaxer = vv.getModel().getRelaxer();
        vv.getModel().getRelaxer().setSleepTime(500);
        vv.setGraphMouse(new DefaultModalGraphMouse<Number,Number>());
        
        Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                if (activations[i]>0.30) return Color.RED;
                else return Color.BLUE;
            }
        };  
        
        Transformer<Integer,Paint> colorTransformer = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) { 
               if (myLinks[i].weight>0) return Color.BLACK;
               else return Color.RED;
            }
        };     
    	
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        //vv.getRenderContext().setVertexShapeTransformer(vertexSize)
        vv.getRenderContext().setArrowFillPaintTransformer(colorTransformer);
        vv.getRenderContext().setArrowDrawPaintTransformer(colorTransformer);
        vv.getRenderContext().setEdgeDrawPaintTransformer(colorTransformer);
        layout.initialize();
        relaxer.resume();
    }
    
    
    @ScheduledMethod(start=0,interval=1)
	public void step() {
    	/*int i=g.getVertexCount();
    	g.addVertex(g.getVertexCount());
    	int node = RandomHelper.nextIntFromTo(0, g.getVertexCount());
    	g.addEdge(g.getEdgeCount(), i, node);*/
    	
    	Context<Object> context = ContextUtils.getContext(this);
    	Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
    	Iterator<CoNetNode> nIterator=net.getNodes().iterator();
    	while (nIterator.hasNext()) {
    		
    		Object cn = nIterator.next();
    		if (cn instanceof CoNetNode) {
    			CoNetNode cNode = (CoNetNode) cn;
    			activations[cNode.id]=cNode.activation;
    		}
    	}
    	display();
    
    }
    
}
