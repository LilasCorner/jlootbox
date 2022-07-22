/**
 * 
 */
package jlootbox;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import java.awt.Color;
import java.awt.Font;

import jlootbox.Player.Manipulate;
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

        if(player == Platform.favorite && Player.manip == Manipulate.FAV_PLAYER) {
        	return Color.green;
        }
        
        switch ((int)player.avgHistValue()) {
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
            return Color.green;
        }
        return Color.red;
    }
    
    @Override
    public int getBorderSize(Object agent) {
        return 200;
    }
    
    @Override
    public float getScale(Object agent) {
        return 1;
    }
    
    @Override
    public Color getLabelColor(Object agent) {
    	return Color.BLACK;
    }
    
    @Override
    public Font getLabelFont(Object agent) {
    	return new Font ("Calibri", Font.BOLD , 15);
    }
    
    @Override
    public float getLabelYOffset(Object agent) { 
    	return 10;
    }
    
	@Override
	public String getLabel(Object agent) {
    	Player player = (Player)agent;
		return player.toString();
	}
	
}
