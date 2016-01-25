package coNetSim;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

import java.util.Iterator;

public class CoordinatorAsynch extends Coordinator {
	
	private double networkHarmony;
	
	public CoordinatorAsynch(int size) {
		super(size);
		
	}
	
	
	public double getHarmony() {
		return this.networkHarmony;
	}
	
	
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		updateNetworkHarmony();
	}

}
