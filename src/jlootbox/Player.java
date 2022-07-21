/**
 * 
 */
package jlootbox;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

/**
 * @author Lila Zayed
 *
 */
public class Player {
	
	//private vars
	public static enum DecisionStrategy{
		   ALWAYS_BUY,
		   COIN_FLIP,
		   PRICE
		}
	
	public static enum Manipulate{
		   NONE,
		   LIM_ED,
		   FAV_PLAYER,
		   FREE_BOX,
		   BIAS_BOX
		}
	
	
	public static Manipulate manip;
	private static final double changeRate = 0.01; 
	private static int count = 0;
	private static int MIN_RANGE = 1;
	private static int MAX_RANGE = 10;
	private static int memorySize = 5;
	private static Boolean breakTies = false;
	
	private static Uniform coinFlip = RandomHelper.createUniform(MIN_RANGE, MAX_RANGE);
	private DecisionStrategy decisionStrat;
	private static Boolean dump = true; //DEBUGGING MODE
	
	private boolean purchased = false;
	private int timeSinceLastPurchase;
	private int id;
	
	private final double availableMoney;  
	private double buyProb;
	private Deque<Lootbox> hist = new ArrayDeque<Lootbox>();
	private Lootbox newLoot;

	private static ProbAdjuster q1;
	private static ProbAdjuster q2;
	private static ProbAdjuster q3;
	private static ProbAdjuster q4;
	private static Context <Object> context;
	private static Network<Object> net;
	private static List<Object> allPlayers = new ArrayList<Object>();
	
	static {
		q1 = new ProbAdjuster(-100 , 0, 0, 4, 0.5, 0.5, 0.1 , 0.05);
		q2 = new ProbAdjuster(0, 100, 0, 4, 0.5, 0.5, 0.05, 0.05);
		q3 = new ProbAdjuster( 0, 100, -4, 0, 0.05, 0.05, -0.1, -0.5);
		q4 = new ProbAdjuster(-100 , 0, -4, 0, 0.1, 0.05, -0.1, -0.1);
	}
	
	
	//default constructor
	public Player(int availMoney, double buy, String strat) {
		
		this.availableMoney = availMoney;
		this.buyProb = buy;
		this.id = ++count;
		
		decisionStrat = Enum.valueOf(Player.DecisionStrategy.class, strat); 
		
		//setting player up with free lootbox so hist.size > 1
		newLoot = new Lootbox();
		hist.addLast(newLoot); 
	}
	
	public static void main(String[] args) {
		
		System.out.println(deltaProbwBoost(2, -50 , 5, -100, 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));
		System.out.println("--------------------------");
		System.out.println(deltaProbwBoost(2, -100 , 5, -100, 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));
		System.out.println("--------------------------");
		System.out.println(deltaProbwBoost(2, -150 , 5, -100, 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));
		System.out.println("--------------------------");
		System.out.println(deltaProbwBoost(2, -200 , 5, -100, 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));
		System.out.println("--------------------------");
		System.out.println(deltaProbwBoost(2, -250 ,5, -100, 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));

	}
	
	
	
	public String toString() {
		return ""+id; 
	}
	
	//TODO: could init quadrant info here
	public static void init(String manipulation, Boolean ties, Context con, ArrayList<Object> tempList) {
		manip = Enum.valueOf(Player.Manipulate.class, manipulation); 
		breakTies = ties;
		context = con;
		net = (Network<Object>)context.getProjection("player network");
		allPlayers = tempList;

	}
	
	public Player getPlayer() {
		return this;
	}
	
	public Deque<Lootbox> getHist(){
		return hist;
	}
	
	public double getMoney() {
		return availableMoney;
	}
	
	public double getThreshold() {
		return buyProb;
	}
	
	public boolean getPurchased() {
		return purchased;
	}
	
	public DecisionStrategy getStrat() {
		return decisionStrat;
	}
	
