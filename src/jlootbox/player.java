/**
 * 
 */
package jlootbox;

import java.util.List;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

/**
 * @author Lilo
 *
 */
public class Player {
	
	//maybe we make personal traits/risk factors an array of 1s/0s - 1 if in risk zone 0 if not
	int availableMoney;  
	int employmentStatus;
	int age; 
	boolean gender; 
	boolean buy; 
	Double riskLvl; 
	int jealousy;
	int happiness; 
	
	
	//TODO: allows players to keep an
	@Watch(watcheeClassName = "jlootbox.Player", watcheeFieldNames = "moved",
			query = "within_moore 1",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void game() {
		

		
	}
	

}
