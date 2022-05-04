/**
 * 
 */
package jlootbox;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

/**
 * @author Lila Zayed
 *
 */
public class Player {
	
	//private vars
	public static enum DecisionStrategy{
		   ALWAYS_BUY,
		   COIN_FLIP,
		   PRICE
		}
	
	public static enum Manipulate{
		   NONE,
		   LIM_ED,
		   FAV_PLAYER,
		   FREE_BOX,
		   BIAS_BOX
		}
	
	
	public static Manipulate manip;
	private static int changeRate = 1; 
	private static int count = 1;
	private static int MIN_RANGE = 1;
	private static int MAX_RANGE = 10;
	private static int memorySize = 5;
	private static Boolean breakTies = false;
	
	private static Uniform coinFlip = RandomHelper.createUniform(MIN_RANGE, MAX_RANGE);
	private DecisionStrategy decisionStrat;
	private static Boolean dump = false; //DEBUGGING MODE
	
	private boolean purchased = false;
	private int timeSinceLastPurchase;
	private int id;
	
	private double availableMoney;  
	private int buyThreshold;
	private Deque<Lootbox> hist = new ArrayDeque<Lootbox>();
	private Lootbox newLoot;

	
	
	//default constructor
	public Player(int availMoney, int buy, String strat) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
		this.id = ++count;
		