	public int getBuyTime() {
		return (int) (RunEnvironment.getInstance().getCurrentSchedule().getTickCount() - timeSinceLastPurchase);
	}
	
	public void setBuyTime(int time) {
		timeSinceLastPurchase = time - 1;
	}
	
	public void setHist(Deque<Lootbox> newHist){
		hist = newHist;
	}
		
//	public void setMoney(double money) {
//		availableMoney = money;
//	}
	
	public void setThreshold(double i) {
		if(i > 1) {
			buyProb = 1.0;
		}
		else if(i < 0) {
			buyProb = 0.0;
		}
		else {
			buyProb = i;
		}
	}
	
	public void addThreshold() {
		System.out.println("About to call setThreshold from addThreshold");	
		setThreshold(getThreshold() + changeRate);
			
	}
	
	public void subtractThreshold() {
		System.out.println("About to call setThreshold from subtractThreshold");	
		setThreshold(getThreshold() - changeRate);
		
	}

	public void changeThreshold(double d) {
		setThreshold(getThreshold() + d);
	}


	/** moneySpent()
	 *  
	 *  returns money spent on newest lootbox
	 *  
	 * @return int price of last lootbox
	 */
	public double moneySpent() {
		if(purchased) {
			return hist.peek().getPrice();
		}
		
		return 0;
	}
	
	
	public double avgHistValue() {
		double ownAvg = 0;
		
		for (Iterator<Lootbox> itr = hist.iterator(); itr.hasNext();) {
            ownAvg += itr.next().getRarity();
        }
		
		ownAvg /= hist.size();
		
		return ownAvg;
	}
	
	public double avgHistPrice() {
		double ownAvg = 0;
		
		for (Iterator<Lootbox> itr = hist.iterator(); itr.hasNext();) {
            ownAvg += itr.next().getPrice();
        }
		
		return ownAvg /= hist.size() ;
	}
	
	/** recordNewLootboxInHistory()
	 * 
	 *  push new lootbox onto history and 
	 *  keep size of deque manageable
	 */
	protected void recordNewLootboxInHistory(){
		
		hist.addLast(newLoot);
		
		if(hist.size() > memorySize) { 
			hist.removeFirst();
		}
	}
	
	
	/** buyNewLootbox()
	 * 
	 * create new lootbox object
	 * 
	 * @return newly generated lootbox newLoot
	 */
	protected Lootbox buyNewLootbox(double amtSpent) {
		return Platform.offerLootbox(amtSpent, this);
	}
	
	protected double amtToSpend() {
		return buyProb  * getMoney();
	}
	
	
	protected Lootbox getFreeBox() {
		return Platform.offerLootbox(0, this);
	}
	
	
	/** updateThreshold()
	 * 
	 * Increase/decrease the buyThreshold 
	 * 
	 * If previous loot value > current loot value, reduce threshold 
	 * and if previous loot < current loot, increase threshold
	 * 
	 * @return int new buyThreshold
	 */
	protected double updateThreshold() {
		double priceDiff = newLoot.getPrice() - hist.peekLast().getPrice();
		double rarityDiff = newLoot.getRarity() - hist.peekLast().getRarity();
		
		double deltaProbb =0; 
		
		switch(decisionStrat) {
			case PRICE:
				
				if (priceDiff < 0) {
					if(rarityDiff < 0) {//fourth quadrant
						deltaProbb =  deltaProb(rarityDiff, priceDiff, -100 , 0, -4, 0, 0.1, 0.05, -0.1, -0.1);
					}
					else {//first quadrant
						deltaProbb = ( deltaProb(rarityDiff, priceDiff, -100 , 0, 0, 4, 0.5, 0.5, 0.1 , 0.05 ));
					}
				}
				else {
					
					if(rarityDiff < 0) { //third quadrant
						deltaProbb = ( deltaProb(rarityDiff, priceDiff, 0, 100, -4, 0, 0.05, 0.05, -0.1, -0.5));
					}
					else { //second quadrant
						deltaProbb = (deltaProb(rarityDiff, priceDiff, 0, 100, 0, 4, 0.5, 0.5, 0.05, 0.05));
					}
				}
				break;
			
			default:
				//old box better than new one, less likely to buy				
				deltaProbb = ( (double) (newLoot.getRarity() - hist.peekLast().getRarity()) / 100d );	
		}
		System.out.print("I'm agent " + this.toString() + " and im changing buyProb from: " + this.getThreshold() + " by " + deltaProbb);

		changeThreshold(deltaProbb);
		
		System.out.println(" to " + this.getThreshold());
		
		return buyProb;
	}
	
	
	private static class ProbAdjuster{
		
