package rogue.creature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rogue.contraption.LightSource;
import rogue.items.Flare;

import jade.ui.Terminal;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

public class Player extends Creature
{
	private Terminal term;
	private List<Monster> enemiesSeen = new ArrayList<Monster>();
	//range is equal to the player's line of sight
	//fades only slightly
	public static LightSource minersLight = new LightSource(' ',"minersLight",true,15,15,90);

	public Player()
	{
		super('@');
		this.hitPoints = 15;
		this.actualHitPoints = 15;
		this.defense = 0;
		this.accuracy = 75;
		this.damageMin = 1;
		this.damageMax = 2;
		this.name = "you";
		this.experience = 0;
		this.healingSpeed = 20;
		this.sight = 15;
		minersLight.setCollectable(true);
		minersLight.attachTo(this);
	}

	//must be called immediately after creating the game terminals
	public void setTerminal(Terminal t){
		this.term = t;
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
	public void act()
	{
		Flare f;
		this.heal();
		try
		{
			int key;
			key = term.getKey();
			//System.out.println(key);
			switch(key)
			{
			// tu pridat ine ciselne kody
			case 81: //'q'
				expire();
				break;
			case 44: //','
				f = getLitFlare();
				if(f!=null){
					f.dropFromHolder();
				}
				else{
					f = this.getFirstHeldItem(Flare.class);
					if(f!=null) f.turnOn();
				}
				break;
			default:
				Direction dir = Direction.keyToDir(key);
				if(dir != null){
					move(dir);
					f = world.getActorAt(Flare.class, this.x(), this.y());
					if(f!=null) {
						world.removeActor(f);
						f.attachTo(this);
						System.out.println("I have the flare now");
					}
				}
				break;
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}


}
