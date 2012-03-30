package rogue.creature;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.Actor;
import jade.fov.RayCaster;
import jade.ui.Camera;
import jade.ui.ColorConstants;
import jade.util.Capitalizer;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.MessageQueue;

public abstract class Creature extends Actor implements Camera
{
	
	protected int hitPoints;
	protected int actualHitPoints;
	protected int accuracy;
	protected int defense;
	protected int damageMin;
	protected int damageMax;
	protected int sight; //ako daleko vidi
	protected int experience;
	protected int healingSpeed; //za kolko tahov zregeneruje 1 HP
	protected int healing;
	
	protected String name;
	
	protected RayCaster view = new RayCaster();
	
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
		super(ColoredChar.create(face, ColorConstants.get(color), ColorConstants.get(background)));
	}
	
	public Creature(char face, int colorCode) {
		super(ColoredChar.create(face, new Color(colorCode)));
	}
	
	public Creature(char face, String color) {
		super(ColoredChar.create(face, ColorConstants.get(color)));
	}
	
	public Creature(char face) {
		super(ColoredChar.create(face));

	}

	public void setProperties(Map<String,String> props) throws MonsterMapException{
		for (Entry<String, String> entry: props.entrySet()){
			try{
				Field f = Creature.class.getDeclaredField(entry.getKey());
				//mame len fieldy typu String a int
				//ak pribudne boolean alebo nieco ine, pridat sem nove ify
				try {
					if (f.getType().equals(String.class)) {
						f.set(this, entry.getValue());
					}
					else{
						f.setInt(this, Integer.parseInt(entry.getValue()));
					}
				} catch (IllegalArgumentException e) {
					throw new MonsterMapException(e.getMessage(),e);
				} catch (IllegalAccessException e) {
					throw new MonsterMapException(e.getMessage(),e);
				}

			}
			catch (NoSuchFieldException nsf){
				//nic nerobit, take pole sa nenamapuje
			}
		}
		this.actualHitPoints = this.hitPoints;
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

    void heal(){
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
		
	public String getName() {
		return name;
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
							if(this instanceof Player) color = ColorConstants.get("darkgreen");
							else color = ColorConstants.get("blood");
							MessageQueue.add(Capitalizer.capitalize(this.name)+" hit "+enemy.name+" who has "+enemy.actualHitPoints+" hitpoints remaining.", color);
						}
						else {
							MessageQueue.add(Capitalizer.capitalize(this.name)+" killed "+enemy.name+".");
							this.addExperience(1);
							this.accuracy += 1;
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

	@Override
	public Collection<Coordinate> getViewField() {
		return view.getViewField(this.world(), this.pos(), this.sight);
	}
	

}
