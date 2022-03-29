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
	
	
	public static double[] dropRates = {.5,.3,.1,.08};
	public static Uniform unigen = RandomHelper.createUniform();

	private boolean biased = false;
	private int rarity;
	private int price; // current value as calculated 
	
	
	//default constructor
	public Lootbox() {
		

		
		this.price = 0; 
	}
	
	//constructor for specific price
	public Lootbox(int money) {
		

		
		this.price = money;
	}
	
	//constructor for specific price
	public Lootbox(Boolean biased) {
		

		
		this.price = 0;
	}
	
	public int generateRarity() {
		int rarity = 1;
		double threshold = 0.0;
		
		double rand = unigen.nextDouble();
		
		for(double d: dropRates) {
			if(rand < (threshold = threshold + d)) {
				return rarity;
			}
			
			rarity++;
		}
		
		return rarity;
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
