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
	public static LightSource minersLight = new LightSource(' ',"minersLight",15,15,90);

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
		minersLight.attach(this);
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

	@Override
	public void act()
	{
		this.heal();
		try
		{
			int key;
			key = term.getKey();
			switch(key)
			{
			// tu pridat ine ciselne kody
			case 'q':
				expire();
				break;
			case 'u':
				//TODO lepsie metody a collectable
				if(this.holds(Flare.class)!=null && !this.holds(Flare.class).isEmpty()){
					Flare f = (Flare) this.holds(Flare.class).toArray()[0];
					f.lightFlare();
				}
				break;
			default:
				Direction dir = Direction.keyToDir(key);
				if(dir != null)
					move(dir);
				Flare f = world.getActorAt(Flare.class, this.x(), this.y());
				if(f!=null) {
					f.setWorld(null);
					f.attach(this);
					System.out.println("I have the flare now");
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