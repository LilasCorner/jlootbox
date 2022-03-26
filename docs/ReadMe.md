## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

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

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## REPAST SIMPHONY FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
