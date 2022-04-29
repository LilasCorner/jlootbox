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
 * @author Lila Zayed
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
	private double price; // current value as calculated 
	

	
	public Lootbox(){
		this(0, 0, false); 
	}

	public Lootbox(double money){
		this(money, 0, false);
	}

	public Lootbox(double money, double weight){
		this(money, weight, true);
	}

	//
	public Lootbox(double money, double weight, Boolean fav){
		this.price = money;
		
		if(fav){
			this.rarity = generateFav();
			}
		else{
			this.rarity = generateRarity(weight);
		}
	}
	
	

	
	public double getPrice() {
		return price;
	}
	
	public int getRarity() {
		return rarity;
	}
	
	private static int generateRarity(double weight) {
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
	private static int generateFav() {
		int rarity = 5;
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
