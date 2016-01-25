package coNetSim;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Coordinator {
	public ArrayList<CoNetNode> registeredNodes;
	public double networkHarmony;
	public final int MAXITERATION = 1;
	public final double THRESHOLD = 0.001;
	public boolean equilibriumReached;
	public double equilibriumTime;
	
	public Coordinator(int size) {
		registeredNodes = new ArrayList<CoNetNode>();
		this.networkHarmony=0;
		this.equilibriumReached=false;
		this.equilibriumTime=-1;
	
	}
	
	public void register(CoNetNode cn) {
		registeredNodes.add(cn);
		System.out.println("Registered node " + registeredNodes.get(registeredNodes.size()-1).id);
	}
	
	public double getHarmony() {
		return this.networkHarmony;
	}
	
	public double getEquilibriumTime() {
		return this.equilibriumTime;
	}
	
	
	public void updateNetworkHarmony() {
		Context<Object> context = ContextUtils.getContext(this);
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		double nH=0;
		for (int i=0; i<registeredNodes.size(); i++)
			for (int j=0; j<i; j++) {
				RepastEdge<CoNetNode> e = net.getEdge(registeredNodes.get(i), registeredNodes.get(j));
				if (e!=null)
					nH=nH+(registeredNodes.get(i).activation*registeredNodes.get(j).activation*e.getWeight());
			}
	
		this.networkHarmony=nH;
	}
	
	public double computeActivationDiff() {
		double aDiff=0;
		Iterator nodeItr = registeredNodes.iterator();
		while (nodeItr.hasNext()) {
			CoNetNode x= (CoNetNode) nodeItr.next();
			if (Math.abs(x.activation - x.old_activation) > aDiff) 
				aDiff=Math.abs(x.activation - x.old_activation);
		}
		return aDiff;
	}
	
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		
	}
}
