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
	public int id;
	public double activation;
	public double old_activation;
	public double decayrate;
	public double MIN;
	public double MAX;
	public int updateMode;
	public boolean evidence;
	final int SYNCHRONOUS = 1;
	final int ASYNCHRONOUS = 0;
	
	public CoNetNode(int id, double activation, int updateModel, boolean evd) {
		this.id = id;
		this.activation=activation;
		this.old_activation=0;
		this.decayrate = 0.05;
		this.MIN = -1;
		this.MAX=1;
		this.updateMode = updateModel;
		this.evidence = evd;
				
	}
	
	public double getActivation() {
		return this.activation;
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
	  if (this.updateMode == ASYNCHRONOUS) {
		double temp=this.activation;
		System.out.println("CoNet Node" + this.id + " is activated with..." + this.activation);
		Context<Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>)context.getProjection("coherence network");
		Iterator<RepastEdge<Object>> myInEdges= net.getInEdges(this).iterator();
		
		double netFlow=0;
		while (myInEdges.hasNext()) {
			RepastEdge<Object> x = myInEdges.next();
			CoNetNode mySource = (CoNetNode) x.getSource();
			netFlow=netFlow+x.getWeight()*mySource.activation;
		}
		
		if ((netFlow > 0) && (this.evidence == false)) {
			this.activation = (this.activation*(1-this.decayrate)) + (netFlow*(this.MAX-this.activation));
			this.activation = Math.min(1, this.activation);
		}
		else 
		if ((netFlow <= 0) && (this.evidence == false))
		{
			this.activation = (this.activation*(1-this.decayrate)) + (netFlow*(this.activation-this.MIN));
			this.activation = Math.max(-1, this.activation);
		}
		this.old_activation=temp;
		System.out.println("CoNet Node" + this.id + " is updated to..." + this.activation);
	  }
	}

}
