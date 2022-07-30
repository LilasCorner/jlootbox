/**
 * 
 */
package jlootbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
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
	private static Iterable<Object> playerNetwork;
	public static  ArrayList<Lootbox> offers = new ArrayList<Lootbox>();

	
	public static void init(String manipulation, Context <Object> newContext, boolean noNet) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		favPlayer = ((manip == Manipulate.FAV_PLAYER) ? true : false);
		biasBox = ((manip == Manipulate.BIAS_BOX) ? true : false);
		context = newContext;
		networkPresent = noNet;
		playerNetwork = Player.net.getNodes();
	}
	
	public static Lootbox offerLootbox(Player buyer) {
		return new Lootbox(0, false, buyer.getThreshold(), buyer.avgHistPrice(), false);
	}
	
	public static Lootbox purchaseLootbox(Lootbox newBox) {
		newLoot = newBox; //inform platform which box chosen, may be useful later
		return newLoot;
	}

	public static Lootbox offerFreeLootbox(Player buyer) {
		return new Lootbox(0, false, buyer.getThreshold(), 0, false);
	}	
	
	public static Lootbox offerLimLootbox(Player buyer){
		return new Lootbox(0, true, buyer.getThreshold(), buyer.avgHistPrice(), true);
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
	
	@ScheduledMethod(start=15, interval=20)
	public static void freeBoxOn() {

		if(manip == Manipulate.FREE_BOX) {
			freeBox = true;
		}
	}
	

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
	@ScheduledMethod(start=1.2, interval=1)
	public static void findFavorite() {		
		
		if(!networkPresent || manip != Manipulate.FAV_PLAYER) {
			return;
		}
		
		List<Object> favorites = new ArrayList<Object>();
		Player player;
		Player favorite;
		int index = RandomHelper.nextIntFromTo(0, Player.allPlayers.size() - 1);
		Object fav = Player.allPlayers.get(index);
		int favNodes = Player.net.getInDegree(fav);

		
		//find most popular player
		for (Object obj : playerNetwork) {
			player = (Player) obj;
			favorite = (Player) fav;
			
			if(Player.net.getInDegree(obj) > favNodes && player.avgHistValue() > favorite.avgHistValue()) {
				favorites.clear();
				fav = obj;
				favNodes = Player.net.getInDegree(fav);
				
			}
		}
		
		favorite = (Player)fav;
			
	}
	
	/**favPlayer
	 * node with most in-degrees(most popular) gets consistently better
	 * luck than regular players
	 * @param buyer
	 * @return lootbox
	 */
	
	public static Lootbox favPlayer(Player buyer) {
		return new Lootbox(0, true, buyer.getThreshold(), buyer.avgHistPrice(), false);
	}

	/**biasedBox
	 * better connected a node is, more likely it is
	 * to pull rare loot 
	 * # connections = weighted draw
	 * @param buyer
	 * @return lootbox
	 */
	
	public static Lootbox biasedBox(Player buyer) {
		
		double diff = (Player.net.getInDegree(favorite) - Player.net.getInDegree(buyer))/100d;				
		return new Lootbox(diff, false, buyer.getThreshold(), buyer.avgHistPrice(), false);
		
	}
	/**platformResponse
	 * platform returning potential lootboxes to offer depending on player & manipualtion at play
	 * @param buyer
	 * @return arraylist of lootbox offers from platform
	 */
	
	public static ArrayList<Lootbox> platformResponse(Player buyer) {
		
		offers.clear();
		
		if(freeBox) {
			offers.add(Platform.offerFreeLootbox(buyer));
		}
		else if(limEd) {
			offers.add(Platform.offerLimLootbox(buyer));
		}
		else if(favPlayer) {
			offers.add(Platform.favPlayer(buyer));
		}
		else if(biasBox) {
			offers.add(Platform.biasedBox(buyer));
		}
		else { //platform generates regular lootbox
			offers.add(Platform.offerLootbox(buyer));
		}
		
		return offers;
	}
	
	
	/***removeOffers
	 * platform removing purchased boxes from offer list
	 * @param offers
	 * @return modified arraylist
	 */
	public static ArrayList<Lootbox> removeOffers(ArrayList<Lootbox> offers){
		
		Iterator<Lootbox> i = offers.iterator();

		while(i.hasNext()) {
		    Lootbox l = i.next();
		    if (l.getPurchased()) {
		        i.remove();
		    }
		}
		
		
		return offers;
	}
	
	

}
