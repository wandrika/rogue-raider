package rogue;

import jade.core.World;
import jade.ui.TermPanel;
import jade.ui.Terminal;
import rogue.creature.Monster;
import rogue.creature.MonsterCatalog;
import rogue.creature.MonsterMapException;
import rogue.creature.Player;
import rogue.level.Level;

public class Rogue {
	//we need to reference the player from outside
	private static Player player = new Player();
	
	public static Player getPlayer(){
		return player;
	}
	
    public static void main(String[] args) throws InterruptedException, MonsterMapException
    {   
    	World world = new Level(80, 24, player);
        Monster m = MonsterCatalog.createRandomMonsterForLevel(1);
        world.addActor(m);
        //najprv je nutne vytvorit level, hraca a prisery a az potom terminal, ktory na ne odkazuje (hlavne StatusScreen)
    	Terminal term = TermPanel.getFramedTerminal("Jade Rogue");
    	player.setTerminal(term);
        term.registerCamera(player, player.pos());
        world.initLight();
        while(!player.expired())
        {
        	world.markSeen(player.getViewField());
            term.clearBuffer();
            for(int x = 0; x < world.width(); x++)
                for(int y = 0; y < world.height(); y++)
                    term.bufferChar(x, y, world.look(x, y));
            term.refreshScreen();
System.out.println();
            world.tick();
            if (m.expired()) {
            	m = MonsterCatalog.createRandomMonsterForLevel(1);
            	//System.out.println(m);
            	world.addActor(m);
            }
        }

        System.exit(0);
    }
}
