package coNetSim;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Coordinator {
	public ArrayList<CoNetNode> registeredNodes;
	final int MAXITERATION = 50;
	
	public Coordinator(int size) {
		registeredNodes = new ArrayList<CoNetNode>();
	
	}
	
	public void register(CoNetNode cn) {
		registeredNodes.add(cn);
		System.out.println("Registered node " + registeredNodes.get(registeredNodes.size()-1).id);
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
		Iterator<CoNetNode> nodeItr = registeredNodes.iterator();
		double[] activationsatT = new double[registeredNodes.size()];
		int index=0;
		while (nodeItr.hasNext()) {
			CoNetNode x = nodeItr.next();
			activationsatT[index]=x.activation;
			System.out.println("Cloned node "+ x.id + "with activation "+ x.activation);
			index++;
		}
		
		Iterator<CoNetNode> nItr = registeredNodes.iterator();
		int count = 0;
		Context<Object> context = ContextUtils.getContext(this);
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		
		while (count < MAXITERATION)  {
		  double maxChange = 0;
		  while (nItr.hasNext()) {		
			CoNetNode y = nItr.next();
			System.out.println("Updating activation of "+ y.id);
			Iterator<RepastEdge<CoNetNode>> myInEdges= net.getInEdges(y).iterator();
			double netFlow=0;
			while (myInEdges.hasNext()) {
				RepastEdge<CoNetNode> e = myInEdges.next();
				CoNetNode mySource = e.getSource();
				System.out.println("flow from node "+ mySource.id + " is " + activationsatT[mySource.id]);
				netFlow=netFlow+e.getWeight()*activationsatT[mySource.id];
			}
			
			if ((netFlow > 0) && (y.evidence == false)) {
				y.activation = (y.activation*(1-y.decayrate)) + (netFlow*(y.MAX-y.activation));
				y.activation = Math.min(1, y.activation);
			}
			else
			if ((netFlow <= 0) && (y.evidence == false))
			{
				y.activation = (y.activation*(1-y.decayrate)) + (netFlow*(y.activation-y.MIN));
				y.activation = Math.max(-1, y.activation);
			}
			double diff = Math.abs(y.activation-activationsatT[y.id]);
			if (diff > maxChange) maxChange = diff;
			System.out.println("Node " + y.id + "new activation="+y.activation + 
					" old activation="+ activationsatT[y.id] + " diff=" + diff);
			
		  }
		  nItr = registeredNodes.iterator();
		  count++;
		  nodeItr = registeredNodes.iterator();
		  System.out.println("Copying back new activations for the next iteration");
		  while (nodeItr.hasNext()) {
				CoNetNode x = nodeItr.next();
				activationsatT[x.id]=x.activation;
			}
		  System.out.println("The maximum activation differential in this iteration was " + maxChange);
		  if (maxChange < 0.001) break;
		}
		System.out.println("Completed " + count + " iterations");
		Iterator<CoNetNode> node1Itr = registeredNodes.iterator();
		
		while (node1Itr.hasNext()) {
			CoNetNode x = node1Itr.next();
			System.out.println("Activation of node " + x.id + " is " + x.activation);
		}
	}
}
