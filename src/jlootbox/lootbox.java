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


	private String name; 
	private double value; // current value as calculated 
	private ContinuousSpace<Object> space; 
	private Grid<Object> grid;
	private Random rnd = new Random();
	
	
	
	
	@ScheduledMethod(start=1, interval=1)
	public void step() {

	}
	
	
}
