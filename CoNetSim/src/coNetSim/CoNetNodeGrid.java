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
	public double old_activation;
	public double decayrate;
	public double conDensity;
	public double MIN;
	public double MAX;
	public double excitationStrength;
	public double inhibitionStrength;
	public boolean evidence;
	public double inhibitionRatio;
	public double vBlue;
	public double vRed;
	public boolean initialSetup;
	
	public CoNetNodeGrid(int id, double activation, boolean evd, 
			double dExcite, double dInhibit, double density, double inhibitR) {
		this.id = id;
		this.initialSetup=false;
		this.activation=activation;
		this.old_activation=0;
		this.decayrate = 0.05;
		this.conDensity=density;
		this.MIN = -1;
		this.MAX=1;
		this.evidence = evd;
		this.excitationStrength=dExcite;
		this.inhibitionStrength=dInhibit;
		this.inhibitionRatio=inhibitR;
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
		Context<Object> context = (Context)ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("grid");
		Network<CoNetNodeGrid> net = (Network<CoNetNodeGrid>)context.getProjection("coherence network");
		double temp=this.activation;
		
	
		if (this.initialSetup==false) {
		  this.initialSetup=true;
		  VNQuery<Object> query = new VNQuery<Object>(grid, this);
		  for (Object agent : query.query()) {
			 if (agent instanceof CoNetNodeGrid) {		    	 
				double rnd = RandomHelper.nextDoubleFromTo(0,1);
				if (rnd <= this.conDensity) {
					double irnd = RandomHelper.nextDoubleFromTo(0, 1);
					if (irnd <= this.inhibitionRatio) {
					  if (net.isAdjacent(this, (CoNetNodeGrid)agent)==false)	
						net.addEdge(this, (CoNetNodeGrid)agent, this.inhibitionStrength);
					}
					else
					{
					  if (net.isAdjacent(this, (CoNetNodeGrid)agent)==false)
						net.addEdge(this, (CoNetNodeGrid)agent, this.excitationStrength);
					}
				}
			 }	
		}
		}		
		
	   boolean netConnected = net.getAdjacent(this).iterator().hasNext();
	   if ((this.evidence==false) && (netConnected==true)) {
		double netFlow =0;
		Iterator<CoNetNodeGrid> myNeighbors = net.getAdjacent(this).iterator();
		while (myNeighbors.hasNext()) {
			CoNetNodeGrid xN = myNeighbors.next();
			RepastEdge<CoNetNodeGrid> xE=net.getEdge(xN, this);
			if (xE!=null)
				netFlow = netFlow + xN.activation*xE.getWeight();		
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
		
	}
  }

}






