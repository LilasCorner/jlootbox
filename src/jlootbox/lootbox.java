/**
 * 
 */
package jlootbox;

import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

/**
 * @author Lilo
 *
 */
public class Lootbox {

	private int rarity;
	private int availability; // current value as calculated 
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	
	
	public Lootbox(int rarity, int availability, ContinuousSpace<Object> space, Grid<Object> grid) {
		this.rarity = rarity;
		this.availability = availability;
		this.space = space;
		this.grid = grid;
		
	}
	
	
	
	
	
	@ScheduledMethod(start=1, interval=1)
	public void step() {

	}
	
	
}
