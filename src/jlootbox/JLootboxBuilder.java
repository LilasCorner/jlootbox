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

		//validation block here!!!
		
		double nwbeta = params.getDouble("NWBeta"); //definition
		
		//validating w/ comment abt valid ranges sos agents 
//		if( ) {
//			
//		}
		
		
		
		
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("player network", context, true);

		
		context.setId("jlootbox");
		
	

		int playerCount = params.getInteger("numPlayers");
		int sqrt = (int) Math.sqrt(playerCount);
		
		if(playerCount < 10) {
			throw new IllegalArgumentException("Please re-initialize the model with > 10 players to create the network.");
		}
		
		//check that number is perfect square for lattice - temp implementation
		if(params.getString("network").equals("LATTICE") && (sqrt * sqrt) != playerCount) {
			throw new IllegalArgumentException("For Lattice networks, player # must be a perfect square. Please re-initialize");
		}
		
		String strat = params.getString("strat");
		
//		String[] temp = strat.split(",");
//        int[] ratios = new int[temp.length];
//        
//
//        for (int i = 0; i < temp.length; i++) {
//        	ratios[i] = Integer.parseInt(temp[i]);
//        }
        
        
		int money = 100;
		int buy = 5;
		
		for (int i =0; i < playerCount; i++) {
			context.add(new Player (money, buy, strat));
		}
		
		

		switch(params.getString("network")) {
			case "WATTS":
				WattsBetaSmallWorldGenerator<Object> watgen = new WattsBetaSmallWorldGenerator<Object>(nwbeta,  
						params.getInteger("NWDegree"), params.getBoolean("NWSym"));
				netBuilder.setGenerator(watgen);
				break;
			
			case "RANDOM":
				RandomDensityGenerator<Object> randgen = new RandomDensityGenerator<Object>(params.getDouble("NRDensity"), 
						params.getBoolean("NRLoop"), params.getBoolean("NRSym"));
				netBuilder.setGenerator(randgen);
				break;
			
			case "LATTICE":

				Lattice2DGenerator<Object> latgen = new Lattice2DGenerator<Object>(params.getBoolean("NLToroidal"));
				netBuilder.setGenerator(latgen);
				
				break;

			default:
				WattsBetaSmallWorldGenerator<Object> defgen = new WattsBetaSmallWorldGenerator<Object>(.3,  4, true);
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
	
		
		Player.init(params.getString("manip"), params.getBoolean("breakTies"));
		
		RunEnvironment.getInstance().endAt(params.getInteger("stopTime"));
		
		
		
		return context;
	}

}
