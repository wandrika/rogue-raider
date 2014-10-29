package rogue.creature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rogue.contraption.LightSource;
import rogue.items.Flare;
import rogue.items.Weapon;

import jade.ui.Terminal;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;
import jade.util.datatype.MessageQueue;

public class Player extends Creature
{
	private Terminal term;
	private List<Monster> enemiesSeen = new ArrayList<Monster>();
	//range is equal to the player's line of sight
	//fades only slightly
	//public static LightSource minersLight = new LightSource(' ',"minersLight",true,15,15,90);
	public static Weapon pistols = new Weapon("pistols",2,100000,6,1,1);

	public Player()
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
		//minersLight.setCollectable(true);
		//minersLight.attachTo(this);
		pistols.attachTo(this);
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
//			case 81: //'q'
//				expire();
//				break;
			case 70: //'f'
				if(!enemiesSeen.isEmpty()){
					Monster chosen = enemiesSeen.get(0);
					term.highlight(this.aim(chosen.x(), chosen.y()));
					//teraz musi hrac zmenit target alebo potvrdit vyber
					int enemyIndex = 0;
					int nextKey=0;
					do {
						nextKey = term.getKey();
						if (nextKey==84){ //'t' change target
							enemyIndex = (enemyIndex+1) % enemiesSeen.size();
							chosen = enemiesSeen.get(enemyIndex);
							term.highlight(this.aim(chosen.x(), chosen.y()));
						}
					}while (nextKey!=10 && nextKey!=27);
					if(nextKey == 10) this.shoot(chosen.x(), chosen.y(), pistols);
				}
				break;
			case 44: //','
				f = getLitFlare();
				if(f!=null){
					f.dropFromHolder();
				}
				else{
					f = this.getFirstHeldItem(Flare.class);
					if(f!=null) f.turnOn();
					else MessageQueue.add("You have no more flares.");
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
