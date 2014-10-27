package rogue.items;

import rogue.contraption.LightSource;

public class Flare extends LightSource{	
	private boolean broken = false;
	private int timeToShine = 15;
	private int shining = 0;
	
	public Flare() {
		super('-', "flareLight", 3, 3, 30);
	}

	public void lightFlare(){
		broken = true;
		System.out.println("flare is lit");
	}
	
	@Override
	public void act() {
		if (broken){
			if(shining++ < timeToShine){
				super.act();
			}
			else{
				this.expire();
			}
		}
		
	}

}
