package coNetSim;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.ContextUtils;

import java.util.Iterator;



public class CoNetBuilder implements ContextBuilder<Object> {
	
	double[][] adjMatrix;
	double[] activations;
	
	final int SYNCHRONOUS = 1;
	final int ASYNCHRONOUS = 0;
	
	
	@Override
	public Context build(Context<Object> context) { 
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"coherence network", context, true);
		netBuilder.buildNetwork();
		
		GridFactoryFinder.createGridFactory(null).createGrid("grid", context, 
				new GridBuilderParameters<Object>(new WrapAroundBorders(), 
						new RandomGridAdder<Object>(), false, 5, 5));

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
				.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
				"coherence space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 50,
				50);
		
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numNodes = (Integer)p.getValue("numberOfNodes");
		double density = (double)p.getValue("density");
		double inhibitionRatio = (double)p.getValue("inhibitionRatio");
		String activationMode = (String)p.getValue("activationMode");
		double defaultExcitation = (double)p.getValue("defaultExcitation");
		double defaultInhibition = (double)p.getValue("defaultInhibition");
		double defaultActivation = (double)p.getValue("defaultActivation");
		double activationThreshold = (double)p.getValue("activationThreshold");
		
		
		//RunEnvironment.getInstance().endAt(100);
		
		AsynchronousUpdateGrid(context,numNodes,defaultExcitation,
				defaultExcitation, defaultInhibition, activationThreshold, density, inhibitionRatio);


		/*
		if (activationMode.compareTo("SYNCHRONOUS")==0) 
			SyncronousUpdateNet(context,numNodes,density,inhibitionRatio,
					defaultExcitation,defaultInhibition,defaultActivation,activationThreshold); 
		else AsyncronousUpdateNet(context,numNodes,density,inhibitionRatio,
				defaultExcitation,defaultInhibition,defaultActivation,activationThreshold);
		*/
		return context;
	}
	
	
	public void AsynchronousUpdateGrid(Context<Object> context, int numNodes, double defaultActivation, 
			double defaultExcitation, double defaultInhibition, double actThr, double density, double inhibitR) 

	{
		Network<CoNetNodeGrid> net = (Network<CoNetNodeGrid>)context.getProjection("coherence network");
		activations = new double[numNodes];	
		for (int i=0; i<numNodes; i++) {
			double rnd = RandomHelper.nextDoubleFromTo(0, 1);
			if (rnd > 0.1) activations[i]=defaultActivation;
			else activations[i]=1;
		}
		
		SimpleGraphView sgv = new SimpleGraphView(numNodes,actThr);
		
		CoordinatorGrid observer = new CoordinatorGrid(numNodes,actThr);
	  // Create the initial agents and add to the context.
		for(int i=0; i<numNodes; i++){
			CoNetNodeGrid x;
			if (activations[i]==1) {
				x = new CoNetNodeGrid(i,activations[i], true,  
						defaultExcitation, defaultInhibition,density,inhibitR, sgv);
				sgv.addNode(x);
			}
			else { 
				x = new CoNetNodeGrid(i,activations[i], false,  
						defaultExcitation, defaultInhibition,density, inhibitR, sgv);
				sgv.addNode(x);
			}
			context.add(x);
		}
		
		context.add(observer);
		
		sgv.initialize();
		
		sgv.display();
		context.add(sgv);
	}

	/*
	public void SyncronousUpdateNet(Context<Object> context, int numNodes, double density, 
			double inhibitionRatio, double excitationStrength, double inhibitionStrength, 
			double defaultActivation,double activationThreshold) {
		
		adjMatrix = new double[numNodes][numNodes];
		activations = new double[numNodes];
		
		for (int i=0; i<numNodes; i++) activations[i]=defaultActivation;
		activations[0]=1;
		
		for (int i=0; i<numNodes; i++) 
			for (int j=0; j<=i; j++) {
				double connection = RandomHelper.nextDoubleFromTo(0,1);
				if (connection <= (density*inhibitionRatio)) {
					adjMatrix[i][j] = inhibitionStrength;
					adjMatrix[j][i] = inhibitionStrength;
				}
				else 
				if ((connection > (density*inhibitionRatio)) & (connection <= (density))) {
						adjMatrix[i][j] =excitationStrength;
						adjMatrix[j][i] = excitationStrength;
				}
				else {
					adjMatrix[i][j] = 0;
					adjMatrix[j][i] = 0;
				}
			}
		
		for (int i=0; i<numNodes; i++) adjMatrix[i][i]=0;
		
		SimpleGraphView sgv = new SimpleGraphView(numNodes,activationThreshold); 
		
		int nodeCount=numNodes;
		CoNetNode myNodes[] = new CoNetNode[numNodes];
		Coordinator synchUpdater = new CoordinatorSynch(numNodes);
		int nCount=0;
		for (int i = 0; i < nodeCount; i++) {
			CoNetNode x;
			if (activations[i]==1) {
				x = new CoNetNode(i,activations[i], SYNCHRONOUS, true);
			}
			else { 
				x = new CoNetNode(i,activations[i], SYNCHRONOUS, false);
			}
			myNodes[nCount]=x; nCount++;
			synchUpdater.register(x);
			context.add(x);
			sgv.addNode(x);		
		}
		
			
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		
		for (int i=0; i<numNodes; i++)
			for (int j=0; j<=i; j++) {
				if (adjMatrix[i][j] != 0) {
					sgv.addEdge(i, j, adjMatrix[i][j]);
					if (activations[j]!=1.0) {
						net.addEdge(myNodes[i],myNodes[j],adjMatrix[i][j]);
						System.out.println("Inserted edge from " + myNodes[i].id + "to " + myNodes[j].id);
					}
					if (activations[i]!=1.0) {
						net.addEdge(myNodes[j],myNodes[i],adjMatrix[i][j]);
						System.out.println("Activation of node "+ myNodes[i].id + " is " + activations[i]);
						System.out.println("Inserted edge from " + myNodes[j].id + "to " + myNodes[i].id);
					}
				}			
			}
		
		for (int i=0; i<numNodes; i++) {
			CoNetNode m = myNodes[i];
			System.out.println("Node " + m.id + " has initial activation= " + m.activation);
			Iterator<RepastEdge<CoNetNode>> myInEdges= net.getInEdges(m).iterator();
			while (myInEdges.hasNext()) {
				RepastEdge<CoNetNode> e = myInEdges.next();
				CoNetNode mySource = e.getSource();
				System.out.println("flow from node "+ mySource.id + " is " + mySource.activation);
			}
		}
		
		sgv.initialize();
	
		sgv.display();
		
		
		context.add(sgv);
		context.add(synchUpdater);
	}
	
	public void AsyncronousUpdateNet(Context<Object> context, int numNodes, double density, 
			double inhibitionRatio, double excitationStrength, double inhibitionStrength, 
			double defaultActivation, double activationThreshold) {

		
		adjMatrix = new double[numNodes][numNodes];
		activations = new double[numNodes];
		
		for (int i=0; i<numNodes; i++) activations[i]=defaultActivation;
		activations[0]=1.0;
		
		for (int i=0; i<numNodes; i++) 
			for (int j=0; j<=i; j++) {
				double connection = RandomHelper.nextDoubleFromTo(0,1);
				if (connection <= density*inhibitionRatio) {
					adjMatrix[i][j] = inhibitionStrength;
					adjMatrix[j][i] = inhibitionStrength;
				}
				else 
				if ((connection > (density*inhibitionRatio)) & (connection <= (density))) {
						adjMatrix[i][j] = excitationStrength;
						adjMatrix[j][i] = excitationStrength;
				}
				else {
					adjMatrix[i][j] = 0;
					adjMatrix[j][i] = 0;
				}
			}
		
		for (int i=0; i<numNodes; i++) adjMatrix[i][i]=0;
		
		SimpleGraphView sgv = new SimpleGraphView(numNodes,activationThreshold); 
		
		Coordinator ca = new CoordinatorAsynch(numNodes);
		int nodeCount=numNodes;
		CoNetNode myNodes[] = new CoNetNode[numNodes];
		int nCount=0;
		for (int i = 0; i < nodeCount; i++) {
			CoNetNode x;
			if (activations[i]==1) {
				x = new CoNetNode(i,activations[i],ASYNCHRONOUS,true);
			}
			else {
				x=  new CoNetNode(i,activations[i],ASYNCHRONOUS,false);
			}
			myNodes[nCount]=x; nCount++;
			ca.register(x);
			context.add(x);
			sgv.addNode(x);		
		}
		
			
		Network<CoNetNode> net = (Network<CoNetNode>)context.getProjection("coherence network");
		
		for (int i=0; i<numNodes; i++)
			for (int j=0; j<=i; j++) {
				if (adjMatrix[i][j] != 0) {
					sgv.addEdge(i, j, adjMatrix[i][j]);
					if (activations[j]!=1) {
						net.addEdge(myNodes[i],myNodes[j],adjMatrix[i][j]);
					}
					if (activations[i]!=1) {
						net.addEdge(myNodes[j],myNodes[i],adjMatrix[i][j]);
					}
				}			
			}
		
		
		sgv.initialize();
	
		sgv.display();
		
		
		context.add(sgv);

		
		context.add(ca);
	}*/

}

