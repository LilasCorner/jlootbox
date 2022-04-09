## WHAT IS IT?

This model demonstrates how gambling-like behavior can emerge in lootbox spending within gaming communities. A lootbox is 
a purchasable mystery box that randomly awards the player a series of in-game items. Since the contents of the box are
largely up to chance, many players can fall into a compulsion loop of purchasing, as the fear of missing out and belief
in the gambler's fallacy allow one to rationalize repeated purchases, especially when one compares their own luck to others.
To simulate this behavior, this model generates players in different network structures to observe how factors such as network 
connectivity, a player's internal decision making strategy, or even common manipulations games use these days may influence
a player's transactions. 

## HOW IT WORKS

Each tick, every agent decides if they want to purchase or not based on the decision structure defined by Decision Strategy.
These strategies are ALWAYS_BUY, COIN_FLIP, and PRICE. A player following ALWAYS_BUY will make a purchase every round
regardless of their satisfaction with their previous luck. A player following COIN_FLIP will generate a number 1-10, 
and if it greater than their current buyThreshold, they will make a purchase. For the Price decision structure, the average price 
value of their previous boxes is calculated and divided by the average rarity of their previous boxes. 
If the rarity is better than the price the player has been paying for the boxes,they will decide to buy - if not, 
the player will resort to COIN_FLIP to determine if they should make the purchase. 

If any manipulations are in play, they are processed next, skewing the luck of players boxes or awarding them free ones depending
on the strategy selected.

After a player has purchased the box, it is added to their intenal lootbox history, which serves as a short term memory of their recent
lootboxes. Their buyThreshold is then updated depending on the value of the new box. 

By default, if the new lootbox they have purchased is worse than the last, their buyThreshold is decreased, reducing their likelyhood
to purchase in the future. If the new box is better than the old one, buyThreshold is increased so they are more likely
to buy again in the future. If a player is using the PRICE decision strategy, their threshold is updated according to a formula. 
The price of the old box is divided by its rarity, and the same is done for the new lootbox. A better box will have a smaller value returned
from this formula. If the old box has a smaller value than the new box, buyThreshold is decreased, and vice verse. This ends the turn for the player
who decided to purhcase.

If a player decides not to make a purchase, they instead compare themselves to their peers. The networks generated are all directed, and players
will compare themselves to a node they are looking at, rather than nodes looking at them (successors). If a node has no successors, a random node
is selected from the network. A player's lootbox rarity history is then averaged and compared to the successor node. If the player has better history than the node, 
the strength of the connection between them is decreased (or the edge is removed if it is equal to 0), and buyThreshold is decreased. If the node 
has better history than the player, the strength of the connection is incremented, and the player's buyThreshold is incremented by the weight of the 
connection between the nodes. If any manipulations are present, they are processed after this calculation.
This ends the turn for the player who decided not to purchase. 

## HOW TO USE IT

After selecting the parameter values for a specific run, click the blue power button at the top of the GUI to initialize the model. 
If you would like to slow down the model, on the bottom left panel labled "Run Options" increase the "Scheduled Tick Delay". 
Click the blue play button to start the model once you have selected the run options you want. 

- Num Players: the number of player agents interacting in the model

- Decision Strategy: The buying strategy each agent utilizes to decide if they want to make a purchase

	- ALWAYS_BUY: Players purchase a box every round, regardless of price/buyThreshold
	- COIN_FLIP: A number is generated 1-10, if it is <= buyThreshold they make the purchase. 
	- PRICE: buyThreshold is multiplied by the money an agent has available to determine how much they're willing to spend per box. 

- Manipulation:

	- NONE: no manipulation run
	- LIM_ED: a "Limited edition" event will run every 50 ticks, making players twice as likely to buy by multiplying their buyThreshold by two. 
	- FREE_BOX: a free lootbox is given to players every 10 ticks, and their buyThreshold is adjusted after recieving it. 
	- BIAS_BOX: the rarity of a lootbox is weighted depending on how popular a player is, more popular players recieve better odds, less popular players recieve normal odds.
	- FAV_PLAYER: the node with the most in-degrees/players looking at them will recieve heavily altered boxes where rarity is inverted. For example, the rarest box type will become the most commonly generated box for this player. 

- Network Type:

	- WATTS: watts small world network where players are generated in a one-dimensional ring lattice in which each vertex has k-neighbors and a few edges are randomly rewired. 
	- RANDOM: random density network where links between players are created depending on a uniformly generated random number
	- LATTICE: a 2d lattice network (n x n) where each node is incident with its four neighbors except for the edge vertices. Player number must be a perfect square for this network type to be generated

- Stop Time: the number of ticks the model will run for before terminating. 
	

## THINGS TO NOTICE

- Frequency of purchases
- Average amounts spent in PRICE mode
- How does each manipulation impact the player's spending?


## THINGS TO TRY

- Increase only the player number and observe how that impacts purchases 
- Observe how each manipulation plays out differently depending on the network type used

## EXTENDING THE MODEL

Many studies on adolescents (N Schuhmacher, P Van Geert, L Ballato) have shown that they demonstrate a tendency to adopt behaviors 
similar to those of their friends, or those who share interests with them. They also engage in risk-taking behaviors if peers who are 
similar to them participate in those same events. This model could be extended by allowing players to be influenced more strongly by 
those who are similar to them in such areas as age, gender, work-status, etc. 

Weighing the impact of each lootbox on buyThreshold is another improvement that could be made to the model. If a player spends a small 
amount of money and gets the rarest item possible, that should impact their future decisions much more sharply than if they recieved
an average box for an average sum of money.

Another improvement to the model concerns the drop rates of specific types of lootboxes. Implementing a rarity generation method which allows
users to input the real life rarity of different types of boxes from real games may allow for very interesting observations and projections. 

## REPAST SIMPHONY FEATURES

- Repast's network suite was utilized to create small world networks for agents that evolve over time
- Scheduled methods were used to update the model each tick
- NetworkGenerators, GridFactories, and ContinuousSpaceFactories were used in the JLootboxBuilder to create the views seen in the GUI   
- Built-in Preferential Attachment Network may be nice to model players basing their judgement on a single popular player in the game


## RELATED MODELS

- Agent-based model of risk behavior in adolescence, retrieved from: https://www.comses.net/codebases/3844/releases/1.1.0/

- El Farol, Netlogo Model Library

## CREDITS AND REFERENCES

Dr. Murphy for his area expertise and boundless patience!

Hing, N., Russell, A., Tolchard, B. et al. Risk Factors for Gambling Problems: An Analysis by Gender. J Gambl Stud 32, 511534 (2016). https://doi.org/10.1007/s10899-015-9548-8

N Schuhmacher, P Van Geert, L Ballato (2019, April 08). Agent-based model of risk behavior in adolescence (Version 1.1.0). CoMSES Computational Model Library. Retrieved from: https://www.comses.net/codebases/3844/releases/1.1.0/

North, MJ, NT Collier, J Ozik, E Tatara, M Altaweel, CM Macal, M Bragen, and P Sydelko, "Complex Adaptive Systems Modeling with Repast Simphony", Complex Adaptive Systems Modeling, Springer, Heidelberg, FRG (2013). https://doi.org/10.1186/2194-3206-1-3

Romanowska, Iza, et al. Agent-Based Modeling for Archaeology: Simulating the Complexity of Societies. Santa Fe Institute Press, 2021. 

Zendle David, Meyer Rachel and Over Harriet (2019) Adolescents and loot boxes: links with problem gambling and motivations for purchaseR. Soc. open sci!