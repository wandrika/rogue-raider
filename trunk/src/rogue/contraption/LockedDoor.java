package rogue.contraption;

public class LockedDoor extends AbstractDoor {
	private boolean locked = true;
	private long keyCode;

	public LockedDoor(char face, int colorCode, int backgroundCode, long keyCode) {
		super(face, colorCode, backgroundCode);
		this.keyCode = keyCode;
	}

	public boolean isLocked() {
		return locked;
	}

	public void unlock(long keyCode) {
		if (this.keyCode == keyCode) this.locked = false;
	}

	@Override
	public void open(){
		if (!this.locked) this.open = true;
	}


	@Override
	public void act() {
		//zamykacie dvere nerobia samy od seba nic
		//TODO pozri sa na brogue Architect.c, ako sa generuje dungeon s dverami
	}


}
