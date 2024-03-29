package rogue.level;

import jade.core.World;
import jade.gen.Generator;
import jade.gen.map.*;
import jade.util.datatype.Coordinate;
import rogue.creature.Creature;
import rogue.player.Player;

public class Level extends World
{
    private final static Generator gen = getLevelGenerator();

    public Level(int width, int height, Player player)
    {
        super(width, height);
        gen.generate(this);
        LightGenerator.addLights(this, 10);
        addActor(player);
    }

    private static Generator getLevelGenerator()
    {
        return new Traditional();
    }
    
    public boolean isCreatureAt(int x, int y)
    {
        return (this.getActorAt(Creature.class, x, y) != null);
    }
    
    public boolean isCreatureAt(Coordinate coord)
    {
        return isCreatureAt(coord.x(), coord.y());
    }
}
