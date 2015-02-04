package rogue.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rogue.contraption.LightSource;
import rogue.creature.Creature;
import rogue.creature.Monster;
import rogue.items.Flare;
import rogue.items.Weapon;

import jade.ui.Terminal;
import jade.util.datatype.Coordinate;

public class Player extends Creature
{
	//we have singleton player instance
	private static Player playerInstance = new Player();
	public static Player getInstance(){
		return playerInstance;
	}
	
	Terminal term;
	List<Monster> enemiesSeen = new ArrayList<Monster>();
	int enemyIndex = 0;
	//range is equal to the player's line of sight
	//fades only slightly
	public static LightSource minersLight = new LightSource(' ',"minersLight",true,15,15,90);
	public static Weapon pistols = new Weapon("pistols",2,100000,6,1,1);

	private PlayerState state = PlayerState.MOVING;
	boolean moveFinished = false;

	private Player()
	{
		super('@');
		this.hitPoints = 15;
		this.actualHitPoints = 15;
		this.defense = 0;
		this.accuracy = 75;
		this.shootingAccuracy = 50;
		this.damageMin = 1;
		this.damageMax = 2;
		this.name = "you";
		this.experience = 0;
		this.healingSpeed = 20;
		this.sight = 15;

	}

	public void initPlayer(){
		//init the inventory here
		pistols.attachTo(this);
		minersLight.setCollectable(true);
		minersLight.attachTo(this);
	}
	
	//must be called immediately after creating the game terminals
	public void setTerminal(Terminal t){
		this.term = t;
	}

	public int getEnemyIndex() {
		return enemyIndex;
	}

	public void setEnemyIndex(int enemyIndex) {
		this.enemyIndex = enemyIndex;
	}
	
	public void lookForEnemies()
	{
		enemiesSeen.clear();
		Collection<Coordinate> coVidiHrac = this.getViewField();
		for (Coordinate coord: coVidiHrac){
			Creature cre = world().getActorAt(Creature.class, coord);
			if (cre instanceof Monster) {
				enemiesSeen.add((Monster)cre);
			}
		}
	}

	public List<Monster> getEnemies(){
		lookForEnemies();
		return enemiesSeen;
	}

	/**
	 * Looks for a lit flare in player's inventory. If not found, returns null.
	 * @return reference to the lit flare
	 */
	public Flare getLitFlare(){
		for (Flare f: this.getHeldItems(Flare.class)){
			if(f.isLit()) return f;
		}
		return null;
	}

	@Override
	public void act(){

		this.heal();
		moveFinished = false;
		while (!moveFinished){
			handleInput();
		}

	}

	private void handleInput(){
		int key = 0;
		try{
			key = term.getKey();
			state = state.handleInput(key);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

	}

}
