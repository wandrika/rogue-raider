package rogue.items;

import rogue.contraption.LightSource;

public class Flare extends LightSource{	
	private int timeToShine = 15;
	private int shining = 0;
	
	public Flare() {
		super('-', "flareLight", false, 3, 3, 30);
	}
	
	@Override
	public void act() {
		if (isLit){
			if(shining++ < timeToShine){
				super.act();
			}
			else{
				this.expire();
			}
		}
		
	}

}
