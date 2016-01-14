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
		
		int nodeCount = 10;
		CoNetNode myNodes[] = new CoNetNode[10];
		for (int i = 0; i < nodeCount; i++) {
			CoNetNode x = new CoNetNode(i,space,grid);
			myNodes[i]=x;
			context.add(x);
		}
		
		
		Network<Object> net = (Network<Object>)context.getProjection("coherence network");
		for (int i = 0; i < 10; i++) {
			double connectionP = RandomHelper.nextDoubleFromTo(0, 1);
			if (connectionP < 0.8) { 
				int node = RandomHelper.nextIntFromTo(0, 9);
				net.addEdge(myNodes[i], myNodes[node]);
			}
		}
		
		
		
		return context;
	}

}
