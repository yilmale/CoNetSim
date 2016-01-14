package coNetSim;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class CoNetNode {
	private int id;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public CoNetNode(int id, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.id = id;
		this.space=space;
		this.grid=grid;
		
		
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		System.out.println("CoNet Node" + this.id + " is activated...");
	}

}
