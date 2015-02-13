package rogue.creature;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rogue.items.Weapon;
import rogue.player.Player;

import jade.core.Actor;
import jade.fov.ModifiedRayCaster;
import jade.path.Bresenham;
import jade.ui.Camera;
import jade.ui.ColorConstants;
import jade.util.Capitalizer;
import jade.util.Dice;
import jade.util.Guard;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.MessageQueue;

public abstract class Creature extends Actor implements Camera
{

	protected int hitPoints;
	protected int actualHitPoints;
	protected int accuracy;
	protected int shootingAccuracy;
	protected int defense;
	protected int damageMin;
	protected int damageMax;
	protected int sight; //ako daleko vidi
	protected int experience;
	protected int healingSpeed; //za kolko tahov zregeneruje 1 HP
	protected int healing;
	protected int inventorySize = 0;
	//only for physical collectible items
	protected Set<Actor> inventory = new HashSet<Actor>();

	protected ModifiedRayCaster view = new ModifiedRayCaster();
	protected Bresenham aimer = new Bresenham();
	protected Coordinate actualAim;

	//protected ShadowCaster view = new ShadowCaster();

	/* Combat rules:
	 * Each combatant has an accuracy rating. This is the percentage of their attacks that will ordinarily hit;
	 * higher numbers are better for them. Numbers over 100 are permitted.
	 *
	 * Each combatant also has a defense rating. The "hit probability" is calculated as given by this formula:
	 * 
	 * 			hit probability = (accuracy) * 0.986 ^ (defense)
	 * 
	 * when hit determinations are made. Negative numbers and numbers over 100 are permitted.
	 * The hit is then randomly determined according to this final percentage.
	 *
	 * Some environmental factors can modify these numbers. An unaware, sleeping or stuck enemy is always hit.
	 *
	 * If the hit lands, damage is calculated in the range provided. However, the clumping factor affects the
	 * probability distribution. If the range is 0-10 with a clumping factor of 1, it's a uniform distribution.
	 * With a clumping factor of 2, it's calculated as 2d5 (with d5 meaing a die numbered from 0 through 5).
	 * With 3, it's 3d3, and so on. Note that a range not divisible by the clumping factor is defective,
	 * as it will never be resolved in the top few numbers of the range. In fact, the top
	 * (rangeWidth % clumpingFactor) will never succeed. Thus we increment the maximum of the first
	 * (rangeWidth % clumpingFactor) dice by 1, so that in fact 0-10 with a CF of 3 would be 1d4 + 2d3. Similarly,
	 * 0-10 with CF 4 would be 2d3 + 2d2. By playing with the numbers, one can approximate a gaussian
	 * distribution of any mean and standard deviation.
	 *
	 * Player combatants take their base defense value of their actual armor. Their accuracy is a combination of weapon, armor
	 * and strength. Each weapon and armor has a strength factor, and each point short of that number means an marginal -15
	 * accuracy for the player.
	 * 
	 * Players have a base accuracy value of 75. This goes up by 10 with each experience level gained past 1.
	 */ 

	public Creature(char face, int colorCode, int backgroundCode) {
		super(ColoredChar.create(face, new Color(colorCode), new Color(backgroundCode)));
	}

	public Creature(char face, String color, String background) {
		super(ColoredChar.create(face, ColorConstants.getColor(color), ColorConstants.getColor(background)));
	}

	public Creature(char face, int colorCode) {
		super(ColoredChar.create(face, new Color(colorCode)));
	}

	public Creature(char face, String color) {
		super(ColoredChar.create(face, ColorConstants.getColor(color)));
	}

	public Creature(char face) {
		super(ColoredChar.create(face));

	}

	@Override
	public void attachItem(Actor item){
		Guard.argumentIsNotNull(item);
		//this is for collectible items
		if (item.isCollectible()){
			if(this.inventory.size() < inventorySize){
				super.attachItem(item);
				inventory.add(item);
			}
		}
		else{
			super.attachItem(item);
		}
	}

	@Override
	public void dropItem(Actor item) {
		Guard.verifyState(item.isHeldBy(this));
		if(item.isCollectible()){
			inventory.remove(item);
		}
		super.dropItem(item);
	}

