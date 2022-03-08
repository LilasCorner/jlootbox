/**
 * 
 */
package jlootbox;

import java.util.Iterator;
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
	
	//maybe we make personal traits/risk factors an array of 1s/0s - 1 if in risk zone 0 if not
	private int availableMoney;  
	private int buyThreshold;
	private Stack<Integer> hist = new Stack<Integer>();
	private Uniform unigen = RandomHelper.createUniform(0, 10);
	private int changeRate = 1;
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	public Player(int availMoney, int buy, ContinuousSpace <Object> space , Grid <Object> grid) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
		this.space = space;
		this.grid = grid;
	}
	
	
	public int getThreshold() {
		return buyThreshold;
	}
	
	public int changeThreshold(int change) {
		
		if(buyThreshold + change < 10 && buyThreshold + change > 0) {
			buyThreshold += change;
		}
		
		return buyThreshold;
	}
	
	//TODO: comment!
	@ScheduledMethod(start=1, interval=1)
	public void step() {
		
		int disp = 0;//these two vars to calculate mvmt
		int old = 0;
		NdPoint myPoint = space.getLocation (this);

		
		if(unigen.nextInt() <= buyThreshold) { //flip a coin....
			System.out.println("++++Buy++++");
			
			Lootbox loot = new Lootbox();
			
			if(hist.size() > 1) {//make sure this isn't the first lootbox pulled
				
		        if (hist.peek() > loot.getRarity()) { //if last loot better than new one, short term mem
		        	old = getThreshold();
		        	disp = changeThreshold(-1 * changeRate);
		        	disp -= old;
		        	System.out.println("old: " + hist.peek());
		        	System.out.println("New: " + loot.getRarity());

		        }
		        else {
		        	old = getThreshold();
		        	disp = changeThreshold(changeRate);
		        	disp -= old;
		        	System.out.println("old: " + hist.peek());
		        	System.out.println("New: " + loot.getRarity());

		        }
				
				
			}
			
			hist.push(loot.getRarity());

		}
		else {
			System.out.println("----No Buy----");
		}

		space.moveByDisplacement(this, 1, disp); //x, y displacement
		myPoint = space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		
		
	}
	

}
