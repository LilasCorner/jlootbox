/**
 * 
 */
package jlootbox;



import java.util.ArrayList;
import java.util.ListIterator;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.Lattice2DGenerator;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.RandomDensityGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;

/**
 * @author Lila Zayed
 *
 */
public class JLootboxBuilder implements ContextBuilder<Object> {
	
	public static double INIT_PROB = 0.5;
	public static int INIT_MONEY = 100;
	

	public void validate(double value, double lowerBound, double upperBound, String msg) {
		
		//Lattice
		if(lowerBound == -1 && upperBound == -1) {
			int sqrt = (int) Math.sqrt(value);
		
			if((sqrt * sqrt) != value) {
				throw new IllegalArgumentException(msg);
			}
		} //NWDegree
		else if(lowerBound == 0 && upperBound == 0) {
			
			if(value % 2 != 0) {
			
				throw new IllegalArgumentException(msg);
			}
		} //Everything else
		else if(value < lowerBound || value > upperBound) {
			throw new IllegalArgumentException(msg);
		}
	}
	
	@Override
	public Context build(Context<Object> context) {

		Parameters params = RunEnvironment.getInstance().getParameters();

		String manip = params.getString("manip");
		String network = params.getString("network");
		String strat = params.getString("strat");
		Boolean breakTies = params.getBoolean("breakTies");
		Boolean networkPresent = params.getBoolean("networkPresent");
		int stopTime = params.getInteger("stopTime");
		int playerCount = params.getInteger("numPlayers");
		int memorySize = params.getInteger("memorySize");
		

		//network params
		double nwbeta = params.getDouble("NWBeta"); //Watts beta: probability of edge being rewired. Must be btwn 0-1
		int nwdegree = params.getInteger("NWDegree"); // Watts degree: # edges connected to each vertex in neighborhood. Must be even #
		Boolean nwsymm = params.getBoolean("NWSym"); // Watts symmetry: generated edges symmetrical t/f
		double nrdensity = params.getDouble("NRDensity"); // Random density: the approximate density of the network. Must be btwn 0-1
		Boolean nrloop = params.getBoolean("NRLoop"); // Random self loops: are self loops allowed t/f
		Boolean nrsymm = params.getBoolean("NRSym"); //Random symmetry: generated edges symmetrical/bidirectional t/f
		Boolean nltoroid = params.getBoolean("NLToroidal"); //Lattice Toroidal: whether lattice is toroidal t/f
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("player network", context, true);
		
		context.setId("jlootbox");
	
		if(networkPresent) { 
			
			//networks present, validate params
			validate(nwbeta, 0, 1, "NWBeta must be between 0 and 1. Please re-initialize");
			validate(nwdegree, 0, 0, "NWDegree must be an even number. Please re-initialize");
			validate(nrdensity, 0, 1,"NRDensity must be between 0 and 1. Please re-initialize");
			validate(playerCount, 9, 99999, "Please re-initialize the model with > 9 players to create the network." );
			if(network.equals("LATTICE")) {
				validate (playerCount, -1, -1, "For Lattice networks, player # must be a perfect square. Please re-initialize");
			}
			
			//create corresponding network
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
			
		}
		
		
		
		
		//create players
		int money = INIT_MONEY;
		double buy = INIT_PROB;
		Player tempPlayer;
		ArrayList<Player> tempList = new ArrayList<Player>();
		
		for (int i =0; i < playerCount; i++) {
			tempPlayer = new Player (money, buy, strat);
			tempList.add(tempPlayer);
			context.add(tempPlayer);
		}
		
		netBuilder.buildNetwork();
		
		//manually add symmetrical edges
		if(playerCount > 1 && !networkPresent) {
			
			Network<Object> net = (Network<Object>)context.getProjection("player network");
			ListIterator<Player> it = tempList.listIterator();
			
			Player p1 = it.next();
			Player p2;
			
			while (it.hasNext())
			{
			    p2 = it.next();
			    
			    // Process p1 and p2
			    net.addEdge(p1, p2);
			    net.addEdge(p2, p1);
			    
			    // Maintain previous player?
			    p2 = p1;
			}
			
		}

		
		Player.init(manip, breakTies);
		Platform.init(manip, context, networkPresent);
		RunEnvironment.getInstance().endAt(stopTime);
		
		
		
		return context;
	}

}
