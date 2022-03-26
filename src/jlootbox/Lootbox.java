/**
 * 
 */
package jlootbox;

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

	private int rarity;
	private int price; // current value as calculated 
	public static Uniform unigen = RandomHelper.createUniform(1, 5);
	
	
	//default constructor
	public Lootbox() {
		this.rarity = unigen.nextInt();
		this.price = 0; 
	}
	
	//constructor for specific price
	public Lootbox(int money) {
		this.rarity = unigen.nextInt();
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
