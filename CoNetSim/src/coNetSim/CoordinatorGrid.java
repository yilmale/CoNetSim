package coNetSim;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class CoordinatorGrid {
	public ArrayList<CoNetNodeGrid> registeredNodes;
	double[][] visited;
	double[] activations;
	public double networkHarmony;
	public final int MAXITERATION = 1;
	public final double THRESHOLD = 0.001;
	public boolean equilibriumReached;
	public double equilibriumTime;
	
	public CoordinatorGrid(int size) {
		registeredNodes = new ArrayList<CoNetNodeGrid>();
		this.networkHarmony=0;
		this.equilibriumReached=false;
		this.equilibriumTime=-1;
		this.visited = new double[size][size];
		this.activations = new double[size];
		for (int i=0; i<size; i++) this.activations[i]=0;
		for (int i = 0; i<size; i++)
			for (int j=0; j< size; j++) 
				this.visited[i][j]=0;
	}
	
	
	public void register(CoNetNodeGrid cn) {
		registeredNodes.add(cn);
		System.out.println("Registered node " + registeredNodes.get(registeredNodes.size()-1).id);
	}
	
	public double[] updateNodeActivations() {
		
		Iterator nodeItr = registeredNodes.iterator();
		while (nodeItr.hasNext()) {
			CoNetNodeGrid x= (CoNetNodeGrid) nodeItr.next();
			this.activations[x.id]=x.activation;
		}
		return this.activations;
	}
	
	public double[] getNodeActivations() {
		return this.activations;
	}
	
	public int getActiveCount() {
		int activeCount = 0;
		Iterator nodeItr = registeredNodes.iterator();
		while (nodeItr.hasNext()) {
			CoNetNodeGrid x= (CoNetNodeGrid) nodeItr.next();
			if (x.activation > 0) activeCount++;
		}
		return activeCount;
	}
	
	public int getPassiveCount() {
		int activeCount = getActiveCount();
		return registeredNodes.size()-activeCount;
	}
	
	public double computeActivationDiff() {
		double aDiff=0;
		Iterator nodeItr = registeredNodes.iterator();
		while (nodeItr.hasNext()) {
			CoNetNodeGrid x= (CoNetNodeGrid) nodeItr.next();
			if (Math.abs(x.activation - x.old_activation) > aDiff) 
				aDiff=Math.abs(x.activation - x.old_activation);
		}
		return aDiff;
	}
	
	public double computeHarmony() {
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid)context.getProjection("grid");
		double nH=0;
		Iterator nodeItr = registeredNodes.iterator();
		while (nodeItr.hasNext()) {
			CoNetNodeGrid x= (CoNetNodeGrid) nodeItr.next();
			VNQuery<Object> query = new VNQuery<Object>(grid, x);
			for (Object agent : query.query()) {
			 if (agent instanceof CoNetNodeGrid)
			 {
			  CoNetNodeGrid cnAgent = (CoNetNodeGrid) agent;
			  if ((visited[x.id][cnAgent.id]==0) && visited[cnAgent.id][x.id]==0)	{
				visited[x.id][cnAgent.id]=1;
				if (cnAgent.type == x.type) {
					nH = nH + x.excitationStrength*x.activation*cnAgent.activation;
				}
				else 
				{
					nH = nH + x.inhibitionStrength*x.activation*cnAgent.activation;
				}
			  }
			 }
			}
		}
		
		this.networkHarmony=nH;
		
		for (int i = 0; i<registeredNodes.size(); i++)
			for (int j=0; j< registeredNodes.size(); j++) 
				visited[i][j]=0;
		
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
			computeHarmony();
			double activationUpdate=computeActivationDiff();	
			System.out.println("The activation differential is " + activationUpdate);
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