	public List<Coordinate> aim(int x, int y){
		//remember where we aim
		actualAim = new Coordinate(x,y);
		return aimer.getPath(world, this.x(), this.y(), x, y);
	}

	public Coordinate getActualAim() {
		return actualAim;
	}

	public void setActualAim(Coordinate actualAim) {
		this.actualAim = actualAim;
	}

	void addExperience(int exp) {	
		this.experience += exp;
		//    	while (rogue.experience >= levelPoints[rogue.experienceLevel - 1] && rogue.experienceLevel < MAX_EXP_LEVEL) {
		//    		rogue.experienceLevel++;
		//    		player.info.maxHP += 5;
		//    		player.currentHP += (5 * player.currentHP / (player.info.maxHP - 5));
		//    		updatePlayerRegenerationDelay();
		//    		player.info.accuracy += 10;
		//    		rogue.gainedLevel = true;
		//    	}
	}

	protected void heal(){
		if (this.healingSpeed>0 && this.actualHitPoints<this.hitPoints){
			this.healing +=1;
			if (this.healing >= this.healingSpeed){
				this.actualHitPoints += 1;
				this.healing -= this.healingSpeed;
				//MessageQueue.add(Capitalizer.capitalize(this.name)+" have "+this.actualHitPoints+" hitpoints now.");
			}
		}

	}

	public int getHitPoints() {
		return hitPoints;
	}

	public int getActualHitPoints() {
		return actualHitPoints;
	}


	protected void decreaseHitPoints(int amount) {
		this.actualHitPoints = this.actualHitPoints - amount;
		if (this.actualHitPoints<=0) this.expire();
	}

	@Override
	public void setPos(int x, int y)
	{
		if(world().passableAt(x, y)){
			if (world().isCreatureAt(x,y)) 
			{
				Creature enemy = world().getActorAt(Creature.class, x, y);
				if (enemy != this){

					double probability = accuracy * Math.pow(0.986, enemy.defense);
					int prob = Math.min(100, (int)probability);

					if (Dice.global.chance(prob)){
						//zasah!
						int damage = Dice.global.nextInt(enemy.damageMin, enemy.damageMax);
						enemy.decreaseHitPoints(damage);
						if (enemy.actualHitPoints >0){
							Color color;
							if(this instanceof Player) color = ColorConstants.getColor("darkgreen");
							else color = ColorConstants.getColor("blood");
							MessageQueue.add(Capitalizer.capitalize(this.name)+" hit "+enemy.name+" who has "+enemy.actualHitPoints+" hitpoints remaining.", color);
						}
						else {
							MessageQueue.add(Capitalizer.capitalize(this.name)+" killed "+enemy.name+".");
							this.addExperience(1);
							this.accuracy = Math.min(100, accuracy+1);
						}
					}
					else{
						//miss
						MessageQueue.add(Capitalizer.capitalize(this.name)+" missed "+enemy.name+".");
					}
				}
			}
			else super.setPos(x, y);
		}
	}

	public void shoot(int x, int y, Weapon gun){
		if (world().isCreatureAt(x,y)) {
			Creature enemy = world().getActorAt(Creature.class, x, y);
			int prob = shootingAccuracy;
			if (Dice.global.chance(prob)){
				//zasah!
				gun.shoot();
				int damage = Dice.global.nextInt(gun.getMinDamage(), gun.getMaxDamage());
				enemy.decreaseHitPoints(damage);
				if (enemy.actualHitPoints >0){
					Color color;
					if(this instanceof Player) color = ColorConstants.getColor("darkgreen");
					else color = ColorConstants.getColor("blood");
					MessageQueue.add(Capitalizer.capitalize(this.name)+" shot "+enemy.name+" who has "+enemy.actualHitPoints+" hitpoints remaining.", color);
				}
				else {
					MessageQueue.add(Capitalizer.capitalize(this.name)+" killed "+enemy.name+".");
					this.addExperience(1);
					this.shootingAccuracy = Math.min(100, shootingAccuracy+1);
				}
			}
			else{
				//miss
				MessageQueue.add(Capitalizer.capitalize(this.name)+" missed "+enemy.name+".");
			}
		}
		//TODO vieme strielat aj do inych veci?
		actualAim = null;
	}

	@Override
	public Collection<Coordinate> getViewField() {
		return view.getViewField(this.world(), this.pos(), this.sight);
	}


}
