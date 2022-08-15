/**
 * 
 */
package jlootbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

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
	private static int lowestRank = 50; // lowest 50th percentile
	private static Iterable<Object> playerNetwork;
	public static ArrayList<Lootbox> offers = new ArrayList<Lootbox>();
	private static List<Double> freqList = new ArrayList<Double>();
	private static List<Double> luckList = new ArrayList<Double>();
	
	
	public static void init(String manipulation, Context <Object> newContext, boolean noNet) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		favPlayer = ((manip == Manipulate.FAV_PLAYER) ? true : false);
		biasBox = ((manip == Manipulate.BIAS_BOX) ? true : false);
		freeBox = ((manip == Manipulate.FREE_BOX) ? true : false);
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
	
	//platform should have option to return null to target specific players
	//target players in lowest 50 percentile (in luck and or purchase freq, whichever lower)
	//and offer box
	public static Lootbox offerFreeLootbox(Player buyer) {
		System.out.println("Calling offerFreeLootbox");
		System.out.println(buyer.toString() + ": is rank = " + getRank(buyer));

		if (getRank(buyer) <= lowestRank) return new Lootbox(0, false, buyer.getThreshold(), 0, false);
		System.out.println("No Loot Offered");
		return null;
		
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

	
	/**compilePlayerInfo()
	 * loops through all players in the network to find
	 * the player with the most in-degrees
	 */
	@ScheduledMethod(start=0.8, interval=1)
	public static void compilePlayerInfo() {		
		
		if(freeBox) compileArrays();
		
		
		if(favPlayer || biasBox) {
			List<Object> favorites = new ArrayList<Object>();
			Player player;
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
		
		return;
			
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
				
		int fav = Player.net.getInDegree(favorite);
		int player = Player.net.getInDegree(buyer);
		
		double diff = (fav - player)/100d;				
		return new Lootbox(diff, false, buyer.getThreshold(), buyer.avgHistPrice(), false);
		
	}
	/**platformResponse
	 * platform returning potential lootboxes to offer depending on player & manipualtion at play
	 * @param buyer
	 * @return arraylist of lootbox offers from platform
	 */
	
	public static ArrayList<Lootbox> platformResponse(Player buyer) {
		
		offers.clear();

		if(favPlayer) {
			offers.add(Platform.favPlayer(buyer));
		}
		else if(limEd) {
			offers.add(Platform.offerLimLootbox(buyer));
		}
		else if(biasBox) {
			offers.add(Platform.biasedBox(buyer));
		}
		else if(freeBox) { 
			// target poor players! 
			// current implementation: if player is in lowest 50th percentile
			// in terms of luck/purchase freq, whichevers lower, 
			// we offer box
			
			System.out.println("====================");

			Lootbox newBox = Platform.offerFreeLootbox(buyer);
			
			if(newBox != null) { 
				System.out.println("Offering lootbox!");
				offers.add(newBox);
			}
			
			offers.add(Platform.offerLootbox(buyer));
		}
		else { //platform generates regular lootbox
			offers.add(Platform.offerLootbox(buyer));
		}
		
		System.out.println("====================");

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
	
	 
	private static double getRank(Player player) {
		double freq = calcPercentile(freqList, player.getBuyTime());
		double luck = calcPercentile(luckList, player.avgHistValue());
		if(freq < luck) return freq;	
		return luck;
	}



	public static void compileArrays() {
		 
		for(Object it: playerNetwork) {
			Player temp =  (Player) it;
			freqList.add((double) temp.getBuyTime());
			luckList.add(temp.avgHistValue());
		}
		
	    Collections.sort(freqList);
	    Collections.sort(luckList);
	}
	
	public static double calcPercentile (List<Double> details, double percentile) {
	    int index = (int) Math.ceil(percentile / 100.0 * details.size());
	    System.out.println("index = " + index );
	    return index;
   }


	

}
