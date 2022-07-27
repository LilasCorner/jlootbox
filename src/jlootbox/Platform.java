/**
 * 
 */
package jlootbox;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;

/**
 * @author Lilo
 *
 */
public class Platform {

	public static enum Manipulate{
		   NONE,
		   LIM_ED,
		   FAV_PLAYER,
		   FREE_BOX,
		   BIAS_BOX
		}
	
	public static Manipulate manip;
	public static Player favorite = null;
	public static Boolean limEd = false;
	public static Boolean freeBox = false;
	public static Boolean networkPresent = false; 
	private static Context <Object> context;
	private Lootbox newLoot;
	
	public static void init(String manipulation, Context <Object> newContext, Boolean noNet) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		context = newContext;
		networkPresent = noNet;
	}
	
	
	public static Lootbox offerLootbox(Player buyer) {

		//check manipulations
		if(networkPresent) {
			switch(manip) {
				case BIAS_BOX:
					return biasedBox(buyer);
				
				case FAV_PLAYER:
					if (favorite == buyer) {
						return favPlayer(buyer);
					}
					
				case FREE_BOX:
					return new Lootbox(buyer.getThreshold(), 0);
					
				default:
					break;
			}
		}

			
		
		//creating lootbox
		return new Lootbox(buyer.getThreshold(), buyer.avgHistPrice());
	}
	
	@ScheduledMethod(start=50, interval=50)
	public static void limEdOn() {
		if(manip == Manipulate.LIM_ED) {
			limEd = true;
		}
	}
	
	@ScheduledMethod(start=60, interval=50)
	public static void limEdOff() {
		if(manip == Manipulate.LIM_ED) {
			limEd = false;
		}
	}
	
	// give players a free box every 10 ticks
	// out of pure generosity :-)
	@ScheduledMethod(start=15, interval=20)
	public static void freeBoxOn() {
		if(manip == Manipulate.FREE_BOX) {
			freeBox = true;
		}
	}
	
	// give players a free box every 10 ticks
	// out of pure generosity :-)
	@ScheduledMethod(start=16, interval=20)
	public static void freeBoxOff() {
		if(manip == Manipulate.FREE_BOX) {
			freeBox = false;
		}
	}
	
	/**findFavorite()
	 * loops through all players in the network to find
	 * the player with the most in-degrees
	 */
	@ScheduledMethod(start=0.95, interval=1)
	public static void findFavorite() {
		
		if(!networkPresent) {
			return;
		}
		
		List<Object> favorites = new ArrayList<Object>();
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		
		Object fav = null;
		int favNodes =0;
		
		//find most popular player
		for (Object obj : net.getNodes()) {
			if(net.getInDegree(obj) > favNodes) {
				favorites.clear();
				fav =  obj;
				favNodes = net.getInDegree(fav);
//				System.out.println("NodeCount:" + favNodes);

			}
			
			if (net.getInDegree(obj) == favNodes && obj != fav){
				favorites.add(obj);
			}
		}
		
//		System.out.println("Current Fav:" + fav);
//		System.out.println("Opponent array: " + favorites);
//		
		if(favorites.size() > 0) {
			for (Object obj : favorites) {
				Player player = (Player) obj;
				Player favorite = (Player) fav;

				if( player.avgHistValue() > favorite.avgHistValue()) {
//					
					fav = obj;
				}
			}
			
//			System.out.println("New Fav:" + fav);
		}
		
		
		favorite = (Player)fav;
		
//		System.out.println(favorite.toString());
		
	}
	
	
	//node with most in-degrees(most popular) gets consistently better
	//luck than regular players
	public static Lootbox favPlayer(Player buyer) {
		return new Lootbox(0, true, buyer.getThreshold(), buyer.avgHistPrice());
	}

	//better connected a node is, more likely it is
	//to pull rare loot
	//# connections = weighted draw
	public static Lootbox biasedBox(Player buyer) {
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		double diff = (net.getInDegree(favorite) - net.getInDegree(buyer))/100d;				
		
		return new Lootbox(diff, false, buyer.getThreshold(), buyer.avgHistPrice());
		
	}


}
