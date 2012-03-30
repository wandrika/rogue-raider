package rogue.creature;

import jade.util.Dice;

public enum Status {
    SLEEPING ("Sleeping"),
    WANDERING   ("Wandering"),
    HUNTING   ("Hunting"),
    FLEEING    ("Fleeing");

    private final String message;  
    
    Status(String message) {
        this.message = message;
    }
    
    public String getMessage()   { return message; }
    
    public static Status getRandomStatus(){
    	Dice d = Dice.global;
    	int cislo = d.nextInt(1, 4);
    	switch (cislo) {
    	case 1:  return Status.SLEEPING;
    	case 2:  return Status.WANDERING;
    	case 3:  return Status.HUNTING;
    	default: return Status.FLEEING;
    	}
    }

}