package jade.util;

public class Capitalizer {

	public static String capitalize(String name){
        String firstLetter = name.substring(0,1);  
        String remainder   = name.substring(1);   
        return firstLetter.toUpperCase() + remainder.toLowerCase();
	}
	
}
