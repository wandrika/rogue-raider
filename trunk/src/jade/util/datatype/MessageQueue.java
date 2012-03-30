package jade.util.datatype;

import java.awt.Color;
import java.util.LinkedList;

/**
 * A cache to store the last few game messages (the number is specified in the capacity field). 
 * When a new message is generated, the oldest is discarded.
 * @author Wandrika
 *
 */
public class MessageQueue {
	private static int capacity = 25;
	private static LinkedList<ColoredString> queue = new LinkedList<ColoredString>();


	public static int size() {
		return queue.size();
	}
	
	public static synchronized boolean add(String s) {
		if (queue.size()>=capacity) queue.removeFirst();
		ColoredString cs = new ColoredString(s);
		return queue.add(cs);
	}
	
	public static synchronized boolean add(String s, Color c) {
		if (queue.size()>=capacity) queue.removeFirst();
		ColoredString cs = new ColoredString(s,c);
		return queue.add(cs);
	}
	
	/**
	 * Returns last few messages from the queue.
	 * 
	 * @param n of elements in the subList
	 * @return the last n messages
	 */
	public static synchronized ColoredString[] getItems(int n){
		int actualSize = queue.size();
		if (n>=actualSize) return queue.toArray(new ColoredString[actualSize]);
		return queue.subList(actualSize-n, actualSize).toArray(new ColoredString[n]);
	}

}