		double slopeForMinRar; 
		double slopeForMaxRar;
		double pDiffMin;
		double pDiffMax;
		double rDiffMin;
		double rDiffMax;
		double zA;
		double zC;

		
		public ProbAdjuster(double pDiffMin, double pDiffMax, double rDiffMin, double rDiffMax, double zA, double zB, double zC, double zD) {
			
			this.pDiffMin = pDiffMin;
			this.pDiffMax =  pDiffMax;
			this.rDiffMin = rDiffMin;
			this.rDiffMax = rDiffMax;
			this.zA = zA;
			this.zC = zC;

			//slope for lower line
			slopeForMinRar = slopeOf(pDiffMin, pDiffMax, zC, zD);

			//slope for upper line
			slopeForMaxRar = slopeOf(pDiffMin, pDiffMax, zA, zB);

		}
	
	
	
		public double getAdjustment(double rarityDiff, double priceDiff) {
			
			//how far btwn the two lines we are
			double rarityScaled = fractionOf(rDiffMin, rDiffMax, rarityDiff);
			
			//this is vertical val of raritydiff, figuring out what is the y-intercept at left side of region
			double zAtMinPrice = zC + (zA - zC) * rarityScaled;//zvalue at left side of bounding box vertically
			
			//slope given the actual raritydiff
			double slope = rarityScaled * (slopeForMaxRar - slopeForMinRar) + slopeForMinRar;

			//final val based on slope and priceDiff
			return zAtMinPrice + (priceDiff - pDiffMin) * slope;

			
		}
	
		
		public double getAdjustmentwBoost(double rarityDiff, double priceDiff, double newRarity) {
			return getAdjustment(rarityDiff, priceDiff) + (newRarity / 5d) * 0.3; 
		}
	
	}
	
	
	
	
	protected static double deltaProb(double rarityDiff, double priceDiff, double pDiffMin, double pDiffMax, double rDiffMin, double rDiffMax, double zA, double zB, double zC, double zD) {
		
		//how far btwn the two lines we are
		double rarityScaled = fractionOf(rDiffMin, rDiffMax, rarityDiff);
//		System.out.println("pFraction: " + pFraction);

		
		//this is vertical val of raritydiff, figuring out what is the y-intercept at left side of region
		double zAtMinPrice = zC + (zA - zC) * rarityScaled;//zvalue at left side of bounding box vertically
		System.out.println("zAtMinPrice: " + zAtMinPrice);
		
		//slope for lower line
		double slopeForMinRar = slopeOf(pDiffMin, pDiffMax, zC, zD);
		System.out.println("slopeForMinRar: " + slopeForMinRar);

		//slope for upper line
		double slopeForMaxRar = slopeOf(pDiffMin, pDiffMax, zA, zB);
		System.out.println("slopeForMaxRar: " + slopeForMaxRar);

		//slope given the actual raritydiff
		double slope = rarityScaled * (slopeForMaxRar - slopeForMinRar) + slopeForMinRar;
		System.out.println("slope: " + slope);

		//final val based on slope and priceDiff
		double dProb = zAtMinPrice + (priceDiff - pDiffMin) * slope;
		System.out.println("dProb: " + dProb);

		return dProb;
		
	}
	
	
	protected static double deltaProbwBoost(double rarityDiff, double priceDiff, double newRarity, double pDiffMin, double pDiffMax, double rDiffMin, double rDiffMax, double zA, double zB, double zC, double zD) {
		
		//how far btwn the two lines we are
		double rarityScaled = fractionOf(rDiffMin, rDiffMax, rarityDiff);
		
		//this is vertical val of raritydiff, figuring out what is the y-intercept at left side of region
		double zAtMinPrice = zC + (zA - zC) * rarityScaled;//zvalue at left side of bounding box vertically

		//slope for lower line
		double slopeForMinRar = slopeOf(pDiffMin, pDiffMax, zC, zD);
		
		//slope for upper line
		double slopeForMaxRar = slopeOf(pDiffMin, pDiffMax, zA, zB);
		
		//slope given the actual raritydiff
		double slope = rarityScaled * (slopeForMaxRar - slopeForMinRar) + slopeForMinRar;
		
		//final val based on slope and priceDiff
		double dProb = zAtMinPrice + (priceDiff - pDiffMin) * slope;
		dProb += (newRarity / 5d) * 0.3; 
		
		return dProb;
		
	}
	
	
	//returns slope of line where these are two endpts of line
	private static double slopeOf(double min, double max, double ymin, double ymax) {
		return (ymax - ymin)/(max - min);
	}
	
