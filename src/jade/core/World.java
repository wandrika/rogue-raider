package jade.core;

import jade.path.AStar;
import jade.path.PathFinder;
import jade.ui.ColorConstants;
import jade.util.Dice;
import jade.util.Guard;
import jade.util.Lambda;
import jade.util.Lambda.FilterFunc;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Light;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rogue.contraption.AbstractDoor;
import rogue.contraption.LightSource;
import rogue.creature.Creature;
import rogue.creature.Monster;
import rogue.player.Player;

/**
 * Represents a game world on which {@code Actor} can interact.
 */
public abstract class World 
{
    private int width;
    private int height;
    private Tile[][] grid;
    private Set<Actor> register;
    private List<Class<? extends Actor>> drawOrder;
    private List<Class<? extends Actor>> actOrder;
    public PathFinder pathFinder = new AStar();
    protected Color baseLight;
    
    /**
     * Constructs a new {@code World} with the given dimensions. Both width and height must be
     * positive integers.
     * @param width the width of the new {@code World}
     * @param height the height of the new {@code World}
     */
    public World(int width, int height)
    {
        Guard.argumentsArePositive(width, height);
        
        baseLight = new Color(-8882056);
        this.width = width;
        this.height = height;
        grid = new Tile[width][height];
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                grid[x][y] = new Tile();
        register = new HashSet<Actor>();

        drawOrder = new ArrayList<Class<? extends Actor>>();
        //player a prisery sa zobrazuju nad vsetkym ostatnym?
        drawOrder.add(Creature.class);
        drawOrder.add(AbstractDoor.class);
        drawOrder.add(LightSource.class);
        
        actOrder = new ArrayList<Class<? extends Actor>>();
        //v akom poradi sa deju akcie
        //TODO ak budeme mat dvere, kuzla a dalsie actory, pridat ich sem (len take, ktore nieco robia)
        
        actOrder.add(Player.class);
        actOrder.add(Monster.class);
        actOrder.add(LightSource.class);
        //TODO najst to spravne poradie
   }

    /**
     * Performs one tick. This will call {@code act()} on all {@code Actor} in the order specified
     * by the act order of the {@code World}. Any {@code Actor} whose type does not appear in the
     * act order does not act. Any expired {@code Actor} are removed from the {@code World}.
     */
    public void tick()
    {
    	resetLight();
        for(Class<? extends Actor> cls : actOrder)
            for(Actor actor : getActors(cls))
                actor.act();

        removeExpired();
    }
    
    /**
     * Returns the width of the {@code World}.
     * @return the width of the {@code World}
     */
    public int width()
    {
        return width;
    }

    /**
     * Returns the height of the {@code World}.
     * @return the height of the {@code World}
     */
    public int height()
    {
        return height;
    }

    /**
     * Returns the draw order for the {@code World}. This list will affect the {@code lookAll()}
     * method by changing the priority with which {@code Actor}s will be drawn. Note that if one
     * class in the draw order is a superclass of another in the list, any {@code Actor} of the type
     * of the subclass will be seen twice in the {@code lookAll()} method.
     * @return the draw order for the {@code World}
     */
    public List<Class<? extends Actor>> getDrawOrder()
    {
        return drawOrder;
    }

    /**
     * Returns the act order for the {@code World}. This list will affect the {@code tick()} method
     * by changing the priority with which {@code Actor}s will act. Note that if one class in the
     * draw order is a superclass of another in the list, any {@code Actor} of the type of the
     * subclass will act twice in the {@code tick()} method.
     * @return the act order for the {@code World}
     */
    public List<Class<? extends Actor>> getActOrder()
    {
        return actOrder;
    }

    /**
     * Adds an {@code Actor} to the {@code World} at the specified location.
     * @param actor the {@code World} being added
     * @param x the x location of the {@code Actor}
     * @param y the y location of the {@code Actor}
     */
    public void addActor(Actor actor, int x, int y)
    {
        Guard.argumentIsNotNull(actor);
        Guard.argumentsInsideBounds(x, y, width, height);
        Guard.verifyState(!actor.bound());
        Guard.verifyState(!actor.held());

        actor.setWorld(this);
        actor.setXY(x, y);
        addToGrid(actor);
        registerActor(actor);
    }

