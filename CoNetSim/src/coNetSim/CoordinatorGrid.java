package coNetSim;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class CoordinatorGrid {
	public double networkHarmony;
	public final double THRESHOLD = 0.001;
	public boolean equilibriumReached;
	public double equilibriumTime;
	public double actThreshold;
	
	public CoordinatorGrid(int size, double threshold) {
		this.networkHarmony=0;
		this.actThreshold=threshold;
		this.equilibriumReached=false;
		this.equilibriumTime=-1;
	}
	
	

	
	
	
	public int getActiveCount() {
		int activeCount = 0;
		
		return activeCount;
	}
	
	public int getPassiveCount() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numNodes = (Integer)p.getValue("numberOfNodes");
		int activeCount = getActiveCount();
		return numNodes-activeCount;
	}
	
	public double computeActivationDiff() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("grid");
		Network<CoNetNodeGrid> net = (Network<CoNetNodeGrid>)context.getProjection("coherence network");
		double aDiff=0;
		Iterator<CoNetNodeGrid> nodeItr = net.getNodes().iterator();
		while (nodeItr.hasNext()) {
			CoNetNodeGrid x= (CoNetNodeGrid) nodeItr.next();
			if (x!=null) {
				if (Math.abs(x.activation-x.old_activation)==1) 
						System.out.println("node: " + x.id + " activation: " + x.activation + "old:" + x.old_activation);
			}
			/*if (Math.abs(x.activation - x.old_activation) > aDiff)  {
				aDiff=Math.abs(x.activation - x.old_activation);
				System.out.println("node: " + x.id + "old: " + x.old_activation + "new: " + x.activation + " neighbors:");
				Iterator<CoNetNodeGrid> nItr=net.getAdjacent(x).iterator();
				while (nItr.hasNext()) {
					CoNetNodeGrid myX = nItr.next();
					System.out.println("node: " + myX.id + " activation: " + myX.activation);
				}
			}*/
			
		}
		return aDiff;
	}
	
	public double computeHarmony() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("grid");
		double nH=0;
		Network<CoNetNodeGrid> net = (Network<CoNetNodeGrid>)context.getProjection("coherence network");
		Iterator <RepastEdge<CoNetNodeGrid>> edgeItr = net.getEdges().iterator();
		while (edgeItr.hasNext()) {
			RepastEdge<CoNetNodeGrid> conetEdge = edgeItr.next();
			nH = nH + conetEdge.getSource().activation*conetEdge.getTarget().activation*conetEdge.getWeight();
		}
		
		
		this.networkHarmony=nH;
		
		return this.networkHarmony;
	}
	
	public double getEquilibriumTime() {
		if (this.equilibriumReached==true) {
			return this.equilibriumTime;
		}
		else return -1;
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("grid");	
		if (this.equilibriumReached == false) {
			double nH=computeHarmony();
			//double activationUpdate=computeActivationDiff();	
			double activationUpdate=1;
			System.out.println("The network harmony is " + nH);
			if (activationUpdate < THRESHOLD) {
				this.equilibriumReached=true;
				ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
				this.equilibriumTime=schedule.getTickCount();
				System.out.println("Equilibrium time is " + this.equilibriumTime);
				System.out.println("Active nodes: " + this.getActiveCount());
				System.out.println("Passive nodes: " + this.getPassiveCount());
				RunEnvironment.getInstance().endRun();
			}
		}
		
	}
	
	
	
}
