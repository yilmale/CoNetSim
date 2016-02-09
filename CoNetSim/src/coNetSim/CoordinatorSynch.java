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

public class CoordinatorSynch extends Coordinator{
	
	public CoordinatorSynch(int size) {
		super(size);
	
	}
	
	@ScheduledMethod(start=0,interval=1)
	public void step() {
	  if (this.equilibriumReached==false) {	
		Iterator<CoNetNode> nodeItr = registeredNodes.iterator();
		double[] activationsatT = new double[registeredNodes.size()];
		int index=0;
		while (nodeItr.hasNext()) {
			CoNetNode x = nodeItr.next();
			activationsatT[index]=x.activation;
			//System.out.println("Cloned node "+ x.id + "with activation "+ x.activation);
			index++;
		}
		
		Iterator<CoNetNode> nItr = registeredNodes.iterator();
		int count = 0;
		Context<Object> context = ContextUtils.getContext(this);
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		double maxChange = 0;
		while (count < MAXITERATION)  {
		  maxChange = 0;
		  while (nItr.hasNext()) {		
			CoNetNode y = nItr.next();
			System.out.println("Updating activation of "+ y.id);
			Iterator<RepastEdge<CoNetNode>> myInEdges= net.getInEdges(y).iterator();
			double netFlow=0;
			while (myInEdges.hasNext()) {
				RepastEdge<CoNetNode> e = myInEdges.next();
				CoNetNode mySource = e.getSource();
				//System.out.println("flow from node "+ mySource.id + " is " + activationsatT[mySource.id]);
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
		  if (maxChange < THRESHOLD) break;
		}
		System.out.println("Completed " + count + " iterations");
		if ((maxChange < THRESHOLD) && (this.equilibriumReached==false)) {
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			double stableTime = schedule.getTickCount();
			System.out.println("The network reached equilibrium at time "+ stableTime);
			this.equilibriumReached=true;
			this.equilibriumTime=stableTime;
		}
		updateNetworkHarmony();
		System.out.println("Network harmony is " + this.networkHarmony);
	   }
	  }

}