    /**
     * Adds an {@code Actor} to the {@code World} at the specified location.
     * @param actor the {@code World} being added
     * @param coord the location of the {@code Actor}
     */
    public final void addActor(Actor actor, Coordinate coord)
    {
        Guard.argumentIsNotNull(coord);

        addActor(actor, coord.x(), coord.y());
    }

    /**
     * Adds an {@code Actor} to the {@code World} at a random open tile, as generated by the
     * specified {@code Dice} with a call to {@code getOpenTile()}/
     * @param actor the {@code Actor} to be added
     * @param dice the random number generator used to find the open tile
     */
    public final void addActor(Actor actor, Dice dice)
    {
        addActor(actor, getOpenTile(dice));
    }

    /**
     * Adds an {@code Actor} to the {@code World} at a random open tile, as generated by the global
     * instance of {@code Dice} with a call to {@code getOpenTile()}/
     * @param actor the {@code Actor} to be added
     */
    public final void addActor(Actor actor)
    {
        addActor(actor, getOpenTile());
    }

    /**
     * Removes an {@code Actor} from the {@code World}. The {@code Actor} must be both bound to this
     * {@code World} and not held by another {@code Actor}. However, if the {@code Actor} is
     * expired, it may always be removed.
     * @param actor the {@code Actor} to be removed
     */
    public void removeActor(Actor actor)
    {
        Guard.argumentIsNotNull(actor);
        Guard.verifyState(actor.bound(this));
        Guard.verifyState(!actor.held() || actor.expired());

        unregisterActor(actor);
        removeFromGrid(actor);
        actor.setWorld(null);
    }

    /**
     * Removes all expired {@code Actor} from the {@code World}.
     */
    public void removeExpired()
    {
        Iterable<Actor> expired = Lambda.filter(register, new FilterFunc<Actor>()
        {
            @Override
            public boolean filter(Actor element)
            {
                return element.expired();
            }
        });

        for(Actor actor : expired){
            removeActor(actor);
            System.out.println("removing "+actor);
        }
    }

    /**
     * Returns an {@code Actor} of the given class located at (x, y), or null if there is none. If
     * there are multiple possible {@code Actor} that could be returned, an arbitrary {@code Actor}
     * is returned.
     * @param <T> the generic type of the class to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @param x the x location being queried
     * @param y the y location being queried
     * @return an {@code Actor} of the given class located at (x, y)
     */
    public <T extends Actor> T getActorAt(Class<T> cls, int x, int y)
    {
        return Lambda.first(Lambda.filterType(grid[x][y].actors, cls));
    }

    /**
     * Returns an {@code Actor} of the given class at the given location, or null if there is none.
     * If there are multiple possible {@code Actor} that could be returned, an arbitrary {@code
     * Actor} is returned.
     * @param <T> the generic type of the class to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @param pos the location being queried
     * @return an {@code Actor} of the given class located at (x, y)
     */
    public final <T extends Actor> T getActorAt(Class<T> cls, Coordinate pos)
    {
        Guard.argumentIsNotNull(pos);

        return getActorAt(cls, pos.x(), pos.y());
    }

    /**
     * Returns a {@code Collection<T extends Actor>} of all {@code Actor} of the given class located
     * at (x, y).
     * @param <T> the generic type of the class to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @param x the x location being queried
     * @param y the y location being queried
     * @return a {@code Collection<T extends Actor>} of all {@code Actor} of the given class located
     *         at (x, y)
     */
    public <T extends Actor> Collection<T> getActorsAt(Class<T> cls, int x, int y)
    {
        return Lambda.toSet(Lambda.filterType(grid[x][y].actors, cls));
    }

    /**
     * Returns a {@code Collection<Actor>} of all collectible {@code Actor} located
     * at (x, y).
     * @param x the x location being queried
     * @param y the y location being queried
     * @return a {@code Collection<Actor>} of all collectible {@code Actor} located
     *         at (x, y)
     */
    public Collection<Actor> getCollectiblesAt(int x, int y) {
        return Lambda.toSet(Lambda.filter(grid[x][y].actors, Actor.collectibleFilter));
    }
    
