package jade.ui;

import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameScreen extends JPanel implements KeyListener {

	private int tileWidth;
	private int tileHeight;
	private BlockingQueue<Integer> inputBuffer;
	private Map<Coordinate, ColoredChar> screenBuffer;
	private List<Coordinate> highlighted;

	public GameScreen(int columns, int rows, int fontSize)
	{
		inputBuffer = new LinkedBlockingQueue<Integer>();
		screenBuffer = new HashMap<Coordinate, ColoredChar>();
		highlighted = new ArrayList<Coordinate>();

		addKeyListener(this);
		tileWidth = fontSize * 3 / 4;
		tileHeight = fontSize;
		setPreferredSize(new Dimension(columns * tileWidth, rows * tileHeight));
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
		setBackground(Color.black);
		setFocusable(true);
	}

	@Override
	protected void paintComponent(Graphics page) 
	{
		super.paintComponent(page);
		synchronized(screenBuffer)
		{
			for(Coordinate coord : screenBuffer.keySet())
			{
				ColoredChar ch = screenBuffer.get(coord);
				int x = tileWidth * coord.x();
				int y = tileHeight * (coord.y() + 1);
				if (ch.background() != null){
					page.setColor(ch.background());
					//a correction to have the rectangle around the character
					page.fillRect(x-1, y-tileHeight+3, tileWidth, tileHeight);
					if(highlighted.contains(coord)){
						page.setColor(Color.red); //FIXME set a contrasting color according to background
						page.fillRect(x-1, y-tileHeight+3, tileWidth, tileHeight);
					}
				}
				page.setColor(ch.color());
				page.drawString(ch.toString(), x, y);
			}
			highlighted.clear();
		}
	}

	public void setBuffer(Map<Coordinate, ColoredChar> buffer)
	{
		synchronized(screenBuffer)
		{
			screenBuffer.clear();
			screenBuffer.putAll(buffer);
		}
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		int c = event.getKeyCode();
		//TODO ak chceme pridat dalsie ovladacie tlacitka, napriklad medzeru, najprv odkomentovat dalsi riadok, zistit kod (namapovat v Direction - len ak by to bolo tlacitko na pohyb), potom pridat v Player
		//System.out.println(c);
		//System.out.println(event.getKeyChar());
		inputBuffer.offer(c);
	}

	public int consumeKeyPress() throws InterruptedException
	{
		return inputBuffer.take();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{}

	@Override
	public void keyTyped(KeyEvent e)
	{}

	public List<Coordinate> getHighlighted() {
		return highlighted;
	}

	public void setHighlighted(List<Coordinate> highlighted) {
		this.highlighted = highlighted;
	}

}
