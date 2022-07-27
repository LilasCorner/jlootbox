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
	public static boolean limEd = false;
	public static boolean freeBox = false;
	public static boolean favPlayer = false;
	public static boolean biasBox = false; 
	public static boolean networkPresent = false; 
	private static Context <Object> context;
	private static Lootbox newLoot;
	private static Iterable<Object> playerNetwork = Player.net.getNodes();
	
	public static void init(String manipulation, Context <Object> newContext, boolean noNet) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		favPlayer = ((manip == Manipulate.FAV_PLAYER) ? true : false);
		biasBox = ((manip == Manipulate.BIAS_BOX) ? true : false);
		context = newContext;
		networkPresent = noNet;
	}
	
	public static double getAskingPrice() {
		return newLoot.getPrice();
	}
	
	public static void offerLootbox(Player buyer) {
		newLoot = new Lootbox(0, false, buyer.getThreshold(), buyer.avgHistPrice());
	}
	
	public static Lootbox purchaseLootbox() {
		return newLoot;
	}

	public static Lootbox offerFreeLootbox(Player buyer) {
		return new Lootbox(0, false, buyer.getThreshold(), 0);
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
	public void findFavorite() {
		
		if(!networkPresent) {
			return;
		}
		
		List<Object> favorites = new ArrayList<Object>();
		
		Object fav = null;
		int favNodes =0;
		
		//find most popular player
		for (Object obj : playerNetwork) {
			if(Player.net.getInDegree(obj) > favNodes) {
				favorites.clear();
				fav =  obj;
				favNodes = Player.net.getInDegree(fav);

			}
			
			if (Player.net.getInDegree(obj) == favNodes && obj != fav){
				if( (((Player) obj).avgHistValue()) > favorite.avgHistValue()) {
					fav = obj;
				}
			}
		}
		
		favorite = (Player)fav;
//		System.out.println("Current Fav: " + favorite.toString());
			
	}
	
	
	//node with most in-degrees(most popular) gets consistently better
	//luck than regular players
	public static void favPlayer(Player buyer) {
		newLoot = new Lootbox(0, true, buyer.getThreshold(), buyer.avgHistPrice());
	}

	//better connected a node is, more likely it is
	//to pull rare loot
	//# connections = weighted draw
	public static void biasedBox(Player buyer) {
		
		double diff = (Player.net.getInDegree(favorite) - Player.net.getInDegree(buyer))/100d;				
		newLoot = new Lootbox(diff, false, buyer.getThreshold(), buyer.avgHistPrice());
		
	}


}
