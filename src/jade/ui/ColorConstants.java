package jade.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorConstants {

	private static Map<String,Color> colors = new HashMap<String,Color>();
	
	static{
		//TODO put all colors here
		//every color used in the csv file must be set here
		colors.put("brown", new Color(-3700147));
		colors.put("white", Color.white);
		colors.put("gray", new Color(-2632235));
		colors.put("darkgray", new Color(-11382961));
		colors.put("darkgreen", new Color(-16620269));
		colors.put("blood", new Color(-4521470));
	}
	
	public static Color get(String name){
		return colors.get(name);
	}
}
