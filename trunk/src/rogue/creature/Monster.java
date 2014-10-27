package rogue.creature;

import jade.util.Dice;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Direction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Monster extends Creature 
{
	protected Status status;
	protected int minLevel;		//na ktorych leveloch sa moze prisera vyskytovat
	protected int maxLevel;
	protected Coordinate lastSeenEnemyPos = null;

	//TODO remove unnecessary constructors

	public Monster(char face, int colorCode, int backgroundCode) {
		super(face, colorCode, backgroundCode);
		this.status = Status.WANDERING;
	}

	public Monster(char face, String color, String background) {
		super(face, color, background);
		this.status = Status.WANDERING;
	}

	public Monster(char face, int colorCode) {
		super(face, colorCode);
		this.status = Status.WANDERING;
	}

	public Monster(char face, String color) {
		super(face, color);
		this.status = Status.WANDERING;
	}

	public Monster(char face) {
		super(face);
		this.status = Status.WANDERING;
	}

	public void setProperties(Map<String,String> props) throws MonsterMapException{
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(Monster.class.getDeclaredFields())); 
		fields.addAll(Arrays.asList(Creature.class.getDeclaredFields())); 
		for (Entry<String, String> entry: props.entrySet()){

			Field f = getFieldByName(fields, entry.getKey());
			if (f!=null){
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

		}
		this.actualHitPoints = this.hitPoints;
	}

	private Field getFieldByName(List<Field> fields, String name){
		for (Field f: fields){
			if (f.getName().equals(name)) return f;
		}
		return null;
	}

	public boolean seesEnemy()
	{
		Collection<Coordinate> coVidiTvor = this.getViewField();
		for (Coordinate coord: coVidiTvor){
			Creature cre = world().getActorAt(Creature.class, coord);
			if (cre instanceof Player) {

				lastSeenEnemyPos = coord;
				//System.out.println("I see @");
				return true;
			}
		}
		return false;
	}

	@Override
	public void act()
	{
		if (this.actualHitPoints>0){
			this.heal();
			switch (this.status)
			{
			case HUNTING:
			{
				boolean seen = this.seesEnemy();
				if (!seen && this.pos().equals(lastSeenEnemyPos)){
					this.status = Status.WANDERING;
					lastSeenEnemyPos = null;
					move(Dice.global.choose(Arrays.asList(Direction.values())));
					System.out.println(this.name+" starts wandering.");
				}
				else {
					List<Coordinate> whereToGo = world().pathFinder.getPath(world(), this.pos(), this.lastSeenEnemyPos);
					this.setPos(whereToGo.get(0));
				}
				break;
			}
			case WANDERING:
			{
				move(Dice.global.choose(Arrays.asList(Direction.values())));
				if (this.seesEnemy()){
					this.status = Status.HUNTING;
					System.out.println(this.name+" starts hunting.");
				}
				break;
			}
			default: move(Dice.global.choose(Arrays.asList(Direction.values())));
			}
		}

	}

	@Override
	public String toString() {
		return "Monster [status=" + status + ", minLevel=" + minLevel
				+ ", maxLevel=" + maxLevel + ", lastSeenEnemyPos="
				+ lastSeenEnemyPos + ", hitPoints=" + hitPoints
				+ ", actualHitPoints=" + actualHitPoints + ", accuracy="
				+ accuracy + ", defense=" + defense + ", damageMin="
				+ damageMin + ", damageMax=" + damageMax + ", sight=" + sight
				+ ", experience=" + experience + ", healingSpeed="
				+ healingSpeed + ", healing=" + healing + ", name=" + name
				+ "]";
	}


}
