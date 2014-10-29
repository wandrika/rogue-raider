package rogue.items;

import java.awt.Color;

import jade.core.Actor;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.MessageQueue;

public class Weapon extends Actor{

	int ammoPerShot;
	int maxShots;
	int actualShots = 0;
	int range;
	int maxDamage;
	int minDamage;
	
	public Weapon(String name, int ammoPerShot, int maxShots, int range, int maxDamage, int minDamage) {
		super(ColoredChar.create((char)172, Color.lightGray));
		this.collectable = true;
		this.name = name;
		this.ammoPerShot = ammoPerShot;
		this.maxShots = maxShots;
		this.actualShots = maxShots;
		this.range = range;
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
	}

	public int getRange() {
		return range;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public int getMinDamage() {
		return minDamage;
	}
	
	public int getAmmoPerShot() {
		return ammoPerShot;
	}

	@Override
	public void act() {
	}
	
	/**
	 * Shoots from the weapon if possible.
	 * @return how much ammo was actually shot
	 */
	public int shoot(){
		if(actualShots>0){
			actualShots--;
			return ammoPerShot;
		}
		else{
			MessageQueue.add("You have to reload!");
			return 0;
		}
	}

}
