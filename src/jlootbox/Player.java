/**
 * 
 */
package jlootbox;

import java.util.Stack;
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
	private int availableMoney;  
	private int buyThreshold;
	private Stack<Integer> hist = new Stack<Integer>();
	private Lootbox newLoot;
	private static Uniform coinFlip;
	private int changeRate = 1;
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
		hist.push(newLoot.getRarity()); 
	}
	

	/**
	 * 
	 * @return
	 */
	//initialize the random generator
	public static void initGen(int lowRange, int upRange) {  
		coinFlip = RandomHelper.createUniform(lowRange, upRange);
	}
	
	
	/** getThreshold()
	 * 
	 * @return the Player's buyThreshold
	 */
	public int getThreshold() {
		return buyThreshold;
	}
	
	
	/** changeThreshold()
	 * 
	 * Increase/decrease the buyThreshold 
	 * 
	 * If previous loot value > current loot value, reduce threshold 
	 * and if previous loot < current loot, increase threshold
	 * 
	 * @return int new buyThreshold
	 */
	public int changeThreshold() {
		
		//old box better than new one
		if(hist.peek() > newLoot.getRarity()) { 
			
			if(buyThreshold + (-1 * changeRate) > 0) { //TODO: remove hardcoded vals, paramaterize
				buyThreshold += changeRate * -1;
			}
			
		}
		else { //new box better than old
			
			if(buyThreshold + changeRate < 10 ) {
				buyThreshold += changeRate;

			}
		}
		

		return buyThreshold;
	}
	
	/** move()
	 * 
	 * Calculates displacement for Player and moves them
	 * to new location
	 * 
	 * @return true for positive displacement, false for negative
	 */
	public Boolean move() {
		
		NdPoint myPoint = space.getLocation (this);
		int disp = 0;//these two vars to calculate mvmt
		int old = 0;
		
		old = getThreshold();
    	disp = changeThreshold();
    	disp -= old;
        	
		space.moveByDisplacement(this, 1, disp); //x, y displacement
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		
		
		return (disp > 0); 
		
	}
	
	/** decide()
	 * Player buying decision structure
	 * @return
	 */
	public void decide() {
		
		//flip a coin....
		if(coinFlip.nextInt() <= buyThreshold) { 
			
			//decided to buy!
			newLoot = new Lootbox();
						
			move();

		}

	}
	
	/** infoDump()
	 * print out of a player's internal variables
	 * fun for debugging :p
	 * @return void
	 */
	public void infoDump() {
		
		if(hist.peek() > newLoot.getRarity()) { 
			System.out.println("++++BUY++++");
			System.out.println("Old Loot Val: " + hist.peek());
			System.out.println("New Loot Val: " + newLoot.getRarity());
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
		
		decide();
		
		infoDump(); //TODO: paramaterize this based on checkbox on front
		
		hist.push(newLoot.getRarity());
		
	}
	

	
}
