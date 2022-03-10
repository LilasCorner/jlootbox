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

	private int rarity;
	private int price; // current value as calculated 
	public static Uniform unigen;
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	//default constructor
	public Lootbox() {
		this.rarity = unigen.nextInt();
		this.price = unigen.nextInt(); 
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
	
	/**
	 * 
	 * @return
	 */
	//initialize the random generator
	public static void initGen(int lowRange, int upRange) {  
		unigen = RandomHelper.createUniform(lowRange, upRange);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getRarity() {
		return rarity;
	}
	
	/**
	 * 
	 * @return
	 */
	public void step() {
		
	}



	
	
}
