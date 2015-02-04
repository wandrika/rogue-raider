package rogue.items;

import rogue.contraption.LightSource;

public class Flare extends LightSource{	
	private int timeToShine = 30;
	private int shining = 0;
	
	public Flare() {
		super('-', "flareLight", false, 6, 6, 30);
		this.collectable = true;
	}
	
	@Override
	public void act() {
		if (isLit){
			if(shining++ <= timeToShine){
				super.act();
			}
			else{
				this.expire();
			}
		}
		
	}

}
