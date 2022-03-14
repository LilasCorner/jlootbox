/**
 * 
 */
package jlootbox;

import java.util.ArrayDeque;
import java.util.Deque;
import cern.jet.random.Uniform;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

/**
 * @author Lilo
 *
 */
public class Player {
	
	//private vars
	private static final int ALWAYS_BUY = 0; 
	private static final int COIN_FLIP = 1; 
	private static final int PRICE = 2; 

	private static Uniform coinFlip;
	private static int decisionStrat;
	private static Boolean dump = false;
	
	private int changeRate = 1; //TODO: paramaterize this
	private int availableMoney;  
	private int buyThreshold;
	private Deque<Lootbox> hist = new ArrayDeque<Lootbox>();
	private Lootbox newLoot;
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	//default constructor
	public Player(int availMoney, int buy, ContinuousSpace <Object> space , Grid <Object> grid) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
		this.space = space;
		this.grid = grid;
		
		//setting player up with free lootbox so hist.size > 1
		newLoot = new Lootbox();
		hist.addLast(newLoot); 
	}
	
	/** initGen(int lowRange, int upRange, String strat, Boolean debug)
	 * 
	 * intializes random generator, decision strategy, 
	 * and debug print statements according to params
	 * 
	 * @return void
	 */
	public static void initGen(int lowRange, int upRange, String strat, Boolean debug) {  
		coinFlip = RandomHelper.createUniform(lowRange, upRange);
		dump = debug;
		
		switch( strat ) {
		
			case "Always-Buy":{
				decisionStrat = ALWAYS_BUY;

			}
			
			case "Coin-Flip":{
				decisionStrat = COIN_FLIP;

			}
			
			case "Price":{
				decisionStrat = PRICE;

			}
			
			default: {
				decisionStrat = COIN_FLIP;
			}
		
		}
			
	}
	
	/** getThreshold()
	 * 
	 * @return the Player's buyThreshold
	 */
	public int getThreshold() {
		return buyThreshold;
	}
	
	public int getMoney() {
		return availableMoney;
	}
	
	public void setMoney(int money) {
		availableMoney = money;
	}
	
	/**deductFunds()
	 * deduct price of lootbox from available funds
	 */
	public void deductFunds() {

		System.out.println("MADE ITTTTTTTTTTTTTTTTTTTTTTTTT: " + getMoney());
		setMoney(getMoney() - newLoot.getPrice());
		System.out.println("thoughts tho: " + getMoney());

	}
	
	/** recordNewLootboxInHistory()
	 * 
	 *  push new lootbox onto history and 
	 *  keep size of deque manageable
	 */
	protected void recordNewLootboxInHistory(){
		
		hist.addLast(newLoot);
		
		//temp attempt at size management
		if(hist.size() > 2) { 
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
			
			newLoot = new Lootbox(getMoney());
			deductFunds();
			System.out.println("HELLLLLLLLLLLLLLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			return newLoot;
		}

		default:
			newLoot = new Lootbox();
			return newLoot;
			
		}
		
		
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
				//if loot worse than price paid for it - assuming loot and price scaled same, temporary
				if(newLoot.getRarity() < newLoot.getPrice()) {
					if(buyThreshold + (-1 * changeRate) > 0) {  //TODO: turn lines 184/185 into method, too many brackets here ;-;
						buyThreshold += changeRate * -1;
					}
				}
				else {
					if(buyThreshold + changeRate < 10 ) {
						buyThreshold += changeRate;
					}
				}
				
			}
	
			default:{
				//old box better than new one
				if(hist.peek().getRarity() > newLoot.getRarity()) { 
					
					if(buyThreshold + (-1 * changeRate) > 0) { 
						buyThreshold += changeRate * -1;
					}
					
				}
				else { //new box better than old
					
					if(buyThreshold + changeRate < 10 ) {
						buyThreshold += changeRate;
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
				//if money > minLootPrice
				//buy!
				if(getMoney() > Lootbox.MIN_PRICE) {
					return true;
				}
				
				//otherwise wait/acrue dogecoin wealth
				return false;
			}

			default:{
				 return false; //impossible
			}
		}
		
		

	}
	
	/** infoDump()
	 * print out of a player's internal variables
	 * fun for debugging :p
	 * @return void
	 */
	protected void infoDump() {
		
		if(hist.peek().getRarity() > newLoot.getRarity()) { 
			System.out.println("++++BUY++++");
			System.out.println("Old Loot Val: " + hist.peek().getRarity());
			System.out.println("New Loot Val: " + newLoot.getRarity());
			if(decisionStrat == PRICE) {
				System.out.println("Price: " + newLoot.getPrice());
			}
			System.out.println("BuyThreshold: " + getThreshold());

		}
		else {
			System.out.println("---NO BUY---");
		}
	}
	
	/** step()
	 * Every tick, determine if player wants to buy a new box, 
	 * and if that new box + or - their likelyhood to buy in future
	 * @return void
	 */
	@ScheduledMethod(start=1, interval=1)
	public void step() {
		
		if(decide()) {
					
			buyNewLootbox();
			
			if(dump) {
				infoDump();
			}

			updateThreshold();
			
			recordNewLootboxInHistory();

			move();
			
			

		}		
		
		
		
	}
	

	/**dieCheck()
	 * 
	 * If player runs out of money, or becomes too old(?), god strikes them down
	 */
	public void dieCheck() {
		//TODO
	}
	
}