	//how far from beginning line/total distance possible from bottom line to top line
	private static double fractionOf(double min, double max, double value) {
		return (value - min)/(max - min);
	}
	
	
	
	
	/** decide()
	 * Player buying decision structure
	 * @return
	 */
	protected Boolean decide() {
		
		switch(decisionStrat) {
			case ALWAYS_BUY: 	return true;
			case COIN_FLIP: 	return (coinFlip.nextInt() <= buyProb);	
			case PRICE: 
			{		
				double askingPrice = Math.random() * 100;
				double adjustedBuyProb = buyProb * (100 - askingPrice) / 50;
				return (coinFlip.nextInt() <= adjustedBuyProb);
			} 
			default: 			return false; //impossible 
		}
	}
	
	

	protected void infoDump(Boolean buy) {
		
		 Iterator value = hist.iterator();
		 System.out.println("-------------------");
		 System.out.print(this.toString() + "- Current Hist:");
		 while (value.hasNext()) {
	            System.out.print(value.next().toString());
	        }
		 System.out.println("");
		 System.out.println(this.toString() + "- BuyProb: " + getThreshold());

		if(buy) {
			
			int d = newLoot.getRarity() - hist.peekLast().getRarity();
			
			System.out.println(this.toString() + "- *BUY*");
			System.out.println(this.toString() + "- Old Loot Val: " + hist.peekLast().getRarity());
			System.out.println(this.toString() + "- New Loot Val: " + newLoot.getRarity());
			System.out.println(this.toString() + "- Price: " + newLoot.getPrice());
		 	System.out.println(this.toString() + "- New BuyProb: " + getThreshold());
			 
			if(d < 0) {
				System.out.println(this.toString() + "- Got a -BAD- lootbox, reducing by magnitude: " + d);
			}
			else if(d == 0) {
				System.out.println(this.toString() + "- Got the SAME lootbox, no magnitude change");

			}
			else{
				System.out.println(this.toString() + "- Got a +GOOD+ lootbox, increasing by magnitude: " + d);
			}


		}
		else {
			System.out.println(this.toString() + "- *NO BUY*");
			System.out.println(this.toString() + "- TimeSincePurchase: " + this.getBuyTime());
			System.out.println(this.toString() + "- changing my BuyProb to "+ this.getThreshold() +" after comparing to another player ");

		}

	}
	
	
	/** askOtherPlayer()
	 * If player doesn't buy lootbox, they 
	 * look at other players to decide if 
	 * they should buy more, or fewer boxes
	 * 
	 * weight of their connection with another
	 * player increased/decreased depending on
	 * if the other player has better or worse loot
	 */
	protected void askOtherPlayer() {
		
		List<Object> players = new ArrayList<Object>();

		players.addAll((Collection<? extends Object>) net.getSuccessors(this));
		
		//no friends ;-; choose anyone
		if(players.size() < 1) players = soloPlayer();

		if(players.size() == 1) compareAndUpdateRelationship( (Player) players.get(0));
		else                    compareAndUpdateRelationship( (Player) players.get(RandomHelper.nextIntFromTo(0, players.size() - 1)));	
				
	}
	
