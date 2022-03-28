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
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

/**
 * @author Lilo
 *
 */
public class Player {
	
	//private vars
	public static enum DecisionStrategy{
		   ALWAYS_BUY,
		   COIN_FLIP,
		   PRICE
		}
	
	public static int MIN_RANGE = 1;
	public static int MAX_RANGE = 10;

	
	private static Uniform coinFlip = RandomHelper.createUniform(MIN_RANGE, MAX_RANGE);
	private DecisionStrategy decisionStrat;
	private static Boolean dump = true;
	
	private boolean purchased = false;
	private int changeRate = 1; 
	private int availableMoney;  
	private int buyThreshold;
	private Deque<Lootbox> hist = new ArrayDeque<Lootbox>();
	private Lootbox newLoot;
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	//default constructor
	public Player(int availMoney, int buy, ContinuousSpace <Object> space , Grid <Object> grid, String strat) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
		this.space = space;
		this.grid = grid;
		
		decisionStrat = Enum.valueOf(Player.DecisionStrategy.class, strat); 

		
		//setting player up with free lootbox so hist.size > 1
		newLoot = new Lootbox();
		hist.addLast(newLoot); 
	}
	

	
	/** getHist()
	 *  returns player's lootbox history
	 */
	public Deque<Lootbox> getHist(){
		return hist;
	}
	
	/** getThreshold()
	 * 
	 * @return the Player's buyThreshold
	 */
	public int getThreshold() {
		return buyThreshold;
	}
	
	public void addThreshold() {
		if(rangeCheck(buyThreshold)) {
			buyThreshold += changeRate;
		}
	}
	
	public void subtractThreshold() {
		if(rangeCheck(buyThreshold)) {
			buyThreshold += -1 * changeRate;
		}
	}

	/**getMoney()
	 * 
	 * @return availableMoney
	 */
	public int getMoney() {
		return availableMoney;
	}
	
	
	/**setMoney(int money)
	 * 
	 * set availableMoney to money
	 * 
	 * @param money
	 */
	public void setMoney(int money) {
		availableMoney = money;
	}
	
	
	/** moneySpent()
	 *  
	 *  returns money spent on newest lootbox
	 *  
	 * @return int price of last lootbox
	 */
	public int moneySpent() {
		if(purchased) {
			return hist.peek().getPrice();
		}
		
		return 0;
	}

	
	/** recordNewLootboxInHistory()
	 * 
	 *  push new lootbox onto history and 
	 *  keep size of deque manageable
	 */
	protected void recordNewLootboxInHistory(){
		
		hist.addLast(newLoot);
		
		if(hist.size() > 5) { 
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

		switch(decisionStrat) {
		
			case ALWAYS_BUY:{ 
				
				newLoot = new Lootbox();
				return newLoot;
			}
			
			case COIN_FLIP:{ 
	
				newLoot = new Lootbox();
				return newLoot;
				
			}
			
			case PRICE:{ 
				newLoot = new Lootbox( (buyThreshold / 100) * getMoney());
				return newLoot;
			}
	
			default:
				newLoot = new Lootbox();
				return newLoot;
			
		}
		
		
	}
	
	/**rangeCheck(int num)
	 * 
	 * verifies that increasing/decreasing the passed
	 * number will not put it out of bounds (0-10)
	 * 
	 * @param num - the number we want to be within range
	 * @return true if operation is within range, false if not
	 */
	public Boolean rangeCheck(int num) {
		
		if(num + (-1 * changeRate) > MIN_RANGE-1 && num + changeRate < MAX_RANGE) {
			return true;
		}
		
		return false;
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
				//if loot worse than price paid for it, less likely to buy later
				if(newLoot.getRarity() < newLoot.getPrice() ) {
					subtractThreshold();				
					}
				else {
					addThreshold();
				}
			
				
				
				break;
			}
	
			default:{
				
				if(rangeCheck(buyThreshold)) {
					//old box better than new one, less likely to buy
					if(hist.peek().getRarity() > newLoot.getRarity()) { 
						subtractThreshold();
					}
					else { //new box better than old, more likely to buy
						addThreshold();
					}		
	
				}
				
			}
			
		}

		return buyThreshold;
	}
	
	
	/** move()
	 * 
	 * Calculates displacement for Player and moves them
	 * to new location
	 * 
	 * Only runs when lootbox is purchased - player holds still
	 * when no purchase is made
	 * 
	 * @return true for positive displacement, false for negative
	 */
	protected Boolean move() {
		
		
		//temp implementation
		NdPoint myPoint = space.getLocation (this);
		int disp = hist.peekLast().getRarity() - hist.peekFirst().getRarity();
		
		space.moveByDisplacement(this, 1, disp); //x, y displacement
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		
		
		return (disp > 0); 
		
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
				//buyThreshold= % of available money we're willing to spend
				//that amount has to be above the minimum price of a lootbox for
				//player to buy
				if((getThreshold()/100) * getMoney() > Lootbox.MIN_PRICE){
					return true;
				}
				
				return false;
			}

			default:{
				 return false; //impossible
			}
		}
		
		

	}
	
	
	/** infoDump(Boolean Buy)
	 * print out of a player's internal variables
	 * buy = true allows us to print details of transaction, 
	 * buy = false prints NO BUY
	 * @return void
	 */
	protected void infoDump(Boolean buy) {
		
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
	 * temp implementation: grid location based
	 * @return
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
			for (Object obj : net.getNodes()) {
				players.add(obj);
			}
		}
		
		//choose random player from that array
		int index = RandomHelper.nextIntFromTo(0, players.size() - 1);
		
		Player otherPlayer = (Player) players.get(index);
		Object obj = players.get(index);

		
		//compare own lootbox to player near us to see how we're doing
		compare(otherPlayer);		
				
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
		Deque<Lootbox> otherLoot = otherPlayer.getHist();
		RepastEdge<Object> friendEdge = net.getEdge(this, otherPlayer); //will = null if dne

		int ownAvg = 0;
		int otherAvg = 0;
		
		//TODO: find cleaner way of doing this?
		for (Iterator<Lootbox> itr = otherLoot.iterator(); itr.hasNext();) {
	            otherAvg += itr.next().getRarity(); 
        }
		for (Iterator<Lootbox> itr = hist.iterator(); itr.hasNext();) {
            ownAvg += itr.next().getRarity();
        }
		
		otherAvg /= otherLoot.size();
		ownAvg /= hist.size();
		
		if(friendEdge == null){
			friendEdge = net.addEdge(this, otherPlayer, 0.0);
		}
			
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

			if (friendEdge.getWeight() < 0) {
				net.removeEdge(friendEdge);
			}
		}
			
		
		
	}

	

	/** TODO: placeholder for player manipulations method
	 * 
	 */
	private void manipulate() {
		
		
	}
	
	

	/** step()
	 * Every tick, determine if player wants to buy a new box, 
	 * and if that new box + or - their likelyhood to buy in future
	 * @return void
	 */
	@ScheduledMethod(start=1, interval=1)
	public void step() {

//		Below is how to keep model updated with params from context mid-run
//		Parameters params = RunEnvironment.getInstance().getParameters();

		
		if(decide()) {
			
			purchased = true;
			
			manipulate();
			
			buyNewLootbox();
			
			if(dump) {
				infoDump(true);
			}

			updateThreshold();

			recordNewLootboxInHistory();

			move();
			
		}		
		else {
			
			//if we didnt buy, we look at other players
			//and edit buyThreshold accordingly
			purchased = false;
			
			manipulate();
			
			askOtherPlayer();
			
			if(dump) {
				infoDump(false);
			}

		}
		
		
		
	}



	
}