    /**
     * Returns a {@code Collection<T extends Actor>} of all {@code Actor} of the given class at the
     * given location.
     * @param <T> the generic type of the class type to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @param pos the location being queried
     * @return a {@code Collection<T extends Actor>} of all {@code Actor} of the given class located
     *         at (x, y)
     */
    public final <T extends Actor> Collection<T> getActorsAt(Class<T> cls, Coordinate pos)
    {
        Guard.argumentIsNotNull(pos);

        return getActorsAt(cls, pos.x(), pos.y());
    }

    /**
     * Returns an {@code Actor} of the given class, regardless of the location on the {@code World}
     * , or null if there is none. If there are multiple possible {@code Actor} that could be
     * returned, an arbitrary {@code Actor} is returned.
     * @param <T> the generic type of the class to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @return an {@code Actor} of the given class
     */
    public <T extends Actor> T getActor(Class<T> cls)
    {
        return Lambda.first(Lambda.filterType(register, cls));
    }

    /**
     * Returns a {@code Collection<T extends Actor>} of all {@code Actor} of the given class,
     * Regardless of their location on the {@code World}.
     * @param <T> the generic type of the class to be returned
     * @param cls the {@code Class<T extends Actor>} of the {@code Actor} to be returned
     * @return a {@code Collection<T extends Actor>} of all {@code Actor} of the given class
     */
    public <T extends Actor> Collection<T> getActors(Class<T> cls)
    {
        return Lambda.toSet(Lambda.filterType(register, cls));
    }

    /**
     * Returns every tile that is visible at a given location, ordered such that the tile with
     * highest priority (the tile returned by look) is first, and the lowest priority is last. This
     * last tile will be the face of the actual tile itself. Note that any actor which has a null
     * face will not be included.
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return every tile that is visible at a given location
     */
    public List<ColoredChar> lookAll(int x, int y)
    {
        Guard.argumentsInsideBounds(x, y, width, height);

        List<ColoredChar> look = new ArrayList<ColoredChar>();

        for(Class<? extends Actor> cls : drawOrder)
            for(Actor actor : getActorsAt(cls, x, y))
            {
                ColoredChar face = actor.face();
                if(face != null)
                    look.add(actor.face());
            }

        look.add(tileAt(x, y));

        return look;
    }

    /**
     * Returns every tile that is visible at a given location, ordered such that the tile with
     * highest priority (the tile returned by look) is first, and the lowest priority is last. This
     * last tile will be the face of the actual tile itself. Note that any actor which has a null
     * face will not be included.
     * @param pos the location being queried
     * @return every tile that is visible at a given location
     */
    public final List<ColoredChar> lookAll(Coordinate pos)
    {
        Guard.argumentIsNotNull(pos);

        return lookAll(pos.x(), pos.y());
    }

    /**
     * Returns the face that should be drawn for the given location. This should be the same as the
     * first face returned by lookAll.
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return the face that should be drawn for the given location.
     */
    public final ColoredChar look(int x, int y)
    {
    	//TODO ak chceme implementovat farebne pozadia, toto treba zmenit
    	//mozno treba pouzit google TreeMultimap
    	//ak vidime policko aktualne, vykresli sa policko aj vsetko co je na nom
    	if (grid[x][y].seenNow){
    		ColoredChar c = lookAll(x, y).get(0);
    		return c.applyLight(this.lightAt(x, y));
    	}
    	//ak sme ho videli predtym, vykresli sa iba policko v tmavsej farbe
    	if (grid[x][y].seen) return tileAt(x,y).getDimmed();
    	//inak sa nevykresli nic
    	return ColoredChar.blank;
    }

    /**
     * Returns the face that should be drawn for the given location.
     * @param pos the location being queried
     * @return the face that should be drawn for the given location.
     */

    public final ColoredChar look(Coordinate pos)
    {
        Guard.argumentIsNotNull(pos);

        return look(pos.x(), pos.y());
    }

    /**
     * Returns the face of the tile at the provided (x, y) coordinates.
     * @param x the x value of the position being queried
     * @param y the y value of the position being queried
     * @return the face of the tile at (x, y)
     */
    public ColoredChar tileAt(int x, int y)
    {
        Guard.argumentsInsideBounds(x, y, width, height);
        return grid[x][y].face;
    }

    public Light lightAt(int x, int y)
    {
        Guard.argumentsInsideBounds(x, y, width, height);
        return grid[x][y].light;
    }
    
