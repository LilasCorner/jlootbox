/**
 * 
 */
package jlootbox;

import java.util.Iterator;
import java.util.Stack;

import cern.jet.random.Uniform;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

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
	
	
	public Player(int availMoney, int buy) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
	}
	
	
	public void changeThreshold(int change) {
		
		if(buyThreshold + change < 9 && buyThreshold + change > 1) {
			buyThreshold += change;
		}
		
		return;
	}
	
	//TODO: allows players to keep an
	@ScheduledMethod(start=1, interval=1)
	public void step() {
		
		if(unigen.nextInt() <= buyThreshold) {
			Lootbox loot = new Lootbox();
			
			if(hist.size() > 1) {//make sure this isn't the first lootbox pulled
				
		        if (hist.peek() > loot.getRarity()) { //if new loot worse than last one
		        	changeThreshold(-1 * changeRate);
		        }
		        else {
		        	changeThreshold(changeRate);
		        }
				
				
			}
			
			hist.push(loot.getRarity());

		}

		
	}
	

}
