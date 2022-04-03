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
	
	
	public static double[] dropRates = {.08, .1, .3, .5}; 
	public static Uniform unigen = RandomHelper.createUniform(0,1);

	private boolean biased = false;
	private int rarity;
	private int price; // current value as calculated 
	
	
	//default constructor
	public Lootbox() {
		
		this.rarity = generateRarity();
		
		this.price = 0; 
	}
	
	//constructor for specific price
	public Lootbox(int money) {
		
		this.rarity = generateRarity();
		
		this.price = money;
	}
	
	//constructor for specific price
	public Lootbox(Boolean biased) {
		
		//this.rarity = generateBias();
		
		this.price = 0;
	}
	
	public int generateRarity() {
		int rarity = 1;
		double threshold = 0.0;
		
		double rand = unigen.nextDouble();
		
		for(double d: dropRates) {
			if(rand < d) {
				return rarity;
			}
			
			rarity++;
		}
		
		return rarity;
	}
	
	//experimenting with reversing the luck for a biased draw,
	// 5 = more common, 1 = extremely rare
	public int generateBias() {
		int rarity = 1;
		double threshold = 0.0;
		
		double rand = unigen.nextDouble();
		
		for(int d = dropRates.length - 1; d >= 0; d--) {
			if(rand < (threshold = threshold + dropRates[d])) {
				return rarity;
			}
			rarity--;
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