	/**soloPlayer()
	 * If a player has no edges, choose a node
	 * in the network at random and create an edge
	 * 
	 * @return List<Object> player - the list of player edges the current agent has
	 */
	public List<Object> soloPlayer() {
		
		List<Object> player = new ArrayList<Object>();
		//choose random player from that array
		int index = RandomHelper.nextIntFromTo(0, allPlayers.size() - 1);
		Player otherPlayer = (Player) allPlayers.get(index);
		RepastEdge<Object> friendEdge = net.getEdge(this, otherPlayer); //will == null if dne
		
		player.add(otherPlayer);
		friendEdge = net.addEdge(this, otherPlayer, 0.0);
		
		return player;
		
	}
	
	
	/**compare(Deque<Lootbox> otherLoot) 
	 * 
	 * if other player has better history, add changeRate to buyThreshold
	 * if other player is worse off, subtract changeRate from buyThreshold
	 * 
	 * @param otherLoot, lootbox history of another player
	 */
	private void compareAndUpdateRelationship(Player otherPlayer) {
		
		RepastEdge<Object> friendEdge = net.getEdge(this, otherPlayer); //will == null if dne
		double ownAvg = avgHistValue();
		double otherAvg = otherPlayer.avgHistValue();
		
		
		if(otherAvg > ownAvg) {

			changeThreshold(friendEdge.getWeight() * .001);

			friendEdge.setWeight(friendEdge.getWeight() + .1); 
			
		}
		else{
			//stronger friendship = stronger influence 
			changeThreshold(-1 * (friendEdge.getWeight() * .001));
			
			friendEdge.setWeight(friendEdge.getWeight() - .1); 

			if (friendEdge.getWeight() < 0 && breakTies) {
				net.removeEdge(friendEdge);
			}
		}
			
	}
	
	
	/**platformCheck()
	 * check if platform has events/manipulations 
	 * going on
	 */
	public void platformCheck() {
		
		if(Platform.limEd) {
			System.out.println("About to call setThreshold from platformCheck");
			setThreshold(getThreshold() + 0.1); 
		}
		
		if(Platform.freeBox) {
			getFreeBox();
			updateThreshold();
		}
		
	}
	
	
	/** step()
	 * Every tick, determine if player wants to buy a new box, 
	 * and if that new box + or - their likelyhood to buy in future
	 */
	@ScheduledMethod(start=1, interval=1)
	public void step() {

//		Below is how to keep model updated with params from context mid-run
//		Parameters params = RunEnvironment.getInstance().getParameters();
		
		platformCheck();
		 
		if(decide()) {	
			
			double amtToSpend = amtToSpend();
			
			newLoot = buyNewLootbox(amtToSpend);
			
			if(dump) {
				infoDump(true);
			}
			
			updateThreshold();

			recordNewLootboxInHistory();

			purchased = true;
			
			setBuyTime((int) (RunEnvironment.getInstance().getCurrentSchedule().getTickCount()));
		}		
		else {
			//if we didnt buy, we look at other players
			//and edit buyThreshold accordingly
			purchased = false; 
			
			if(Platform.networkPresent) {
				askOtherPlayer();
			}
			
			
			if(dump) {
				infoDump(false);
				
			}
			
			

		}
		
		
		
	}



	
}
