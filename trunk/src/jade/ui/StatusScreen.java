package jade.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import rogue.Rogue;
import rogue.creature.Creature;
import rogue.creature.Monster;
import rogue.player.Player;

/**
 * This panes shows health (actual hit points) of the player and all the monsters in visual range.
 * @author Wandrika
 *
 */
@SuppressWarnings("serial")
public class StatusScreen extends JPanel {
	private int tileWidth;
	private int tileHeight;
	private int componentWidth;
	//player reference
	private Player player = Rogue.getPlayer();

	public StatusScreen(int columns, int rows, int fontSize)
	{
		tileWidth = fontSize * 3 / 4;
		tileHeight = fontSize;
		componentWidth = columns * tileWidth;
		setPreferredSize(new Dimension(componentWidth, rows * tileHeight));
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
		setBackground(Color.gray);
		setFocusable(false);
	}

	private void paintStatusBar(Graphics page, Creature cre, int y){
		int x = 2;
		page.setColor(Color.blue);
		page.fillRect(x, y+2, Math.round(componentWidth*cre.getActualHitPoints()/cre.getHitPoints())-5, tileHeight);
		page.setColor(Color.white);
		page.drawString(cre.getName()+":", x, y);
		y += tileHeight;
		page.drawString("Health", x, y);
	}
	
	@Override
	protected void paintComponent(Graphics page) 
	{
		super.paintComponent(page);
		if (player!=null){
			int y = tileHeight;
			paintStatusBar(page, player, y);
			
			List<Monster> monsters = player.getEnemies();
			for (Monster m: monsters){
				y += tileHeight*3;
				paintStatusBar(page, m, y);
			}
		}
	}
}
