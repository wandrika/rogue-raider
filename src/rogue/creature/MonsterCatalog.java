package rogue.creature;

import jade.util.Dice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MonsterCatalog {
	private static Map<String, Map<String,String>> catalog = new HashMap<String, Map<String,String>>();

	//reading monster prototypes from the file
	static{
		Scanner s;
		try {
			s = new Scanner(new File(".\\monsters.csv"));
			String[] hlavicka = s.nextLine().split(";");
			
			while (s.hasNextLine()){
				Map<String,String> vlastnosti = new HashMap<String,String>(hlavicka.length);
				String[] riadok = s.nextLine().split(";");
				vlastnosti.clear();
				for (int i=0; i<hlavicka.length; i++){
					vlastnosti.put(hlavicka[i], riadok[i]);
				}
				catalog.put(vlastnosti.get("name"), vlastnosti);
  			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}



	}

	public static Monster createRandomMonsterForLevel(int level) throws MonsterMapException{
		ArrayList<String> povolene = new ArrayList<String>(); 
		for (String monsterName: catalog.keySet()){
			int minLevel = Integer.parseInt(catalog.get(monsterName).get("minLevel"));
			int maxLevel = Integer.parseInt(catalog.get(monsterName).get("maxLevel"));
			if (minLevel<=level && maxLevel>=level){
				povolene.add(monsterName);
			}
		}
		int index = Dice.global.nextInt(0, povolene.size()-1);
		String vzor = povolene.get(index);
		Map<String,String> line = catalog.get(vzor);
		String stringFace = line.get("face");	//toto musime brat osobitne, face je vlastnostou Actora
		char charFace = stringFace.charAt(0);
		//int intColor = Integer.parseInt(line.get("color"));
		//Monster m = new Monster(charFace,intColor);
		String colorName = line.get("color");
		Monster m = new Monster (charFace, colorName);
		m.setProperties(line);
		return m;
	}

public static void main(String[]args) throws MonsterMapException{
	for (int i=0; i<20; i++){
		Monster m = MonsterCatalog.createRandomMonsterForLevel(1);
		System.out.println(m.name);
	}
}


}