    public void resetLight(){
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[i][j].light.setLights(this.baseLight);

			}
		}
    }
    
    public void initLight(){
    	resetLight();
        for(Actor actor : getActors(LightSource.class))
            actor.act();
    }
    
    /**
     * Returns the face of the tile at the provided coordinates.
     * @param coord the value of the position being queried
     * @return the face of the tile at then given {@code Coordinate}
     */
    public final ColoredChar tileAt(Coordinate coord)
    {
        Guard.argumentIsNotNull(coord);

        return tileAt(coord.x(), coord.y());
    }

    /**
     * Returns true if the tile at the provided (x, y) coordinates is passable.
     * @param x the x value of the position being queried
     * @param y the y value of the position being queried
     * @return true if the tile at (x, y)
     */
    public boolean passableAt(int x, int y)
    {
        Guard.argumentsInsideBounds(x, y, width, height);

        return grid[x][y].passable;
    }

    /**
     * Returns true if the tile at the provided {@code Coordinate} is passable.
     * @param coord the value of the position being queried
     * @return true if the tile at the given {@code Coordinate}
     */
    public final boolean passableAt(Coordinate coord)
    {
        Guard.argumentIsNotNull(coord);

        return passableAt(coord.x(), coord.y());
    }

    /**
     * Marks tiles which are currently seen by the camera view. These will be drawn on the screen.
     * 
     * @param view - which tiles are currently seen
     */
    public void markSeen(Collection<Coordinate> view){
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                grid[x][y].seenNow = false;
        //vynulovali sme predchadzajuci view
    	for (Coordinate c: view){
    		grid[c.x()][c.y()].seen = true;
    		grid[c.x()][c.y()].seenNow = true;
    	}
    }
    
    /**
     * Sets the face and passable value of the tile at the specified (x, y) location.
     * @param face the new face of the tile
     * @param passable the new passable value of the tile
     * @param x the x value of the position being updated
     * @param y the y value of the position being updated
     */
    public void setTile(ColoredChar face, boolean passable, int x, int y)
    {
        Guard.argumentIsNotNull(face);
        Guard.argumentsInsideBounds(x, y, width, height);

        grid[x][y].face = face;
        grid[x][y].passable = passable;
    }

    /**
     * Sets the face and passable value of the tile at the specified {@code Coordinate}.
     * @param face the new face of the tile
     * @param passable the new passable value of the tile
     * @param coord the value of the position being updated
     */
    public final void setTile(ColoredChar face, boolean passable, Coordinate coord)
    {
        Guard.argumentIsNotNull(coord);

        setTile(face, passable, coord.x(), coord.y());
    }

    /**
     * Gets a randomly chosen open tile on the {@code World} within the given bounds. If after 100
     * randomly selected tiles are closed, then each tile will be checked and the first open tile
     * found is returned. If the entire {@code World} is closed, null is returned.
     * @param dice the random number generator used to randomly select tiles
     * @param x1 the bounds left most value
     * @param y1 the bounds upper most value
     * @param x2 the bounds right most value
     * @param y2 the bounds bottom most value
     * @return a randomly chosen open tile
     */
    public Coordinate getOpenTile(Dice dice, int x1, int y1, int x2, int y2)
    {
        Guard.argumentIsNotNull(dice);
        Guard.argumentsInsideBounds(x1, y1, width, height);
        Guard.argumentsInsideBounds(x2, y2, width, height);

        for(int i = 0; i < 100; i++)
        {
            int x = dice.nextInt(x1, x2);
            int y = dice.nextInt(y1, y2);
            if(passableAt(x, y))
                return new Coordinate(x, y);
        }

        for(int x = x1; x <= x2; x++)
            for(int y = y1; y <= y2; y++)
                if(passableAt(x, y))
                    return new Coordinate(x, y);

        return null;
    }

    /**
     * Gets a randomly chosen open tile on the {@code World} within the given bounds. If after 100
     * randomly selected tiles are closed, then each tile will be checked and the first open tile
     * found is returned. If the entire {@code World} is closed, null is returned.
     * @param dice the random number generator used to randomly select tiles
     * @param topLeft the bounds upper-left most value
     * @param bottomRight the bounds bottom-right most value
     * @return a randomly chosen open tile
     */
    public final Coordinate getOpenTile(Dice dice, Coordinate topLeft, Coordinate bottomRight)
    {
        Guard.argumentsAreNotNull(topLeft, bottomRight);

        return getOpenTile(dice, topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y());
    }

    /**
     * Gets a randomly chosen open tile from anywhere on the {@code World}. If after 100 randomly
     * selected tiles are closed, then each tile will be checked and the first open tile found is
     * returned. If the entire {@code World} is closed, null is returned.
     * @param dice the random number generator used to randomly select tiles
     * @return a randomly chosen open tile
     */
    public final Coordinate getOpenTile(Dice dice)
    {
        return getOpenTile(dice, 0, 0, width - 1, height - 1);
    }

    /**
     * Gets a randomly chosen open tile on the {@code World} within the given bounds. The global
     * instance of {@code Dice} is used as the default parameter for randomly choosing the tiles. If
     * after 100 randomly selected tiles are closed, then each tile will be checked and the first
     * open tile found is returned. If the entire {@code World} is closed, null is returned.
     * @param x1 the bounds left most value
     * @param y1 the bounds upper most value
     * @param x2 the bounds right most value
     * @param y2 the bounds bottom most value
     * @return a randomly chosen open tile
     */
    public final Coordinate getOpenTile(int x1, int y1, int x2, int y2)
    {
        return getOpenTile(Dice.global, x1, y1, x2, y2);
    }

    /**
     * Gets a randomly chosen open tile on the {@code World} within the given bounds. The global
     * instance of {@code Dice} is used as the default parameter for randomly choosing the tiles. If
     * after 100 randomly selected tiles are closed, then each tile will be checked and the first
     * open tile found is returned. If the entire {@code World} is closed, null is returned.
     * @param topLeft the bounds upper-left most value
     * @param bottomRight the bounds bottom-right most value
     * @return a randomly chosen open tile
     */
    public final Coordinate getOpenTile(Coordinate topLeft, Coordinate bottomRight)
    {
        return getOpenTile(Dice.global, topLeft, bottomRight);
    }

    /**
     * Gets a randomly chosen open tile from anywhere on the {@code World}. The global instance of
     * {@code Dice} is used as the default parameter for randomly choosing the tiles. If after 100
     * randomly selected tiles are closed, then each tile will be checked and the first open tile
     * found is returned. If the entire {@code World} is closed, null is returned.
     * @return a randomly chosen open tile
     */
    public final Coordinate getOpenTile()
    {
        return getOpenTile(Dice.global);
    }

    /**
     * Returns true if the given (x, y) location is inside the bounds of the {@code World}.
     * @param x the x value of the location being queried
     * @param y the y value of the location being queried
     * @return true if the given (x, y) location is inside the bounds of the {@code World}
     */
    public boolean insideBounds(int x, int y)
    {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /**
     * Returns true if the given {@code Coordinate} location is inside the bounds of the {@code
     * World}.
     * @param pos the location on the {@code World} being queried
     * @return true if pos is inside the bounds of the {@code World}
     */
    public final boolean insideBounds(Coordinate pos)
    {
        return insideBounds(pos.x(), pos.y());
    }

    void addToGrid(Actor actor)
    {
        grid[actor.x()][actor.y()].actors.add(actor);
    }

    void removeFromGrid(Actor actor)
    {
        grid[actor.x()][actor.y()].actors.remove(actor);
    }

    void registerActor(Actor actor)
    {
        register.add(actor);
        for(Actor held : actor.getHeldItems(Actor.class))
            registerActor(held);
    }

    void unregisterActor(Actor actor)
    {
        register.remove(actor);
    }

    private class Tile
    {
        public boolean passable;
        public ColoredChar face;
        public Set<Actor> actors;
        public boolean seen; //ci uz toto policko videl
        public boolean seenNow; //ci ho vidi prave teraz
        public Light light = new Light(Color.gray,0,0,0,0);
        //to je jedno aka je tam farba, updatuje sa to pred vykreslenim

        public Tile()
        {
            passable = true;
            face = ColoredChar.create('.');
            actors = new HashSet<Actor>();
            seen = false;
            seenNow = false;
        }
    }
    
    public abstract boolean isCreatureAt(int x, int y);
    public abstract boolean isCreatureAt(Coordinate coord);

}
