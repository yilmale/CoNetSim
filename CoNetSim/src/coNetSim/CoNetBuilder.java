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
	
	double[][] adjMatrix;
	double[] activations;
	
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
		
		
		int size = 10;
		double density = 0.5;
		
		adjMatrix = new double[size][size];
		activations = new double[size];
		
		for (int i=0; i<size; i++) activations[i]=0.1;
		activations[0]=1.0;
		
		for (int i=0; i<size; i++) 
			for (int j=0; j<=i; j++) {
				double connection = RandomHelper.nextDoubleFromTo(0,1);
				if (connection <= (density/5)) {
					adjMatrix[i][j] = -1.0;
					adjMatrix[j][i] = -1.0;
				}
				else 
				if ((connection > (density/5)) & (connection <= (density))) {
						adjMatrix[i][j] = 1.0;
						adjMatrix[j][i] = 1.0;
				}
				else {
					adjMatrix[i][j] = 0;
					adjMatrix[j][i] = 0;
				}
			}
		
		for (int i=0; i<size; i++) adjMatrix[i][i]=0;
		
		SimpleGraphView sgv = new SimpleGraphView(size); 
		
		int nodeCount=size;
		CoNetNode myNodes[] = new CoNetNode[size];
		int nCount=0;
		for (int i = 0; i < nodeCount; i++) {
			CoNetNode x = new CoNetNode(i,activations[i]);
			myNodes[nCount]=x; nCount++;
			context.add(x);
			sgv.addNode(x);		
		}
		
			
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		
		for (int i=0; i<size; i++)
			for (int j=0; j<=i; j++) {
				if (adjMatrix[i][j] != 0) {
					sgv.addEdge(i, j, adjMatrix[i][j]);
					net.addEdge(myNodes[i],myNodes[j],adjMatrix[i][j]);
					net.addEdge(myNodes[j],myNodes[i],adjMatrix[i][j]);
				}			
			}
		
		
		sgv.initialize();
	
		sgv.display();
		
		
		context.add(sgv);

		
		   
		
		return context;
	}

}

