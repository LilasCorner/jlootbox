/**
 * 
 */
package jlootbox;

import java.util.ArrayList;
import java.util.List;

import jlootbox.Player.Manipulate;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

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
	private static Context <Object> context ;
	
	public static void init(String manipulation, Context <Object> newContext) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		context = newContext;
	}
	
	
	public static Lootbox offerLootbox(double money, Player buyer) {
		
		//check manipulations
		switch(manip) {
			case BIAS_BOX:
				
				break;
			case FAV_PLAYER:
				
				break;
		}
			
		
		//creating lootbox
		return new Lootbox(money);
	}
	
	
	/**findFavorite()
	 * loops through all players in the network to find
	 * the player with the most in-degrees
	 */
	@ScheduledMethod(start=0.95, interval=1)
	public static void findFavorite() {
		
		System.out.println("yeehaw");

		
		List<Object> players = new ArrayList<Object>();
		List<Object> favorites = new ArrayList<Object>();
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		
		Object fav = null;
		int favNodes =0;
		int count = 0;
		
		//find most popular player
		for (Object obj : net.getNodes()) {
			if(net.getInDegree(obj) > favNodes) {
				favorites.clear();
				fav =  obj;
				favNodes = net.getInDegree(fav);
			}
			
			if (net.getInDegree(obj) == favNodes){
				favorites.add(obj);
			}
		}
		
		if(favorites.size() > 0) {
			for (Object obj : net.getNodes()) {
				Player player = (Player) obj;
				Player favorite = (Player) fav;

				if( player.avgHistValue() > favorite.avgHistValue()) {
					fav = obj;
				}
			}
		}
		
		
		favorite = (Player)fav;
		
		System.out.println(favorite.toString());
		
	}
	
	
}
