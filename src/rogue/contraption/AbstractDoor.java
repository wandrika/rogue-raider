package rogue.contraption;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;

public abstract class AbstractDoor extends Actor {
	protected boolean open = false;

	public AbstractDoor(char face, int colorCode, int backgroundCode) {
		super(ColoredChar.create(face, new Color(colorCode), new Color(backgroundCode)));
	}

	public boolean isOpen() {
		return open;
	}

	public abstract void open();


}
