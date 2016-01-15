package coNetSim;

import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class CoNetNode {
	private int id;
	private double activation;
	private double decayrate;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private double MIN;
	private double MAX;
	
	public CoNetNode(int id, double activation, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.id = id;
		this.space=space;
		this.grid=grid;
		this.activation=activation;
		this.decayrate = 0.05;
		this.MIN = -1;
		this.MAX=1;
				
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		System.out.println("CoNet Node" + this.id + " is activated with..." + this.activation);
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>)context.getProjection("coherence network");
		Iterator<RepastEdge<Object>> myInEdges= net.getInEdges(this).iterator();
		System.out.println("reached here");
		double netFlow=0;
		while (myInEdges.hasNext()) {
			RepastEdge<Object> x = myInEdges.next();
			CoNetNode mySource = (CoNetNode) x.getSource();
			netFlow=netFlow+x.getWeight()*mySource.activation;
		}
		
		if (netFlow > 0) {
			this.activation = (this.activation*(1-this.decayrate)) + (netFlow*(this.MAX-this.activation));
			this.activation = Math.min(1, this.activation);
		}
		else
		{
			this.activation = (this.activation*(1-this.decayrate)) + (netFlow*(this.activation-this.MIN));
			this.activation = Math.max(-1, this.activation);
		}
		
		System.out.println("CoNet Node" + this.id + " is updated to..." + this.activation);
	}

}
