package coNetSim;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.awt.Dimension;

import javax.swing.JFrame;

public class CoNetBuilder implements ContextBuilder<Object> {
	
	@Override
	public Context build(Context<Object> context) { 
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
						new SimpleGridAdder<Object>(), true, 50, 50));
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"coherence network", context, true);
		netBuilder.buildNetwork();

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"coherence space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 50,
				50);
		
		SimpleGraphView sgv = new SimpleGraphView(); 
		
		int nodeCount=10;
		CoNetNode myNodes[] = new CoNetNode[10];
		int nCount=0;
		for (int i = 0; i < nodeCount; i++) {
			CoNetNode x = new CoNetNode(i,0.1,space,grid);
			myNodes[nCount]=x; nCount++;
			context.add(x);
			sgv.addNode(x);		
		}
		
			
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		double edgeWeight = 1.0;
		net.addEdge(myNodes[0],myNodes[1],edgeWeight);
		net.addEdge(myNodes[2],myNodes[3],edgeWeight);
		net.addEdge(myNodes[0],myNodes[3],edgeWeight);
		net.addEdge(myNodes[3],myNodes[4],edgeWeight);
		
		sgv.addEdge(0, 1, edgeWeight); sgv.addEdge(2, 3, edgeWeight);
		sgv.addEdge(0, 3, edgeWeight); sgv.addEdge(3, 4, edgeWeight);
		
		sgv.initialize();
	
		sgv.display();
		
		
		context.add(sgv);

		
		   
		
		return context;
	}

}

