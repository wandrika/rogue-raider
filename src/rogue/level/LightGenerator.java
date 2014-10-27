package rogue.level;

import rogue.contraption.LightSource;
import rogue.items.Flare;
import jade.core.World;

public class LightGenerator {

	public void addLights(World world, int numberOfLights){
		for (int i=0; i<numberOfLights; i++){
			addTorch(world);
		}
		addFlare(world);
		addFlare(world);
		addFlare(world);
		addFlare(world);
		addFlare(world);
	}
	
	
	public void addTorch(World world){
		//&torchLightColor,		{1000, 1000, 1},		50,		false},		// torch
		LightSource newLight = new LightSource('^',"torchLight",5,7,20);
        world.addActor(newLight);
	}
	
	public void addFlare(World world){
		//&torchLightColor,		{1000, 1000, 1},		50,		false},		// torch
		Flare newLight = new Flare();
        world.addActor(newLight);
	}
}
