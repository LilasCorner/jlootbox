/**
 * 
 */
package jlootbox;

import java.util.ArrayList;
import java.util.List;

import jlootbox.Player.DecisionStrategy;
import jlootbox.Player.Manipulate;
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
	private static Context <Object> context ;
	
	public static void init(String manipulation, Context <Object> newContext) {
		manip = Enum.valueOf(Platform.Manipulate.class, manipulation); 
		context = newContext;
	}
	
	
	public static Lootbox offerLootbox(double money, Player buyer) {
		
		//check manipulations
		switch(manip) {
			case BIAS_BOX:
				return biasedBox(money, buyer);
			
			case FAV_PLAYER:
				if (favorite == buyer) {
					return favPlayer(money, buyer);
				}
				break;
				
			default:
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
				System.out.println("NodeCount:" + favNodes);

			}
			
			if (net.getInDegree(obj) == favNodes && obj != fav){
				favorites.add(obj);
			}
		}
		
		System.out.println("Current Fav:" + fav);
		System.out.println("Opponent array: " + favorites);
		
		if(favorites.size() > 0) {
			for (Object obj : favorites) {
				Player player = (Player) obj;
				Player favorite = (Player) fav;

				if( player.avgHistValue() > favorite.avgHistValue()) {
					System.out.println("Opponent Up!");
					System.out.println("Opp-"+ player + ": " + player.avgHistValue() + "\nFav-" + fav + ": " + favorite.avgHistValue());
					fav = obj;
				}
			}
			
			System.out.println("New Fav:" + fav);
		}
		
		
		favorite = (Player)fav;
		
//		System.out.println(favorite.toString());
		
	}
	
	
	//node with most in-degrees(most popular) gets consistently better
	//luck than regular players
	public static Lootbox favPlayer(double money, Player buyer) {
		double price =  ((buyer.getThreshold() / 100d) * buyer.getMoney());
		return new Lootbox(price, 0, true);
	}

	//better connected a node is, more likely it is
	//to pull rare loot
	//# connections = weighted draw
	public static Lootbox biasedBox(double money, Player buyer) {
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		double diff = (net.getInDegree(favorite) - net.getInDegree(buyer))/100d;				
		double price = ((buyer.getThreshold() / 100d) * buyer.getMoney());
		
		return new Lootbox(price, diff, false);
		
	}


}
