package rogue.level;

import rogue.contraption.LightSource;
import rogue.items.Flare;
import jade.core.World;

public class LightGenerator {

	public void addLights(World world, int numberOfLights){
		for (int i=0; i<numberOfLights; i++){
			addTorch(world);
		}
		for (int i=0; i<numberOfLights; i++){
			addFlare(world);
		}
	}
	
	
	public void addTorch(World world){
		LightSource newLight = new LightSource('^',"torchLight",true,5,7,20);
        world.addActor(newLight);
	}
	
	public void addFlare(World world){
		Flare newLight = new Flare();
        world.addActor(newLight);
	}
}
