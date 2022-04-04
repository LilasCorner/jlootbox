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
//	public static double[] dropRates = {.99,.94,.18,.075}; //overwatch #'s
//	public static double[] dropRates = {.06, .76, .105}; //overwatch #'s converted

	public static Uniform unigen = RandomHelper.createUniform(0,1);

	private boolean biased = false;
	private int rarity;
	private int price; // current value as calculated 
	
	
	//default constructor
	public Lootbox() {
		
		this.rarity = generateRarity(0);
		
		this.price = 0; 
	}
	
	//constructor for specific price
	public Lootbox(int money) {
		
		this.rarity = generateRarity(0);
		
		this.price = money;
	}
	
	//constructor for favorite player biased boxes
	public Lootbox(Boolean biased) {
		
		this.rarity = generateBias();
		
		this.price = 0;
	}
	
	//constructor for generating weighted luck 
	public Lootbox(Boolean biased, int weight) {
		
		this.rarity = generateRarity(weight);
		
		this.price = 0;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getRarity() {
		return rarity;
	}
	
	public int generateRarity(int weight) {
		int rarity = 1;
		double threshold = 0.0;
		
		double rand = unigen.nextDouble();
		
		for(double d: dropRates) {
			if(rand < (threshold = threshold + (d + weight))) {
				return rarity;
			}
			
			rarity++;
		}
//		rarity = 4;
//		
//		
//		for(double d: dropRates) {
//			if(rand < (d + weight)) {
//				return rarity;
//			}
//			
//			rarity--;
//		}
		
		
		
		
		
		return rarity;
	}
	
	//experimenting with reversing the luck for a biased draw,
	// 5 = more common, 1 = extremely rare
	public int generateBias() {
		int rarity = 1;
		double threshold = 0.0;
		
		double rand = unigen.nextDouble();
		
		for(int d = dropRates.length - 1; d >= 0; d--) {
			if(rand < (threshold = threshold + (dropRates[d]))) {
				return rarity;
			}
			rarity--;
		}
		
		return rarity;
	}
	


	
	
}
