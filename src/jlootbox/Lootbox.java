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

	private static int MIN_PRICE = 1;
	private static int MAX_PRICE = 10;

	private int rarity;
	private int price; // current value as calculated 
	public static Uniform unigen;
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	//default constructor
	public Lootbox() {
		this.rarity = unigen.nextInt();
		this.price = unigen.nextInt() + 1; 
	}
	
	public Lootbox(int money) {
		this.rarity = unigen.nextInt();
		this.price = unigen.nextInt() + 1;
		
		while(this.price > money) {
			this.price = unigen.nextInt() + 1;
		}
	}
	
	
	//tbd if this constructor needed
	/**
	 * 
	 * @return
	 */
	public Lootbox(int rarity, int price, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.rarity = rarity;
		this.price = price;	
		this.space = space;
		this.grid = grid;
	}
	
	/** initGen(int lowRange, int upRange)
	 * 
	 * @return void
	 */
	//initialize the random generator
	public static void initGen(int lowRange, int upRange) {  
		unigen = RandomHelper.createUniform(lowRange, upRange);
		MIN_PRICE = lowRange + 1;
		MAX_PRICE = upRange;
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
	
	/** tbd
	 * 
	 * @return
	 */
	public void step() {
		
	}



	
	
}