		decisionStrat = Enum.valueOf(Player.DecisionStrategy.class, strat); 

		
		//setting player up with free lootbox so hist.size > 1
		newLoot = new Lootbox();
		hist.addLast(newLoot); 
	}
	
	public String toString() {
		return ""+id; 
	}
	
	public static void init(String manipulation, Boolean ties) {
		manip = Enum.valueOf(Player.Manipulate.class, manipulation); 
		breakTies = ties;
	}
	
	public Player getPlayer() {
		return this;
	}
	
	public Deque<Lootbox> getHist(){
		return hist;
	}
	
	public double getMoney() {
		return availableMoney;
	}
	
	public int getThreshold() {
		return buyThreshold;
	}
	
	public boolean getPurchased() {
		return purchased;
	}
	
	public DecisionStrategy getStrat() {
		return decisionStrat;
	}
	
	public int getBuyTime() {
		return (int) (RunEnvironment.getInstance().getCurrentSchedule().getTickCount() - timeSinceLastPurchase);
	}
	
	public void setBuyTime(int time) {
		timeSinceLastPurchase = time;
	}
	
	public void setHist(Deque<Lootbox> newHist){
		hist = newHist;
	}
		
	public void setMoney(double money) {
		availableMoney = money;
	}
	
	public void setThreshold(int i) {
		if(i > 10) {
			buyThreshold = 10;
		}
		else if(i < 1) {
			buyThreshold = 1;
		}
		else {
			buyThreshold = i;
		}
	}
	
	public void addThreshold() {
			setThreshold(getThreshold() + changeRate);
		
	}
	
	public void subtractThreshold() {
			setThreshold(getThreshold() - changeRate);
		
	}




	/** moneySpent()
	 *  
	 *  returns money spent on newest lootbox
	 *  
	 * @return int price of last lootbox
	 */
	public double moneySpent() {
		if(purchased) {
			return hist.peek().getPrice();
		}
		
		return 0;
	}

	
	/**priceHistValue()
	 * if we get a good return on our cash investment
	 * overall ($ spent is < cash we have available), 
	 * we decide to buy again. otherwise no sale
	 * @return true if cash spent < availableMoney, false for reverse
	 */
	public boolean priceHistValue() {
		
		//buy again if we've been getting good return on investment
		if(avgHistValue() >= avgHistPrice()) {
			return true;
		}
		else {//else it's up to (small)chance
			if(coinFlip.nextInt() + changeRate <= buyThreshold) {
				return true;
			}
			return false;
		}
		
	}
	
	
	public double avgHistValue() {
		double ownAvg = 0;
		
		for (Iterator<Lootbox> itr = hist.iterator(); itr.hasNext();) {
            ownAvg += itr.next().getRarity();
        }
		
		ownAvg /= hist.size();
		
		return ownAvg;
	}
	
	public double avgHistPrice() {
		double ownAvg = 0;
		
		for (Iterator<Lootbox> itr = hist.iterator(); itr.hasNext();) {
            ownAvg += itr.next().getPrice();
        }
		
		ownAvg /= hist.size() ;
		
		return ownAvg;
	}
	
	/** recordNewLootboxInHistory()
	 * 
	 *  push new lootbox onto history and 
	 *  keep size of deque manageable
	 */
	protected void recordNewLootboxInHistory(){
		
		hist.addLast(newLoot);
		
		if(hist.size() > memorySize) { 
			hist.removeFirst();
		}
	}
	
	
	/** buyNewLootbox()
	 * 
	 * create new lootbox object
	 * 
	 * @return newly generated lootbox newLoot
	 */
	protected Lootbox buyNewLootbox() {
		return Platform.offerLootbox(((buyThreshold / 100d) * getMoney()), this);
	}
	
	protected Lootbox giveFreeBox() {
		return Platform.offerLootbox(0, this);
	}
	
	
	/** updateThreshold()
	 * 
	 * Increase/decrease the buyThreshold 
	 * 
	 * If previous loot value > current loot value, reduce threshold 
	 * and if previous loot < current loot, increase threshold
	 * 
	 * @return int new buyThreshold
	 */
	protected int updateThreshold() {
		
		switch(decisionStrat) {
			
			case PRICE:{ 
					double oldVal;
					double newVal;
					
					if(hist.peek().getPrice() == 0 || hist.peek().getRarity() == 0) {
						oldVal = 0;
					}
					else {
						oldVal =  hist.peek().getPrice()/ hist.peek().getRarity();
					}
				
					 
					
					if(newLoot.getPrice() == 0 || newLoot.getRarity() == 0) {
						newVal = 0;
					} 
					else {
						newVal = newLoot.getPrice()/newLoot.getRarity();
					}
						
					//lower # means better return on investment
					if (oldVal < newVal) {
						subtractThreshold();				
					}
					else {
						addThreshold();
					}
		
				break;
			}
	
			default:{
				
			
					//old box better than new one, less likely to buy
					if(hist.peek().getRarity() > newLoot.getRarity()) { 
						subtractThreshold();
					}
					else { //new box better than old, more likely to buy
						addThreshold();
					}		
	
				
				
			}
			
		}

		return buyThreshold;
	}
	
	
	/** decide()
	 * Player buying decision structure
	 * @return
	 */
	protected Boolean decide() {
		
		switch(decisionStrat) {
		
			case ALWAYS_BUY:{ 
				return true;
			}
			
			case COIN_FLIP:{ 
				
				if(coinFlip.nextInt() <= buyThreshold) { 
					return true;
				}
				return false;
			}
			
			case PRICE:{ 

				return priceHistValue();
			}

			default:{
				 return false; //impossible
			}
		}
		
		

	}
	
	

	protected void infoDump(Boolean buy) {
		
		System.out.println(decisionStrat);
		
		if(buy) {
			System.out.println("++++BUY++++");
			System.out.println("Old Loot Val: " + hist.peek().getRarity());
			System.out.println("New Loot Val: " + newLoot.getRarity());
			if(decisionStrat == DecisionStrategy.PRICE) {
				System.out.println("Price: " + newLoot.getPrice());
			}
			System.out.println("BuyThreshold: " + getThreshold());

		}
		else {
			System.out.println("----NO BUY----");
		}

	}
	
	
	/** askOtherPlayer()
	 * If player doesn't buy lootbox, they 
	 * look at other players to decide if 
	 * they should buy more, or fewer boxes
	 * 
	 * weight of their connection with another
	 * player increased/decreased depending on
	 * if the other player has better or worse loot
	 */
	protected void askOtherPlayer() {
		
		List<Object> players = new ArrayList<Object>();
		Context <Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		
		
		//grab all players this player looks up to
		for (Object obj : net.getSuccessors(this)) {
			players.add(obj);
		}
		
		//no friends ;-; choose anyone
		if(players.size() < 1) {
			players = soloPlayer();
		}

		//choose random player from that array
		int index = RandomHelper.nextIntFromTo(0, players.size() - 1);
		
		Player otherPlayer = (Player) players.get(index);
		
		compare(otherPlayer);		
				
	}
	
	/**soloPlayer()
	 * If a player has no edges, choose a node
	 * in the network at random and create an edge
	 * 
	 * @return List<Object> player - the list of player edges the current agent has
	 */
	public List<Object> soloPlayer() {
		List<Object> allPlayers = new ArrayList<Object>();
		List<Object> player = new ArrayList<Object>();
		Context <Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		
		
		for (Object obj : net.getNodes()) {
			if((Player)obj != this) {
				allPlayers.add(obj);
			}
		}
		
		//choose random player from that array
		int index = RandomHelper.nextIntFromTo(0, allPlayers.size() - 1);
		Player otherPlayer = (Player) allPlayers.get(index);
		RepastEdge<Object> friendEdge = net.getEdge(this, otherPlayer); //will == null if dne
		player.add(otherPlayer);
		friendEdge = net.addEdge(this, otherPlayer, 0.0);
		
		return player;
		
	}
	
	
	/**compare(Deque<Lootbox> otherLoot) 
	 * 
	 * if other player has better history, add changeRate to buyThreshold
	 * if other player is worse off, subtract changeRate from buyThreshold
	 * 
	 * @param otherLoot, lootbox history of another player
	 */
	private void compare(Player otherPlayer) {
		
		Context <Object> context = ContextUtils.getContext(this);
		Network<Object> net = (Network<Object>)context.getProjection("player network");
		RepastEdge<Object> friendEdge = net.getEdge(this, otherPlayer); //will == null if dne

		double ownAvg = avgHistValue();
		double otherAvg = otherPlayer.avgHistValue();
		if(otherAvg > ownAvg) {
			
			//stronger friendship = stronger influence 
			for(double i = 0; i < friendEdge.getWeight(); i++) {
				addThreshold();
			}

			
			friendEdge.setWeight(friendEdge.getWeight() + changeRate); 
			
		}
		else if (otherAvg < ownAvg) {
			
			//stronger friendship = stronger influence 
			for(double i = 0; i < friendEdge.getWeight(); i++) {
				subtractThreshold();
			}
			
			friendEdge.setWeight(friendEdge.getWeight() - changeRate); 

			if (friendEdge.getWeight() < 0 && breakTies) {
				net.removeEdge(friendEdge);
			}
		}
			
	}

	


	
	
	// give players a free box every 10 ticks
	// out of pure generosity :-)
	@ScheduledMethod(start=15, interval=20)
	public void freeBox() {
		if(manip == Manipulate.FREE_BOX) {
			giveFreeBox();
			updateThreshold();
		}

	}
	
	
	//every 50 ticks, start limited edition event
	//TODO: closer timer gets to end of event, more likely
	//players are to buy? 
	@ScheduledMethod(start=50, interval=50)
	public void limEdition() {
		if(manip == Manipulate.LIM_ED) {
			setThreshold(getThreshold() * 2); 
			
		}
		
	}
	
	
	/** step()
	 * Every tick, determine if player wants to buy a new box, 
	 * and if that new box + or - their likelyhood to buy in future
	 */
	@ScheduledMethod(start=1.1, interval=1)
	public void step() {

//		Below is how to keep model updated with params from context mid-run
//		Parameters params = RunEnvironment.getInstance().getParameters();

		
		if(decide()) {

			purchased = true;
			
			setBuyTime((int) (RunEnvironment.getInstance().getCurrentSchedule().getTickCount()));
		
			newLoot = buyNewLootbox();
			
			
			if(dump) {
				infoDump(true);
			}
			
			updateThreshold();

			recordNewLootboxInHistory();

			
		}		
		else {
			//if we didnt buy, we look at other players
			//and edit buyThreshold accordingly
			purchased = false;
						
			askOtherPlayer();
			
			if(dump) {
				infoDump(false);
			}
			
			

		}
		
		
		
	}



	
}
