/**
 * 
 */
package jlootbox;



import repast.simphony.context.Context;
import java.lang.Math;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.Lattice2DGenerator;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.RandomDensityGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * @author Lila Zayed
 *
 */
public class JLootboxBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {

		Parameters params = RunEnvironment.getInstance().getParameters();

		String manip = params.getString("manip");
		String network = params.getString("network");
		String strat = params.getString("strat");
		Boolean breakTies = params.getBoolean("breakTies");
		int stopTime = params.getInteger("stopTime");
		int playerCount = params.getInteger("numPlayers");
		int memorySize = params.getInteger("memorySize");
		int sqrt = (int) Math.sqrt(playerCount);

		//network params
		double nwbeta = params.getDouble("NWBeta"); //Watts beta: probability of edge being rewired. Must be btwn 0-1
		int nwdegree = params.getInteger("NWDegree"); // Watts degree: # edges connected to each vertex in neighborhood. Must be even #
		Boolean nwsymm = params.getBoolean("NWSym"); // Watts symmetry: generated edges symmetrical t/f
		double nrdensity = params.getDouble("NRDensity"); // Random density: the approximate density of the network. Must be btwn 0-1
		Boolean nrloop = params.getBoolean("NRLoop"); // Random self loops: are self loops allowed t/f
		Boolean nrsymm = params.getBoolean("NRSym"); //Random symmetry: generated edges symmetrical/bidirectional t/f
		Boolean nltoroid = params.getBoolean("NLToroidal"); //Lattice Toroidal: whether lattice is toroidal t/f
		
		
		//validation method here
		
		//TODO: this is so ugly ;-; switch statement perhaps
		if(nwbeta > 1 || nwbeta < 0) {
			throw new IllegalArgumentException("NWBeta must be between 0 and 1. Please re-initialize");
		}
		if(nwdegree % 2 != 0) {
			throw new IllegalArgumentException("NWDegree must be an even number. Please re-initialize");
		}
		if(nrdensity > 1 || nrdensity < 0) {
			throw new IllegalArgumentException("NRDensity must be between 0 and 1. Please re-initialize");
		}
		if(playerCount < 10) {
			throw new IllegalArgumentException("Please re-initialize the model with > 10 players to create the network.");
		}
		if(network.equals("LATTICE") && (sqrt * sqrt) != playerCount) {
			throw new IllegalArgumentException("For Lattice networks, player # must be a perfect square. Please re-initialize");
		}
		
		
		
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("player network", context, true);

		context.setId("jlootbox");
        
		int money = 100;
		int buy = 5;
		
		for (int i =0; i < playerCount; i++) {
			context.add(new Player (money, buy, strat));
		}
		
		
		switch(network) {
			case "WATTS":
				WattsBetaSmallWorldGenerator<Object> watgen = new WattsBetaSmallWorldGenerator<Object>(nwbeta,  
						nwdegree, nwsymm);
				netBuilder.setGenerator(watgen);
				break;
			
			case "RANDOM":
				RandomDensityGenerator<Object> randgen = new RandomDensityGenerator<Object>(nrdensity, 
						nrloop, nrsymm);
				netBuilder.setGenerator(randgen);
				break;
			
			case "LATTICE":

				Lattice2DGenerator<Object> latgen = new Lattice2DGenerator<Object>(nltoroid);
				netBuilder.setGenerator(latgen);
				
				break;

			default:
				WattsBetaSmallWorldGenerator<Object> defgen = new WattsBetaSmallWorldGenerator<Object>(nwbeta,  
						nwdegree, nwsymm);
				netBuilder.setGenerator(defgen);
		}
		
		boolean keepGoin = true;
		
		while(keepGoin) {
			try {
				keepGoin = true;
				netBuilder.buildNetwork();
				keepGoin = false;
			}
			catch(Exception e) {
				//delete all edges in network, mayb loop & delete successors
				
				context.add(new Player (money, buy,  strat));
			}
		}
		

		
		Player.init(manip, breakTies);
		Platform.init(manip, context);
		RunEnvironment.getInstance().endAt(stopTime);
		
		
		
		return context;
	}

}
