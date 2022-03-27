/**
 * 
 */
package jlootbox;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.Lattice2DGenerator;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * @author Lilo
 *
 */
public class JLootboxBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("player network", context, true);

		
		context.setId("jlootbox");
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		ContinuousSpaceFactory spaceFactory = 
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = 
				spaceFactory.createContinuousSpace("space", context, 
						new RandomCartesianAdder<Object>(),
						new repast.simphony.space.continuous.WrapAroundBorders(),
						50, 50);
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		
		Grid<Object> grid =  gridFactory.createGrid("grid", context, 
				new GridBuilderParameters<Object>(new WrapAroundBorders(),
				new SimpleGridAdder<Object>(),
				true, 50, 50));	

		
		int playerCount = params.getInteger("numPlayers");
		String strat = params.getString("strat");
		int money = 100;
		int buy = 5;
		
		for (int i =0; i < playerCount; i++) {
			context.add(new Player (money, buy, space, grid, strat));
		}
		
		
		for (Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
			
		}
		
		//network experiments!
		
		//beta(probability of rewiring), degree, symmetry
		WattsBetaSmallWorldGenerator<Object> gen = new WattsBetaSmallWorldGenerator<Object>(.3,  4, true);
		
		//density, selfLoops, symmetry
//		RandomDensityGenerator<Object> gen = new RandomDensityGenerator<Object>(2,  false, true);

		//isToroidal - think pacman
		// BUG: "Pair cannot contain null values"
//		Lattice2DGenerator<Object> gen = new Lattice2DGenerator<Object>(true);

		
		
		netBuilder.setGenerator(gen);
		netBuilder.buildNetwork();
		
		RunEnvironment.getInstance().setScheduleTickDelay(100);
		RunEnvironment.getInstance().endAt(params.getInteger("stopTime"));
		
		
		
		
		return context;
	}

}
