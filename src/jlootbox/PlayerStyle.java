/**
 * 
 */
package jlootbox;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import java.awt.Color;
import repast.simphony.gis.styleEditor.SimpleMarkFactory;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

/**
 * @author Lilo
 *
 */

//will be implemented if enough time remaining, 
//idea is to change player color depending on their loot history rarity
public class PlayerStyle extends DefaultStyleOGL2D {

    @Override
    public Color getColor(Object agent) {
        Player player = (Player)agent;

        switch (player.avgHistValue()) {
	    	case 1: //common
	    		return Color.GRAY;
	    	case 2: //rare
	    		return Color.BLUE;
	    	case 3: //epic
	    		return Color.MAGENTA;
	    	case 4: //legendary
	    		return Color.YELLOW;
	    	case 5: //limited_edition
	    		return Color.RED;
	    	default:
	    		return Color.BLACK;
        
        }
    }

    @Override
    public Color getBorderColor(Object agent) {
    	Player player = (Player)agent;
        if (player.getPurchased()) {
            return Color.decode("#CDA50A");
        }
        return Color.black;
    }
    
    @Override
    public int getBorderSize(Object agent) {
        return 20;
    }
    
    @Override
    public float getScale(Object agent) {
        return 1;
    }
	
	
	
}
