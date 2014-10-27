package jade.ui;

import jade.util.datatype.Light;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public class ColorConstants {

	private static Map<String,Color> colors = new HashMap<String,Color>();
	private static Map<String,Light> lights = new HashMap<String,Light>();
	
	static{
		//TODO put all colors here
		//every color used in the csv file must be set here
		colors.put("brown", new Color(-3700147));
		colors.put("white", Color.white);
		colors.put("gray", new Color(-2632235));
		colors.put("darkgray", new Color(-11382961));
		colors.put("lightgray", new Color(-6579558));;
		colors.put("darkgreen", new Color(-16620269));
		colors.put("blood", new Color(-4521470));
		
		
		lights.put("minersLight", new Light(new Color(180,180,180),0,0,0,0));
		lights.put("torchLight", new Light(Color.orange,0,15,7,0));
		lights.put("flareLight", new Light(new Color(-10485981),10,10,10,0));
	}
	
	public static Light getLight(String name){
		if(!lights.containsKey(name)) throw new IllegalArgumentException("Color name "+name+" not defined in ColorConstants.lights.");
		return lights.get(name);
	}
	
	public static Color getColor(String name){
		if(!colors.containsKey(name)) throw new IllegalArgumentException("Color name "+name+" not defined in ColorConstants.colors.");
		return colors.get(name);
	}
}
