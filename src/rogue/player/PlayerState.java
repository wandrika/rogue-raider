package rogue.player;

import jade.util.datatype.Direction;
import jade.util.datatype.MessageQueue;
import rogue.creature.Monster;
import rogue.items.Flare;

public enum PlayerState {
	

    MOVING {
        @Override
        public PlayerState handleInput(int key) {
        	Player pl = Player.getInstance();
    		switch(key)	{
    		case 70: //'f'
    			if(!pl.getEnemies().isEmpty()){
    				pl.enemyIndex = pl.enemyIndex % pl.enemiesSeen.size();
    				Monster chosen = pl.enemiesSeen.get(pl.enemyIndex);
    				pl.term.highlight(pl.aim(chosen.x(), chosen.y()));
    				//teraz musi hrac zmenit target alebo potvrdit vyber
    				pl.enemyIndex = 0;
    				
    			}
    			pl.moveFinished = false;
    			return AIMING;
    		case 44: //','
    			Flare f = pl.getLitFlare();
    			if(f!=null){
    				f.dropFromHolder();
    			}
    			else{
    				f = pl.getFirstHeldItem(Flare.class);
    				if(f!=null) f.turnOn();
    				else MessageQueue.add("You have no more flares.");
    			}
    			pl.moveFinished = true;
    			return MOVING;
    		default: //direction keys or nothing
    			Direction dir = Direction.keyToDir(key);
    			if(dir != null){
    				pl.move(dir);
    				f = pl.world().getActorAt(Flare.class, pl.x(), pl.y());
    				if(f!=null) {
    					pl.world().removeActor(f);
    					pl.pickUp(f);
    				}
    				pl.moveFinished = true;
    			}
    			else{ //unmapped key
    				pl.moveFinished = false;
    			}
    			return MOVING;
    		}
        }
    }, 
    
    AIMING {
        @Override
        public PlayerState handleInput(int key) {
        	Player pl = Player.getInstance();
    		switch(key)	{
    		case 84: //'t' change target
    			pl.enemyIndex = (pl.enemyIndex+1) % pl.enemiesSeen.size();
    			Monster chosen = pl.enemiesSeen.get(pl.enemyIndex);
				pl.term.highlight(pl.aim(chosen.x(), chosen.y()));
				pl.moveFinished = false;
				return AIMING;
    		case 70: //'f'
    		case 10: //Enter
    			chosen = pl.enemiesSeen.get(pl.enemyIndex);
    			pl.shoot(chosen.x(), chosen.y(), pl.pistols);
    			pl.moveFinished = true;
    			return MOVING;
    		case 27: //Esc
    			pl.moveFinished = false;
    			return MOVING;
    		default: 
    			return AIMING;
    		}
    		
        }
    };

    //really WEIRD that the abstract method must be defined after all the implementations, but here we go:
    public abstract PlayerState handleInput(int key);

} 
