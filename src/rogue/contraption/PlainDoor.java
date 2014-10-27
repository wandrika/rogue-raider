package rogue.contraption;

public class PlainDoor extends AbstractDoor {


	public PlainDoor(char face, int colorCode, int backgroundCode) {
		super(face, colorCode, backgroundCode);
	}

	@Override
	public void open(){
		this.open = true;
	}


	@Override
	public void act() {
		//obycajne dvere nerobia samy od seba nic
	}


}
