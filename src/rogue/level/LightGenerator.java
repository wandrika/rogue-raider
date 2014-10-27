package rogue.level;

import rogue.contraption.LightSource;
import jade.core.World;

public class LightGenerator {

	public void addLights(World world, int numberOfLights){
		for (int i=0; i<numberOfLights; i++){
			addLight(world);
		}
	}
	
	
	public void addLight(World world){
		//&torchLightColor,		{1000, 1000, 1},		50,		false},		// torch
		LightSource newLight = new LightSource('^',"torchLight",0,0,5,7,20);
        world.addActor(newLight);
	}
}
