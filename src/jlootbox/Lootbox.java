/**
 * 
 */
package jlootbox;

import java.util.ArrayList;

import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import repast.simphony.random.RandomHelper;

/**
 * @author Lila Zayed
 *
 */
public class Lootbox {

	public static double MIN_PRICE = .99;
	public static double MAX_PRICE = 100;
	
	public static double[] dropPrices = {99.99, 44.99, 9.99, 4.99, 0.99}; //referenced directly from DiabloImmortal starting prices
	public static double[] dropRates = {.5,.3,.1,.08}; 
	
	public static Uniform unigen = RandomHelper.createUniform(0,1);
	public static Normal normgen = RandomHelper.createNormal(MIN_PRICE, MAX_PRICE);

	private int rarity;
	private double price; // current value as calculated 
	private boolean limEd;
	private boolean purchased = false;

	
	public Lootbox(){
		this(0, false, 0, 0, false); 
	}

	public Lootbox(double buyProb, double avgHistPrice){
		this(0, false, buyProb, avgHistPrice, false);
	}
	
	public Lootbox(double weight, double buyProb, double avgHistPrice){
		this(weight, false, buyProb, avgHistPrice, false);
	}

	//
	public Lootbox(double weight, boolean fav, double buyProb, double avgHistPrice, boolean limEdition){
		this.price = generatePrice(buyProb, avgHistPrice) ;
		this.limEd = limEdition;
		
		if(fav){
			this.rarity = generateFav();
			}
		else{
			this.rarity = generateRarity(weight);
		}
	}
	
	 @Override
    public String toString() {
        return " " + this.getRarity();
    }

	
	public double getPrice() {
		return price;
	}
	
	public int getRarity() {
		return rarity;
	}

	public boolean getLimEdStatus() {
		return limEd;
	}
	

	private static double generatePrice(double buyProb, double avgHistPrice) {
		
		ArrayList<Double> playerProb = new ArrayList<Double>();
		Double rand = normgen.nextDouble();
		
		//determining probability to purchase every box
		for(double it: dropPrices){
			playerProb.add((avgHistPrice / it) * buyProb);
		}

		for(int i = 0; i < dropPrices.length; i++){

			if(rand <= playerProb.get(i)){
				return dropPrices[i];
			}

		}

		//player wasn't convinced by any prices but still wants purchase,
		//giving them cheapest option
		return dropPrices[4];
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

	/**
	 * @return the purchased
	 */
	public boolean getPurchased() {
		return purchased;
	}

	/**
	 * @param purchased the purchased to set
	 */
	public void setPurchased(boolean purchased) {
		this.purchased = purchased;
	}
	


	
	
}
