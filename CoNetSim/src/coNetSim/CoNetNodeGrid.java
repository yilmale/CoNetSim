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

public class CoNetNodeGrid {
	public int id;
	public double activation;
	public double decayrate;
	public double MIN;
	public double MAX;
	public double excitationStrength;
	public double inhibitionStrength;
	public boolean evidence;
	public int type;
	public double vBlue;
	public double vRed;
	
	public CoNetNodeGrid(int id, double activation, boolean evd, int numberOfNodeTypes, double dExcite, double dInhibit) {
		this.id = id;
		this.activation=activation;
		this.decayrate = 0.05;
		this.MIN = -1;
		this.MAX=1;
		this.evidence = evd;
		this.type = RandomHelper.nextIntFromTo(0, numberOfNodeTypes-1);
		this.excitationStrength=dExcite;
		this.inhibitionStrength=dInhibit;
		if (this.evidence==true) {this.vBlue=0; this.vRed=1;}
		else {this.vBlue=1; this.vRed=0;}
		
	}
	
	public double getActivation() {
		return this.activation;
	}
	
	public double getBActivation() {
		if (this.activation > 0) 
		   return 10*(1-this.activation);
		else return 10;
	}
	
	public double getRActivation() {
		if (this.activation > 0) return 10*this.activation;
		else return 0;
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		Context<CoNetNodeGrid> context = (Context)ContextUtils.getContext(this);
		Grid<CoNetNodeGrid> grid = (Grid)context.getProjection("grid");
		
		VNQuery<CoNetNodeGrid> query = new VNQuery<CoNetNodeGrid>(grid, this);
		
		double netFlow =0;
		for (CoNetNodeGrid agent : query.query()) {
			if (agent.type == this.type) {
				netFlow = netFlow + this.excitationStrength*agent.activation;
			}
			else 
			{
				netFlow = netFlow + this.inhibitionStrength*agent.activation;
			}
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
	}
	

}






