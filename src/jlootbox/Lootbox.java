/**
 * 
 */
package jlootbox;

import java.util.Random;

import cern.jet.random.Uniform;
import repast.simphony.engine.schedule.ScheduledMethod;
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
	private Uniform unigen = RandomHelper.createUniform(0, 5);
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	public Lootbox() {
		this.rarity = unigen.nextInt();
		this.price = unigen.nextInt();
	}
	
	public Lootbox(int rarity, int price, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.rarity = rarity;
		this.price = price;	
		this.space = space;
		this.grid = grid;
	}
	
	
	public int getRarity() {
		return rarity;
	}
	
	
	public void step() {
		
	}
	
	
}
