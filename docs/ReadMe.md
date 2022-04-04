## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

This model demonstrates how gambling-like behavior can emerge in lootbox spending within gaming communities. A lootbox is 
a purchasable mystery box that randomly awards the player a series of in-game items. Since the contents of the box are
largely up to chance, many players can fall into a compulsion loop of purchasing, as the fear of missing out and belief
in the gambler's fallacy allow one to rationalize repeated purchases, especially when one compares their own luck to others. 

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

Each tick, every agent decides if they want to purchase or not based on the decision structure defined by Decision Strategy. 
To determine if an agent is satisfied with their purchase, different logic is run for each decision structure. By default, 
if the new lootbox they have purchased is worse than the last, their buyThreshold is decreased, reducing their likelyhood
to purchase in the future. If the new box is better than the old one, buyThreshold is increased so they are more likely
to buy again in the future. For the Price decision structure, if the rarity of the new lootbox is not as good as the price
the player paid for it, buyThreshold goes down, and vice versa (TEMP). 


## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

After selecting the parameter values for a specific run, click the blue power button at the top of the s
If you would like to slow down the model, on the bottom left panel labled "Run Options" increase the "Scheduled Tick Delay". 

- # Players: the number of player agents interacting in the model
- Buy Threshold Max/Min: 
- Decision Strategy: The buying strategy each agent utilizes to decide if they want to make a purchase
	- ALWAYS_BUY: Players purchase a box every round, regardless of price/buyThreshold
	- COIN_FLIP: A number is generated 1-10, if it is <= buyThreshold they make the purchase. 
	- PRICE: buyThreshold is multiplied by the money an agent has available to determine how much they're willing to spend per box. 
	

## THINGS TO NOTICE

- Frequency of purchases
- Average amounts spent in PRICE mode
- How does each manipulation impact the player's spending?

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

Many studies on adolescents (model linked below) have shown that they demonstrate a tendency to adopt behaviors similar to those of their friends,
or those who share interests with them. They also engage in risk-taking behaviors if peers who are similar to them participate
in those same events. This model could be extended by allowing players to be influenced more strongly by those who are similar to
them in such areas as age, gender, work-status, etc. 

Weighing the impact of each lootbox on buyThreshold is another improvement that could be made to the model. If a player spends a small 
amount of money and gets the rarest item possible, that should impact their future decisions much more sharply than if they recieved
an average box for an average sum of money.

## REPAST SIMPHONY FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)
- Repast's network suite was utilized to create small world networks for agents that evolve over time
- Built-in Preferential Attachment Network may be nice to model players basing their judgement on a single popular youtuber/streamer playing


## RELATED MODELS

- Agent-based model of risk behavior in adolescence


## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)

- Insert COMSES link  


- Hing, N., Russell, A., Tolchard, B. et al. Risk Factors for Gambling Problems: An Analysis by Gender. J Gambl Stud 32, 511–534 (2016). https://doi.org/10.1007/s10899-015-9548-8


- N Schuhmacher, P Van Geert, L Ballato (2019, April 08). “Agent-based model of risk behavior in adolescence” (Version 1.1.0). CoMSES Computational Model Library. Retrieved from: https://www.comses.net/codebases/3844/releases/1.1.0/


- Zendle David, Meyer Rachel and Over Harriet (2019) Adolescents and loot boxes: links with problem gambling and motivations for purchaseR. Soc. open sci.6190049190049
http://doi.org/10.1098/rsos.190049