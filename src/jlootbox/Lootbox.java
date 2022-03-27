/**
 * 
 */
package jlootbox;

import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/**
 * @author Lilo
 *
 */
public class Lootbox {

	public static int MIN_PRICE = 1;
	public static int MAX_PRICE = 10;
	public static int MIN_RARITY = 1;
	public static int MAX_RARITY = 5;

	

	private int rarity;
	private int price; // current value as calculated 
	public static Normal normgen = RandomHelper.createNormal(MIN_RARITY, MIN_RARITY);
	
	
	//default constructor
	public Lootbox() {
		
		while(this.rarity > MAX_RARITY || this.rarity < MIN_RARITY) { //CLAMP!
			this.rarity = normgen.nextInt();
		}
		
		this.price = 0; 
	}
	
	//constructor for specific price
	public Lootbox(int money) {
		
		while(this.rarity > MAX_RARITY || this.rarity < MIN_RARITY) { //CLAMP!
			this.rarity = normgen.nextInt();
		}
		
		this.price = money;
	}
	
	
	/** getPrice()
	 * 
	 * @return price
	 */
	public int getPrice() {
		return price;
	}
	
	/**
	 * 
	 * @return rarity
	 */
	public int getRarity() {
		return rarity;
	}
	
	
}
