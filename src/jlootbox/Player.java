/**
 * 
 */
package jlootbox;

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
	int availableMoney;  
	int buyThreshold;
	Stack<Integer> hist = new Stack<Integer>();
	private Uniform unigen = RandomHelper.createUniform(0, 10);

	
	public Player(int availMoney, int buy) {
		this.availableMoney = availMoney;
		this.buyThreshold = buy;
	}
	
	
	
	//TODO: allows players to keep an
	@ScheduledMethod(start=1, interval=1)
	public void step() {
		
		if(unigen.nextInt() <= buyThreshold) {
			Lootbox loot = new Lootbox();
			hist.push(loot.getRarity());
			
			if(hist.size() > 1) {//make sure this isn't the first lootbox pulled
				
				
				
				
			}
			
		}

		
	}
	

}
