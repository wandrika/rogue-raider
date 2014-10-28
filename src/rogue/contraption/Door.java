package rogue.contraption;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public class Door extends Actor {
	private boolean open = false;
	private boolean locked = false;
	//mozno nahradit odkazom na objekt
	private int keyCode = 0; //ktory kluc pasuje do zamky, ak tam je zamka
	

	public Door(char face, int colorCode, int backgroundCode) {
		super(ColoredChar.create(face, new Color(colorCode), new Color(backgroundCode)));
	}
	
	@Override
	public void act() {

	}

	public boolean isOpen() {
		return open;
	}

	public void open() {
		if (!locked)
			this.open = true;
	}

	public boolean isLocked() {
		return locked;
	}

	public void unlock(int keyCode) {
		if (keyCode==this.keyCode)
			this.locked = false;
	}

}
