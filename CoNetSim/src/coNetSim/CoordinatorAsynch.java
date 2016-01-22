package coNetSim;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

import java.util.Iterator;

public class CoordinatorAsynch {
	
	private double networkHarmony;
	private ArrayList<CoNetNode> networkNodes;
	
	public CoordinatorAsynch() {
		this.networkHarmony=0;	
		this.networkNodes = new ArrayList<CoNetNode>();
		
	}
	
	public void register(CoNetNode in) {
		networkNodes.add(in);
		System.out.println("Registered node " + networkNodes.get(networkNodes.size()-1).id);
	}
	
	public double getHarmony() {
		return this.networkHarmony;
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		Context<Object> context = ContextUtils.getContext(this);
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		double nH=0;
		for (int i=0; i<networkNodes.size(); i++)
			for (int j=0; j<i; j++) {
				RepastEdge<CoNetNode> e = net.getEdge(networkNodes.get(i), networkNodes.get(j));
				if (e!=null) {
				nH=nH+(networkNodes.get(i).activation*networkNodes.get(j).activation*e.getWeight());}
			}
	
		this.networkHarmony=nH;
	}

}
